package eu.finwest.web.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import eu.finwest.dao.MockDataBuilder;
import eu.finwest.dao.NotificationObjectifyDatastoreDAO;
import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.Listing;
import eu.finwest.datamodel.Notification;
import eu.finwest.datamodel.SBUser;
import eu.finwest.vo.BaseVO;
import eu.finwest.vo.DtoToVoConverter;
import eu.finwest.vo.ListingVO;
import eu.finwest.vo.NotificationVO;
import eu.finwest.web.DocService;
import eu.finwest.web.EmailService;
import eu.finwest.web.HttpHeaders;
import eu.finwest.web.HttpHeadersImpl;
import eu.finwest.web.ListingFacade;
import eu.finwest.web.ListingImportService;
import eu.finwest.web.ListingSearchService;
import eu.finwest.web.ModelDrivenController;
import eu.finwest.web.NotificationFacade;
import eu.finwest.web.ServiceFacade;
import eu.finwest.web.UserMgmtFacade;
import eu.finwest.web.ListingFacade.UpdateReason;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class TaskController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(TaskController.class.getName());
	private Object model;

	private String queueName;
	private String taskName;
	private int taskRetryCount;
	private String failFast;
	
	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		queueName = request.getHeader("X-AppEngine-QueueName");
		taskName = request.getHeader("X-AppEngine-TaskName");
		taskRetryCount = 0;
		if (StringUtils.isNotEmpty(request.getHeader("X-AppEngine-TaskRetryCount"))) {
			taskRetryCount = NumberUtils.createLong(request.getHeader("X-AppEngine-TaskRetryCount")).intValue();
		}
		failFast = request.getHeader("X-AppEngine-FailFast");
		
		log.log(Level.INFO, "Task '" + taskName + "' called of queue '" + queueName
				+ "', taksRetryCount=" + taskRetryCount + ", failFast=" + failFast);
		
		if("calculate-user-stats".equalsIgnoreCase(getCommand(1))) {
			return calculateUserStats(request);
		} else if("calculate-listing-stats".equalsIgnoreCase(getCommand(1))) {
			return calculateListingStats(request);
		} else if("update-mock-listing-images".equalsIgnoreCase(getCommand(1))) {
			return updateMockListingImages(request);
		} else if("update-mock-listing-pictures".equalsIgnoreCase(getCommand(1))) {
			return updateMockListingPictures(request);
		} else if("fetch-listing-doc".equalsIgnoreCase(getCommand(1))) {
			return fetchListingDoc(request);
		} else if("send-notification".equalsIgnoreCase(getCommand(1))) {
			return sendNotification(request);
		} else if("send-admin-notification".equalsIgnoreCase(getCommand(1))) {
			return sendAdminNotification(request);
		} else if("schedule-comment-notifications".equalsIgnoreCase(getCommand(1))) {
			return scheduleCommentNotifications(request);
		} else if("schedule-qa-notifications".equalsIgnoreCase(getCommand(1))) {
			return scheduleQANotifications(request);
		} else if("schedule-message-notifications".equalsIgnoreCase(getCommand(1))) {
			return scheduleMessageNotifications(request);
		} else if("schedule-listing-notifications".equalsIgnoreCase(getCommand(1))) {
			return scheduleListingNotifications(request);
		} else if("schedule-bid-notifications".equalsIgnoreCase(getCommand(1))) {
			return scheduleBidNotifications(request);
		} else if("update-listing-doc".equalsIgnoreCase(getCommand(1))) {
            return updateListingDoc(request);
        } else if("load-listing-data".equalsIgnoreCase(getCommand(1))) {
            return loadListingData(request);
        }
		return null;
	}

	private HttpHeaders calculateUserStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("calculate-user-stats");
		
		String userId = getCommandOrParameter(request, 2, "id");
		UserMgmtFacade.instance().calculateUserStatistics(userId);
		model = userId;
		
		return headers;
	}

	private HttpHeaders calculateListingStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("calculate-listing-stats");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		String updateType = getCommandOrParameter(request, 3, "update_type");
		UpdateReason reason = UpdateReason.valueOf(updateType);
		
		long id = ListingVO.toKeyId(listingId);
		ListingFacade.instance().calculateListingStatistics(id);
		Listing listing = ObjectifyDatastoreDAO.getInstance().getListing(id);
		ListingSearchService.instance().updateListingData(listing, reason);
		model = DtoToVoConverter.convert(listing);
		
		return headers;
	}

	private HttpHeaders updateMockListingImages(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("update-mock-listing-images");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		model = listingId;
		
		return headers;
	}

	private HttpHeaders updateMockListingPictures(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("update-mock-listing-pictures");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		ListingFacade.instance().updateMockListingImages(ListingVO.toKeyId(listingId));
		
		Listing listing = ObjectifyDatastoreDAO.getInstance().getListing(ListingVO.toKeyId(listingId));
		ListingFacade.instance().updateMockListingPictures(listing, new MockDataBuilder().picUrls.get(listing.name));
		model = listingId;
		
		return headers;
	}

	private HttpHeaders fetchListingDoc(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("fetch-listing-doc");
		
		String listingId = getCommandOrParameter(request, 2, "id");
		String indexStr = getCommandOrParameter(request, 3, "index");
		int index = NumberUtils.toInt(indexStr, 1);
		Listing listing = ListingFacade.instance().importListingPictures(listingId, index);
		model = DtoToVoConverter.convert(listing);
		
		return headers;
	}

	private HttpHeaders sendNotification(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("send-notification");
		
		String notifId = getCommandOrParameter(request, 2, "id");
		
		Notification notification = NotificationObjectifyDatastoreDAO.getInstance().getNotification(BaseVO.toKeyId(notifId));

		if (!notification.read) {
			SBUser receiver = ObjectifyDatastoreDAO.getInstance().getUserByEmail(notification.userEmail);
			if (receiver.notifyEnabled && receiver.email != null) {
				log.info("Sending notification: " + notification);
				NotificationVO notifVO = DtoToVoConverter.convert(notification, receiver.recentLang);
				model = notifVO;
				notifVO.setUserRecentDomain(receiver.recentDomain);
				notifVO.setUserRecentLang(receiver.recentLang);
				if (EmailService.instance().sendNotificationEmail(notifVO)) {
					notification.sentDate = new Date();
					NotificationObjectifyDatastoreDAO.getInstance().storeNotification(notification);
				}
			} else {
				log.info("Notification email not sent. Notify enabled flag: " + receiver.notifyEnabled
						+ ", email:" + receiver.email + " " + notification);
			}
		} else {
			log.info("Notification has been already read. " + notification);
		}
		
		return headers;
	}
	
	private HttpHeaders sendAdminNotification(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("send-admin-notification");
		
		String notifId = getCommandOrParameter(request, 2, "id");
		
		Notification notification = NotificationObjectifyDatastoreDAO.getInstance().getNotification(BaseVO.toKeyId(notifId));
		model = DtoToVoConverter.convert(notification);

		SBUser receiver = ObjectifyDatastoreDAO.getInstance().getUserByEmail(notification.userEmail);
		log.info("Sending admin notification: " + notification + " to admin " + receiver.email);
		NotificationVO notifVO = DtoToVoConverter.convert(notification);
		notifVO.setUserRecentDomain(receiver.recentDomain);
		notifVO.setUserRecentLang(receiver.recentLang);
		if (EmailService.instance().sendNotificationEmail(notifVO)) {
			notification.sentDate = new Date();
			NotificationObjectifyDatastoreDAO.getInstance().storeNotification(notification);
		}
		
		return headers;
	}
	
	private HttpHeaders scheduleCommentNotifications(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("schedule-comment-notifications");		
		String commentId = getCommandOrParameter(request, 2, "id");
		model = NotificationFacade.instance().createCommentNotification(commentId);
		return headers;
	}
	
	private HttpHeaders scheduleQANotifications(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("schedule-qa-notifications");		
		String qaId = getCommandOrParameter(request, 2, "id");		
		model = NotificationFacade.instance().createQANotification(qaId);
		return headers;
	}
	
	private HttpHeaders scheduleMessageNotifications(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("schedule-message-notifications");		
		String messageId = getCommandOrParameter(request, 2, "id");		
		model = NotificationFacade.instance().createPrivateMessageNotification(messageId);
		return headers;
	}
	
	private HttpHeaders scheduleListingNotifications(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("schedule-listing-notifications");		
		String listingId = getCommandOrParameter(request, 2, "id");		
		model = NotificationFacade.instance().createListingStateNotification(listingId);
		return headers;
	}
	
	private HttpHeaders scheduleBidNotifications(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("schedule-bid-notifications");		
		String bidId = getCommandOrParameter(request, 2, "id");		
		model = NotificationFacade.instance().createBidNotification(bidId);
		return headers;
	}
	
    private HttpHeaders updateListingDoc(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeadersImpl("update-listing-doc");
        String listingId = getCommandOrParameter(request, 2, "id");
        if (listingId == null) {
        	log.severe("Listing doc update not scheduled correctly, listing id is null!");
        	return headers;
        }
        Listing listing = ObjectifyDatastoreDAO.getInstance().getListing(ListingVO.toKeyId(listingId));
        ListingSearchService.instance().updateListingData(listing, UpdateReason.NONE);
        model = DtoToVoConverter.convert(listing);
        
        return headers;
    }

	private HttpHeaders loadListingData(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("load-listing-data");		
		model = ListingImportService.importListingData();
		return headers;
	}
	
	@Override
	public Object getModel() {
		return model;
	}

}
