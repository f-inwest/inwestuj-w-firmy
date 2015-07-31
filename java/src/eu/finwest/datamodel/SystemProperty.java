/**
 * inwestujwfirmy.eu
 * Copyright 2012
 */
package eu.finwest.datamodel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30*2)
public class SystemProperty implements Serializable {
	private static final long serialVersionUID = 9579454626201L;
	
	public static final String GOOGLEDOC_USER = "googledoc.user";
	public static final String GOOGLEDOC_PASSWORD = "googledoc.password";
	public static final String GOOGLEPLACES_API_KEY = "googleplaces.apikey";
	public static final String GOOGLE_API_KEY = "google.apikey";

	public static final String TWITTER_CONSUMER_KEY = "twitter.consumer.key";
	public static final String TWITTER_CONSUMER_SECRET = "twitter.consumer.secret";
	public static final String TWITTER_ACCESS_TOKEN = "twitter.access.token";
	public static final String TWITTER_ACCESS_TOKEN_SECRET = "twitter.access.token.secret";

	public static final String FACEBOOK_CLIENT_ID = "facebook.client.id";
	public static final String FACEBOOK_CLIENT_SECRET = "facebook.client.secret";
	
	public static final String PAYMENT_SECURITY_CODE = "payment.security.code";
	public static final String PAYMENT_ACTION_URL = "payment.action.url";
	public static final String PAYMENT_CUSTOMER_ID = "payment.customer.id";
	public static final String PAYMENT_FREE_USAGE = "payment.free.usage";
	public static final String PAYMENT_FREE_INVESTOR_REG = "payment.free.investor.registration";
	public static final String PAYMENT_FREE_CAMPAIGN_REG = "payment.free.campaign.activation";
	
	public static final String LISTING_UPDATE_VERSION = "listing.update.version";

	@Id public String name;
	public String value;
	@Indexed public Date created;
	public String author;

	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}

	public boolean booleanValue() {
		if (value == null) {
			return false;
		}
		return new Boolean(value);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getWebKey() {
		return new Key<SystemProperty>(SystemProperty.class, name).getString();
	}
}
