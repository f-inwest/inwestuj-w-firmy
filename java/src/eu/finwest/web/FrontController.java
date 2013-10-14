package eu.finwest.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import eu.finwest.web.controllers.CommentController;
import eu.finwest.web.controllers.CronTaskController;
import eu.finwest.web.controllers.FileController;
import eu.finwest.web.controllers.ListingController;
import eu.finwest.web.controllers.MonitorController;
import eu.finwest.web.controllers.NotificationController;
import eu.finwest.web.controllers.SystemController;
import eu.finwest.web.controllers.TaskController;
import eu.finwest.web.controllers.UserController;
import eu.finwest.web.servlets.WarmupListener;

/**
 *
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class FrontController extends HttpServlet {
	private static final Logger log = Logger.getLogger(FrontController.class.getName());
	
	private static final String ACCEPT_LANGUAGE = "Accept-Language";
	private static final String LANGUAGE_COOKIE = "SELECTED_LANGUAGE";

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if ("GET".equals(request.getMethod()) && !"true".equalsIgnoreCase(request.getHeader("X-AppEngine-Cron"))
				&& "inwestuj-w-firmy.appspot.com".equals(request.getServerName())) {
			String redirectUrl = request.getScheme() + "://www.inwestujwfirmy.pl" + request.getServletPath();
			String queryString = request.getQueryString();
			if (StringUtils.isNotEmpty(queryString)) {
				redirectUrl += "?" + queryString;
			}
			log.info("Got request to inwestuj-w-firmy.appspot.com, redirecting to: " + redirectUrl);
			response.sendRedirect(redirectUrl);
			return;
		}
		
		String version = getVersionFolder(request);

		if (StringUtils.endsWith(pathInfo, ".css")) {
			response.setContentType("text/css");
			IOUtils.copy(new FileInputStream(versionedRelative(version, WarmupListener.MAIN_CSS_FILE)), response.getOutputStream());
			return;
		} else if (StringUtils.endsWith(pathInfo, ".js")) {
			response.setContentType("text/javascript");
			if (StringUtils.countMatches(pathInfo, "/") == 2) {
				IOUtils.copy(new FileInputStream(versionedRelative(version, WarmupListener.MAIN_JS_FILE)), response.getOutputStream());
				return;
			} else {
				String parts[] = pathInfo.split("/");
				String jsName = parts[parts.length - 1];
				IOUtils.copy(new FileInputStream(versionedRelative(version, WarmupListener.JS_FOLDER + "/" + jsName)), response.getOutputStream());
				return;
			}
		}

		ModelDrivenController controller = null;
		HttpHeaders headers = null;
		if (pathInfo.startsWith("/user")) {
			controller = new UserController();
		} else if (pathInfo.startsWith("/listing")) {
			controller = new ListingController();
		} else if (pathInfo.startsWith("/comment")) {
			controller = new CommentController();
		} else if (pathInfo.startsWith("/system")) {
			controller = new SystemController();
		} else if (pathInfo.startsWith("/file")) {
			controller = new FileController();
		} else if (pathInfo.startsWith("/task")) {
			controller = new TaskController();
		} else if (pathInfo.startsWith("/notification")) {
			controller = new NotificationController();
		} else if (pathInfo.startsWith("/monitor")) {
			controller = new MonitorController();
		} else if (pathInfo.startsWith("/cron")) {
			controller = new CronTaskController();
		} else {
			log.log(Level.WARNING, "Unknown action '" + pathInfo + "'");
		}

		if (controller != null) {
			headers = ((ModelDrivenController)controller).execute(request);
			if (controller.getModel() != null) {
			} else {
				log.log(Level.WARNING, "Returned object is NULL");
			}
		} else if (!StringUtils.endsWith(pathInfo, ".html")) {
			log.log(Level.WARNING, request.getMethod() + " " + request.getPathInfo() + " is not supported!  Redirecting to error page.");
			response.sendRedirect(versioned(version, "/error-page.html"));
			return;
		}

		if (headers != null) {
			if (headers.apply(request, response, controller.getModel()) != null) {
				if (request.getRequestURI().endsWith(".html")) {
					// default is plain/text
					controller.generateHtml(response);
				} else {
					// default is JSON
					response.setContentType("application/json");
					controller.generateJson(response);
				}
			}
		} else if (StringUtils.endsWith(pathInfo, ".html")) {
			response.setContentType("text/html");
			IOUtils.copy(new FileInputStream(versionedRelative(version, pathInfo)), response.getOutputStream());
			return;
		}
	}
	
	private String getVersionFolder(HttpServletRequest request) {
		String langCookie = null;
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (LANGUAGE_COOKIE.equals(cookie.getName())) {
					langCookie = cookie.getValue();
				}
			}
		}
		if (langCookie != null && (langCookie.equals("en") || langCookie.equals("pl"))) {
			log.info("Selected version: " + langCookie);
			return langCookie;
		} else {
			if (langCookie == null)
				log.info("Language cookie not set");
			else
				log.info("Language cookie set to not handled value: " + langCookie);
		}
		
		String version = "en";
		String acceptLang = request.getHeader(ACCEPT_LANGUAGE);
		for (String lang : acceptLang.split(",")) {
			if (lang.trim().startsWith("pl")) {
				log.info("User accepts language pl");
				version = "pl";
				break;
			}
			if (lang.trim().startsWith("en")) {
				log.info("User accepts language en");
				version = "en";
				break;
			}
		}
		log.info("Selected version: " + version);
		return version;
	}
	
	private String versionedRelative(String version, String path) {
		if (StringUtils.startsWith(path, "/pl") || StringUtils.startsWith(path, "/en")) {
			return path;
		} else {
			return "./" + version + "/" + path;
		}
	}
	
	private String versioned(String version, String path) {
		if (StringUtils.startsWith(path, "/pl") || StringUtils.startsWith(path, "/en")) {
			return path;
		} else {
			return "/" + version + path;
		}
	}
}
