/**
 * inwestujwfirmy.eu
 * Copyright 2012
 */
package eu.finwest.datamodel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

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
@Cached(expirationSeconds=60*30)
public class Campaign extends BaseObject<Campaign> implements Serializable {
	private static final long serialVersionUID = 9087134514857634L;
	
	public enum Status {NEW, ACTIVE, CLOSED};

	/**
	 * List of allowed languages withing the domain
	 */
	public enum Language {PL, EN, ALL};
	
	public Key<Campaign> getKey() {
		return new Key<Campaign>(Campaign.class, id);
	}
	@Id public Long id;
	@Indexed public Status status;

	public boolean mockData;

	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}

	@Indexed public Key<SBUser> creator;
	public String creatorName;
	@Indexed public String subdomain;
	@Indexed public Date created;
	public String name;
	public String description;
	public String comment;
	public Date   activeFrom;
	public Date   activeTo;
	public boolean	publicBrowsing;
	public String admins;
	public Language allowedLanguage;
	public Key<ListingDoc> logoId;
	public boolean paid = false;
	public Key<Transaction> payment;

	public String getWebKey() {
		return new Key<Campaign>(Campaign.class, id).getString();
	}
}
