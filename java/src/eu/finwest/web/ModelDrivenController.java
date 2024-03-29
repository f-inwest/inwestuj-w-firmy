package eu.finwest.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.finwest.util.*;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import eu.finwest.datamodel.SBUser;
import eu.finwest.vo.BaseResultVO;
import eu.finwest.vo.ErrorCodes;
import eu.finwest.vo.ListPropertiesVO;
import eu.finwest.vo.UserDataUpdatableContainer;
import eu.finwest.vo.UserVO;

/**
 *
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public abstract class ModelDrivenController {
	private static final Logger log = Logger.getLogger(ModelDrivenController.class.getName());

	private static int DEFAULT_MAX_RESULTS = 5;
    private static int MAX_RESULTS = 20;
	private String command[];
	private UserVO loggedInUser;
	private LangVersion langVersion;
	
	/**
	 * Executes action handler for particular controller
	 * @param request
	 * @return Http headers and return code
	 */
	abstract protected HttpHeaders executeAction(HttpServletRequest request)
		throws JsonParseException, JsonMappingException, IOException;

	/**
	 * Returns object which should be trasformed into one of the result types (JSON, HTML)
	 */
	abstract public Object getModel();

	public final HttpHeaders execute(HttpServletRequest request) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		SBUser emailUser = EmailAuthHelper.getUser(request);
		FacebookUser fbUser = FacebookHelper.getFacebookUser(request);
		twitter4j.User twitterUser = TwitterHelper.getTwitterUser(request);

		if (user != null) {
			// logged in via Google
			loggedInUser = UserMgmtFacade.instance().getLoggedInUser(user);
			if (loggedInUser == null) {
				// first time logged in
				loggedInUser = UserMgmtFacade.instance().createUser(user);
			}
			if (loggedInUser != null) {
				loggedInUser.setAdmin(userService.isUserAdmin());
			}
			log.info("Logged in via Google: " + loggedInUser);
		} else if (emailUser != null) {
			// login via email/pass
			loggedInUser = UserMgmtFacade.instance().getLoggedInUser(emailUser);
			log.info("Logged in via email: " + loggedInUser);
		} else if (fbUser != null) {
			// login via Facebook
			log.info("Logged in via Facebook as " + fbUser.getId() + ", email: " + fbUser.getEmail());
			loggedInUser = UserMgmtFacade.instance().getLoggedInUser(fbUser);
			if (loggedInUser == null) {
				log.info("User not found via facebook id " + fbUser.getId() + ", email: " + fbUser.getEmail());
				loggedInUser = UserMgmtFacade.instance().createUser(fbUser);
			}
		} else if (twitterUser != null) {
			// login via Twitter
			log.info("Logged in via Twitter as " + twitterUser.getScreenName());
			loggedInUser = UserMgmtFacade.instance().getLoggedInUser(twitterUser);
			if (loggedInUser == null) {
				log.info("User not found via twitter id " + twitterUser.getId() + ", screen name '" + twitterUser.getScreenName() + "'");
				loggedInUser = UserMgmtFacade.instance().createUser(twitterUser);
			}
		} else {
			// not logged in
			loggedInUser = null;
		}
		
		if (loggedInUser != null) {
			UserMgmtFacade.instance().updateUserRecentData(loggedInUser, FrontController.getCampaign().getSubdomain(), FrontController.getLangVersion());
			UserMgmtFacade.instance().updateUserListings(loggedInUser);
		}

		if (loggedInUser != null && !StringUtils.isEmpty(loggedInUser.getEditedListing())) {
			// calling this method checks also if listing is in valid state
			ListingFacade.instance().editedListing(loggedInUser);
		}
		if (loggedInUser != null && StringUtils.isNotEmpty(request.getHeader("X-AppEngine-Country"))) {
			Map<String, String> locationHeaders = new HashMap<String, String>();
			locationHeaders.put("X-AppEngine-Country", request.getHeader("X-AppEngine-Country"));
			locationHeaders.put("X-AppEngine-Region", request.getHeader("X-AppEngine-Region"));
			locationHeaders.put("X-AppEngine-City", request.getHeader("X-AppEngine-City"));
			locationHeaders.put("X-AppEngine-CityLatLong", request.getHeader("X-AppEngine-CityLatLong"));
			loggedInUser.setLocationHeaders(locationHeaders);
		}
		
		command = decomposeRequest(request.getPathInfo());

		HttpHeaders headers = null;
		try {
			headers = executeAction(request);
			if (headers == null) {
				log.log(Level.INFO, request.getMethod() + " " + getCommand(1) + " is not supported!");
			}
			Object model = getModel();
			if (model instanceof UserDataUpdatableContainer) {
				((UserDataUpdatableContainer)model).updateUserData();
			}
			if (model instanceof BaseResultVO) {
				String appHost = TwitterHelper.getApplicationUrl(request);
				if (loggedInUser != null) {
					((BaseResultVO) model).setLoggedUser(loggedInUser);
					if (user != null) {
						((BaseResultVO) model).setLogoutUrl(userService.createLogoutURL(appHost));
					} else if (emailUser != null) {
						((BaseResultVO) model).setLogoutUrl(EmailAuthHelper.getLogoutUrl(request));
					} else if (fbUser != null) {
						((BaseResultVO) model).setLogoutUrl(FacebookHelper.getLogoutUrl(request));
					} else if (twitterUser != null) {
						loggedInUser.setNickname(twitterUser.getScreenName());
						((BaseResultVO) model).setLogoutUrl(TwitterHelper.getLogoutUrl(request));
					}
				} else {
					((BaseResultVO) model).setLoginUrl(userService.createLoginURL(appHost, "inwestujwfirmy.pl"));
					if (FacebookHelper.getFacebookAuthParams() != null) {
						((BaseResultVO) model).setFacebookLoginUrl(FacebookHelper.getLoginUrl(request));
					}
					if (TwitterHelper.configureTwitterFactory() != null) {
						((BaseResultVO) model).setTwitterLoginUrl(TwitterHelper.getLoginUrl(request));
					}
				}
				((BaseResultVO) model).setCampaign(FrontController.getCampaign());
				((BaseResultVO) model).setAllCampaigns(MemCacheFacade.instance().getAllCampaigns().values());

				if (((BaseResultVO) model).getErrorCode() == ErrorCodes.NOT_LOGGED_IN) {
					headers.setStatus(401);
				} else if (((BaseResultVO) model).getErrorCode() != ErrorCodes.OK) {
					headers.setStatus(500);
				}
			}
		} catch (Exception e) {
			headers = new HttpHeadersImpl();
			headers.setStatus(501);
			log.log(Level.SEVERE, "Error handling request", e);
		}

		return headers;
	}

	public void generateJson(HttpServletResponse response) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.getSerializerProvider().setNullKeySerializer(new NullKeySerializer());
			response.setContentType("application/json;charset=UTF-8");
			mapper.writeValue(response.getWriter(), getModel());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating response JSON", e);
		}
	}

	public void generateHtml(HttpServletResponse response) {
		PrintWriter writer;
		try {
			response.setContentType("text/html;charset=UTF-8");
			writer = response.getWriter();
			writer.println("<html><head><title>inwestujwfirmy.pl</title></head><body>");
			String modelHtml = getModel() != null ? getModel().toString() : "Result is empty.";
			modelHtml.replaceAll("]", "]<br/>");
			writer.println(modelHtml);
			writer.println("</body></html>");
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error generating response HTML", e);
		}
	}

	public void generateText(HttpServletResponse response) {
		PrintWriter writer;
		try {
			response.setContentType("text/plain;charset=UTF-8");
			writer = response.getWriter();
			String modelText = getModel() != null ? getModel().toString() : "Result is empty.";
			writer.println(modelText);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error generating response text", e);
		}
	}

	public void generateCSV(HttpServletResponse response) {
		PrintWriter writer;
		try {
			response.setContentType("text/csv;charset=UTF-8");
			writer = response.getWriter();
			String modelText = getModel() != null ? getModel().toString() : "Result is empty.";
			writer.println(modelText);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error generating response csv", e);
		}
	}

	/**
	 * Returns command encoded in the path info.
	 * eg. for request uri: /listings/top.json?maxItems=5&cursor=qerqsdfgsdfgh43t6dsfhg
	 *     available commands are: listings (0), top (1)
	 *
	 * @param index Command order number
	 * @return String representing command or empty string
	 */
	protected String getCommand(int index) {
		return index < command.length ? command[index] : "";
	}

	private String[] decomposeRequest(String path) {
		int dotPos = path.indexOf('.');
		int questionPos = path.indexOf('.');
		// handling -1
		dotPos = dotPos < 0 ? path.length() : dotPos;
		questionPos = questionPos < 0 ? path.length() : questionPos;

		path = path.substring(0, dotPos > questionPos ? questionPos : dotPos);
		StringTokenizer tokenizer = new StringTokenizer(path, "/");

		List<String> pathElements = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			pathElements.add(tokenizer.nextToken());
		}

		log.log(Level.INFO, "Commands: " + pathElements.toString());
		return pathElements.toArray(new String[0]);
	}

	protected ListPropertiesVO getListProperties(HttpServletRequest request) {
		ListPropertiesVO listingProperties = new ListPropertiesVO();

		try {
			String maxItemsStr = request.getParameter("max_results");
			int maxItems = maxItemsStr != null ? Integer.parseInt(maxItemsStr) : DEFAULT_MAX_RESULTS;
	        if (maxItems > MAX_RESULTS) { // avoid DoS attacks
	            maxItems = MAX_RESULTS;
	        }
			listingProperties.setMaxResults(maxItems);
		} catch (NumberFormatException e) {
			listingProperties.setMaxResults(DEFAULT_MAX_RESULTS);
		}

		try {
			String startIndexStr = request.getParameter("start_index");
			int startIndex = startIndexStr != null ? Integer.parseInt(startIndexStr) : 1;
			listingProperties.setStartIndex(startIndex);
		} catch (NumberFormatException e) {
			listingProperties.setStartIndex(1);
		}

		listingProperties.setNextCursor(request.getParameter("next_cursor"));
		listingProperties.setRequestData(request);

		return listingProperties;
	}

	protected String getCommandOrParameter(HttpServletRequest request, int commandNum, String parameter) {
		if ("".equals(getCommand(commandNum))) {
			return request.getParameter(parameter);
		} else {
			return getCommand(commandNum);
		}
	}

	protected String getJsonString(ObjectMapper mapper, HttpServletRequest request, String paramName) {
		String paramValue = request.getParameter(paramName);
		if (paramValue != null) {
			try {
				return mapper.readValue(paramValue, String.class);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	protected UserVO getLoggedInUser() {
		return loggedInUser;
	}

	public LangVersion getLangVersion() {
		return langVersion;
	}

	public void setLangVersion(LangVersion langVersion) {
		this.langVersion = langVersion;
	}
}
