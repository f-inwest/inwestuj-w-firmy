package eu.finwest.web.controllers;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import eu.finwest.web.HttpHeaders;
import eu.finwest.web.HttpHeadersImpl;
import eu.finwest.web.ListingFacade;
import eu.finwest.web.ModelDrivenController;

/**
 *
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class CronTaskController extends ModelDrivenController {
	private static final Logger log = Logger.getLogger(CronTaskController.class.getName());
	private Object model;

	@Override
	protected HttpHeaders executeAction(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		if (!("true".equalsIgnoreCase(request.getHeader("X-AppEngine-Cron")) || getLoggedInUser().isAdmin())) {
			log.warning("Cron actions can be only executed by AppEngine's Cron or F-Inwest admins");
			return null;
		}

        if("update-aggregate-stats".equalsIgnoreCase(getCommand(1))) {
            return updateAggregateStats(request);
        } else if("update-listing-stats".equalsIgnoreCase(getCommand(1))) {
            return updateListingStats(request);
        }

		return null;
	}

	private HttpHeaders updateAggregateStats(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeadersImpl("update-aggregate-stats");
		model = ListingFacade.instance().updateAllAggregateStatistics();
		return headers;
	}

    private HttpHeaders updateListingStats(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeadersImpl("update-listing-stats");
        model = ListingFacade.instance().updateAllListingStatistics();
        return headers;
    }

	@Override
	public Object getModel() {
		return model;
	}

}
