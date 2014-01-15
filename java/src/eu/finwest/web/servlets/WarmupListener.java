/**
 * inwestujwfirmy.eu
 * Copyright 2012
 */
package eu.finwest.web.servlets;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;

import eu.finwest.dao.AngelListCache;
import eu.finwest.dao.GeocodeLocation;
import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.dao.StartuplyCache;
import eu.finwest.datamodel.Bid;
import eu.finwest.datamodel.BidUser;
import eu.finwest.datamodel.Campaign;
import eu.finwest.datamodel.Category;
import eu.finwest.datamodel.Comment;
import eu.finwest.datamodel.Listing;
import eu.finwest.datamodel.ListingDoc;
import eu.finwest.datamodel.ListingLocation;
import eu.finwest.datamodel.ListingStats;
import eu.finwest.datamodel.Location;
import eu.finwest.datamodel.Monitor;
import eu.finwest.datamodel.Notification;
import eu.finwest.datamodel.PictureImport;
import eu.finwest.datamodel.PrivateMessage;
import eu.finwest.datamodel.PrivateMessageUser;
import eu.finwest.datamodel.QuestionAnswer;
import eu.finwest.datamodel.Rank;
import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.SystemProperty;
import eu.finwest.datamodel.UserStats;
import eu.finwest.datamodel.Vote;
import eu.finwest.util.TwitterHelper;
import eu.finwest.web.ListingFacade;
import eu.finwest.web.MemCacheFacade;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class WarmupListener implements ServletContextListener {
	private static final Logger log = Logger.getLogger(WarmupListener.class.getName());

	public static String MAIN_CSS_FILE;
	public static String MAIN_JS_FILE;
	public static String JS_FOLDER;

	static {
		ObjectifyService.register(SBUser.class);
		ObjectifyService.register(Listing.class);
		ObjectifyService.register(UserStats.class);
		ObjectifyService.register(Campaign.class);
		ObjectifyService.register(Comment.class);
		ObjectifyService.register(ListingDoc.class);
		ObjectifyService.register(ListingStats.class);
		ObjectifyService.register(Monitor.class);
		ObjectifyService.register(Notification.class);
		ObjectifyService.register(QuestionAnswer.class);
		ObjectifyService.register(PrivateMessage.class);
		ObjectifyService.register(PrivateMessageUser.class);
		ObjectifyService.register(Bid.class);
		ObjectifyService.register(BidUser.class);
		ObjectifyService.register(Rank.class);
		ObjectifyService.register(SystemProperty.class);
		ObjectifyService.register(Vote.class);
		ObjectifyService.register(Category.class);
		ObjectifyService.register(Location.class);
		ObjectifyService.register(ListingLocation.class);
        ObjectifyService.register(AngelListCache.class);
        ObjectifyService.register(GeocodeLocation.class);
        ObjectifyService.register(StartuplyCache.class);
        ObjectifyService.register(PictureImport.class);
	}

	public void contextInitialized(ServletContextEvent event) {
		// This will be invoked as part of a warmup request, or the first user
		// request if no warmup request was invoked.

		File css = new File("./pl/css");
		if (css.exists()) {
			File csses[] = css.listFiles();
			if (csses.length > 0) {
				MAIN_CSS_FILE = "./css/" + csses[0].getName();
			}
		}
		File js = new File("./pl/js");
		if (js.exists()) {
			for(File jsFile : js.listFiles()) {
				if (jsFile.isDirectory()) {
					JS_FOLDER = "./js/" + jsFile.getName();
				}
				if (jsFile.isFile()) {
					MAIN_JS_FILE = "./js/" + jsFile.getName();
				}
			}
		}
		log.info("MAIN_CSS_FILE = " + MAIN_CSS_FILE);
		log.info("MAIN_JS_FILE = " + MAIN_JS_FILE);
		log.info("JS_FOLDER = " + JS_FOLDER);

		MemCacheFacade.instance().updateCategories();
		TwitterHelper.configureTwitterFactory();
		List<SBUser> users = ObjectifyDatastoreDAO.getInstance().getAllUsers();
		log.info("Fetched " + users.size() + " users");
		ListingFacade.instance().getDiscoverListingList(null);
	}

	public void contextDestroyed(ServletContextEvent event) {
		// App Engine does not currently invoke this method.
	}
}
