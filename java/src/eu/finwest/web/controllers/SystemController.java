package eu.finwest.web.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.util.AuthenticationException;

import eu.finwest.dao.DatastoreMigration;
import eu.finwest.dao.MockDataBuilder;
import eu.finwest.datamodel.PricePoint;
import eu.finwest.datamodel.SystemProperty;
import eu.finwest.datamodel.Transaction;
import eu.finwest.datamodel.Transaction.Status;
import eu.finwest.vo.BaseVO;
import eu.finwest.vo.SystemPropertyVO;
import eu.finwest.vo.UserVO;
import eu.finwest.web.HttpHeaders;
import eu.finwest.web.HttpHeadersImpl;
import eu.finwest.web.MemCacheFacade;
import eu.finwest.web.ModelDrivenController;
import eu.finwest.web.ServiceFacade;
import eu.finwest.web.UserMgmtFacade;

/**
 *
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class SystemController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(SystemController.class.getName());

	private Object model;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			if("set-property".equalsIgnoreCase(getCommand(1))) {
				return setProperty(request);
			} else if("clear-datastore".equalsIgnoreCase(getCommand(1))) {
				return clearDatastore(request);
			} else if("print-datastore".equalsIgnoreCase(getCommand(1))) {
				return printDatastoreContents(request);
            } else if("create-mock-datastore".equalsIgnoreCase(getCommand(1))) {
                return createMockDatastore(request);
            } else if("delete-angellist-cache".equalsIgnoreCase(getCommand(1))) {
                return deleteAngelListCache(request);
            } else if("delete-startuply-cache".equalsIgnoreCase(getCommand(1))) {
                return deleteStartuplyCache(request);
            } else if("delete-geocode-cache".equalsIgnoreCase(getCommand(1))) {
                return deleteGeocodeCache(request);
            } else if("import-angellist-data".equalsIgnoreCase(getCommand(1))) {
                return importAngelListData(request);
            } else if("import-startuply-data".equalsIgnoreCase(getCommand(1))) {
                return importStartuplyData(request);
			} else if("export-datastore".equalsIgnoreCase(getCommand(1))) {
				return exportDatastore(request);
			} else if("migrate20140225_to_current".equalsIgnoreCase(getCommand(1))) {
				return migrate20140225_to_current(request);
			} else if("migrate_fix_recent_domain".equalsIgnoreCase(getCommand(1))) {
				return migrateFixRecentDomain(request);
			} else if("migrate_reindex_listings".equalsIgnoreCase(getCommand(1))) {
				return migrateReindexListings(request);
			} else if("reset_pricepoints".equalsIgnoreCase(getCommand(1))) {
				return resetPricePoints(request);
			} else if("associate_mock_images".equalsIgnoreCase(getCommand(1))) {
				return associateMockImages(request);
			} else if("update_avatars_dragon_lister".equalsIgnoreCase(getCommand(1))) {
				return updateAvatarsDragonLister(request);
			} else if("transferuj_pl_notification".equalsIgnoreCase(getCommand(1))) {
				return transferujPlConfirmation(request);
			} else if("transaction_confirmation".equalsIgnoreCase(getCommand(1))) {
				return transactionConfirmation(request);
			} else if("store_pricepoint".equalsIgnoreCase(getCommand(1))) {
				return storePricepoint(request);
			} else if("validate_sms_code".equalsIgnoreCase(getCommand(1))) {
				return validateSmsCode(request);
			} else if("download_sms_payments".equalsIgnoreCase(getCommand(1))) {
				return downloadSmsPayments(request);
			} else if("get_sms_payments".equalsIgnoreCase(getCommand(1))) {
				return getSmsPayments(request);
			}
		}
		return null;
	}

	private HttpHeaders validateSmsCode(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("transferuj_pl_notification");
		
		String sellerId = getCommandOrParameter(request, 2, "p24_id_sprzedawcy");
		String value = getCommandOrParameter(request, 2, "p24_kwota");
		String code = getCommandOrParameter(request, 2, "p24_kod");
		int returnCode = ServiceFacade.instance().validateSmsCode(getLoggedInUser(), sellerId, value, code);
		if (returnCode == 0) {
			headers.setRedirectUrl(BaseVO.getServiceLocation() + "/sms-page.html?success=true");
		} else {
			headers.setRedirectUrl(BaseVO.getServiceLocation() + "/sms-page.html?error=" + returnCode);
		}
		
		return headers;
	}

	private HttpHeaders downloadSmsPayments(HttpServletRequest request) {
		model = ServiceFacade.instance().downloadSmsPayments(getLoggedInUser());
		return new HttpHeadersImpl("download_sms_payments").disableCaching();
	}

	private HttpHeaders getSmsPayments(HttpServletRequest request) {
		model = ServiceFacade.instance().getSmsPayments(getLoggedInUser());
		return new HttpHeadersImpl("get_sms_payments").disableCaching();
	}

	@SuppressWarnings("unchecked")
	private HttpHeaders transferujPlConfirmation(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("transferuj_pl_notification");

		try {
			String remoteHost = request.getRemoteHost();
			log.info("We got transferuj.pl confirmation from: " + remoteHost);
			if (!StringUtils.equals("195.149.229.109", remoteHost)) {
				log.warning("Transferuj.pl confirmation received from " + remoteHost + ", supposed to be 195.149.229.109");
			}
			
			Map<String, String[]> params = (Map<String, String[]>)request.getParameterMap();
			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				StringBuffer buf = new StringBuffer();
				for (String value : entry.getValue()) {
					buf.append(" ").append(value);
				}
				log.info("Parameter " + entry.getKey() + " : " + buf.toString());
			}
			Transaction trans = new Transaction();
			trans.seller_id = params.get("id") != null ? params.get("id")[0] : null;
			String status = params.get("tr_status") != null ? params.get("tr_status")[0] : null;
			trans.status = StringUtils.equals("TRUE", status) ? Status.OK : Status.ERROR;
			trans.transactionId = params.get("tr_id") != null ? params.get("tr_id")[0] : null;
			trans.amountStr = params.get("tr_amount") != null ? params.get("tr_amount")[0] : null;
			trans.paidStr = params.get("tr_paid") != null ? params.get("tr_paid")[0] : null;
			trans.error = params.get("tr_error") != null ? params.get("tr_error")[0] : null;
			trans.date = params.get("tr_date'") != null ? params.get("tr_date'")[0] : null;
			trans.description = params.get("tr_desc") != null ? params.get("tr_desc")[0] : null;
			trans.crc = params.get("tr_crc") != null ? params.get("tr_crc")[0] : null;
			trans.email = params.get("tr_email") != null ? params.get("tr_email")[0] : null;
			trans.md5sum = params.get("md5sum") != null ? params.get("md5sum")[0] : null;
						
			String md5string = trans.seller_id + trans.transactionId + trans.amountStr + trans.crc
					+ MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_SECURITY_CODE);
			String thedigest = DigestUtils.md5Hex(md5string);
			if (!StringUtils.equals(trans.md5sum, thedigest)) {
				// we'll return true even in case of wrong md5sum to prevent hacker attacks
				model = "TRUE";
				log.warning("MD5 not valid! string: " + md5string + " -> MD5 -> " + thedigest);
			} else {
				log.warning("Received valid transaction confirmation for crc: " + trans.crc);
				if (!(trans.status == Status.OK && StringUtils.equalsIgnoreCase("none", trans.error))) {
					log.info("Transaction (" + trans.transactionId + ") for crc " + trans.crc
							+ " failed but we're going to store it anyway.");
				}
				model = "TRUE";
				UserMgmtFacade.instance().storeTransaction(trans);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error parsing transferuj.pl notification!", e);
			model = "FALSE";
		}
		
		return headers;
	}

	@SuppressWarnings("unchecked")
	private HttpHeaders transactionConfirmation(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("transaction_confirmation");

		String successUrl = null;
		String failureUrl = null;
		try {
			Map<String, String[]> params = (Map<String, String[]>)request.getParameterMap();
			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				StringBuffer buf = new StringBuffer();
				for (String value : entry.getValue()) {
					buf.append(" ").append(value);
				}
				log.info("Parameter " + entry.getKey() + " : " + buf.toString());
			}
			Transaction trans = new Transaction();
			trans.seller_id = params.get("id") != null ? params.get("id")[0] : null;
			trans.status = Status.OK;
			trans.transactionId = "local_transaction";
			trans.amountStr = params.get("kwota") != null ? params.get("kwota")[0] : null;
			trans.paidStr = params.get("kwota") != null ? params.get("kwota")[0] : null;
			trans.error = "none";
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss");
			trans.date = fmt.print(new Date().getTime());
			trans.description = params.get("opis") != null ? params.get("opis")[0] : null;
			trans.crc = params.get("crc") != null ? params.get("crc")[0] : null;
			trans.email = params.get("email") != null ? params.get("email")[0] : null;
			trans.md5sum = params.get("md5sum") != null ? params.get("md5sum")[0] : null;
			
			successUrl = params.get("pow_url") != null ? params.get("pow_url")[0] : null;
			failureUrl = params.get("pow_url_blad") != null ? params.get("pow_url_blad")[0] : null;
			
			String md5string = trans.seller_id + trans.amountStr + trans.crc
					+ MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_SECURITY_CODE);
			String thedigest = DigestUtils.md5Hex(md5string);
			if (!StringUtils.equals(trans.md5sum, thedigest)) {
				model = "TRUE";
				log.warning("MD5 not valid! string: " + md5string + " -> MD5 -> " + thedigest);
			} else {
				log.warning("Received valid transaction confirmation for crc: " + trans.crc);
				model = "TRUE";
				UserMgmtFacade.instance().storeTransaction(trans);
			}
			headers.setRedirectUrl(successUrl != null ? successUrl : "?");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error parsing during transaction confirmation!", e);
			model = "FALSE";
			headers.setRedirectUrl(failureUrl != null ? failureUrl : "?");
		}
		
		return headers;
	}

	@SuppressWarnings("unchecked")
	private HttpHeaders storePricepoint(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("store_pricepoint");
		UserVO loggedInUser = getLoggedInUser();
		if (!(loggedInUser != null && loggedInUser.isAdmin())) {
			log.info("Only admins can change pricepoints");
			headers.setStatus(501);
			return headers;
		}
		
		try {
			PricePoint pp = new PricePoint();
			pp.name = getCommandOrParameter(request, 2, "name");
			String amountTxt = getCommandOrParameter(request, 3, "amount");
			amountTxt = amountTxt.replace(",", ".");
			double amountDbl = Double.parseDouble(amountTxt);
			pp.amount = (int)(amountDbl * 100);
			pp.descriptionPl = getCommandOrParameter(request, 4, "descriptionPl");
			pp.descriptionEn = getCommandOrParameter(request, 5, "descriptionEn");
			pp.buttonPl = getCommandOrParameter(request, 4, "buttonPl");
			pp.buttonEn = getCommandOrParameter(request, 5, "buttonEn");
			pp.freeButtonPl = getCommandOrParameter(request, 4, "freeButtonPl");
			pp.freeButtonEn = getCommandOrParameter(request, 5, "freeButtonEn");
			log.info("Updating pricepoint " + pp.name + " with amount: " + pp.amount + ", descriptionPl: " + pp.descriptionPl
					+ ", descriptionEn: " + pp.descriptionEn + ", buttonPl=" + pp.buttonPl + ", buttonEn=" + pp.buttonEn
					 + ", freeButtonPl=" + pp.freeButtonPl + ", freeButtonEn=" + pp.freeButtonEn);
			
			model = UserMgmtFacade.instance().storePricepoint(pp);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error handling store_pricepoint request!", e);
		}
		
		return headers;
	}

	private HttpHeaders associateMockImages(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("associate_mock_images");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			model = DatastoreMigration.associateImages();
		}
		return headers;
	}

	private HttpHeaders updateAvatarsDragonLister(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("update_avatars_dragon_lister");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			model = DatastoreMigration.updateAvatarsAndDragonListerFlag();
		}
		return headers;
	}

	private HttpHeaders migrate20140225_to_current(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("migrate20140225_to_current");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			model = DatastoreMigration.migrate20140225_to_current();
		}
		return headers;
	}

	private HttpHeaders migrateFixRecentDomain(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("migrate_fix_recent_domain");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			model = DatastoreMigration.fixRecentDomain();
		}
		return headers;
	}

	private HttpHeaders migrateReindexListings(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("migrate_reindex_listings");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			model = DatastoreMigration.reindexListings();
		}
		return headers;
	}

	private HttpHeaders resetPricePoints(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("reset_pricepoints");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			model = DatastoreMigration.resetPricePoints();
		}
		return headers;
	}

	private HttpHeaders setProperty(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("set-property");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			String name = request.getParameter("name");
			String value = request.getParameter("value");

			SystemPropertyVO property = new SystemPropertyVO();
			property.setName(name);
			property.setValue(value);
			model = ServiceFacade.instance().setSystemProperty(getLoggedInUser(), property);

			String name1 = request.getParameter("name.1");
			if (!StringUtils.isEmpty(name1)) {
				String value1 = request.getParameter("value.1");
				property = new SystemPropertyVO();
				property.setName(name1);
				property.setValue(value1);
				ServiceFacade.instance().setSystemProperty(getLoggedInUser(), property);

				if (SystemProperty.GOOGLEDOC_USER.equals(name) && SystemProperty.GOOGLEDOC_PASSWORD.equals(name1)) {
					DocsService client = new DocsService("www-inwestuj-w-firmy-v1");
					try {
						client.setUserCredentials(value, value1);
						model = "Google Doc user/password verified!";
					} catch (AuthenticationException e) {
						model = "Google Doc user/password verification error!";
						log.log(Level.SEVERE, "Error while logging to GoogleDoc!", e);
					}
				}
			}
		} else {
			headers.setStatus(500);
		}

		return headers;
	}

	private HttpHeaders clearDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("clear-datastore");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			String deletedObjects = new MockDataBuilder().clearDatastore(loggedInUser);
			model = deletedObjects;
		} else {
			headers.setStatus(500);
		}
		return headers;
	}

    private HttpHeaders deleteAngelListCache(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeadersImpl("delete");

        UserVO loggedInUser = getLoggedInUser();
        if (loggedInUser != null && loggedInUser.isAdmin()) {
            String fromId = request.getParameter("fromId");
            String toId = request.getParameter("toId");
            String deletedObjects = new MockDataBuilder().deleteAngelListCache(loggedInUser, fromId, toId);
            model = deletedObjects;
        } else {
            headers.setStatus(500);
        }
        return headers;
    }

    private HttpHeaders deleteStartuplyCache(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeadersImpl("delete");

        UserVO loggedInUser = getLoggedInUser();
        if (loggedInUser != null && loggedInUser.isAdmin()) {

            String deletedObjects = new MockDataBuilder().deleteStartuplyCache(loggedInUser);
            model = deletedObjects;
        } else {
            headers.setStatus(500);
        }
        return headers;
    }

    private HttpHeaders deleteGeocodeCache(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeadersImpl("delete");

        UserVO loggedInUser = getLoggedInUser();
        if (loggedInUser != null && loggedInUser.isAdmin()) {
            String deletedObjects = new MockDataBuilder().deleteGeocodeCache(loggedInUser);
            model = deletedObjects;
        } else {
            headers.setStatus(500);
        }
        return headers;
    }

    private HttpHeaders createMockDatastore(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeadersImpl("recreate-mock-datastore");

        UserVO loggedInUser = getLoggedInUser();
        if (loggedInUser != null && loggedInUser.isAdmin()) {
            String deletedObjects = new MockDataBuilder().clearDatastore(loggedInUser);
            model = deletedObjects + new MockDataBuilder().createMockDatastore(true, false);
        } else {
            headers.setStatus(500);
        }
        return headers;
    }

    private HttpHeaders importStartuplyData(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeadersImpl("import-startuply-data");

        UserVO loggedInUser = getLoggedInUser();
        String max = request.getParameter("max");
        if (loggedInUser != null && loggedInUser.isAdmin() && !StringUtils.isEmpty(max)) {
            model = new MockDataBuilder().importStartuplyData(0, Integer.valueOf(max));
        } else {
            headers.setStatus(500);
        }
        return headers;
    }

    private HttpHeaders importAngelListData(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeadersImpl("import-angellist-data");

        UserVO loggedInUser = getLoggedInUser();
        String fromId = request.getParameter("fromId");
        String toId = request.getParameter("toId");
        if (loggedInUser != null && loggedInUser.isAdmin() && !StringUtils.isEmpty(fromId) && !StringUtils.isEmpty(toId)) {
            model = new MockDataBuilder().importAngelListData(fromId, toId);
        } else {
            headers.setStatus(500);
        }
        return headers;
    }

	private HttpHeaders printDatastoreContents(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("print-datastore");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			String printedObjects = new MockDataBuilder().printDatastoreContents(loggedInUser);
			model = printedObjects;
		} else {
			headers.setStatus(500);
		}
		return headers;
	}

	private HttpHeaders exportDatastore(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("export-datastore");

		UserVO loggedInUser = getLoggedInUser();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			model = new MockDataBuilder().exportDatastoreContents(loggedInUser);

			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd_HHmm_ss");
			headers.addHeader("Content-Disposition", "attachment; filename=export" + fmt.print(new Date().getTime()) + ".json");
		} else {
			headers.setStatus(500);
		}

		return headers;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
