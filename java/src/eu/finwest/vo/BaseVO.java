package eu.finwest.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.appengine.api.utils.SystemProperty;
import com.googlecode.objectify.Key;

import eu.finwest.web.FrontController;

public abstract class BaseVO {
	public abstract String getId();

	public long toKeyId() {
		return Key.create(getId()).getId();
	}

	public static long toKeyId(String id) {
		return Key.create(id).getId();
	}

	public static String getServiceLocation () {
		boolean develEnv = SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;
		String subdomain = FrontController.getCampaign() != null ?
				FrontController.getCampaign().getSubdomain() + ".": 
				(develEnv ? "" : "www.");
		return  develEnv ? "http://" + subdomain + "localhost:7777" : "https://" + subdomain + "inwestujwfirmy.pl";
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
