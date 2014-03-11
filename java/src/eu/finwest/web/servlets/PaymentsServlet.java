package eu.finwest.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.PricePoint;
import eu.finwest.util.TwitterHelper;
import eu.finwest.vo.ListingVO;
import eu.finwest.vo.PricePointVO;
import eu.finwest.vo.UserListingsVO;
import eu.finwest.vo.UserVO;
import eu.finwest.web.FrontController;
import eu.finwest.web.ListingFacade;
import eu.finwest.web.MemCacheFacade;
import eu.finwest.web.ServiceFacade;
import eu.finwest.web.UserMgmtFacade;

/**
 *
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@SuppressWarnings("serial")
public class PaymentsServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(PaymentsServlet.class.getName());

	static {
		new FrontController ();
	}

	DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
	DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyyMMddHHmm");

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setCharacterEncoding("UTF-8");
		new FrontController ().setLanguageAndCampaign(req, resp);
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		twitter4j.User twitterUser = TwitterHelper.getTwitterUser(req);

		resp.setContentType("text/html");

		ServiceFacade service = ServiceFacade.instance();
		ObjectifyDatastoreDAO datastore = ServiceFacade.instance().getDAO();

		PrintWriter out = resp.getWriter();
		try {
			out.println("<html><head><title>Inwestuj w Firmy - payment test page</title>"
					+ "<meta charset=\"utf-8\">"
					+ "<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-type\">"
					+ "<script src=\"/md5.js\"></script>"
					+ "</head><body>");
            
			UserVO currentUser = null;
			if (user != null) {
				out.println("<p>Hello, " + user.getNickname() + " ..................................");
				out.println("<a href=\"" + userService.createLogoutURL("/hello") + "\">logout</a></p>");

				currentUser = UserMgmtFacade.instance().getLoggedInUser(user);
				if (currentUser == null) {
					currentUser = UserMgmtFacade.instance().createUser(user);
				}
				currentUser.setAdmin(userService.isUserAdmin());
			}
			
			if (!(currentUser != null && currentUser.isAdmin())) {
				out.println("<p>You need to be logged in as admin to see this page</p>");
				return;
			}

			out.println("<a href=\"/hello/\">Hello page</a><br/>");
			out.println("<a href=\"/setup/\">Setup page</a></p>");

			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Pricepoints:</p>");
			
			out.println("<form method=\"POST\" action=\"/system/reset_pricepoints.html\">"
					+ "<input type=\"submit\" value=\"Reset all pricepoints\"/></form>");
			
			Map<PricePoint.Group, List<PricePoint>> pricePoints = MemCacheFacade.instance().getAllPricePoints();
			printPricePoints(out, pricePoints);

			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Payment system examples:</p>");
			
			ObjectMapper mapper = new ObjectMapper();
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">User listings</p>");
			out.println("<table border=\"1\"><tr><td>Generated button</td><td>Pricepoint JSON</td></tr>");
			UserListingsVO listings = ListingFacade.instance().getDiscoverUserListings(currentUser);
			if (listings.getEditedListing() != null) {
				out.println("<tr><td colspan='2'>Edited listing</td></tr>");
				List<PricePointVO> list = UserMgmtFacade.instance().getPricePoints(currentUser, listings.getEditedListing());
				for (PricePointVO pp : list) {
					printPricePointAction(out, mapper, pp);
				}
			} else {
				out.println("<tr><td colspan='2'>No listing(s)</td></tr>");
			}

			if (listings.getActiveListings() != null && listings.getActiveListings().size() > 0) {
				out.println("<tr><td colspan='2'>Active listing(s)</td></tr>");
				ListingVO listing = ListingFacade.instance().getListing(currentUser, listings.getActiveListings().get(0).getId()).getListing();
				List<PricePointVO> list = UserMgmtFacade.instance().getPricePoints(currentUser, listing);
				for (PricePointVO pp : list) {
					printPricePointAction(out, mapper, pp);
				}
			} else {
				out.println("<tr><td colspan='2'>No listing(s)</td></tr>");
			}
			out.println("</table>");
			
			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">User pricepoints</p>");
			out.println("<table border=\"1\"><tr><td>Generated button</td><td>Pricepoint JSON</td></tr>");
			List<PricePointVO> list = UserMgmtFacade.instance().getPricePoints(currentUser, MemCacheFacade.instance().getUserCampaigns(currentUser));
			for (PricePointVO pp : list) {
				printPricePointAction(out, mapper, pp);
			}
			out.println("</table>");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while creating payment test page", e);
		} finally {
			out.println("</body></html>");
		}
	}

	private void printPricePointAction(PrintWriter out, ObjectMapper mapper,
			PricePointVO pp) throws IOException, JsonGenerationException,
			JsonMappingException {
		out.println("<tr><td>");
		out.println("Description: " + pp.getDescription() + "</br>");
		out.println("Amount: " + (pp.getValueDisplayed() == null ? "FREE" : pp.getValueDisplayed()) + "</br>");
		out.println("<form action=\"" + pp.getActionUrl() + "\" method=\"post\" accept-charset=\"utf-8\">"); 
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"id\" value=\"" + pp.getSellerId() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"kwota\" value=\"" + pp.getAmount() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"opis\" value=\"" + pp.getTransactionDescClient() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"opis_sprzed\" value=\"" + pp.getTransactionDescSeller() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"crc\" value=\"" + pp.getCrc() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"pow_url\" value=\"" + pp.getReturnUrlSuccess() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"pow_url_blad\" value=\"" + pp.getReturnUrlFailure() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"email\" value=\"" + pp.getUserEmail() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"nazwisko\" value=\"" + pp.getUserName() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"telefon\" value=\"" + pp.getUserPhone() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"jezyk\" value=\"" + pp.getPaymentLanguage() + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"md5sum\" value=\"" + pp.getMd5sum() + "\">");
		out.println("<input type=\"submit\" autocomplete=\"off\" name=\"submit\" value=\"" + pp.getButtonText() + "\">");
		out.println("</form>");
		out.println("</td><td>");
		out.println("<form>"); 
		out.println("<textarea autocomplete=\"off\" rows=\"10\" cols=\"100\">" + mapper.writeValueAsString(pp) + "</textarea>");
		out.println("</form></td></tr>");
	}

	private void printPricePoints(PrintWriter out, Map<PricePoint.Group, List<PricePoint>> pricePoints) {
		int numColumns = 0;
		for (List<PricePoint> list : pricePoints.values()) {
			numColumns = list.size() > numColumns ? list.size() : numColumns;
		}
		
		out.println("<table border='1'><tr>");
		for (int c = 0; c <= numColumns; c++) {
			if (c == 0) {
				out.println("<td>Group</td>");
			} else {
				out.println("<td>" + c + "</td>");
			}
		}
		out.println("</tr>");
		for (List<PricePoint> list : pricePoints.values()) {
			out.println("<tr>");
			for (int c = 0; c <= numColumns; c++) {
				if (c == 0) {
					out.println("<td>" + list.get(0).group + "</td>");
				} else if (c <= list.size()) {
					out.println("<td>");
					printPricePoint(out, list.get(c - 1));
					out.println("</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
			}
			out.println("</tr>");
		}
		out.println("</table>");
	}
	
	private void printPricePoint(PrintWriter out, PricePoint pp) {
		out.println("<form action=\"/system/store_pricepoint.json\" method=\"post\" accept-charset=\"utf-8\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"name\" value=\"" + pp.name + "\">");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"type\" value=\"" + pp.type + "\"><br/>");
		out.println("<input type=\"hidden\" autocomplete=\"off\" name=\"group\" value=\"" + pp.group + "\"><br/>");
		out.println(pp.name + "<br/>" + pp.type + "<br/>");
		out.println("<label for=\"amount\">Amount (" + pp.currency + "): </label>");
		out.println("<input type=\"text\" autocomplete=\"off\" name=\"amount\" value=\"" + amountToString(pp.amount) + "\">");
		out.println("<label for=\"descriptionPl\">PL: </label>");
		out.println("<textarea name=\"descriptionPl\" autocomplete=\"off\" rows=\"4\" cols=\"40\">" + pp.descriptionPl + "</textarea>");
		out.println("<label for=\"descriptionEn\">EN: </label>");
		out.println("<textarea name=\"descriptionEn\" autocomplete=\"off\" rows=\"4\" cols=\"40\">" + pp.descriptionEn + "</textarea>");
		out.println("<label for=\"amount\">Success url: </label>");
		out.println("<input type=\"text\" autocomplete=\"off\" name=\"successUrl\" value=\"" + pp.successUrl + "\">");
		out.println("<input type=\"submit\" name=\"submit\" value=\"Update " + pp.name + "\"/>");
		out.println("</form>");
	}
	
	private String amountToString(int amount) {
		return "" + (amount / 100) + "." + (amount % 100);
	}

}
