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
import eu.finwest.datamodel.SystemProperty;
import eu.finwest.datamodel.Transaction;
import eu.finwest.datamodel.Transaction.Status;
import eu.finwest.vo.SystemPropertyVO;
import eu.finwest.vo.UserVO;
import eu.finwest.web.HttpHeaders;
import eu.finwest.web.HttpHeadersImpl;
import eu.finwest.web.ModelDrivenController;
import eu.finwest.web.ServiceFacade;

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
			} else if("associate_mock_images".equalsIgnoreCase(getCommand(1))) {
				return associateMockImages(request);
			} else if("update_avatars_dragon_lister".equalsIgnoreCase(getCommand(1))) {
				return updateAvatarsDragonLister(request);
			} else if("transferuj_pl_notification".equalsIgnoreCase(getCommand(1))) {
				return transferujPlNotification(request);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private HttpHeaders transferujPlNotification(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("transferuj_pl_notification");

		try {
			String remoteHost = request.getRemoteHost();
			log.info("We got transferuj.pl confirmation from: " + remoteHost);
			if (!StringUtils.equals("195.149.229.109,", remoteHost)) {
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
			
			if (!(trans.status == Status.OK && StringUtils.equalsIgnoreCase("none", trans.error))) {
				log.info("Transaction " + trans.transactionId + " failed.");
			}
			
			String md5string = trans.seller_id + trans.transactionId + trans.amountStr + trans.crc + "718N3Xa1b9qk1QG4";
			String thedigest = DigestUtils.md5Hex(md5string);
			if (!StringUtils.equals(trans.md5sum, thedigest)) {
				log.warning("MD5 not valid! string: " + md5string + " -> MD5 -> " + thedigest);
			}
			model = "TRUE";
			ServiceFacade.instance().storeTransaction(trans);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error parsing transferuj.pl notification!", e);
			model = "FALSE";
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
