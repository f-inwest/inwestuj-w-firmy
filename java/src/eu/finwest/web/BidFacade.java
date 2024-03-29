package eu.finwest.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import eu.finwest.dao.BidObjectifyDatastoreDAO;
import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.Bid;
import eu.finwest.datamodel.BidUser;
import eu.finwest.datamodel.Listing;
import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.VoToModelConverter;
import eu.finwest.util.OfficeHelper;
import eu.finwest.util.Translations;
import eu.finwest.vo.BaseVO;
import eu.finwest.vo.BidListVO;
import eu.finwest.vo.BidUserListVO;
import eu.finwest.vo.BidUserVO;
import eu.finwest.vo.BidVO;
import eu.finwest.vo.DtoToVoConverter;
import eu.finwest.vo.ErrorCodes;
import eu.finwest.vo.ListPropertiesVO;
import eu.finwest.vo.OrderBook;
import eu.finwest.vo.OrderBookVO;
import eu.finwest.vo.UserShortVO;
import eu.finwest.vo.UserVO;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class BidFacade {
	private static final Logger log = Logger.getLogger(BidFacade.class.getName());
	private static BidFacade instance;
	
	public static BidFacade instance() {
		if (instance == null) {
			instance = new BidFacade();
		}
		return instance;
	}
	
	private BidFacade() {
	}
	
	public BidObjectifyDatastoreDAO getDAO () {
		return BidObjectifyDatastoreDAO.getInstance();
	}
	public ObjectifyDatastoreDAO getGeneralDAO () {
		return ObjectifyDatastoreDAO.getInstance();
	}
	
	public BidListVO makeBid(UserVO loggedInUser, String listingId, String type, int amount, int percentage, String text) {
		BidListVO result = new BidListVO();
		if (loggedInUser == null) {
			log.info("User not logged in.");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
			return result;
		}
		if (!SBUser.Status.ACTIVE.toString().equals(loggedInUser.getStatus())) {
			log.warning("User is not active.");
			result.setErrorCode(ErrorCodes.OPERATION_NOT_ALLOWED);
			result.setErrorMessage(Translations.getText("lang_error_user_not_active"));
			return result;
		}
		SBUser investor = VoToModelConverter.convert(loggedInUser);
		if (StringUtils.isEmpty(investor.nickname)) {
			// in dev environment sometimes nickname is empty
			investor = getGeneralDAO().getUser(investor.getWebKey());
		}
		Bid.Type bidType = null;
		try {
			bidType = Bid.Type.valueOf(StringUtils.upperCase(type));
		} catch (Exception e) {
			log.warning("Bid type not recognized: '" + type + "'");
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_bid_not_recognized"));
			return result;
		}
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing.state != Listing.State.ACTIVE) {
			log.warning("Listing is not active: " + listing);
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_listing_not_active"));
			return result;
		}
		SBUser owner = getGeneralDAO().getUser(listing.owner.getString());
		if (owner == null || owner.status != SBUser.Status.ACTIVE) {
			log.warning("Listing owner doesn't exist or is not active: " + owner);
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_user_not_active"));
			return result;
		}
		
		BidUser[] shorts = getDAO().getBidShorts(listing, owner, investor);
		String validActions = getListOfValidActions(shorts[0] != null ? shorts[0].type : null, false);
		if (!validActions.contains(bidType.toString().toLowerCase())) {
			log.warning("Action '" + bidType + "' is not allowed now");
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_bid_type_not_allowed"));
			return result;
		}
		if (bidType == Bid.Type.INVESTOR_ACCEPT && (shorts[0].amount != amount || shorts[0].percentage != percentage)) {
			log.warning("Accepted amount/percentage is not the same as in the offer. Last bid: " + shorts[0]);
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_bid_accepted_value_not_correct"));
			return result;
		}
		log.info("User '" + investor.nickname + "' is making '" + bidType + "' for listing '" + listing.name + "' owned by '" + owner.nickname + "'");
		Bid bid = getDAO().makeBid(shorts, listing, owner, investor, bidType, amount, percentage, text);
		NotificationFacade.instance().scheduleBidNotification(bid);
		if (bid != null) {
			ServiceFacade.instance().setListingMonitor(loggedInUser, listingId);
		}
		
		List<BidVO> bids = new ArrayList<BidVO>();
		bids.add(DtoToVoConverter.convert(bid));
		result.setBids(bids);
		result.setValidActions(getListOfValidActions(bidType, true));
		result.setListing(DtoToVoConverter.convert(listing));
		
		ListPropertiesVO props = new ListPropertiesVO();
		props.setNumberOfResults(shorts[0].counter + 1);
		props.setStartIndex(shorts[0].counter + 1);
		props.setTotalResults(shorts[0].counter + 1);
		result.setBidsProperties(props);
		return result;
	}

	public BidListVO ownerMakesBid(UserVO loggedInUser, String listingId, String investorId, String type, int amount, int percentage, String text) {
		BidListVO result = new BidListVO();
		if (loggedInUser == null) {
			log.info("User not logged in.");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
			return result;
		}
		SBUser owner = VoToModelConverter.convert(loggedInUser);
		if (StringUtils.isEmpty(owner.nickname)) {
			// in dev environment sometimes nickname is empty
			owner = getGeneralDAO().getUser(owner.getWebKey());
		}
		Bid.Type bidType = null;
		try {
			bidType = Bid.Type.valueOf(StringUtils.upperCase(type));
		} catch (Exception e) {
			log.warning("Bid type not recognized: '" + type + "'");
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_bid_not_recognized"));
			return result;
		}
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null || listing.state != Listing.State.ACTIVE) {
			log.warning("Listing is not active: " + listing);
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_listing_not_active"));
			return result;
		}
		SBUser investor = getGeneralDAO().getUser(investorId);
		if (investor == null || investor.status != SBUser.Status.ACTIVE) {
			log.warning("Investor doesn't exist or is not active: " + investor);
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_user_not_active"));
			return result;
		}
		BidUser[] shorts = getDAO().getBidShorts(listing, investor, owner);
		String validActions = getListOfValidActions(shorts[0] != null ? shorts[0].type : null, true);
		if (!validActions.contains(bidType.toString().toLowerCase())) {
			log.warning("Action '" + bidType + "' is not allowed now");
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_bid_type_not_allowed"));
			return result;
		}
		if (bidType == Bid.Type.OWNER_ACCEPT && (shorts[0].amount != amount || shorts[0].percentage != percentage)) {
			log.warning("Accepted amount/percentage is not the same as in the offer. Last bid: " + shorts[0]);
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_bid_accepted_value_not_correct"));
			return result;
		}

		log.info("User '" + owner.nickname + "', owner of listing '" + listing.name + "' is making '" + bidType + "' to offer made by '" + investor.nickname + "'");
		Bid bid = getDAO().makeBid(shorts, listing, investor, owner, bidType, amount, percentage, text);
		NotificationFacade.instance().scheduleBidNotification(bid);
		List<BidVO> bids = new ArrayList<BidVO>();
		bids.add(DtoToVoConverter.convert(bid));
		result.setBids(bids);
		result.setValidActions(getListOfValidActions(bidType, true));
		result.setListing(DtoToVoConverter.convert(listing));
		
		ListPropertiesVO props = new ListPropertiesVO();
		props.setNumberOfResults(shorts[0].counter + 1);
		props.setStartIndex(shorts[0].counter + 1);
		props.setTotalResults(shorts[0].counter + 1);
		result.setBidsProperties(props);
		return result;
	}

	private String getListOfValidActions(Bid.Type bidType, boolean isOwner) {
		if (isOwner) {
			if (bidType == null) {
				return "";
			}
			switch(bidType) {
			case INVESTOR_POST:
			case INVESTOR_COUNTER:
				return "owner_accept,owner_reject,owner_counter";
			case INVESTOR_ACCEPT:
			case INVESTOR_REJECT:
			case INVESTOR_WITHDRAW:
			case OWNER_WITHDRAW:
			case OWNER_REJECT:
			case OWNER_ACCEPT:
				return "";
			case OWNER_COUNTER:
				return "owner_withdraw";
			}
		} else {
			if (bidType == null) {
				return "investor_post";
			}
			switch(bidType) {
			case INVESTOR_POST:
				return "investor_withdraw";
			case INVESTOR_ACCEPT:
			case INVESTOR_REJECT:
			case INVESTOR_WITHDRAW:
			case OWNER_WITHDRAW:
			case OWNER_REJECT:
			case OWNER_ACCEPT:
				return "investor_post";
			case OWNER_COUNTER:
				return "investor_counter,investor_reject,investor_accept";
			}
		}
		return "";
	}

	public OrderBook getOrderBook(UserVO loggedInUser, String listingId) {
		OrderBookVO result = new OrderBookVO();
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			log.warning("Listing doesn't exist.");
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_listing_not_found"));
			return result;
		}
		result.setListing(DtoToVoConverter.convert(listing));
		fetchOrderBook(listing, result);
		return result;
	}

	private void fetchOrderBook(Listing listing, OrderBook result) {
		List<Bid> investorBids = new ArrayList<Bid>();
		List<Bid> ownerBids = new ArrayList<Bid>();
		List<Bid> acceptedBids = new ArrayList<Bid>();
		// getBidsForListing returns bids for which userA is always listing owner
		List<Bid> bids = getDAO().getBidsForListing(listing);
		for (Bid bid : bids) {
			switch(bid.type) {
			case INVESTOR_POST:
			case INVESTOR_COUNTER:
				investorBids.add(bid);
				break;
			case INVESTOR_ACCEPT:
				acceptedBids.add(bid);
				investorBids.add(bid);
				break;
			case OWNER_ACCEPT:
				acceptedBids.add(bid);
				ownerBids.add(bid);
				break;
			case OWNER_COUNTER:
				ownerBids.add(bid);
				break;
			}
		}
		result.setInvestorBids(DtoToVoConverter.convertAnonBids(investorBids));
		result.setOwnerBids(DtoToVoConverter.convertAnonBids(ownerBids));
		result.setAcceptedBids(DtoToVoConverter.convertAnonBids(acceptedBids));
	}

	public BidUserListVO getInvestors(UserVO loggedInUser, String listingId, ListPropertiesVO listProperties) {
		BidUserListVO result = new BidUserListVO();
		if (loggedInUser == null) {
			log.info("User not logged in.");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
			return result;
		}
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null || listing.owner.getId() != loggedInUser.toKeyId()) {
			log.warning("User not an owner of the listing.");
			result.setErrorCode(ErrorCodes.NOT_AN_OWNER);
			result.setErrorMessage(Translations.getText("lang_error_user_not_project_owner"));
			return result;
		}
		result.setListing(DtoToVoConverter.convert(listing));
		SBUser user = VoToModelConverter.convert(loggedInUser);
		log.info("Bid users for user: " + user);
		List<BidUser> bidUsers = getDAO().getBidShortList(listing, user, listProperties);
		for (BidUser bid : bidUsers) {
			log.info("  * " + bid.userA.getId() + " (" + bid.userANickname + ") - "
					+ bid.userB.getId() + " (" + bid.userBNickname + ") - "
					+ bid.direction + " - " + bid.type + " " + bid.amount + " for " + bid.percentage + "%");
		}
		List<BidUserVO> bids = DtoToVoConverter.convertBidUsers(bidUsers);
		result.setInvestors(bids);
		result.setInvestorsProperties(listProperties);
		fetchOrderBook(listing, result);
		return result;
	}

	public BidListVO getBids(UserVO loggedInUser, String listingId, String investorId, ListPropertiesVO listProperties) {
		BidListVO result = new BidListVO();
		if (loggedInUser == null) {
			log.info("User not logged in.");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
			return result;
		}
		Listing listing = getGeneralDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			log.warning("Listing doesn't exist.");
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_listing_not_found"));
			return result;
		}
		result.setListing(DtoToVoConverter.convert(listing));
		SBUser owner = null;
		SBUser investor = null;
		boolean isOwner = true;
		if (StringUtils.isEmpty(investorId)) {
			// call made by investor
			isOwner = false;
			owner = getGeneralDAO().getUser(listing.owner.getString());
			investor = VoToModelConverter.convert(loggedInUser);
		} else {
			if (listing.owner.getId() != loggedInUser.toKeyId()) {
				log.warning("User is not a listing owner.");
				result.setErrorCode(ErrorCodes.NOT_AN_OWNER);
				result.setErrorMessage(Translations.getText("lang_error_user_not_project_owner"));
				return result;
			}
			owner = VoToModelConverter.convert(loggedInUser);
			investor = getGeneralDAO().getUser(investorId);
		}
		log.info("Retrieving bids for listing " + listing.name + " (" + listing.getKey() + ") between '"
				+ investor.nickname + "' (" + investor.getKey() + ") and '" + owner.nickname + "' (" + owner.getKey() + ")");
		List<Bid> bids = getDAO().getBidList(listing, investor, owner, listProperties);
		for (Bid bid : bids) {
			log.info("  * " + bid.userA.getId() + " (" + bid.userANickname + ") - "
					+ bid.userB.getId() + " (" + bid.userBNickname + ") - "
					+ bid.direction + " - " + bid.type + " " + bid.amount + " for " + bid.percentage + "%");
		}
		List<BidVO> msgsVO = DtoToVoConverter.convertBids(bids);
		getDAO().updateReadFlag(listing, owner, investor, bids);
		log.info("Returning " + msgsVO.size() + " messages.");
		result.setBids(msgsVO);
		result.setValidActions(getListOfValidActions(bids != null && bids.size() > 0 ? bids.get(0).type : null, isOwner));
		result.setInvestor(new UserShortVO(DtoToVoConverter.convert(investor)));
		result.setBidsProperties(listProperties);
		fetchOrderBook(listing, result);
		return result;
	}

}
