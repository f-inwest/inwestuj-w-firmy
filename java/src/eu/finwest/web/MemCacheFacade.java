/**
 * 
 */
package eu.finwest.web;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import eu.finwest.dao.MockDataBuilder;
import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.Campaign;
import eu.finwest.datamodel.Category;
import eu.finwest.datamodel.Listing;
import eu.finwest.datamodel.ListingLocation;
import eu.finwest.datamodel.Location;
import eu.finwest.datamodel.PricePoint;
import eu.finwest.vo.CampaignVO;
import eu.finwest.vo.DtoToVoConverter;
import eu.finwest.vo.UserVO;

/**
 * @author grzegorznittner
 *
 */
public class MemCacheFacade {
	private static final Logger log = Logger.getLogger(MemCacheFacade.class.getName());
	
	private static MemCacheFacade instance;
	
	public static MemCacheFacade instance() {
		if (instance == null) {
			instance  = new MemCacheFacade();
		}
		return instance;
	}

	private static final String MEMCACHE_ALL_LISTING_LOCATIONS = "AllListingLocations";
	private static final String MEMCACHE_TOP_LISTING_LOCATIONS = "TopListingLocations";
	private static final String MEMCACHE_CATEGORIES = "Categories";
	private static final String MEMCACHE_CAMPAIGNS = "CampaingsVO";
	private static final String MEMCACHE_CAMPAIGNS_RAW = "CampaingsDTO";
	private static final String MEMCACHE_PRICEPOINTS_GROUPED = "PricePointsGrouped";
	
	private MemCacheFacade() {
	}
	
	private ObjectifyDatastoreDAO getDAO() {
		return ObjectifyDatastoreDAO.getInstance();
	}
	
	public void clearAllListingLocations() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		mem.delete(MemCacheFacade.MEMCACHE_ALL_LISTING_LOCATIONS);
		mem.delete(MemCacheFacade.MEMCACHE_TOP_LISTING_LOCATIONS);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Category> getCategoriesMap() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		if (!mem.contains(MEMCACHE_CATEGORIES)) {
			updateCategories();
		}
		Map<String, Map<String, Category>> categories = (Map<String, Map<String, Category>>)mem.get(MEMCACHE_CATEGORIES);
		if (categories.containsKey(FrontController.getCampaign().getSubdomain())) {
			return categories.get(FrontController.getCampaign().getSubdomain());
		} else {
			return categories.get("en");
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Category> getInitalCategoriesMap() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		if (!mem.contains(MEMCACHE_CATEGORIES)) {
			updateCategories();
		}
		return ((Map<String, Map<String, Category>>)mem.get(MEMCACHE_CATEGORIES)).get("en");
	}

	public String getCategoryLabel(String category) {
		if (category == null) {
			return null;
		}
		Map<String, Category> categories = getCategoriesMap();
		Category cat = categories.get(category);
		
		if (cat == null) {
			log.log(Level.WARNING, "@@@@@@@@@@@@@@@@@@@ Category '" + category + "' doesn't exist!!!");
			return category;
		}
		return FrontController.getLangVersion() == LangVersion.PL ? cat.namePl : cat.name;
	}
	
	public void updateCategories() {
		List<Category> categories = getDAO().getCategories();
		if (categories.size() == 0) {
			new MockDataBuilder().quickDatastoreInit();
			categories = getDAO().getCategories();
		}

		Map<String, Map<String, Category>> statCategories = new HashMap<String, Map<String, Category>>();
		for (Category c : categories) {
			String campaign = c.campaign;
			if (campaign == null) {
				c.campaign = "en";
				campaign = "en";
			}
			Map<String, Category> campCategories = statCategories.get(campaign);
			if (campCategories == null) {
				campCategories = new HashMap<String, Category>();
				statCategories.put(campaign, campCategories);
			}
			campCategories.put(c.name, c);
		}
		updateCategories(statCategories);
	}
	
	public void updateCategories(Map<String, Map<String, Category>> statCategories) {
        MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		mem.put(MemCacheFacade.MEMCACHE_CATEGORIES, statCategories);
	}
	
	public Map<String, String> getTopCategories() {
		Collection<Category> categories = getCategoriesMap().values();

		Map<String, String> result = new LinkedHashMap<String, String>();
		for (Category cat : categories) {
			if (cat.count > 0) {
				result.put(cat.name, FrontController.getLangVersion() == LangVersion.EN ? cat.name : cat.namePl);
			}
		}
		return result;
	}
	
