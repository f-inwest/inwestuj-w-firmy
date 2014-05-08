package eu.finwest.web;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.Notification;
import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.SystemProperty;
import eu.finwest.util.Translations;
import eu.finwest.vo.DtoToVoConverter;
import eu.finwest.vo.ListPropertiesVO;
import eu.finwest.vo.ListingTileVO;
import eu.finwest.vo.NotificationVO;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class EmailService {
	private static final Logger log = Logger.getLogger(EmailService.class.getName());
	
	private static final String ADMIN_EMAIL = "grzegorz.nittner@inwestujwfirmy.pl";
	
	private static final String TEMPLATE_AUTHORIZATION = "./WEB-INF/email-templates/email-authentication.html";
	private static final String TEMPLATE_NOTIFICATION = "./WEB-INF/email-templates/notification.html";
	private static final String TEMPLATE_3LISTING_NOTIFICATION = "./WEB-INF/email-templates/welcome-email.html";

	private static final String WELCOME_IMAGE_URL = "https://www.inwestujwfirmy.pl/img/email-welcome.jpg";
	private static final String NOTIFICATION_IMAGE_URL = "https://www.inwestujwfirmy.pl/img/email-notification.jpg";

	private static final String LINK_TO_HEADER_IMAGE = "##NOTIFICATION_LINK_TO_HEADER_IMAGE##";

	private static final String NOT_DISPLAYING_PROPERLY = "##NOTIFICATION_NOT_DISPLAYING_PROPERLY##";
	private static final String VIEW_ON_INWESTUJ_W_FIRMY = "##NOTIFICATION_VIEW_ON_INWESTUJ_W_FIRMY##";
	private static final String NOTIFICATION_TITLE = "##NOTIFICATION_TITLE##";
	private static final String NOTIFICATION_TITLE_ESCAPED = "##NOTIFICATION_TITLE_ESCAPED##";
	private static final String TEXT_NO_LINK = "##NOTIFICATION_TEXT_NO_LINK##";
	private static final String VISIT_LISTING_TEXT = "##NOTIFICATION_VISIT_LISTING_TEXT##";
	private static final String LINK_TO_LISTING = "##NOTIFICATION_LINK_TO_LISTING##";
	private static final String LINK_TO_LISTING_LOGO = "##NOTIFICATION_LINK_TO_LISTING_LOGO##";
	private static final String LISTING_NAME = "##NOTIFICATION_LISTING_NAME##";
	private static final String LISTING_CATEGORY_LOCATION = "##NOTIFICATION_LISTING_CATEGORY_LOCATION##";
	private static final String LISTING_MANTRA = "##NOTIFICATION_LISTING_MANTRA##";
	private static final String COPYRIGHT_TEXT = "##NOTIFICATION_COPYRIGHT_TEXT##";
	private static final String NOTIFICATION_UPDATE_PROFILE_PAGE = "##NOTIFICATION_UPDATE_PROFILE_PAGE##";
	private static final String NOTIFICATION_MAILING_LIST_ADDRESS_TEXT = "##NOTIFICATION_MAILING_LIST_ADDRESS_TEXT##";
	private static final String NOTIFICATION_MAILING_LIST_ADDRESS = "##NOTIFICATION_MAILING_LIST_ADDRESS##";

	private static final String NOTIFICATION_FEATURED_PROJECTS = "##NOTIFICATION_FEATURED_PROJECTS##";
	private static final String LINK_TO_LISTING_1 = "##NOTIFICATION_LINK_TO_LISTING_1##";
	private static final String LINK_TO_LISTING_1_LOGO = "##NOTIFICATION_LINK_TO_LISTING_1_LOGO##";
	private static final String LISTING_1_NAME = "##NOTIFICATION_LISTING_1_NAME##";
	private static final String LISTING_1_CATEGORY_LOCATION = "##NOTIFICATION_LISTING_1_CATEGORY_LOCATION##";
	private static final String LISTING_1_MANTRA = "##NOTIFICATION_LISTING_1_MANTRA##";

	private static final String LINK_TO_LISTING_2 = "##NOTIFICATION_LINK_TO_LISTING_2##";
	private static final String LINK_TO_LISTING_2_LOGO = "##NOTIFICATION_LINK_TO_LISTING_2_LOGO##";
	private static final String LISTING_2_NAME = "##NOTIFICATION_LISTING_2_NAME##";
	private static final String LISTING_2_CATEGORY_LOCATION = "##NOTIFICATION_LISTING_2_CATEGORY_LOCATION##";
	private static final String LISTING_2_MANTRA = "##NOTIFICATION_LISTING_2_MANTRA##";

	private static final String LINK_TO_LISTING_3 = "##NOTIFICATION_LINK_TO_LISTING_3##";
	private static final String LINK_TO_LISTING_3_LOGO = "##NOTIFICATION_LINK_TO_LISTING_3_LOGO##";
	private static final String LISTING_3_NAME = "##NOTIFICATION_LISTING_3_NAME##";
	private static final String LISTING_3_CATEGORY_LOCATION = "##NOTIFICATION_LISTING_3_CATEGORY_LOCATION##";
	private static final String LISTING_3_MANTRA = "##NOTIFICATION_LISTING_3_MANTRA##";

	private static final String NOTIFICATION_TEXT_1 = "##NOTIFICATION_TEXT_1##";
	private static final String NOTIFICATION_TEXT_2 = "##NOTIFICATION_TEXT_2##";
	private static final String NOTIFICATION_TEXT_3 = "##NOTIFICATION_TEXT_3##";
	private static final String NOTIFICATION_LINK_TEXT = "##NOTIFICATION_LINK_TEXT##";
	private static final String NOTIFICATION_LINK_HREF = "##NOTIFICATION_LINK_HREF##";

	private static EmailService instance = null;

	public static EmailService instance() {
		if (instance == null) {
			instance = new EmailService();
		}
		return instance;
	}

	private EmailService() {
	}

	public void sendAdmin(String from, String to, String subject, String htmlBody) throws AddressException, MessagingException {
		log.info("Email to: " + to + " subject:" + subject);
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject, "UTF-8");

		Multipart multipart = new MimeMultipart();
		BodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		htmlPart.setDisposition(BodyPart.INLINE);
		multipart.addBodyPart(htmlPart);

		message.setContent(multipart);

		Transport.send(message, message.getAllRecipients());
	}

	public void send(String to, String subject, String htmlBody, String textBody) throws AddressException, MessagingException {
		log.info("Email to: " + to + " subject: " + subject);
		SystemProperty fromProperty = ObjectifyDatastoreDAO.getInstance().getSystemProperty("notification_admin_email");
		SystemProperty noBccAdmins = ObjectifyDatastoreDAO.getInstance().getSystemProperty("notification_no_bcc_admins");
		SystemProperty realReceivers = ObjectifyDatastoreDAO.getInstance().getSystemProperty("notification_real_receivers");
		
		String from = ADMIN_EMAIL;
		if (fromProperty != null && StringUtils.isNotBlank(fromProperty.value)) {
			from = fromProperty.value;
		}

		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

		if (realReceivers != null && realReceivers.booleanValue()) {
			// notification_real_receivers == true
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject, "UTF-8");
			sendEmail(htmlBody, textBody, message);
			
			if (noBccAdmins == null || (noBccAdmins != null && !noBccAdmins.booleanValue())) {
				log.info("Email will be sent to: " + to + " and to admins");
				sendAdminEmail(to, subject, htmlBody, textBody, from, session);
			}
		} else {
			log.info("Email will be sent to admins only");
			sendAdminEmail(to, subject, htmlBody, textBody, from, session);
		}
	}

	private void sendAdminEmail(String to, String subject, String htmlBody, String textBody, String from, Session session) {
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("admins"));
			message.setSubject(subject + " real addressee " + to, "UTF-8");
			sendEmail(htmlBody, textBody, message);
		} catch (Exception e) {
			log.log(Level.WARNING, "Error sending admin email", e);
		}
	}

	private void sendEmail(String htmlBody, String textBody, MimeMessage message)
			throws MessagingException {
		Multipart multipart = new MimeMultipart();
		BodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		htmlPart.setDisposition(BodyPart.INLINE);
		multipart.addBodyPart(htmlPart);
		
		if (textBody != null) {
			BodyPart textPart = new MimeBodyPart();
			textPart.setContent(textBody, "text/plain");
			textPart.setDisposition(BodyPart.INLINE);
			multipart.addBodyPart(textPart);
		}
		message.setContent(multipart);
		Transport.send(message, message.getAllRecipients());
	}

	public boolean sendNotificationEmail(NotificationVO notification) {
		if (Notification.Type.USER_PROMOTED_TO_INVESTOR.toString().equalsIgnoreCase(notification.getType())) {
			return sendAdminNotification(notification);
		} if (Notification.Type.PRIVATE_MESSAGE.toString().equalsIgnoreCase(notification.getType())) {
			return send3ListingNotification(notification);
		} else {
			return sendListingNotification(notification);
		}
	}

	private boolean sendListingNotification(NotificationVO notification) {
		try {
			Map<String, String> props = prepareListingNotificationProps(notification);
			String htmlTemplate = FileUtils.readFileToString(new File(TEMPLATE_NOTIFICATION), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			send(notification.getUserEmail(), subject, htmlBody, null);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending notification email", e);
			return false;
		}
	}

	public Map<String, String> prepareListingNotificationProps(NotificationVO notification) {
		Map<String, String> props = new HashMap<String, String>();

		props.put(NOT_DISPLAYING_PROPERLY, getText(notification, "email_not_displaying_properly"));
		props.put(VIEW_ON_INWESTUJ_W_FIRMY, getText(notification, "email_view_on_portal", getDomainUrl(notification, "/notifications-page.html")));

		props.put(NOTIFICATION_TITLE, notification.getTitle());
		props.put(NOTIFICATION_TITLE_ESCAPED, escape(notification.getTitle()));
		props.put(TEXT_NO_LINK, escape(notification.getText1()) + " <br/> <br/> <i>" + escape(notification.getText2()) + "</i>");
		props.put(VISIT_LISTING_TEXT, notification.getText3());
		props.put(LINK_TO_LISTING, notification.getLink());
		props.put(LINK_TO_LISTING_LOGO, notification.getListingLogoLink());
		props.put(LISTING_NAME, escape(notification.getListingName()));
		props.put(LISTING_CATEGORY_LOCATION, notification.getListingCategory() + " <br/>" + notification.getListingBriefAddress());
		props.put(LISTING_MANTRA, escape(notification.getListingMantra()));
		
		props.put(COPYRIGHT_TEXT, getText(notification, "email_copyright", String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS_TEXT, getText(notification, "email_mailing_address_text"));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS, getText(notification, "email_mailing_address"));
		props.put(NOTIFICATION_UPDATE_PROFILE_PAGE, getText(notification, "email_profil_page", getDomainUrl(notification, "/edit-profile-page.html")));
		return props;
	}

	private boolean send3ListingNotification(NotificationVO notification) {
		try {
			ListPropertiesVO listingsProps = new ListPropertiesVO();
			listingsProps.setMaxResults(3); 
			List<ListingTileVO> listingsTiles = DtoToVoConverter.convertListingTiles(
					ObjectifyDatastoreDAO.getInstance().getTopListings(notification.getUserRecentDomain(), listingsProps));
			if (listingsTiles == null || listingsTiles.size() == 0) {
				listingsTiles = DtoToVoConverter.convertListingTiles(
						ObjectifyDatastoreDAO.getInstance().getActiveListings(notification.getUserRecentDomain(), listingsProps));
			}
			ListingTileVO listings[] = new ListingTileVO[3];
			listings[0] = listingsTiles.get(0);
			if (listingsTiles.size() == 1) {
				listings[1] = listingsTiles.get(0);
				listings[2] = listingsTiles.get(0);				
			} else if (listingsTiles.size() == 2) {
				listings[1] = listingsTiles.get(1);
				listings[2] = listingsTiles.get(1);				
			} else {
				listings[1] = listingsTiles.get(1);
				listings[2] = listingsTiles.get(2);				
			}
			Map<String, String> props = prepare3ListingNotificationProps(notification, listings);
			String htmlTemplate = FileUtils.readFileToString(new File(TEMPLATE_3LISTING_NOTIFICATION), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			send(notification.getUserEmail(), subject, htmlBody, null);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending 3 listing notification email", e);
			return false;
		}
	}

	public Map<String, String> prepare3ListingNotificationProps(NotificationVO notification, ListingTileVO listings[]) {
		Map<String, String> props = new HashMap<String, String>();

		props.put(NOT_DISPLAYING_PROPERLY, getText(notification, "email_not_displaying_properly"));
		props.put(VIEW_ON_INWESTUJ_W_FIRMY, getText(notification, "email_view_on_portal", getDomainUrl(notification, "/notifications-page.html")));
		
		props.put(NOTIFICATION_TITLE, notification.getTitle());
		props.put(NOTIFICATION_TITLE_ESCAPED, escape(notification.getTitle()));
		props.put(TEXT_NO_LINK, escape(notification.getText1()) + " <i>" + escape(notification.getText2()) + "</i>");
		props.put(NOTIFICATION_TEXT_3, escape(notification.getText3()));

		props.put(LINK_TO_HEADER_IMAGE, NOTIFICATION_IMAGE_URL);
		props.put(NOTIFICATION_FEATURED_PROJECTS, getText(notification, "email_featured_projects"));

		props.put(LINK_TO_LISTING_1, getDomainUrl(notification, "/company-page.html?id=" + listings[0].getId()));
		props.put(LINK_TO_LISTING_1_LOGO, getDomainUrl(notification, "/listing/logo?id=" + listings[0].getId()));
		props.put(LISTING_1_NAME, escape(listings[0].getName()));
		props.put(LISTING_1_CATEGORY_LOCATION, listings[0].getCategory() + " <br/>" + listings[0].getBriefAddress());
		props.put(LISTING_1_MANTRA, escape(listings[0].getMantra()));

		props.put(LINK_TO_LISTING_2, getDomainUrl(notification, "/company-page.html?id=" + listings[1].getId()));
		props.put(LINK_TO_LISTING_2_LOGO, getDomainUrl(notification, "/listing/logo?id=" + listings[1].getId()));
		props.put(LISTING_2_NAME, escape(listings[1].getName()));
		props.put(LISTING_2_CATEGORY_LOCATION, listings[1].getCategory() + " <br/>" + listings[1].getBriefAddress());
		props.put(LISTING_2_MANTRA, escape(listings[1].getMantra()));

		props.put(LINK_TO_LISTING_3, getDomainUrl(notification, "/company-page.html?id=" + listings[2].getId()));
		props.put(LINK_TO_LISTING_3_LOGO, getDomainUrl(notification, "/listing/logo?id=" + listings[2].getId()));
		props.put(LISTING_3_NAME, escape(listings[2].getName()));
		props.put(LISTING_3_CATEGORY_LOCATION, listings[2].getCategory() + " <br/>" + listings[2].getBriefAddress());
		props.put(LISTING_3_MANTRA, escape(listings[2].getMantra()));

		props.put(COPYRIGHT_TEXT, getText(notification, "email_copyright", String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS_TEXT, getText(notification, "email_mailing_address_text"));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS, getText(notification, "email_mailing_address"));
		props.put(NOTIFICATION_UPDATE_PROFILE_PAGE, getText(notification, "email_profil_page", getDomainUrl(notification, "/edit-profile-page.html")));
		return props;
	}

	private boolean sendAdminNotification(NotificationVO notification) {
		try {
			Map<String, String> props = prepareAdminNotificationProps(notification);
			String htmlTemplate = FileUtils.readFileToString(new File(TEMPLATE_AUTHORIZATION), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			sendAdmin(ADMIN_EMAIL, notification.getUserEmail(), subject, htmlBody);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending notification email", e);
			return false;
		}
	}

	public Map<String, String> prepareAdminNotificationProps(NotificationVO notification) {
		Map<String, String> props = new HashMap<String, String>();

		props.put(NOT_DISPLAYING_PROPERLY, getText(notification, "email_not_displaying_properly"));
		props.put(VIEW_ON_INWESTUJ_W_FIRMY, getText(notification, "email_view_on_portal", getDomainUrl(notification, "/notifications-page.html")));
		
		props.put(NOTIFICATION_TITLE, notification.getTitle());
		props.put(NOTIFICATION_TITLE_ESCAPED, escape(notification.getTitle()));
		props.put(TEXT_NO_LINK, "");
		props.put(NOTIFICATION_TEXT_1, escape(notification.getText1()));
		props.put(NOTIFICATION_TEXT_2, escape(notification.getText2()));
		props.put(NOTIFICATION_TEXT_3, escape(notification.getText3()));
		props.put(NOTIFICATION_LINK_HREF, notification.getLink());
		props.put(NOTIFICATION_LINK_TEXT, notification.getListingOwner());
		
		props.put(COPYRIGHT_TEXT, getText(notification, "email_copyright", String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS_TEXT, getText(notification, "email_mailing_address_text"));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS, getText(notification, "email_mailing_address"));
		props.put(NOTIFICATION_UPDATE_PROFILE_PAGE, getText(notification, "email_profil_page", getDomainUrl(notification, "/edit-profile-page.html")));
		return props;
	}

	public boolean sendEmailVerification(SBUser userByTwitter) {
		try {
			// send verification email with link /user/confirm_user_email?id=<twitter_id>&token=<token>
			Map<String, String> props = prepareEmailVerificationProps(userByTwitter);
			String htmlTemplate = FileUtils.readFileToString(new File(TEMPLATE_AUTHORIZATION), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			send(userByTwitter.twitterEmail, subject, htmlBody, null);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending email verification email", e);
			return false;
		}
	}

	public Map<String, String> prepareEmailVerificationProps(SBUser userByTwitter) {
		Map<String, String> props = new HashMap<String, String>();

		String activationUrl = getDomainUrl(userByTwitter, "/user/confirm_user_email?id="
				+ userByTwitter.twitterId + "&token=" +userByTwitter.activationCode);

		props.put(NOT_DISPLAYING_PROPERLY, "&nbsp;");
		props.put(VIEW_ON_INWESTUJ_W_FIRMY, "&nbsp;");
		props.put(NOTIFICATION_TITLE, getText(userByTwitter, "email_address_verification_title"));
		props.put(NOTIFICATION_TITLE_ESCAPED, getText(userByTwitter, "email_address_verification_title"));
		props.put(TEXT_NO_LINK, "&nbsp;");
		props.put(NOTIFICATION_TEXT_1, getText(userByTwitter, "email_address_verification_by_click_on", activationUrl));
		props.put(NOTIFICATION_TEXT_2, getText(userByTwitter, "email_address_verification_you_confirm", userByTwitter.twitterEmail));
		props.put(NOTIFICATION_TEXT_3, getText(userByTwitter, "email_address_verification_info") + " " + escape(activationUrl));
		props.put(COPYRIGHT_TEXT, getText(userByTwitter, "email_copyright", String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS_TEXT, getText(userByTwitter, "email_mailing_address_text"));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS, getText(userByTwitter, "email_mailing_address"));
		props.put(NOTIFICATION_UPDATE_PROFILE_PAGE, getText(userByTwitter, "email_profil_page", getDomainUrl(userByTwitter, "/edit-profile-page.html")));
		return props;
	}

	public boolean sendAccountActivation(SBUser user) {
		try {
			String activationUrl = getDomainUrl(user, "/user/activate.html?code=" + user.activationCode);

			Map<String, String> props = prepareAccountActivationProps(user, activationUrl);
			String htmlTemplate = FileUtils.readFileToString(new File(TEMPLATE_AUTHORIZATION), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String textBody = getText(user, "email_account_activation_message", activationUrl, escape(user.email), escape(activationUrl));
			String subject = props.get(NOTIFICATION_TITLE);
			send(user.email, subject, htmlBody, textBody);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending account activation email", e);
			return false;
		}
	}

	public Map<String, String> prepareAccountActivationProps(SBUser user, String accessUrl) {
		Map<String, String> props = new HashMap<String, String>();

		props.put(NOT_DISPLAYING_PROPERLY, "&nbsp;");
		props.put(VIEW_ON_INWESTUJ_W_FIRMY, "&nbsp;");
		props.put(NOTIFICATION_TITLE, getText(user, "email_account_activation_title"));
		props.put(NOTIFICATION_TITLE_ESCAPED, getText(user, "email_account_activation_title_escaped"));
		props.put(TEXT_NO_LINK, "&nbsp;");
		props.put(NOTIFICATION_TEXT_1, getText(user, "email_account_activation_message", accessUrl, escape(user.email)));
		props.put(NOTIFICATION_TEXT_2, "&nbsp;");
		log.info("escaped access url: " + escape(accessUrl));
		log.info("NOTIFICATION_TEXT_3 = " + getText(user, "email_account_activation_info") + " " + escape(accessUrl));
		props.put(NOTIFICATION_TEXT_3, getText(user, "email_account_activation_info") + " " + escape(accessUrl));
		props.put(COPYRIGHT_TEXT, getText(user, "email_copyright", String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS_TEXT, getText(user, "email_mailing_address_text"));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS, getText(user, "email_mailing_address"));
		props.put(NOTIFICATION_UPDATE_PROFILE_PAGE, getText(user, "email_profil_page", getDomainUrl(user, "/edit-profile-page.html")));
		return props;
	}

	public boolean sendPasswordResetEmail(SBUser user) {
		try {
			Map<String, String> props = preparePasswordResetProps(user);
			String htmlTemplate = FileUtils.readFileToString(new File(TEMPLATE_AUTHORIZATION), "UTF-8");
			String htmlBody = applyProperties(htmlTemplate, props);
			String subject = props.get(NOTIFICATION_TITLE);
			send(user.email, subject, htmlBody, null);
			return true;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error sending password reset email", e);
			return false;
		}
	}

	public Map<String, String> preparePasswordResetProps(SBUser user) {
		Map<String, String> props = new HashMap<String, String>();

		String resetUrl = getDomainUrl(user, "/profile-page.html?code=" + user.activationCode);

		props.put(NOT_DISPLAYING_PROPERLY, "&nbsp;");
		props.put(VIEW_ON_INWESTUJ_W_FIRMY, "&nbsp;");
		props.put(NOTIFICATION_TITLE, getText(user, "email_password_reset_title"));
		props.put(NOTIFICATION_TITLE_ESCAPED, getText(user, "email_password_reset_title"));
		props.put(TEXT_NO_LINK, "&nbsp;");
		props.put(NOTIFICATION_TEXT_1, getText(user, "email_password_reset_by_click_on", resetUrl, user.email));
		props.put(NOTIFICATION_TEXT_2, "");
		props.put(NOTIFICATION_TEXT_3, getText(user, "email_password_reset_info") + " " + escape(resetUrl));
		props.put(COPYRIGHT_TEXT, getText(user, "email_copyright", Calendar.getInstance().get(Calendar.YEAR)));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS_TEXT, getText(user, "email_mailing_address_text"));
		props.put(NOTIFICATION_MAILING_LIST_ADDRESS, getText(user, "email_mailing_address"));
		props.put(NOTIFICATION_UPDATE_PROFILE_PAGE, getText(user, "email_profil_page", getDomainUrl(user, "/edit-profile-page.html")));
		return props;
	}

	public String applyProperties(String htmlTemplate, Map<String, String> props) {
		for (Map.Entry<String, String> entry : props.entrySet()) {
			htmlTemplate = StringUtils.replace(htmlTemplate, entry.getKey(), entry.getValue());
		}
		return htmlTemplate;
	}
	
	private String escape(String text) {
		//text = text.replaceAll("@", "<span>{AT}</span>");
		text = text.replaceAll("\\.", "<span>.</span>");
		text = text.replaceAll("http", "<span>http</span>");
		text = text.replaceAll("https", "<span>https</span>");
		return text;
	}
	
	private String getDomainUrl(NotificationVO notif, String path) {
		String domain = notif.getUserRecentDomain();
		if (StringUtils.isBlank(domain)) {
			domain = "www.inwestujwfirmy.pl";
		}
		if (!StringUtils.endsWith(domain, ".inwestujwfirmy.pl")) {
			domain += ".inwestujwfirmy.pl";
		}
		return "https://" + domain + path;
	}
	
	private String getDomainUrl(SBUser user, String path) {
		String domain = user.recentDomain;
		if (StringUtils.isBlank(domain)) {
			domain = "www.inwestujwfirmy.pl";
		}
		if (!StringUtils.endsWith(domain, ".inwestujwfirmy.pl")) {
			domain += ".inwestujwfirmy.pl";
		}
		return "https://" + domain + path;
	}
	
	private String getText(NotificationVO notification, String key) {
		return Translations.getText(notification.getUserRecentLang(), key);
	}

	private String getText(NotificationVO notification, String key, Object... params) {
		return Translations.getText(notification.getUserRecentLang(), key, params);
	}

	private String getText(SBUser user, String key) {
		return Translations.getText(user.recentLang, key);
	}

	private String getText(SBUser user, String key, Object... params) {
		return Translations.getText(user.recentLang, key, params);
	}

}
