package eu.finwest.web;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import eu.finwest.dao.MessageObjectifyDatastoreDAO;
import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.PrivateMessage;
import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.VoToModelConverter;
import eu.finwest.util.OfficeHelper;
import eu.finwest.vo.DtoToVoConverter;
import eu.finwest.vo.ErrorCodes;
import eu.finwest.vo.ListPropertiesVO;
import eu.finwest.vo.PrivateMessageListVO;
import eu.finwest.vo.PrivateMessageUserListVO;
import eu.finwest.vo.PrivateMessageUserVO;
import eu.finwest.vo.PrivateMessageVO;
import eu.finwest.vo.UserShortVO;
import eu.finwest.vo.UserVO;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class MessageFacade {
	private static final Logger log = Logger.getLogger(MessageFacade.class.getName());
	private static MessageFacade instance;
	
	public static MessageFacade instance() {
		if (instance == null) {
			instance = new MessageFacade();
		}
		return instance;
	}
	
	private MessageFacade() {
	}
	
	public MessageObjectifyDatastoreDAO getDAO () {
		return MessageObjectifyDatastoreDAO.getInstance();
	}
	public ObjectifyDatastoreDAO getUserDAO () {
		return ObjectifyDatastoreDAO.getInstance();
	}

	public PrivateMessageVO sendPrivateMessage(UserVO loggedInUser,	String userId, String text) {
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			return null;
		}
		SBUser toUser = getUserDAO().getUser(userId);
		SBUser fromUser = VoToModelConverter.convert(loggedInUser);
		if (toUser.id == fromUser.id) {
			log.warning("User is trying to send message to himself/herself.");
			return null;
		}
		if (StringUtils.isEmpty(fromUser.nickname)) {
			// in dev environment sometimes nickname is empty
			fromUser = getUserDAO().getUser(fromUser.getWebKey());
		}
		log.info("User '" + fromUser.nickname + "' is sending private message to '" + toUser.nickname + "'");
		PrivateMessage msg = getDAO().createPrivateMessage(toUser, fromUser, text);
		NotificationFacade.instance().schedulePrivateMessageNotification(msg);
		return DtoToVoConverter.convert(msg);
	}

	public PrivateMessageUserListVO getPrivateMessageUsers(UserVO loggedInUser, ListPropertiesVO listProperties) {
		PrivateMessageUserListVO result = new PrivateMessageUserListVO();
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage(OfficeHelper.instance().getTranslation("lang_error_user_not_logged_in"));
			return result;
		}
		SBUser user = VoToModelConverter.convert(loggedInUser);
		List<PrivateMessageUserVO> msgs = DtoToVoConverter.convertPrivateMessageUsers(
				getDAO().getMessageShortList(user, listProperties));
		result.setMessages(msgs);
		result.setMessagesProperties(listProperties);
		return result;
	}

	public PrivateMessageListVO getPrivateMessages(UserVO loggedInUser, String userId, ListPropertiesVO listProperties) {
		PrivateMessageListVO result = new PrivateMessageListVO();
		if (loggedInUser == null) {
			log.warning("User not logged in.");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage(OfficeHelper.instance().getTranslation("lang_error_user_not_logged_in"));
			return result;
		}
		SBUser user = VoToModelConverter.convert(loggedInUser);
		SBUser otherUser = getUserDAO().getUser(userId);
		log.info("Retrieving messages between '" + user.nickname + "' (" + user.id + ") and '" + otherUser.nickname + "' (" + otherUser.id + ")");
		List<PrivateMessage> msgs = getDAO().getMessageList(user, otherUser, listProperties);
		List<PrivateMessageVO> msgsVO = DtoToVoConverter.convertPrivateMessages(msgs);
		getDAO().updateReadFlag(user, otherUser, msgs);
		log.info("Returning " + msgsVO.size() + " messages.");
		result.setMessages(msgsVO);
		result.setOtherUser(new UserShortVO(DtoToVoConverter.convert(otherUser)));
		result.setMessagesProperties(listProperties);
		return result;
	}

}
