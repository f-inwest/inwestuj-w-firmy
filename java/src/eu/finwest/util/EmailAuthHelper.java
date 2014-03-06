/**
 * 
 */
package eu.finwest.util;

import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.SBUser;

/**
 * @author grzegorznittner
 *
 */
public class EmailAuthHelper {
	private static final Logger log = Logger.getLogger(EmailAuthHelper.class.getName());
	
	public static final String SERVLET_EMAIL_LOGIN = "/user/authorize.html";
	public static final String SERVLET_EMAIL_LOGOUT = "/user/logout.html";
	
	public static final String SESSION_EMAIL_USER = "email_user";
	public static final String EMAIL_AUTHENTICATION_COOKIE = "EMAIL_AUTHENTICATION_COOKIE";
	
	public static Cookie authorizeUser(HttpServletRequest request, SBUser user) {
		request.getSession().setAttribute(SESSION_EMAIL_USER, user);
		
		Cookie authCookie = new Cookie(EMAIL_AUTHENTICATION_COOKIE, user.authCookie);
		authCookie.setMaxAge(30 * 24 * 60 * 60);
		return authCookie;
	}
	
	public static Cookie logoutUser(HttpServletRequest request) {
		log.info("Removing email authenticated user from session: " + request.getSession().getAttribute(SESSION_EMAIL_USER));
		request.getSession().removeAttribute(SESSION_EMAIL_USER);
		Cookie authCookie = new Cookie(EMAIL_AUTHENTICATION_COOKIE, "");
		authCookie.setMaxAge(0);
		return authCookie;
	}
	
	public static SBUser getUser(HttpServletRequest request) {
		SBUser user = (SBUser)request.getSession().getAttribute(SESSION_EMAIL_USER);
		if (user == null) {
			log.info("Email auth session user: " + user);
			if (request != null && request.getCookies() != null) {
				for (Cookie cookie : request.getCookies()) {
					if (StringUtils.equals(EMAIL_AUTHENTICATION_COOKIE, cookie.getName())) {
						if (StringUtils.isNotBlank(cookie.getValue())) {
							String authCookie = cookie.getValue();
							user = ObjectifyDatastoreDAO.getInstance().getUserByAuthCookie(authCookie);
							log.info("Email auth user by cookie: " + authCookie + " is " + user);
							break;
						} else {
							log.info("Auth cookie is blank.");
						}
					}
				}
			}
		}
		return user;
	}
	
	public static String getLoginUrl(HttpServletRequest request) {
		String appUrl = TwitterHelper.getApplicationUrl(request);
		return appUrl + SERVLET_EMAIL_LOGIN;
	}
	
	public static String getLogoutUrl(HttpServletRequest request) {
		String appUrl = TwitterHelper.getApplicationUrl(request);
		return appUrl + SERVLET_EMAIL_LOGOUT;
	}
}
