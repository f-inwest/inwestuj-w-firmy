package eu.finwest.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import eu.finwest.dao.BidObjectifyDatastoreDAO;
import eu.finwest.dao.MessageObjectifyDatastoreDAO;
import eu.finwest.dao.NotificationObjectifyDatastoreDAO;
import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.Bid;
import eu.finwest.datamodel.Comment;
import eu.finwest.datamodel.Listing;
import eu.finwest.datamodel.Listing.State;
import eu.finwest.datamodel.Monitor;
import eu.finwest.datamodel.Notification;
import eu.finwest.datamodel.Notification.Type;
import eu.finwest.datamodel.PrivateMessage;
import eu.finwest.datamodel.QuestionAnswer;
import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.VoToModelConverter;
import eu.finwest.util.Translations;
import eu.finwest.vo.BaseVO;
import eu.finwest.vo.DtoToVoConverter;
import eu.finwest.vo.ErrorCodes;
import eu.finwest.vo.ListPropertiesVO;
import eu.finwest.vo.ListingVO;
import eu.finwest.vo.NotificationAndUserVO;
import eu.finwest.vo.NotificationListVO;
import eu.finwest.vo.NotificationVO;
import eu.finwest.vo.UserVO;

/**
 *
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class NotificationFacade {
	private static final Logger log = Logger.getLogger(NotificationFacade.class.getName());
	private static NotificationFacade instance;

	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");

	public static NotificationFacade instance() {
		if (instance == null) {
			instance = new NotificationFacade();
		}
		return instance;
	}

	private NotificationFacade() {
	}

	public NotificationObjectifyDatastoreDAO getDAO () {
		return NotificationObjectifyDatastoreDAO.getInstance();
	}
	public ObjectifyDatastoreDAO getListingDAO () {
		return ObjectifyDatastoreDAO.getInstance();
	}
	public ObjectifyDatastoreDAO getUserDAO () {
		return ObjectifyDatastoreDAO.getInstance();
	}
	public BidObjectifyDatastoreDAO getBidDAO () {
		return BidObjectifyDatastoreDAO.getInstance();
	}
	public MessageObjectifyDatastoreDAO getMessageDAO () {
		return MessageObjectifyDatastoreDAO.getInstance();
	}

	public NotificationListVO getUnreadNotificationsForUser(UserVO loggedInUser, ListPropertiesVO notifProperties) {
		NotificationListVO list = new NotificationListVO();
		if (loggedInUser == null) {
			list.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			list.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
			log.info("User not logged in!");
			return list;
		}
		List<NotificationVO> notifications = DtoToVoConverter.convertNotifications(
				getDAO().getUnreadUserNotifications(VoToModelConverter.convert(loggedInUser), notifProperties));
		notifProperties.setNumberOfResults(notifications.size());
		list.setNotifications(notifications);
		list.setNotificationsProperties(notifProperties);

		return list;
	}

    public NotificationListVO getNotificationsForUser(UserVO loggedInUser, ListPropertiesVO notifProperties) {
        NotificationListVO list = new NotificationListVO();
        if (loggedInUser == null) {
            list.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
            list.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
            log.info("User not logged in!");
            return list;
        }
        List<NotificationVO> notifications = null;

        notifications = DtoToVoConverter.convertNotifications(
                getDAO().getAllUserNotifications(VoToModelConverter.convert(loggedInUser), notifProperties));
        notifProperties.setTotalResults(notifications.size());
        list.setNotifications(notifications);
        list.setNotificationsProperties(notifProperties);

        return list;
    }

    public NotificationListVO getNotificationsForUserAndMarkRead(UserVO loggedInUser, ListPropertiesVO notifProperties) {
        NotificationListVO list = new NotificationListVO();
        if (loggedInUser == null) {
            list.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
            list.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
            log.info("User not logged in!");
            return list;
        }

        SBUser user = VoToModelConverter.convert(loggedInUser);
        List<Notification> notifications = getDAO().getAllUserNotificationsAndMarkRead(user, notifProperties);
        if (notifications != null && notifications.size() > 0) {
            List<NotificationVO> notificationVOs = DtoToVoConverter.convertNotifications(notifications);
            notifProperties.setTotalResults(notificationVOs.size());
            list.setNotifications(notificationVOs);
        }
        else {
            list.setNotifications(null);
        }
        list.setNotificationsProperties(notifProperties);

        return list;
    }

	public NotificationAndUserVO getNotification(UserVO loggedInUser, String notifId) {
		NotificationAndUserVO result = new NotificationAndUserVO();
		Notification notification = getDAO().getNotification(BaseVO.toKeyId(notifId));
		if (notification == null) {
			log.warning("Notification with id '" + notifId + "' not found!");
			result.setErrorCode(ErrorCodes.APPLICATION_ERROR);
			result.setErrorMessage(Translations.getText("lang_error_notification_not_exists"));
			return result;
		}
		notification.read = true;
		getDAO().storeNotification(notification);
		if (notification.listing != null) {
			ListingVO listing = DtoToVoConverter.convert(getListingDAO().getListing(notification.listing.getId()));
			result.setListing(listing);
		}
		NotificationVO notificationVO = DtoToVoConverter.convert(notification);
		result.setNotification(notificationVO);
		return result;
	}

	public void scheduleCommentNotification(Comment comment) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_comment_notification_" + comment.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-comment-notifications")
				.param("id", "" + comment.getWebKey())
				.taskName(taskName));
	}

	public void scheduleQANotification(QuestionAnswer qa) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_qa_notification_" + qa.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-qa-notifications")
				.param("id", "" + qa.getWebKey())
				.taskName(taskName));
	}

	public void schedulePrivateMessageNotification(PrivateMessage message) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_message_notification_" + message.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-message-notifications")
				.param("id", "" + message.getWebKey())
				.taskName(taskName));
	}

	public void scheduleListingStateNotification(Listing listing) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_listing_notification_" + listing.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-listing-notifications")
				.param("id", "" + listing.getWebKey())
				.taskName(taskName));
	}

	public void scheduleBidNotification(Bid bid) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_bid_notification_" + bid.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/schedule-bid-notifications")
				.param("id", "" + bid.getWebKey())
				.taskName(taskName));
	}

	public void schedulePictureImport(Listing listing, int index) {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_picture_to_fetch_" + listing.getWebKey();
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/fetch-listing-doc")
				.param("id", "" + listing.getWebKey())
				.param("index", "" + index)
				.taskName(taskName));
	}

	public List<NotificationVO> createListingStateNotification(String listingId) {
		Listing listing = getListingDAO().getListing(BaseVO.toKeyId(listingId));
		SBUser listingOwner = getUserDAO().getUser(listing.owner.getString());
		List<Notification> toStore = new ArrayList<Notification>();

		Notification notification = new Notification(listing, listingOwner);
		switch (listing.state) {
		case NEW:
			notification.message = "Your listing is not yet visible for the public. You'll get private message from ADMINISTRATOR soon.";
            notification.type = Notification.Type.LISTING_SENT_BACK;
			break;
		case POSTED:
			notification.message = "";
			notification.type = Notification.Type.NEW_LISTING;
			break;
		case ACTIVE:
			notification.message = "Listing has been activated and is visible for the public on inwestujwfirmy.pl";
			notification.type = Notification.Type.LISTING_ACTIVATED;
			break;
		case FROZEN:
			notification.message = "Listing has been frozen and is no longer visible for the public (except for those having direct link to the listing).";
			notification.type = Notification.Type.LISTING_FROZEN;
			break;
		case WITHDRAWN:
			notification.message = "Listing has been withdrawn and is no longer visible for the public (except for those having direct link to the listing).";
			notification.type = Notification.Type.LISTING_WITHDRAWN;
			break;
		case CLOSED:
			notification.message = "Listing has been closed and is no longer visible for the public (except for those having direct link to the listing).";
			break;
		}

		List<String> skipEmails = new ArrayList<String>();
		if (listing.state == State.NEW || listing.state == State.ACTIVE || listing.state == State.FROZEN) {
			Notification ownerNotif = (Notification)notification.copy();
			ownerNotif.user = listingOwner.getKey();
			ownerNotif.userEmail = listingOwner.email;
			ownerNotif.userNickname = listingOwner.nickname;
			log.info("Creating notification: " + ownerNotif);
			skipEmails.add(ownerNotif.userEmail);
			toStore.add(ownerNotif);
		}
		if (listing.state == State.POSTED) {
			SBUser admin1 = getUserDAO().getUserByEmail("grzegorz.nittner@gmail.com");
			SBUser admin2 = getUserDAO().getUserByEmail("johnarleyburns@gmail.com");

			Notification adminNotif = (Notification)notification.copy();
			adminNotif.user = admin1.getKey();
			adminNotif.userEmail = admin1.email;
			adminNotif.userNickname = admin1.nickname;
			log.info("Creating admin notification: " + adminNotif);
			skipEmails.add(adminNotif.userEmail);
			toStore.add(adminNotif);

			adminNotif = (Notification)notification.copy();
			adminNotif.user = admin2.getKey();
			adminNotif.userEmail = admin2.email;
			adminNotif.userNickname = admin2.nickname;
			log.info("Creating admin notification: " + adminNotif);
			skipEmails.add(adminNotif.userEmail);
			toStore.add(adminNotif);
		}

		if (listing.state == State.FROZEN || listing.state == State.ACTIVE
				|| listing.state == State.WITHDRAWN || listing.state == State.CLOSED) {
			Notification monitoredNotif = null;
			ListPropertiesVO props = new ListPropertiesVO();
			props.setMaxResults(1000);
			for (Monitor monitor : getListingDAO().getMonitorsForListing(listing.id, props)) {
				if (monitor.userEmail != null && !skipEmails.contains(monitor.userEmail)) {
					monitoredNotif = (Notification)notification.copy();
					monitoredNotif.user = monitor.user;
					monitoredNotif.userEmail = monitor.userEmail;
					monitoredNotif.userNickname = monitor.userNickname;
					log.info("Creating notification: " + monitoredNotif);
					toStore.add(monitoredNotif);
				}
			}
		}

		if (!toStore.isEmpty()) {
			for (Notification notif : getDAO().storeNotifications(toStore)) {
				String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notif.type + "_" + notif.user.getId();
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notif.getWebKey())
						.taskName(taskName));
			}
		}
		return DtoToVoConverter.convertNotifications(toStore);
	}

	public List<NotificationVO> createCommentNotification(String commentId) {
		Comment comment = getListingDAO().getComment(BaseVO.toKeyId(commentId));
		Listing listing = getListingDAO().getListing(comment.listing.getId());
		SBUser listingOwner = getUserDAO().getUser(listing.owner.getString());
		SBUser commenter = getUserDAO().getUser(comment.user.getString());
		Notification notification = new Notification(listing, listingOwner);
		List<Notification> toStore = new ArrayList<Notification>();

		List<String> skipEmails = new ArrayList<String>();
		skipEmails.add(commenter.email);
		if (listing.owner.getId() != comment.user.getId()) {
			// comment from user, we need to notify monitoring users and owner
			Notification ownerNotif = (Notification)notification.copy();
			ownerNotif.type = Notification.Type.NEW_COMMENT_FOR_YOUR_LISTING;
			ownerNotif.message = comment.comment;
			ownerNotif.user = listingOwner.getKey();
			ownerNotif.userEmail = listingOwner.email;
			ownerNotif.userNickname = listingOwner.nickname;
			ownerNotif.fromUserNickname = commenter.nickname;
			log.info("Creating notification: " + ownerNotif);
			skipEmails.add(listingOwner.email);
			toStore.add(ownerNotif);
		}

		Notification monitoredNotif = null;
		ListPropertiesVO props = new ListPropertiesVO();
		props.setMaxResults(1000);
		for (Monitor monitor : getListingDAO().getMonitorsForListing(listing.id, props)) {
			if (monitor.userEmail != null && !skipEmails.contains(monitor.userEmail)) {
				monitoredNotif = (Notification)notification.copy();
				monitoredNotif.type = Notification.Type.NEW_COMMENT_FOR_MONITORED_LISTING;
				monitoredNotif.message = comment.comment;
				monitoredNotif.user = monitor.user;
				monitoredNotif.userEmail = monitor.userEmail;
				monitoredNotif.userNickname = monitor.userNickname;
				monitoredNotif.fromUserNickname = commenter.nickname;
				log.info("Creating notification: " + monitoredNotif);
				toStore.add(monitoredNotif);
			}
		}

		for (Notification notif : getDAO().storeNotifications(toStore)) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notif.type + "_" + notif.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notif.getWebKey())
					.taskName(taskName));
		}
		return DtoToVoConverter.convertNotifications(toStore);
	}

	public NotificationVO createBidNotification(String bidId) {
		Bid bid = getBidDAO().getBid(BaseVO.toKeyId(bidId));
		Listing listing = getListingDAO().getListing(bid.listing.getId());
		SBUser listingOwner = getUserDAO().getUser(listing.owner.getString());
		SBUser toUser = getUserDAO().getUser(bid.userB.getString());
		SBUser investor = getUserDAO().getUser(bid.userA.equals(listing.owner) ? bid.userB.getString() : bid.userA.getString());

		Notification notification = new Notification(listing, listingOwner);
		notification.user = toUser.getKey();
		notification.userEmail = toUser.email;
		notification.userNickname = toUser.nickname;
		notification.fromUserNickname = investor.nickname;
		notification.investor = investor.getKey();
		switch (bid.type) {
		case INVESTOR_POST:
			notification.type = Notification.Type.NEW_BID_FOR_YOUR_LISTING;
			break;
		case INVESTOR_COUNTER:
		case OWNER_COUNTER:
			notification.type = Notification.Type.YOUR_BID_WAS_COUNTERED;
			break;
		case OWNER_ACCEPT:
		case INVESTOR_ACCEPT:
			notification.type = Notification.Type.YOUR_BID_WAS_ACCEPTED;
			break;
		case INVESTOR_REJECT:
		case OWNER_REJECT:
			notification.type = Notification.Type.YOUR_BID_WAS_REJECTED;
			break;
		case INVESTOR_WITHDRAW:
		case OWNER_WITHDRAW:
			notification.type = Notification.Type.BID_WAS_WITHDRAWN;
			break;
		}
		log.info("Creating notification: " + notification);
		notification = getDAO().storeNotification(notification)[0];
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
		return DtoToVoConverter.convert(notification);
	}

	public NotificationVO createPrivateMessageNotification(String messageId) {
		PrivateMessage message = getMessageDAO().getMessage(BaseVO.toKeyId(messageId));

		Notification notification = new Notification();
		notification.user = message.userB;
		notification.userEmail = message.userBEmail;
		notification.userNickname = message.userBNickname;
		notification.type = Type.PRIVATE_MESSAGE;
        notification.investor = message.userA;
		notification.fromUserNickname = message.userANickname;
		// we don't have field to store from user key
		notification.listingOwnerUser = message.userA;
		notification.message = message.text;
		notification.read = false;
		log.info("Creating notification: " + notification);
		notification = getDAO().storeNotification(notification)[0];
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
		return DtoToVoConverter.convert(notification);
	}

	public NotificationVO createQANotification(String qaId) {
		QuestionAnswer qa = getListingDAO().getQuestionAnswer(BaseVO.toKeyId(qaId));
		Listing listing = getListingDAO().getListing(qa.listing.getId());
		SBUser listingOwner = getUserDAO().getUser(listing.owner.getString());

		Notification notification = new Notification(listing, listingOwner);
		if (qa.answerDate != null) {
			// this is answer
			SBUser questionAuthor = getUserDAO().getUser(qa.user.getString());
			notification.user = qa.user;
			notification.userEmail = questionAuthor.email;
			notification.userNickname = questionAuthor.nickname;
			notification.message = qa.answer;
		} else {
			// this is question
			notification.user = qa.listingOwner;
			notification.userEmail = listingOwner.email;
			notification.userNickname = listingOwner.nickname;
			notification.message = qa.question;
		}
		notification.type = Type.ASK_LISTING_OWNER;
		notification.read = false;
		log.info("Creating notification: " + notification);
		notification = getDAO().storeNotification(notification)[0];
		if (notification != null) {
			String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notification.type + "_" + notification.user.getId();
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/task/send-notification").param("id", "" + notification.getWebKey())
					.taskName(taskName));
		} else {
			log.warning("Can't schedule notification " + notification);
		}
		return DtoToVoConverter.convert(notification);
	}

	public List<NotificationVO> scheduleUserDragonRequestNotification(SBUser user) {
		List<Notification> toStore = new ArrayList<Notification>();

		Notification notification = new Notification();
		notification.message = "User '" + user.nickname + "' has got investor badge";
		notification.listingOwner = user.nickname;
		notification.listingOwnerUser = user.getKey();
        notification.type = Notification.Type.USER_PROMOTED_TO_INVESTOR;

		SBUser admin1 = getUserDAO().getUserByEmail("grzegorz.nittner@gmail.com");
		SBUser admin2 = getUserDAO().getUserByEmail("johnarleyburns@gmail.com");

		Notification adminNotif = (Notification)notification.copy();
		adminNotif.user = admin1.getKey();
		adminNotif.userEmail = admin1.email;
		adminNotif.userNickname = admin1.nickname;
		log.info("Creating admin notification: " + adminNotif);
		toStore.add(adminNotif);

		adminNotif = (Notification)notification.copy();
		adminNotif.user = admin2.getKey();
		adminNotif.userEmail = admin2.email;
		adminNotif.userNickname = admin2.nickname;
		log.info("Creating admin notification: " + adminNotif);
		toStore.add(adminNotif);

		if (!toStore.isEmpty()) {
			for (Notification notif : getDAO().storeNotifications(toStore)) {
				String taskName = timeStampFormatter.print(new Date().getTime()) + "send_notification_" + notif.type + "_" + notif.user.getId();
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(TaskOptions.Builder.withUrl("/task/send-admin-notification").param("id", "" + notif.getWebKey())
						.taskName(taskName));
			}
		}
		return DtoToVoConverter.convertNotifications(toStore);
	}
	
	public void scheduleLoadListingData() {
		String taskName = timeStampFormatter.print(new Date().getTime()) + "schedule_load_listing_data";
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/load-listing-data")
				.taskName(taskName));
	}

}
