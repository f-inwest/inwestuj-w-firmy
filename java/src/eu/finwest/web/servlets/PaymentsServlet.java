package eu.finwest.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.PricePoint;
import eu.finwest.util.TwitterHelper;
import eu.finwest.vo.UserVO;
import eu.finwest.web.FrontController;
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
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		twitter4j.User twitterUser = TwitterHelper.getTwitterUser(req);

		resp.setContentType("text/html");

		ServiceFacade service = ServiceFacade.instance();
		ObjectifyDatastoreDAO datastore = ServiceFacade.instance().getDAO();

		PrintWriter out = resp.getWriter();
		try {
			out.println("<html><head><title>Inwestuj w Firmy - test page</title>"
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

			out.println("<p style=\"background: none repeat scroll 0% 0% rgb(187, 187, 187);\">Payment system:</p>");
			out.println("<form action=\"https://secure.transferuj.pl\" method=\"post\" accept-charset=\"utf-8\">"); 
			out.println("<input type=\"hidden\" name=\"id\" value=\"12330\">");
			out.println("<label for=\"kwota\">Value: </label>");
			out.println("<input type=\"text\" name=\"kwota\" value=\"10.00\"><br/>");
			out.println("<label for=\"opis\">Transaction description: </label>");
			out.println("<input type=\"text\" name=\"opis\" value=\"Aktywacja projektu\"><br/>");
			out.println("<label for=\"crc\">Additional text: </label>");
			out.println("<input type=\"text\" name=\"crc\" value=\"project [id will go here]\"><br/>");
			out.println("<label for=\"opis_sprzed\">Transaction description for seller (optional): </label>");
			out.println("<input type=\"text\" name=\"opis_sprzed\" value=\"Test transaction\"><br/>");
			out.println("<label for=\"pow_url\">Return url for success: </label>");
			out.println("<input type=\"text\" name=\"pow_url\" value=\"http://www.inwestujwfirmy.pl\"><br/>");
			out.println("<label for=\"pow_url_blad\">Return url for failure: </label>");
			out.println("<input type=\"text\" name=\"pow_url_blad\" value=\"http://www.inwestujwfirmy.pl\"><br/>");
			out.println("<label for=\"email\">Customer email address (optional): </label>");
			out.println("<input type=\"text\" name=\"email\" value=\"grzegorz.nittner@gmail.com\"><br/>");
			out.println("<label for=\"nazwisko\">Name of the customer (optional): </label>");
			out.println("<input type=\"text\" name=\"nazwisko\" value=\"Grzegorz Nittner\"><br/>");
			out.println("<label for=\"telefon\">Customer phone number (optional): </label>");
			out.println("<input type=\"text\" name=\"telefon\" value=\"515261xxx\"><br/>");
			out.println("<label for=\"jezyk\">Language for payment system (pl, end or de): </label>");
			out.println("<input type=\"text\" name=\"jezyk\" value=\"pl\"><br/>");
			out.println("<input type=\"hidden\" name=\"md5sum\" value=\"default_md5\">");
			out.println("<input type=\"submit\" name=\"submit\" value=\"Submit payment\""
					+ " onclick=\"var txt=this.form.elements['id'].value"
					+ "+this.form.elements['kwota'].value"
					+ "+this.form.elements['crc'].value+'718N3Xa1b9qk1QG4';"
					+ "this.form.elements['md5sum'].value=CryptoJS.MD5(txt);\">");
			out.println("</form>");
			
			out.println("Action below is called by payment system, in development environment we'll have to emulate that call "
					+ "to confirm the payment. Use the same 'Additional data' value as for payment since we'll identify "
					+ "transaction by this field.<br/>");
			out.println("<form action=\"/system/transferuj_pl_notification.txt\" method=\"post\" accept-charset=\"utf-8\">");
			out.println("<input type=\"hidden\" name=\"id\" value=\"12330\">");
			out.println("<label for=\"kwota\">Transaction status, TRUE or FALSE (failure): </label>");
			out.println("<input type=\"text\" name=\"tr_status\" value=\"TRUE\"><br/>");
			out.println("<label for=\"tr_id\">Transaction id, it's given by payment system: </label>");
			out.println("<input type=\"text\" name=\"tr_id\" value=\"Opis transakcji\"><br/>");
			out.println("<label for=\"tr_amount\">Transaction amount: </label>");
			out.println("<input type=\"text\" name=\"tr_amount\" value=\"10.00\"><br/>");
			out.println("<label for=\"tr_paid\">Amount charged (might be different): </label>");
			out.println("<input type=\"text\" name=\"tr_paid\" value=\"10.00\"><br/>"); 
			out.println("<label for=\"tr_error\">Error message ('none' if no error): </label>");
			out.println("<input type=\"text\" name=\"tr_error\" value=\"none\"><br/>");
			out.println("<label for=\"tr_date\">Transaction date: </label>");
			out.println("<input type=\"text\" name=\"tr_date\" value=\"2014-02-24\"><br/>");
			out.println("<label for=\"tr_desc\">Transaction description: </label>");
			out.println("<input type=\"text\" name=\"email\" value=\"Description of transaction\"><br/>");
			out.println("<label for=\"tr_crc\">Additional data: </label>");
			out.println("<input type=\"text\" name=\"tr_crc\" value=\"project [id will go here]\"><br/>");
			out.println("<label for=\"tr_email\">Customer email: </label>");
			out.println("<input type=\"text\" name=\"tr_email\" value=\"grzegorz.nittner@gmail.com\"><br/>");
			out.println("<input type=\"hidden\" name=\"md5sum\" value=\"1234567890\">");
			out.println("<input type=\"submit\" name=\"submit\" value=\"Submit transaction confirmation\""
					+ " onclick=\"var txt=this.form.elements['id'].value"
					+ "+this.form.elements['tr_id'].value+this.form.elements['tr_amount'].value"
					+ "+this.form.elements['tr_crc'].value+'718N3Xa1b9qk1QG4';"
					+ "this.form.elements['md5sum'].value=CryptoJS.MD5(txt);\">");
			out.println("</form>");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.println("</body></html>");
		}
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
		out.println("<input type=\"submit\" name=\"submit\" value=\"Update " + pp.name + "\"/>");
		out.println("</form>");
	}
	
	private String amountToString(int amount) {
		return "" + (amount / 100) + "." + (amount % 100);
	}

}