	public List<Object[]> getListingLocationsFromCache() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		@SuppressWarnings("unchecked")
		Map<String, List<Object[]>> allData = (Map<String, List<Object[]>>)mem.get(MemCacheFacade.MEMCACHE_ALL_LISTING_LOCATIONS);
		if (allData == null) {
			List<ListingLocation> locations = getDAO().getAllListingLocations();

			allData = convertListingLocations(locations);
			mem.put(MemCacheFacade.MEMCACHE_ALL_LISTING_LOCATIONS, allData);
		}
		List<Object[]> list = allData.get(FrontController.getCampaign().getSubdomain());
		return list != null ? list : new ArrayList<Object[]>();
	}

	public void updateLocations(List<Location> allLocations, List<ListingLocation> allListingLocations) {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		Map<String, List<Object[]>> convertedListingLocations = convertListingLocations(allListingLocations);
		mem.put(MemCacheFacade.MEMCACHE_ALL_LISTING_LOCATIONS, convertedListingLocations);
		
		Map<String, Map<String, Integer>> convertedTopLocations = convertTopLocations(allLocations);
		mem.put(MemCacheFacade.MEMCACHE_TOP_LISTING_LOCATIONS, convertedTopLocations);
	}

	private Map<String, List<Object[]>> convertListingLocations(List<ListingLocation> locations) {
		Map<String, List<Object[]>> allData = new HashMap<String, List<Object[]>>();
		
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("###.######", dfs);			
		Set<String> locationSet = new HashSet<String>();

		String location[] = new String[2];
		for (ListingLocation loc : locations) {
			List<Object[]> data = allData.get(loc.campaign);
			if (data == null) {
				data = new ArrayList<Object[]>();
				allData.put(loc.campaign, data);
			}

			location[0] = df.format(loc.latitude);
			location[1] = df.format(loc.longitude);
			while (locationSet.contains(location[0] + location[1])) {
				location = randomizeLocation(loc, df);
			}
			locationSet.add(location[0] + location[1]);
			data.add(new Object[] {loc.getWebKey(), location[0], location[1]});
		}
		return allData;
	}

    private String[] randomizeLocation(ListingLocation loc, DecimalFormat df) {
    	log.info("Randomizing: " + loc);
		return new String[] {df.format(loc.latitude + ((double)new Random().nextInt(100)) * 0.000001),
				df.format(loc.longitude + ((double)new Random().nextInt(100)) * 0.000001)};
	}

	public void updateCacheForListing(Listing listing, Listing.State oldState) {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		@SuppressWarnings("unchecked")
		Map<String, List<Object[]>> allData = (Map<String, List<Object[]>>)mem.get(MemCacheFacade.MEMCACHE_ALL_LISTING_LOCATIONS);
		
		List<Object[]> result = allData.get(FrontController.getCampaign().getSubdomain());
		if (result == null) {
			result = new ArrayList<Object[]>();
			allData.put(FrontController.getCampaign().getSubdomain(), result);
		}

		boolean modified = false;
		if (oldState == Listing.State.POSTED && listing.state == Listing.State.ACTIVE) {
			ListingLocation loc = new ListingLocation(listing);
			result.add(new Object[]{loc.getWebKey(), loc.latitude, loc.longitude});
			modified = true;
		}
		if (listing.state == Listing.State.CLOSED || listing.state == Listing.State.WITHDRAWN) {
			Object[] array = null;
			for (int i = 0; i < result.size(); i++) {
				array = result.get(i);
				if (listing.getWebKey().equals(array[0])) {
					result.remove(i);
					modified = true;
					break;
				}
			}
		}
		if (modified) {
			mem.put(MemCacheFacade.MEMCACHE_ALL_LISTING_LOCATIONS, allData);
		} else {
			log.log(Level.WARNING, "Cache has not contained listing data, but it should. " + listing);
		}
	}

	public Map<String, Integer> getTopLocations() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Integer>> allTopLocations = (Map<String, Map<String, Integer>>)mem.get(MemCacheFacade.MEMCACHE_TOP_LISTING_LOCATIONS);
		
		if (allTopLocations == null) {
			List<Location> locations = getDAO().getTopLocations();
			allTopLocations = convertTopLocations(locations);
		}
		return allTopLocations.get(FrontController.getCampaign().getSubdomain());
	}

	private Map<String, Map<String, Integer>> convertTopLocations(List<Location> locations) {
		Map<String, Map<String, Integer>> allTopLocations = new HashMap<String, Map<String, Integer>>();
		
		for (Location loc : locations) {
			Map<String, Integer> campaignTop = allTopLocations.get(loc.campaign);
			if (!allTopLocations.containsKey(loc.campaign)) {
				campaignTop = new LinkedHashMap<String, Integer>();
				allTopLocations.put(loc.campaign, campaignTop);
			}
			campaignTop.put(loc.briefAddress, loc.value);
		}
		return allTopLocations;
	}
	
	public void cleanCampaingsCache() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		mem.delete(MEMCACHE_CAMPAIGNS);
		mem.delete(MEMCACHE_CAMPAIGNS_RAW);
	}

	public Map<String, CampaignVO> getAllCampaigns() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		@SuppressWarnings("unchecked")
		Map<String, CampaignVO> allCampaigns = (Map<String, CampaignVO>)mem.get(MemCacheFacade.MEMCACHE_CAMPAIGNS);
		
		if (allCampaigns == null) {
			List<Campaign> campaigns = getDAO().getAllCampaigns();
			allCampaigns = new HashMap<String, CampaignVO>();
			allCampaigns.put(FrontController.PL_CAMPAIGN.getSubdomain(), FrontController.PL_CAMPAIGN);
			allCampaigns.put(FrontController.EN_CAMPAIGN.getSubdomain(), FrontController.EN_CAMPAIGN);
			for (Campaign c : campaigns) {
				if (c.status == Campaign.Status.ACTIVE) {
					allCampaigns.put(c.subdomain, DtoToVoConverter.convert(c));
				}
			}
			mem.put(MEMCACHE_CAMPAIGNS_RAW, campaigns);
			mem.put(MEMCACHE_CAMPAIGNS, allCampaigns);
		}
		return allCampaigns;
	}
	
	public CampaignVO getCampaign(String subdomain) {
		return getAllCampaigns().get(subdomain);
	}

	@SuppressWarnings("unchecked")
	public List<CampaignVO> getUserCampaigns(UserVO user) {
		List<CampaignVO> userCampaigns = new ArrayList<CampaignVO>();
		if (user != null) {
			MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
			List<Campaign> allCampaigns = (List<Campaign>)mem.get(MemCacheFacade.MEMCACHE_CAMPAIGNS_RAW);
			if (allCampaigns == null) {
				getAllCampaigns();
				allCampaigns = (List<Campaign>)mem.get(MemCacheFacade.MEMCACHE_CAMPAIGNS_RAW);
			}
			if (allCampaigns == null) {
				return userCampaigns;
			}
			
			for (Campaign c : allCampaigns) {
				if (c.creator.getId() == user.toKeyId()) {
					userCampaigns.add(DtoToVoConverter.convert(c));
				}
			}
		}
		return userCampaigns;
	}

	public void cleanPricePointsCache() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		Map<PricePoint.Group, List<PricePoint>> groupedPP = loadPricePoints();
		mem.delete(MEMCACHE_PRICEPOINTS_GROUPED);
		mem.put(MEMCACHE_PRICEPOINTS_GROUPED, groupedPP);
	}

	private Map<PricePoint.Group, List<PricePoint>> loadPricePoints() {
		List<PricePoint> pricePoints = getDAO().getAllPricePoints();
		Map<PricePoint.Group, List<PricePoint>> groupedPP = new HashMap<PricePoint.Group, List<PricePoint>>();
		for (PricePoint pp : pricePoints) {
			List<PricePoint> list = groupedPP.get(pp.group);
			if (list == null) {
				list = new ArrayList<PricePoint>();
				groupedPP.put(pp.group, list);
			}
			list.add(pp);
		}
		return groupedPP;
	}
	
	@SuppressWarnings("unchecked")
	public List<PricePoint> getPricePoints(PricePoint.Group pricePointGroup) {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		Map<PricePoint.Group, List<PricePoint>> groupedPP = (Map<PricePoint.Group, List<PricePoint>>)mem.get(MEMCACHE_PRICEPOINTS_GROUPED);
		if (groupedPP == null) {
			groupedPP = loadPricePoints();
			mem.put(MEMCACHE_PRICEPOINTS_GROUPED, groupedPP);
		}
		return (List<PricePoint>)groupedPP.get(pricePointGroup);
	}
	
	@SuppressWarnings("unchecked")
	public Map<PricePoint.Group, List<PricePoint>> getAllPricePoints() {
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		Map<PricePoint.Group, List<PricePoint>> groupedPP = (Map<PricePoint.Group, List<PricePoint>>)mem.get(MEMCACHE_PRICEPOINTS_GROUPED);
		if (groupedPP == null) {
			groupedPP = loadPricePoints();
			mem.put(MEMCACHE_PRICEPOINTS_GROUPED, groupedPP);
		}
		return groupedPP;
	}
}
