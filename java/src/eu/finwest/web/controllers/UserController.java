package eu.finwest.web.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.SBUser.Status;
import eu.finwest.util.EmailAuthHelper;
import eu.finwest.util.Translations;
import eu.finwest.util.TwitterHelper;
import eu.finwest.vo.CampaignVO;
import eu.finwest.vo.ListPropertiesVO;
import eu.finwest.vo.PrivateMessageListVO;
import eu.finwest.vo.PrivateMessageUserListVO;
import eu.finwest.vo.PrivateMessageVO;
import eu.finwest.vo.UserAndUserVO;
import eu.finwest.vo.UserVO;
import eu.finwest.web.HttpHeaders;
import eu.finwest.web.HttpHeadersImpl;
import eu.finwest.web.MessageFacade;
import eu.finwest.web.ModelDrivenController;
import eu.finwest.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class UserController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(UserController.class.getName());
	
	public static final String EMAIL_AUTHENTICATION_COOKIE = "AUTHENTICATION_COOKIE";

	private Object model = null;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// GET method handler
			
			if("all".equalsIgnoreCase(getCommand(1))) {
				return all(request);
			} else if("get".equalsIgnoreCase(getCommand(1))) {
				return get(request);
			} else if("dragons".equalsIgnoreCase(getCommand(1))) {
				return dragons(request);
			} else if("listers".equalsIgnoreCase(getCommand(1))) {
				return listers(request);
			} else if("message_users".equalsIgnoreCase(getCommand(1))) {
				return messageUsers(request);
			} else if("messages".equalsIgnoreCase(getCommand(1))) {
				return messages(request);
			} else if("loggedin".equalsIgnoreCase(getCommand(1))) {
				return loggedin(request);
			} else if("check_user_name".equalsIgnoreCase(getCommand(1))) {
				return checkUserName(request);
			} else if("find".equalsIgnoreCase(getCommand(1))) {
				return find(request);
			} else if("confirm_update_email".equalsIgnoreCase(getCommand(1))) {
				return confirmEmailUpdate(request);
			} else if("request_email_access".equalsIgnoreCase(getCommand(1))) {
				return requestEmailAccess(request);
			} else if("activate".equalsIgnoreCase(getCommand(1))) {
				return activate(request);
			} else if("logout".equalsIgnoreCase(getCommand(1))) {
				return logout(request);
			} else {
				return index(request);
			}
		} else if ("POST".equalsIgnoreCase(request.getMethod())) {
			if ("update".equalsIgnoreCase(getCommand(1))) {
				return update(request);
			} else if("autosave".equalsIgnoreCase(getCommand(1))) {
				return autoSave(request);
			} else if("deactivate".equalsIgnoreCase(getCommand(1))) {
				return deactivate(request);
			} else if ("create".equalsIgnoreCase(getCommand(1))) {
				return create(request);
			} else if ("delete".equalsIgnoreCase(getCommand(1))) {
				return delete(request);
			} else if("send_message".equalsIgnoreCase(getCommand(1))) {
				return sendMessage(request);
			} else if("request_update_email".equalsIgnoreCase(getCommand(1))) {
				return requestEmailUpdate(request);
			} else if("promote_to_dragon".equalsIgnoreCase(getCommand(1))) {
				return promoteToDragon(request);
			} else if("request_dragon".equalsIgnoreCase(getCommand(1))) {
				return requestDragon(request);
			} else if("store_campaign".equalsIgnoreCase(getCommand(1))) {
				return storeCampaign(request);
			} else if("register".equalsIgnoreCase(getCommand(1))) {
				return register(request);
			} else if("authenticate".equalsIgnoreCase(getCommand(1))) {
				return authenticateByEmail(request);
			} else if("request_reset_password".equalsIgnoreCase(getCommand(1))) {
				return requestResetPassword(request);
			} else if("reset_password".equalsIgnoreCase(getCommand(1))) {
				return resetPassword(request);
			} else if("not_valid_request_reset_password".equalsIgnoreCase(getCommand(1))) {
				return notValidRequestResetPassword(request);
			}
		}
		return null;
	}
	
	private HttpHeaders register(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("register");
		
		UserAndUserVO result = new UserAndUserVO();
		
		ObjectMapper mapper = new ObjectMapper();
		String email = getJsonString(mapper, request, "email");
		String password = getJsonString(mapper, request, "password");
		String name = getJsonString(mapper, request, "name");
		String location = getJsonString(mapper, request, "location");
		if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password)) {
			result = UserMgmtFacade.instance().createUser(email, password, name, location, false);
		} else {
			log.log(Level.WARNING, "Parameters 'email' and 'password' are mandatory!");
			result.setErrorCode(500);
			result.setErrorMessage(Translations.getText("lang_email_password_mandatory"));
		}
		model = result;

		return headers;
	}

	private HttpHeaders activate(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("activate");
    	String activationCode = getCommandOrParameter(request, 2, "code");
		if (!StringUtils.isEmpty(activationCode)) {
			UserVO user = UserMgmtFacade.instance().activateUser(activationCode);
			if (user != null && StringUtils.equals(SBUser.Status.ACTIVE.name(), user.getStatus())) {
				log.info("User " + user.getEmail() + " has been activated");
				headers.setRedirectUrl(request.getContextPath() + "/");
			} else {
				log.warning("Activation code is not valid");
				headers.setRedirectUrl(request.getContextPath() + "/error_page.html");
			}
		} else {
			log.log(Level.WARNING, "Parameter 'code' is mandatory!");
			headers.setRedirectUrl(request.getContextPath() + "/error_page.html");
		}

		return headers;
	}

	private HttpHeaders deactivate(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	if (userId == null) {
    		userId = getLoggedInUser().getId();
    	}
    	model = UserMgmtFacade.instance().deactivateUser(getLoggedInUser(), userId);

		HttpHeaders headers = new HttpHeadersImpl("deactivate");
		return headers;
	}


	private HttpHeaders authenticateByEmail(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("authenticate");
		UserAndUserVO result = new UserAndUserVO();
		model = result;
		if (getLoggedInUser() != null) {
			log.warning("User already logged in, cannot login again using email: " + getCommandOrParameter(request, 2, "email"));
			result.setErrorCode(500);
			result.setErrorMessage(Translations.getText("lang_error_user_already_logged_in"));
			return headers;
		}
		
		ObjectMapper mapper = new ObjectMapper();
    	String email = getJsonString(mapper, request, "email");
    	String password = getJsonString(mapper, request, "password");
    	
    	if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(password)) {
    		SBUser user = UserMgmtFacade.instance().authenticateUser(email, password);
    		if (user == null) {
				result.setErrorCode(500);
				result.setErrorMessage(Translations.getText("lang_error_user_invalid_email_or_password"));
    		} else  if (user.status != Status.ACTIVE) {
				result.setErrorCode(500);
				result.setErrorMessage(Translations.getText("lang_error_user_not_activated"));
    		} else {
	    		Cookie authCookie = EmailAuthHelper.authorizeUser(request, user);
				headers.addCookie(authCookie);
    		}
		} else {
			log.log(Level.WARNING, "Parameters 'email' and 'password' are mandatory!");
			result.setErrorCode(500);
			result.setErrorMessage(Translations.getText("lang_error_user_invalid_email_or_password"));
		}
		return headers;
	}
	
	private HttpHeaders logout(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("logout");
		if (getLoggedInUser() != null) {
			Cookie authCookie = EmailAuthHelper.logoutUser(request);
			headers.addCookie(authCookie);
			headers.setRedirectUrl(request.getContextPath() + "/");
		}
		return headers;
	}

	private HttpHeaders notValidRequestResetPassword(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	private HttpHeaders resetPassword(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("reset_password");

		log.info("Received parameter authcode: " + request.getParameter("code"));
		
		ObjectMapper mapper = new ObjectMapper();
    	String authCode = getJsonString(mapper, request, "code");
    	String password = getJsonString(mapper, request, "password");
    	log.log(Level.INFO, "Received password reset request for code: " + authCode);
    	model = UserMgmtFacade.instance().resetPassword(getLoggedInUser(), authCode, password);
    			
		return headers;
	}

	private HttpHeaders requestResetPassword(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("request_reset_password");

		log.info("Received parameter email: " + request.getParameter("email"));
		
		ObjectMapper mapper = new ObjectMapper();
    	String email = getJsonString(mapper, request, "email");
    	log.log(Level.INFO, "Received password reset request " + email);
    	model = UserMgmtFacade.instance().requestResetPassword(getLoggedInUser(), email);
    			
		return headers;
	}

	private HttpHeaders delete(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("delete");
		log.log(Level.WARNING, "Deleting user is not supported! User can be only deactivated.");
		headers.setStatus(501);
		return headers;
	}

	private HttpHeaders create(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("create");
		
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String listingString = request.getParameter("profile");
		if (!StringUtils.isEmpty(listingString)) {
			ObjectMapper mapper = new ObjectMapper();
			UserVO user = mapper.readValue(listingString, UserVO.class);
			user.setId(null);
			//model = ServiceFacade.instance().createUser(getLoggedInUser(), user);
			log.log(Level.WARNING, "User creation is not supported! You need to login using external account.");
			headers.setStatus(501);
		} else {
			log.log(Level.WARNING, "Parameter 'profile' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders update(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update");
		
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String profileString = request.getParameter("profile");
		if (!StringUtils.isEmpty(profileString)) {
			ObjectMapper mapper = new ObjectMapper();
			UserVO user = mapper.readValue(profileString, UserVO.class);
			if (user.getId() == null) {
				log.log(Level.WARNING, "User update called but user id not provided.");
				headers.setStatus(501);
			} else {
				log.log(Level.INFO, "Updating user: " + user);
				model = UserMgmtFacade.instance().updateUser(getLoggedInUser(), 
						user.getName(), user.getNickname(), user.getLocation(),
						user.getPhone(), user.isAccreditedInvestor(), user.isNotifyEnabled());
				if (model == null) {
					log.log(Level.WARNING, "User update error!");
					headers.setStatus(500);
				}
			}
		} else {
			log.log(Level.WARNING, "Parameter 'profile' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders autoSave(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("update");
		
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String profileString = request.getParameter("profile");
		if (!StringUtils.isEmpty(profileString)) {
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> user = mapper.readValue(profileString, Map.class);
			log.log(Level.INFO, "Autosaving user: " + user);
			String name = (String)user.get("name");
			String nickname = (String)user.get("nickname");
			String phone = (String)user.get("phone");
			String location = (String)user.get("location");
			Boolean investor = toBooleanObject(user.get("investor"));
			Boolean notifyEnabled = toBooleanObject(user.get("notify_enabled"));
			model = UserMgmtFacade.instance().updateUser(getLoggedInUser(),
					name, nickname, location, phone, investor, notifyEnabled);
			if (model == null) {
				log.log(Level.WARNING, "User autosave error!");
				headers.setStatus(500);
			} else {
				EmailAuthHelper.updateUser(request);
			}
		} else {
			log.log(Level.WARNING, "Parameter 'profile' is empty!");
			headers.setStatus(500);
		}

		return headers;
	}
	
	private Boolean toBooleanObject(Object value) {
		if (value instanceof Boolean) {
			return (Boolean)value;
		} else if (value instanceof String) {
			return BooleanUtils.toBooleanObject((String)value);
		} else {
			return null;
		}
	}

	private HttpHeaders all(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("all");
		model = UserMgmtFacade.instance().getAllUsers(getLoggedInUser());
		return headers;
	}

	private HttpHeaders dragons(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("dragons");
		ListPropertiesVO listProperties = getListProperties(request);
		model = UserMgmtFacade.instance().getDragons(getLoggedInUser(), listProperties);
		return headers;
	}
	
	private HttpHeaders listers(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("listers");
		ListPropertiesVO listProperties = getListProperties(request);
		model = UserMgmtFacade.instance().getListers(getLoggedInUser(), listProperties);
		return headers;
	}
	
	private HttpHeaders loggedin(HttpServletRequest request) {
		model = getLoggedInUser();
        return new HttpHeadersImpl("loggedin").disableCaching();
	}

	private HttpHeaders get(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 2, "id");
    	model = UserMgmtFacade.instance().getUser(getLoggedInUser(), userId);
        return new HttpHeadersImpl("get").disableCaching();
	}

	private HttpHeaders index(HttpServletRequest request) {
    	String userId = getCommandOrParameter(request, 1, "id");
    	model = UserMgmtFacade.instance().getUser(getLoggedInUser(), userId);
        return new HttpHeadersImpl("index").disableCaching();
	}

	private HttpHeaders checkUserName(HttpServletRequest request) {
    	String userName = getCommandOrParameter(request, 2, "name");
    	model = UserMgmtFacade.instance().checkUserNameIsValid(getLoggedInUser(), userName);

    	HttpHeaders headers = new HttpHeadersImpl("check_user_name");
		return headers;
	}

    // POST /user/send_message
    private HttpHeaders sendMessage(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("send_message");

		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String messageString = request.getParameter("message");
		if (!StringUtils.isEmpty(messageString)) {
			JsonNode rootNode = mapper.readValue(messageString, JsonNode.class);
			String userId = null;
			if (rootNode.get("profile_id") != null) {
				userId = rootNode.get("profile_id").getValueAsText();
			}
			String text = null;
			if (rootNode.get("text") != null) {
				text = rootNode.get("text").getValueAsText();
			}
			PrivateMessageVO message = MessageFacade.instance().sendPrivateMessage(getLoggedInUser(), userId, text);
			model = message;
		} else {
			log.severe("Missing message parameter!");
			headers.setStatus(500);
		}
		return headers;
    }

	/*
	 *  /user/message_users/
	 */
	private HttpHeaders messageUsers(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("message_users");
		
		ListPropertiesVO listProperties = getListProperties(request);
		PrivateMessageUserListVO result = MessageFacade.instance().getPrivateMessageUsers(getLoggedInUser(), listProperties);
		model = result;
		
		return headers;
	}
	
	/*
	 *  /user/messages/?id=ag1zdGFydHVwYmlkZGVychQLEgdMaXN0aW5nIgdtaXNsZWFkDA.html
	 */
	private HttpHeaders messages(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("messages");
		
		ListPropertiesVO listProperties = getListProperties(request);
		String userId = getCommandOrParameter(request, 2, "id");
		PrivateMessageListVO result = MessageFacade.instance().getPrivateMessages(getLoggedInUser(), userId, listProperties);
		model = result;
		return headers;
	}

	private HttpHeaders requestEmailUpdate(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("request_update_email");
		
		String email = getCommandOrParameter(request, 2, "email");
		twitter4j.User twitterUser = TwitterHelper.getTwitterUser(request);
		if (twitterUser != null) {
			model = UserMgmtFacade.instance().requestEmailUpdate(twitterUser, email);
		} else {
			log.warning("User not logged in via Twitter while trying to update email: " + email);
		}
		
		return headers;
	}

	private HttpHeaders confirmEmailUpdate(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("confirm_update_email");
		
		String id = getCommandOrParameter(request, 2, "id");
		String token = getCommandOrParameter(request, 3, "token");
		model = UserMgmtFacade.instance().confirmEmailUpdate(id, token);
		
		return headers;
	}

	private HttpHeaders promoteToDragon(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("promote_to_dragon");
		
		String userId = getCommandOrParameter(request, 2, "id");
		model = UserMgmtFacade.instance().promoteToDragon(getLoggedInUser(), userId);
		
		return headers;
	}

	private HttpHeaders requestDragon(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("request_dragon");
		UserAndUserVO result = new UserAndUserVO();
		result.setUser(getLoggedInUser());
		result.setErrorCode(1000);
		result.setErrorMessage(Translations.getText("lang_error_dragon_not_supported"));
		model = result;
		
		// we don't do that anymore
		//model = UserMgmtFacade.instance().requestDragon(getLoggedInUser());
		
		return headers;
	}

	private HttpHeaders requestEmailAccess(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("request_email_access");
		String email = getCommandOrParameter(request, 2, "email");
		String url = getCommandOrParameter(request, 3, "url");
		
		model = UserMgmtFacade.instance().requestEmailAccess(getLoggedInUser(), email, url);
		
		return headers;
	}
	
	private HttpHeaders storeCampaign(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = new HttpHeadersImpl("store_campaign");

		ObjectMapper mapper = new ObjectMapper();
		log.log(Level.INFO, "Parameters: " + request.getParameterMap());
		String messageString = request.getParameter("campaign");
		try {
			if (!StringUtils.isEmpty(messageString)) {
				CampaignVO campaign = mapper.readValue(messageString, CampaignVO.class);
				
				model = UserMgmtFacade.instance().storeCampaign(getLoggedInUser(), campaign);
			} else {
				log.severe("Missing campaign json parameter!");
				headers.setStatus(500);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error parsing campaign data", e);
			headers.setStatus(500);
		}
		return headers;
	}

	private HttpHeaders find(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("find");
		String query = getCommandOrParameter(request, 2, "query");
		
		model = UserMgmtFacade.instance().findUser(getLoggedInUser(), query);
		
		return headers;
	}
	
	@Override
	public Object getModel() {
		return model;
	}

}
