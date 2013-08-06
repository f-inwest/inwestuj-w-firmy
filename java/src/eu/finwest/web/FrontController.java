package eu.finwest.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
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

		if (StringUtils.endsWith(pathInfo, ".css")) {
			response.setContentType("text/css");
			IOUtils.copy(new FileInputStream(WarmupListener.MAIN_CSS_FILE), response.getOutputStream());
			return;
		} else if (StringUtils.endsWith(pathInfo, ".js")) {
			response.setContentType("text/javascript");
			if (StringUtils.countMatches(pathInfo, "/") == 2) {
				IOUtils.copy(new FileInputStream(WarmupListener.MAIN_JS_FILE), response.getOutputStream());
				return;
			} else {
				String parts[] = pathInfo.split("/");
				String jsName = parts[parts.length - 1];
				IOUtils.copy(new FileInputStream(WarmupListener.JS_FOLDER + "/" + jsName), response.getOutputStream());
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
		} else {
			log.log(Level.WARNING, request.getMethod() + " " + request.getPathInfo() + " is not supported!  Redirecting to error page.");
			response.sendRedirect("/error-page.html");
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
		}
	}
}
