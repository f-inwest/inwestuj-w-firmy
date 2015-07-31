package eu.finwest.web;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

import eu.finwest.dao.NotificationObjectifyDatastoreDAO;
import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.Comment;
import eu.finwest.datamodel.Listing;
import eu.finwest.datamodel.ListingToImport;
import eu.finwest.datamodel.Monitor;
import eu.finwest.datamodel.QuestionAnswer;
import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.SmsPayment;
import eu.finwest.datamodel.VoToModelConverter;
import eu.finwest.util.Translations;
import eu.finwest.vo.BaseVO;
import eu.finwest.vo.CommentListVO;
import eu.finwest.vo.CommentVO;
import eu.finwest.vo.DtoToVoConverter;
import eu.finwest.vo.ErrorCodes;
import eu.finwest.vo.ListPropertiesVO;
import eu.finwest.vo.ListingDocumentVO;
import eu.finwest.vo.ListingPropertyVO;
import eu.finwest.vo.ListingVO;
import eu.finwest.vo.MonitorListVO;
import eu.finwest.vo.MonitorVO;
import eu.finwest.vo.QuestionAnswerListVO;
import eu.finwest.vo.QuestionAnswerVO;
import eu.finwest.vo.SystemPropertyVO;
import eu.finwest.vo.UserBasicVO;
import eu.finwest.vo.UserVO;
import eu.finwest.web.ListingFacade.UpdateReason;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class ServiceFacade {
	private static final Logger log = Logger.getLogger(ServiceFacade.class.getName());
	private static ServiceFacade instance;
	
	public static ServiceFacade instance() {
		if (instance == null) {
			instance = new ServiceFacade();
		}
		return instance;
	}
	
	private ServiceFacade() {
	}
	
	public ObjectifyDatastoreDAO getDAO () {
		return ObjectifyDatastoreDAO.getInstance();
	}

	private NotificationObjectifyDatastoreDAO getNotificationDAO() {
		return NotificationObjectifyDatastoreDAO.getInstance();
	}

	public CommentListVO getCommentsForListing(UserVO loggedInUser, String listingId, ListPropertiesVO commentProperties) {
		CommentListVO list = new CommentListVO();
		ListingVO listing = DtoToVoConverter.convert(getDAO().getListing(BaseVO.toKeyId(listingId)));
		if (listing == null) {
			log.log(Level.WARNING, "Listing '" + listingId + "' not found");

			commentProperties.setNumberOfResults(0);
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(0);
		} else {
			Monitor monitor = loggedInUser != null ? getDAO().getListingMonitor(loggedInUser.toKeyId(), listing.toKeyId()) : null;
			ListingFacade.instance().applyListingData(loggedInUser, listing, monitor);
			List<CommentVO> comments = DtoToVoConverter.convertComments(
					getDAO().getCommentsForListing(BaseVO.toKeyId(listingId), commentProperties));
			list.setComments(comments);
			list.setListing(listing);
			list.setCommentsProperties(commentProperties);
		}
		list.setCommentsProperties(commentProperties);

		return list;
	}

	public CommentListVO getCommentsForUser(UserVO loggedInUser, String userId, ListPropertiesVO commentProperties) {
		CommentListVO list = new CommentListVO();

		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		if (user == null) {
			log.log(Level.WARNING, "User '" + userId + "' not found");
			commentProperties.setNumberOfResults(0);
			commentProperties.setStartIndex(0);
			commentProperties.setTotalResults(0);
		} else {
			List<CommentVO> comments = DtoToVoConverter.convertComments(
					getDAO().getCommentsForUser(BaseVO.toKeyId(userId), commentProperties));
			list.setComments(comments);
		}
		list.setCommentsProperties(commentProperties);
		list.setUser(new UserBasicVO(user));
		return list;
	}

	/**
	 * Returns listing's rating
	 * @param listingId Listing id
	 * @return Current rating
	 */
	public int getRating(User loggedInUser, String listingId) {
		return getDAO().getNumberOfVotesForListing(BaseVO.toKeyId(listingId));
	}
	
	/**
	 * Returns listings's activity (number of comments)
	 * @param listingId Business plan id
	 * @return Activity
	 */
	public int getActivity(User loggedInUser, String listingId) {
		return getDAO().getActivity(BaseVO.toKeyId(listingId));
	}
 
	public CommentVO getComment(UserVO loggedInUser, String commentId) {
		Comment comment = getDAO().getComment(BaseVO.toKeyId(commentId));
		if (comment == null) {
			log.log(Level.WARNING, "Comment entity '" + commentId + "' not found");
			return null;
		}
		return DtoToVoConverter.convert(comment);
	}

	public CommentListVO deleteComment(UserVO loggedInUser, String commentId) {
		CommentListVO result = new CommentListVO();
		if (loggedInUser == null) {
			log.info("User is not logged in!");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
			return result;
		}
		CommentVO comment = getComment(loggedInUser, commentId);
		if (loggedInUser.isAdmin()) {
			log.info("Admin is going to delete comment: " + comment);
		} else if (StringUtils.equals(comment.getUser(), loggedInUser.getId())) {
			log.info("Comment author is going to delete comment: " + comment);
		} else {
			Listing listing = getDAO().getListing(BaseVO.toKeyId(comment.getListing()));
			if (listing.owner.getId() == loggedInUser.toKeyId()) {
				log.info("Listing owner is going to delete comment: " + comment);
			} else {
				log.warning("User '" + loggedInUser.getNickname() + "' is not allowed to delete comment: " + comment);
				result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
				result.setErrorMessage("User is not allowed to delete this comment.");
				return result;
			}
		}
		ListingFacade.instance().scheduleUpdateOfListingStatistics(comment.getListing(), UpdateReason.DELETE_COMMENT);
		getDAO().deleteComment(BaseVO.toKeyId(commentId));
		return getCommentsForListing(loggedInUser, comment.getListing(), new ListPropertiesVO());
	}

	public CommentVO createComment(UserVO loggedInUser, String listingId, String commentText) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			log.warning("Listing with id '" + listingId + "' doesn't exist.");
			return null;
		}
		SBUser user = null;
		if (loggedInUser.getNickname() == null) {
			// sorting out dev env issue where google user doesn't have nickname
			user = getDAO().getUser(loggedInUser.getId());
		} else {
			user = VoToModelConverter.convert(loggedInUser);
		}
		
		Comment comment = new Comment();
		comment.listing = listing.getKey();
		comment.listingName = listing.name;
		comment.user = user.getKey();
		comment.userNickName = user.nickname;
		comment.comment = commentText;
		CommentVO commentVO = DtoToVoConverter.convert(getDAO().createComment(comment));
		commentVO.setAvatar(loggedInUser.getAvatar());

		setListingMonitor(loggedInUser, listing.getWebKey());
		
		UserMgmtFacade.instance().scheduleUpdateOfUserStatistics(loggedInUser.getId(), UserMgmtFacade.UpdateReason.NEW_COMMENT);
		ListingFacade.instance().scheduleUpdateOfListingStatistics(listingId, UpdateReason.NEW_COMMENT);
		
		NotificationFacade.instance().scheduleCommentNotification(comment);
		return commentVO;
	}

	public CommentVO updateComment(UserVO loggedInUser, CommentVO comment) {
		if (StringUtils.isEmpty(comment.getId())) {
			log.warning("Comment id was not provided!");
			return null;
		}
		if (StringUtils.isEmpty(comment.getComment())) {
			log.warning("Comment '" + comment.getId() + "' cannot be updated with empty text");
			return null;
		}
		comment = DtoToVoConverter.convert(getDAO().updateComment(VoToModelConverter.convert(comment)));
		comment.setAvatar(loggedInUser.getAvatar());
		return comment;
	}

	public SystemPropertyVO getSystemProperty(UserVO loggedInUser, String name) {
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			return null;
		}
		SystemPropertyVO prop = DtoToVoConverter.convert(getDAO().getSystemProperty(name));
		if (prop != null && (StringUtils.contains(prop.getName(), "password")
				|| StringUtils.contains(prop.getName(), "secret"))) {
			String value = prop.getValue();
			if (value.length() < 5) {
				prop.setValue("*****");
			} else {
				prop.setValue(value.substring(0, 4) + "*****");
			}
		}
		return prop;
	}

	public List<SystemPropertyVO> getSystemProperties(UserVO loggedInUser) {
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			return new ArrayList<SystemPropertyVO>();
		}
		List<SystemPropertyVO> result = DtoToVoConverter.convertSystemProperties(getDAO().getSystemProperties());
		for (SystemPropertyVO prop : result) {
			if (StringUtils.contains(prop.getName(), "password") || StringUtils.contains(prop.getName(), "secret")) {
				String value = prop.getValue();
				if (value.length() < 5) {
					prop.setValue("*****");
				} else {
					prop.setValue(value.substring(0, 4) + "*****");
				}
			}
		}
		return result;
	}

	public SystemPropertyVO setSystemProperty(UserVO loggedInUser, SystemPropertyVO property) {
		if (loggedInUser == null) {
			return null;
		}
		property.setAuthor(loggedInUser.getEmail());
		property = DtoToVoConverter.convert(getDAO().setSystemProperty(VoToModelConverter.convert(property)));
		MemCacheFacade.instance().clearSystemPropertiesCache();
		return property;
	}

	public ListingDocumentVO deleteDocument(UserVO loggedInUser, String docId) {
		if (loggedInUser == null) {
			return null;
		}
		getDAO().deleteDocument(BaseVO.toKeyId(docId));
		return null;
	}
	
	public String[] createUploadUrls(UserVO loggedInUser, String uploadUrl, int numberOfUrls) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String[] urls = new String[numberOfUrls];
		while (numberOfUrls > 0) {
			String discreteUploadUrl = uploadUrl + (uploadUrl.endsWith("/") ? "" : "/") ;
			discreteUploadUrl += "" + new Date().getTime() + numberOfUrls + loggedInUser.hashCode();
            urls[--numberOfUrls] = blobstoreService.createUploadUrl(discreteUploadUrl);
		}
		return urls;
	}

	public String[] createUploadUrls(UserVO loggedInUser, String uploadUrl, String campaignId, int numberOfUrls) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String[] urls = new String[numberOfUrls];
		while (numberOfUrls > 0) {
			String discreteUploadUrl = uploadUrl + (uploadUrl.endsWith("/") ? "" : "/") ;
			discreteUploadUrl += "" + new Date().getTime() + numberOfUrls + (loggedInUser != null ? loggedInUser.hashCode() : RandomUtils.nextInt());
			String nonCampaignUploadUrl = blobstoreService.createUploadUrl(discreteUploadUrl);
            //String campaignSensitiveUploadUrl = nonCampaignUploadUrl.replaceFirst("(https?://)", "$1" + campaignId + ".");
            //urls[--numberOfUrls] = campaignSensitiveUploadUrl;
			urls[--numberOfUrls] = nonCampaignUploadUrl;
		}
		return urls;
	}

	public QuestionAnswerVO askOwner(UserVO loggedInUser, String text, String listingId) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (listing == null) {
			log.warning("Listing '" + listingId + "' doesn't exist.");
			return null;
		}
		if (listing.state != Listing.State.ACTIVE) {
			log.warning("Listing '" + listingId + "' is not active.");
			return null;
		}
		SBUser listingOwner = getDAO().getUser(listing.owner.getString());
		SBUser fromUser = VoToModelConverter.convert(loggedInUser);
		if (StringUtils.isEmpty(fromUser.nickname)) {
			// in dev environment sometimes nickname is empty
			fromUser = getDAO().getUser(fromUser.getWebKey());
		}
		log.info("User " + loggedInUser.getNickname() + " is asking question about listing '" + listing.name + "' onwed by user " + listingOwner.nickname);
		QuestionAnswer qa = getDAO().askListingOwner(fromUser, listing, text);
		if (qa != null) {
			setListingMonitor(loggedInUser, listingId);
		}
		NotificationFacade.instance().scheduleQANotification(qa);
		QuestionAnswerVO result = DtoToVoConverter.convert(qa);
		UserMgmtFacade.instance().updateUserData(result);
		return result;
	}

	public QuestionAnswerVO answerQuestion(UserVO loggedInUser, String messageId, String text) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		
		QuestionAnswer qa = getDAO().getQuestionAnswer(BaseVO.toKeyId(messageId));
		if (qa == null) {
			log.warning("QuestionAnswer '" + messageId + "' doesn't exist.");
			return null;
		}
		if (loggedInUser.toKeyId() != qa.listingOwner.getId()) {
			log.warning("User '" + loggedInUser.getNickname() + "' is replying to question not addressed to him. Original question: " + qa);
			return null;
		}
		if (qa.answerDate != null) {
			log.warning("Question is already answered.");
			return null;
		}
		Listing listing = getDAO().getListing(qa.listing.getId());
		if (listing == null) {
			log.warning("Listing '" + qa.listing.getString() + "' doesn't exist.");
			return null;
		}
		if (listing.state != Listing.State.ACTIVE) {
			log.warning("Listing '" + qa.listing.getString() + "' is not active.");
			return null;
		}
		
		log.info("User '" + loggedInUser.getNickname() + "' (" + loggedInUser.getId() + ") is replying to question: " + qa);
		qa = getDAO().answerQuestion(listing, qa, text, true);

        ListingFacade.instance().scheduleUpdateOfListingStatistics(listing.getWebKey(), UpdateReason.QUESTION_ANSWERED);
        NotificationFacade.instance().scheduleQANotification(qa);
        
		log.info("Answered q&a: " + qa);
		QuestionAnswerVO result = DtoToVoConverter.convert(qa);
		UserMgmtFacade.instance().updateUserData(result);
		return result;
	}
	
	public QuestionAnswerListVO getQuestionsAndAnswers(UserVO loggedInUser, String listingId, ListPropertiesVO listProperties) {
		QuestionAnswerListVO result = new QuestionAnswerListVO();
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		if (loggedInUser == null) {
			result.setQuestionAnswers(DtoToVoConverter.convertQuestionAnswers(getDAO().getQuestionAnswers(listing, listProperties)));
		} else if (loggedInUser.toKeyId() == listing.owner.getId()) {
			result.setQuestionAnswers(DtoToVoConverter.convertQuestionAnswers(getDAO().getQuestionAnswersForListingOwner(listing, listProperties)));
		} else {
			result.setQuestionAnswers(DtoToVoConverter.convertQuestionAnswers(
					getDAO().getQuestionAnswersForUser(VoToModelConverter.convert(loggedInUser), listing, listProperties)));
		}
		result.setQuestionAnswersProperties(listProperties);
		result.setListing(DtoToVoConverter.convert(listing));
		result.setUser(loggedInUser != null ? new UserBasicVO(loggedInUser) : null);
		return result;
	}
	
	public MonitorVO setListingMonitor(UserVO loggedInUser, String listingId) {
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User not logged in!");
			return null;
		}
		if (StringUtils.isEmpty(listingId)) {
			log.warning("Listing id is empty!");
			return null;
		}
		Listing listing = getDAO().getListing(BaseVO.toKeyId(listingId));
		log.info("User " + loggedInUser.getEmail() + " ("+ loggedInUser.toKeyId() + ") setting monitor on listing: " + listing);
		if (listing == null || listing.owner.getId() == loggedInUser.toKeyId()) {
			log.warning("Listing doesn't exist or user is an owner of this listing. Listing: " + listing);
			return null;
		}
		Monitor monitor = new Monitor();
		monitor.user = new Key<SBUser>(SBUser.class, loggedInUser.toKeyId());
		monitor.userNickname = loggedInUser.getNickname();
		monitor.userEmail = loggedInUser.getEmail();
		monitor.monitoredListing = new Key<Listing>(Listing.class, ListingVO.toKeyId(listingId));
		
		MonitorVO monitorVO = DtoToVoConverter.convert(getDAO().setMonitor(monitor));
		ListingFacade.instance().scheduleUpdateOfListingStatistics(listingId, UpdateReason.NEW_MONITOR);
		return monitorVO;
	}

	public MonitorVO deactivateListingMonitor(UserVO loggedInUser, String listingId) {
		MonitorVO monitor = DtoToVoConverter.convert(
				getDAO().deactivateListingMonitor(loggedInUser.toKeyId(), BaseVO.toKeyId(listingId)));
		ListingFacade.instance().scheduleUpdateOfListingStatistics(listingId, UpdateReason.DELETE_MONITOR);
		if (monitor == null) {
			log.warning("Monitor for listing id '" + listingId + "' not found!");
		} else {
			log.info("Monitor for listing id '" + listingId + "' was deactivated.");
		}
		return monitor;
	}

	public MonitorListVO getMonitorsForObject(UserVO loggedInUser, String listingId, ListPropertiesVO listProperties) {
		MonitorListVO list = new MonitorListVO();
		List<MonitorVO> monitors = null;

		if (StringUtils.isEmpty(listingId)) {
			log.warning("Parameter listingId not provided!");
			return null;
		}
		monitors = DtoToVoConverter.convertMonitors(
				getDAO().getMonitorsForListing(BaseVO.toKeyId(listingId), listProperties));
		int index = listProperties.getStartIndex() > 0 ? listProperties.getStartIndex() : 1;
		for (MonitorVO monitor : monitors) {
			monitor.setOrderNumber(index++);
		}
		list.setMonitorsProperties(listProperties);
		list.setMonitors(monitors);
		
		return list;
	}

	public MonitorListVO getMonitorsForUser(UserVO loggedInUser, ListPropertiesVO listProperties) {
		MonitorListVO list = new MonitorListVO();
		List<MonitorVO> monitors = null;
		if (loggedInUser == null) {
			log.log(Level.WARNING, "User not logged in!");
			return null;
		}

		monitors = DtoToVoConverter.convertMonitors(getDAO().getMonitorsForUser(loggedInUser.toKeyId(), listProperties));
		int index = listProperties.getStartIndex() > 0 ? listProperties.getStartIndex() : 1;
		for (MonitorVO monitor : monitors) {
			monitor.setOrderNumber(index++);
		}
		list.setMonitorsProperties(listProperties);
		list.setMonitors(monitors);
		list.setUser(loggedInUser);
		
		return list;
	}

	public int validateSmsCode(UserVO loggedInUser, String sellerId, String value, String code) {
		String returnValue = fetchVerificationData(sellerId, value, code);
		int error = 0;
		String message = "";
		if (StringUtils.equals(returnValue, "OK")) {
			log.info("SMS validation successful");
			error = 0;
			message = "OK";
		} else if (StringUtils.startsWith(returnValue, "ERROR")) {
			try {
				String errorCode = StringUtils.substring(returnValue, 5).trim();
				error = NumberUtils.toInt(errorCode);
				switch(error) {
					case 4:
						message = "invalid sms code or code has been already used";
					break;
					case 101:
						message = "invalid or incomplete verification request";
					break;
					case 102:
						message = "sms code expired";
					break;
					case 103:
						message = "invalid value for code";
					break;
				}
				log.info("SMS validation error: " + error + " " + message);
			} catch (Exception e) {
				log.log(Level.INFO, "Error while parsing przelewy24.pl response", e);
			}
		} else {
			log.info("SMS validation error: " + returnValue);
			error = 1;
			message = "Unknown return value: " + returnValue;
		}
		SmsPayment payment = new SmsPayment();
		payment.sellerId = sellerId;
		payment.amount = value;
		payment.code = code;
		payment.date = new Date();
		payment.status = returnValue;
		payment.description = message;
		if (loggedInUser != null) {
			payment.customer = new Key<SBUser>(loggedInUser.getId());
			payment.customerName = loggedInUser.getNickname();
			payment.email = loggedInUser.getEmail();
		}
		
		getDAO().storeSmsPayment(payment);
		return error;
	}
	
	public String getSmsPayments(UserVO loggedInUser) {
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.info("User not logged in or is not an admin");
			return Translations.getText("lang_error_user_not_admin");
		}
		
		ListPropertiesVO listProperties = new ListPropertiesVO();
		listProperties.setMaxResults(2000);
		List<SmsPayment> contribs = getDAO().getSmsPayments(listProperties);
		
		StringBuffer buf = new StringBuffer();
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		Map<Long, MutablePair<String, Integer>> map = new HashMap<Long, MutablePair<String, Integer>>();
		
		buf.append("<html><header><title>SMS Payments " + dateFormatter.print(new Date().getTime()) + "</title>\n");
		buf.append("<meta charset=\"utf-8\"><meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-type\"></header>\n");
		buf.append("<body><table><th>");
		buf.append("<td>data (GMT)</td><td>użytkownik</td><td>nick</td><td>kod</td><td>kwota</td><td>status</td><td>opis</td></th>\n");
		for (SmsPayment payment : contribs) {
			buf.append("<tr>");
			long smsPayer = payment.customer != null ? payment.customer.getId() : 0;
			buf.append(dateFormatter.print(payment.date.getTime())).append(";");
			buf.append(smsPayer).append("</td>");
			buf.append("<td>").append(payment.customerName).append("</td>");
			buf.append("<td>").append(payment.code).append("</td>");
			buf.append("<td>").append(payment.amount).append("</td>");
			buf.append("<td>").append(payment.status).append("</td>");
			buf.append("<td>").append(payment.description).append("</td>");
			buf.append("</tr>\n");
			
			if (StringUtils.equalsIgnoreCase(payment.status, "ok")) {
				if (!map.containsKey(smsPayer)) {
					map.put(smsPayer, new MutablePair<String, Integer>(payment.customerName, 0));
				}
				map.get(smsPayer).right += 1;
			}
		}
		
		buf.append("</table><br/><br/><div>Dane zbiorcze</div><table><th>");
		buf.append("<td>użytkownik</td><td>ilosc sms</td></th>\n");
		for (MutablePair<String, Integer> pair : map.values()) {
			buf.append("<tr><td>").append(pair.left).append("</td>").append("<td>").append(pair.right).append("</td></tr>");
		}
		buf.append("</table></body>");

		return buf.toString();
	}

	public String downloadSmsPayments(UserVO loggedInUser) {
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.info("User not logged in or is not an admin");
			return Translations.getText("lang_error_user_not_admin");
		}
		
		ListPropertiesVO listProperties = new ListPropertiesVO();
		listProperties.setMaxResults(2000);
		List<SmsPayment> contribs = getDAO().getSmsPayments(listProperties);
		
		StringBuffer buf = new StringBuffer();
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		Map<Long, MutablePair<String, Integer>> map = new HashMap<Long, MutablePair<String, Integer>>();
		
		buf.append("data (GMT); użytkownik; nick; kod; kwota; status; opis; \n");
		for (SmsPayment payment : contribs) {
			long smsPayer = payment.customer != null ? payment.customer.getId() : 0;
			buf.append(dateFormatter.print(payment.date.getTime())).append(";");
			buf.append(smsPayer).append(";");
			buf.append(payment.customerName).append(";");
			buf.append(payment.code).append(";");
			buf.append(payment.amount).append(";");
			buf.append(payment.status).append(";");
			buf.append(StringUtils.replace(payment.description, ";", " ")).append(";");
			buf.append(";\n");
			
			if (StringUtils.equalsIgnoreCase(payment.status, "ok")) {
				if (!map.containsKey(smsPayer)) {
					map.put(smsPayer, new MutablePair<String, Integer>(payment.customerName, 0));
				}
				map.get(smsPayer).right += 1;
			}
		}
		
		buf.append("dane zbiorcze; \n");
		buf.append("użytkownik; ilosc sms; \n");
		for (MutablePair<String, Integer> pair : map.values()) {
			buf.append(pair.left).append(";").append(pair.right).append(";");
		}

		return buf.toString();
	}

	public String downloadSmsPaymentsReport(UserVO loggedInUser) {
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.info("User not logged in or is not an admin");
			return Translations.getText("lang_error_user_not_admin");
		}
		
		ListPropertiesVO listProperties = new ListPropertiesVO();
		listProperties.setMaxResults(2000);
		List<SmsPayment> contribs = getDAO().getSmsPayments(listProperties);
		
		StringBuffer buf = new StringBuffer();
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		
		buf.append("data platnosci (GMT); nick; projekt; kod; kwota; \n");
		for (SmsPayment payment : contribs) {
			buf.append(dateFormatter.print(payment.date.getTime())).append(";");
			buf.append(payment.ownerNick).append(";");
			buf.append(payment.listingName).append(";");
			buf.append(payment.code).append(";");
			int amount = NumberUtils.createInteger(payment.amount);
			buf.append((amount / 100) + "," + (amount % 100)).append(";");
			buf.append(";\n");
		}
		
		return buf.toString();
	}
	
	private static String fetchVerificationData(String sellerId, String value, String code) {
		String url = "https://secure.przelewy24.pl/smsver.php";
		try {
			String params = "p24_id_sprzedawcy=" + sellerId + "&p24_kwota=" + value + "&p24_sms=" +code;

			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(5000);
			connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");			
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod("POST"); 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
			connection.setUseCaches (false);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.writeBytes(params);
			wr.flush();
			wr.close();
			
			byte[] docBytes = IOUtils.toByteArray(connection.getInputStream());
			log.info("Fetched " + docBytes.length + " bytes from " + url);
			return new String(docBytes, "UTF-8");
		} catch (Exception e) {
			log.log(Level.WARNING, "Error fetching import source from " + url, e);
			return null;
		}
	}

	public Object sendInvestorReport(UserVO loggedInUser, String title, String message, String user_id, List<String> ids) {
		if (!loggedInUser.isAdmin()) {
			return "Only admins can send reports";
		}
		SBUser user = getDAO().getUser(user_id);
		List<Listing> listings = new ArrayList<Listing>();
		for (String id : ids) {
			Listing listing = getDAO().getListing(id);
			listings.add(listing);
		}
		
		boolean returnCode = EmailService.instance().sendInvestorReport(user, title, message, listings);
		return (returnCode ? "Investor report email has been send to user: " : "Error sending investor report email to: ") + user.email;
	}

	public Object loadImportData(UserVO loggedInUser, String url) {
		if (!loggedInUser.isAdmin()) {
			return "Only admins can send reports";
		}
		
		try {
			String urlData = new String(ListingImportService.fetchBytes(url), "UTF-8");
			StringBuffer output = new StringBuffer();
			
			ObjectMapper mapper = new ObjectMapper();
			int index = 1;
			JsonNode rootNode = mapper.readValue(urlData, JsonNode.class);
			if (rootNode.get("results") != null) {
				if (StringUtils.contains(url, "ios/apps")) {
					parseAppleStore(output, index, rootNode);
				} else {
					parseAndroidStore(output, index, rootNode);
				}
			}
			return output.toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while loading import data", e);
			return "Error loading import data: " + e.getMessage();
		}
	}

	private void parseAppleStore(StringBuffer output, int index, JsonNode rootNode) {
		Iterator<JsonNode> fields = rootNode.get("results").getElements();
		for (JsonNode node = null; fields.hasNext();) {
			node = fields.next();
			output.append("<p> " + index++ + " iOS - ");
			String identity = node.get("trackId").getValueAsText();
			if (!getDAO().importDataExists(identity)) {
				ListingToImport listing = new ListingToImport();
				listing.type = ListingToImport.Type.IOS;
				listing.identity = identity;
				listing.creator = node.get("sellerName").getValueAsText();
				if (StringUtils.isBlank(listing.creator)) {
					listing.creator = node.get("artistName").getValueAsText();
				}
				listing.name = node.get("trackCensoredName").getValueAsText();
				listing.description = node.get("description").getValueAsText();
				listing.logoUrl = node.get("artworkUrl100").getValueAsText();
				if (StringUtils.isBlank(listing.logoUrl)) {
					listing.logoUrl = node.get("artworkUrl60").getValueAsText();
				}
				//listing.videoUrl = node.get("promo_video").getValueAsText();
				
				Iterator<JsonNode> screenshots = null;
				if (node.get("ipadScreenshotUrls") != null) {
					screenshots = node.get("ipadScreenshotUrls").getElements();
				}
				if (node.get("screenshotUrls") != null && (screenshots == null || !screenshots.hasNext())) {
					screenshots = node.get("screenshotUrls").getElements();
				}
				
				if (screenshots != null) {
					int i = 1;
					for (JsonNode scrNode = null; screenshots.hasNext();) {
						scrNode = screenshots.next();
						switch (i) {
						case 1:
							listing.screenshotUrl1 = scrNode.getValueAsText();
							break;
						case 2:
							listing.screenshotUrl2 = scrNode.getValueAsText();
							break;
						case 3:
							listing.screenshotUrl3 = scrNode.getValueAsText();
							break;
						case 4:
							listing.screenshotUrl4 = scrNode.getValueAsText();
							break;
						case 5:
							listing.screenshotUrl5 = scrNode.getValueAsText();
							break;
						}
						i++;
						if (i > 5) {
							break;
						}
					}
				}
				log.info(listing.toString());
				output.append(listing.toString());
				getDAO().storeListingToImport(listing);
			} else {
				output.append(" already imported " + identity);
			}
			output.append("</p>");
		}
	}

	private void parseAndroidStore(StringBuffer output, int index, JsonNode rootNode) {
		Iterator<JsonNode> fields = rootNode.get("results").getElements();
		for (JsonNode node = null; fields.hasNext();) {
			node = fields.next();
			output.append("<p>" + index++ + " Android - ");
			String identity = node.get("package_name").getValueAsText();
			if (!getDAO().importDataExists(identity)) {
				ListingToImport listing = new ListingToImport();
				listing.type = ListingToImport.Type.ANDROID;
				listing.identity = identity;
				listing.creator = node.get("developer").getValueAsText();
				listing.name = node.get("title").getValueAsText();
				listing.description = node.get("description").getValueAsText();
				listing.logoUrl = node.get("icon").getValueAsText();
				listing.videoUrl = node.get("promo_video").getValueAsText();
				
				if (node.get("screenshots") != null) {
					Iterator<JsonNode> screenshots = node.get("screenshots").getElements();
					int i = 1;
					for (JsonNode scrNode = null; screenshots.hasNext();) {
						scrNode = screenshots.next();
						switch (i) {
						case 1:
							listing.screenshotUrl1 = scrNode.getValueAsText();
							break;
						case 2:
							listing.screenshotUrl2 = scrNode.getValueAsText();
							break;
						case 3:
							listing.screenshotUrl3 = scrNode.getValueAsText();
							break;
						case 4:
							listing.screenshotUrl4 = scrNode.getValueAsText();
							break;
						case 5:
							listing.screenshotUrl5 = scrNode.getValueAsText();
							break;
						}
						i++;
						if (i > 5) {
							break;
						}
					}
				}
				log.info(listing.toString());
				output.append(listing.toString());
				getDAO().storeListingToImport(listing);
			} else {
				output.append(" already imported " + identity);
			}
			output.append("</p>");
		}
	}

	public Object startImportData(UserVO loggedInUser, int numToImport) {
		if (!loggedInUser.isAdmin()) {
			return "Only admins can start import";
		}
		MemCacheFacade.instance().setListingsToImport(Math.min(numToImport - 1, 200));
		NotificationFacade.instance().scheduleLoadListingData();
		return "Import scheduled";
	}
}
