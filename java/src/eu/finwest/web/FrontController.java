package eu.finwest.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.appengine.api.utils.SystemProperty;

import eu.finwest.datamodel.Campaign;
import eu.finwest.datamodel.Campaign.Language;
import eu.finwest.vo.CampaignVO;
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
	private static final String SET_LANGUAGE = "set-lang";
	private static final String LANGUAGE_COOKIE = "SELECTED_LANGUAGE";
	
	private static final ThreadLocal<LangVersion> langVersion = new ThreadLocal<LangVersion>();
	private static final ThreadLocal<String> campaign = new ThreadLocal<String>();
	
	public static final CampaignVO PL_CAMPAIGN = createMainCampaign(Language.PL);
	public static final CampaignVO EN_CAMPAIGN = createMainCampaign(Language.EN);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String pathInfo = request.getPathInfo();
		log.log(Level.INFO, ">>>>>>> Path info: " + pathInfo);
//		if ("GET".equals(request.getMethod()) && !"true".equalsIgnoreCase(request.getHeader("X-AppEngine-Cron"))
//				&& "inwestuj-w-firmy.appspot.com".equals(request.getServerName())) {
//			String redirectUrl = request.getScheme() + "://www.inwestujwfirmy.pl" + request.getServletPath();
//			String queryString = request.getQueryString();
//			if (StringUtils.isNotEmpty(queryString)) {
//				redirectUrl += "?" + queryString;
//			}
//			log.info("Got request to inwestuj-w-firmy.appspot.com, redirecting to: " + redirectUrl);
//			response.sendRedirect(redirectUrl);
//			return;
//		}
		
		boolean develEnv = SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;
		if (!develEnv && !request.isSecure() && "GET".equals(request.getMethod()) && StringUtils.endsWith(pathInfo, ".html")) {
			String redirectUrl = "https://" + request.getServerName() + request.getServletPath();
			String queryString = request.getQueryString();
			if (StringUtils.isNotEmpty(queryString)) {
				redirectUrl += "?" + queryString;
			}
			log.info("Got insecure request to: " + request.getServerName() + request.getServletPath() + ", redirecting to: " + redirectUrl);
			response.sendRedirect(redirectUrl);
			return;
		}
				
		try {
			setLanguageAndCampaign(request, response);
	
			ModelDrivenController controller = null;
			HttpHeaders headers = null;
			if (StringUtils.isEmpty(pathInfo) || pathInfo.equals("/")) {
				log.info("Accessed / so redirecting to discover page.");
				response.sendRedirect(request.getContextPath() + "/discover-page.html");
				return;
			} else if (pathInfo.startsWith("/user")) {
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
				log.log(Level.INFO, "No controller associated with '" + pathInfo + "'");
			}
	
			if (controller != null) {
				controller.setLangVersion(langVersion.get());
				headers = ((ModelDrivenController)controller).execute(request);
				if (controller.getModel() != null) {
				} else {
					log.log(Level.WARNING, "Returned object is NULL");
				}
				if (headers != null) {
					if (headers.apply(request, response, controller.getModel()) != null) {
						if (request.getRequestURI().endsWith(".html")) {
							controller.generateHtml(response);
						} else if (request.getRequestURI().endsWith(".txt")) {
							controller.generateText(response);
						} else {
							// default is JSON
							response.setContentType("application/json");
							controller.generateJson(response);
						}
					}
				} else if (StringUtils.endsWith(pathInfo, ".html")) {
					response.setContentType("text/html");
					IOUtils.copy(new FileInputStream(versionedRelative(langVersion.get(), pathInfo)), response.getOutputStream());
				}
			} else {
				handleStaticFiles(request, response, pathInfo, langVersion.get());
				// after this is called do not modify output
			}
		} finally {
			langVersion.remove();
		}
	}

	public void setLanguageAndCampaign(HttpServletRequest request, HttpServletResponse response) {
		langVersion.set(obtainLanguageVersion(request, response));
		String userCampaign = obtainCampaign(request);
		campaign.set(userCampaign);
		if (StringUtils.isNotBlank(userCampaign)) {
			try {
				CampaignVO campaign = getCampaign();
				langVersion.set(LangVersion.valueOf(campaign.getAllowedLanguage()));
				log.info("Setting language from campaign: " + langVersion.get());
			} catch (Exception e) {
				log.log(Level.WARNING, "Error while setting language from campaign, we'll use default lang version");
			}
		}
	}

	private void handleStaticFiles(HttpServletRequest request, HttpServletResponse response, String pathInfo, LangVersion version)
			throws IOException, FileNotFoundException {
		String outFile = null;
		if (pathInfo.equals("/") || StringUtils.endsWith(pathInfo, ".html")) {
			String path = "/".equals(pathInfo) ? "/discover-page.html" : pathInfo;
//				String queryString = request.getQueryString();
//				if (!"/".equals(pathInfo) && StringUtils.isNotEmpty(queryString)) {
//					path += "?" + queryString;
//				}
			response.setContentType("text/html");
			outFile = versionedRelative(version, path);
		} else if (StringUtils.endsWith(pathInfo, ".png")) {
			response.setContentType("image/png");
			outFile = versionedRelative(version, pathInfo);
		} else if (StringUtils.endsWith(pathInfo, ".jpg")) {
			response.setContentType("image/jpg");
			outFile = versionedRelative(version, pathInfo);
		} else if (StringUtils.endsWith(pathInfo, ".gif")) {
			response.setContentType("image/gif");
			outFile = versionedRelative(version, pathInfo);
		} else if (StringUtils.endsWith(pathInfo, ".ico")) {
			response.setContentType("image/x-icon");
			outFile = versionedRelative(version, pathInfo);
		} else if (StringUtils.endsWith(pathInfo, ".css")) {
			response.setContentType("text/css");
			outFile = versionedRelative(version, WarmupListener.MAIN_CSS_FILE);
		} else if (StringUtils.endsWith(pathInfo, ".js")) {
			response.setContentType("text/javascript");
			if (StringUtils.countMatches(pathInfo, "/") == 2) {
				outFile = versionedRelative(version, WarmupListener.MAIN_JS_FILE);
			} else {
				String parts[] = pathInfo.split("/");
				String jsName = parts[parts.length - 1];
				outFile = versionedRelative(version, WarmupListener.JS_FOLDER + "/" + jsName);
			}
		}
		log.log(Level.INFO, "Handling static file " + pathInfo + ", will respond with file: " + outFile);
		if (outFile != null && new File(outFile).exists()) {
			log.log(Level.INFO, ">>>>>>> Sending back file: " + outFile);
			IOUtils.copy(new FileInputStream(outFile), response.getOutputStream());
		} else {
			log.log(Level.WARNING, request.getMethod() + " " + request.getPathInfo() + " is not supported!  Redirecting to error page.");
			response.sendRedirect("/error-page.html");
		}
	}
	
	private LangVersion obtainLanguageVersion(HttpServletRequest request, HttpServletResponse response) {
		String langCookie = null;
		if (request.getParameter(SET_LANGUAGE) != null) {
			String version = request.getParameter(SET_LANGUAGE);
			if ("en".equals(version) || "pl".equals(version)) {
				log.info("Setting language cookie to: " + version);
				response.addCookie(new Cookie(LANGUAGE_COOKIE, version));
				return "en".equals(version) ? LangVersion.EN : LangVersion.PL;
			}
		}
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (LANGUAGE_COOKIE.equals(cookie.getName())) {
					langCookie = cookie.getValue();
				}
			}
		}
		if (langCookie != null && (langCookie.equals("en") || langCookie.equals("pl"))) {
			log.info("Selected version from cookie: " + langCookie);
			return "en".equals(langCookie) ? LangVersion.EN : LangVersion.PL;
		} else {
			if (langCookie == null)
				log.info("Language cookie not set");
			else {
				log.info("Language cookie set to not handled value: " + langCookie);
				langCookie = null;
			}
		}
		
		String version = "en";
		String acceptLang = request.getHeader(ACCEPT_LANGUAGE);
		if (acceptLang != null) {
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
		} else {
			log.info("Header " + ACCEPT_LANGUAGE + " not passed!");
		}
		if (langCookie == null) {
			log.info("Setting language cookie to: " + version);
			response.addCookie(new Cookie(LANGUAGE_COOKIE, version));
		}
		return "en".equals(version) ? LangVersion.EN : LangVersion.PL;
	}
	
	private String versionedRelative(LangVersion version, String path) {
		if (StringUtils.startsWith(path, "/pl") || StringUtils.startsWith(path, "/en")) {
			return path;
		} else {
			path = path.startsWith("./") ? path.substring(2) : path;
			return "./" + version.toString().toLowerCase() + (path.startsWith("/") ? "" : "/") + path;
		}
	}
	
	private String versioned(LangVersion version, String path) {
		if (StringUtils.startsWith(path, "/pl") || StringUtils.startsWith(path, "/en")) {
			return path;
		} else {
			return "/" + version.toString().toLowerCase() + (path.startsWith("/") ? "" : "/") + path;
		}
	}
	
	private String obtainCampaign(HttpServletRequest request) {
		String nameParts[] = request.getServerName().split("\\.");
		String campaignName = null;
		if (nameParts.length <= 1) {
			// subdomain is not provided
		} else if ("localhost".equals(nameParts[nameParts.length - 1])) {
			// accessed campaign.localhost
			campaignName = nameParts[nameParts.length - 2];			
		} else if (nameParts.length >= 3 && "appspot".equals(nameParts[nameParts.length - 2])) {
			if (nameParts.length == 3) {
				// accessed inwestuj-w-firmy.appspot.com
			} else if (nameParts.length == 4) {
				if (NumberUtils.isNumber(nameParts[0]) && nameParts[0].length() == 12) {
					// accessed version.inwestuj-w-firmy.appspot.com
				} else {
					// accessed campaign.inwestuj-w-firmy.appspot.com
					campaignName = nameParts[0];
				}
			} else {
				// accessed campaign.version.inwestuj-w-firmy.appspot.com
				campaignName = nameParts[0];
			}
		} else {
			if (nameParts.length == 2) {
				// accessed inwestujwfirmy.pl
			} else {
				// accessed campaign.inwestujwfirmy.pl
				campaignName = nameParts[nameParts.length - 3];
			}
		}
		if ("www".equals(campaignName)) {
			campaignName = null;
		}
		if (campaignName != null) {
			campaignName = campaignName.toLowerCase();
			return campaignName;
		} else {
			log.log(Level.INFO, "CampaignName: main " + getLangVersion() + " " + request.getServerName() + " " + request.getMethod() + " " + request.getPathInfo());
			return null;
		}
	}
	
	public static LangVersion getLangVersion() {
		LangVersion lang = langVersion.get();
		return lang == null ? LangVersion.PL : lang;
	}
	
	public static CampaignVO getCampaign() {
		String campaignName = campaign.get();
		CampaignVO campaign = MemCacheFacade.instance().getCampaign(campaignName);
		if (StringUtils.isNotEmpty(campaignName) && campaign == null) {
			// campaign for given subdomain is not defined
			campaign = new CampaignVO();
			campaign.setActiveFrom(new Date(0));
			campaign.setActiveTo(new Date(0));
			campaign.setAllowedLanguage(getLangVersion() == LangVersion.PL ? PL_CAMPAIGN.toString() : EN_CAMPAIGN.toString());
			campaign.setCreated(new Date(0));
			campaign.setCreator("Admin");
			campaign.setSubdomain(campaignName);
			campaign.setId("doesn't exist");
			campaign.setName(campaignName);
			campaign.setPublicBrowsing(false);
		}
		return campaign == null ? (getLangVersion() == LangVersion.PL ? PL_CAMPAIGN : EN_CAMPAIGN) : campaign;
	}
	
	private static final CampaignVO createMainCampaign(Language language) {
		CampaignVO campaign = new CampaignVO();
		campaign.setName(language == Language.EN ? "English projects" : "Polskie projekty");
		campaign.setSpecial(true);
		campaign.setCreator("system");
		campaign.setCreated(new Date(0));
		campaign.setActiveFrom(new Date(0));
		campaign.setStatus(Campaign.Status.ACTIVE.toString());
		campaign.setActiveTo(new Date(Long.MAX_VALUE - 1000));
		campaign.setDescription(language == Language.EN ? "Main campaign" : "Główna kampania");
		campaign.setComment(language == Language.EN ? "Campaign dedicated for english language projects" : "Kampania dla polskojęzycznych projektów");
		campaign.setId(language.name());
		campaign.setSubdomain(language.name().toLowerCase());
		campaign.setAllowedLanguage(language.name());
		campaign.setPublicBrowsing(true);
		return campaign;
	}
}
