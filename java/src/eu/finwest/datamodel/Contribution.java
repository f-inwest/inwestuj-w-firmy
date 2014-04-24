/**
 * inwestujwfirmy.eu
 * Copyright 2012
 */
package eu.finwest.datamodel;

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
public class Contribution extends BaseObject<Contribution> {
	public Key<Contribution> getKey() {
		return new Key<Contribution>(Contribution.class, id);
	}
	@Id public Long id;

	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}

	@Indexed public Key<Listing> listing;
	public String listingName;
	public Key<SBUser> approver;
	public Key<SBUser> contributor;
	public String contributorNickname;
	@Indexed public Date date;
	public Date approvedOn;
	@Indexed public boolean approved;
	
	/* number of days from 1970.01.01, used to quickly calculate days held value */
	public long daysSinceZero;
	public String contributorNickName;
	public int minutes;
	public int perHour;
	public int money;
	/* value represents 1/10000 */
	public int interestPerDay;
	public String description;

	public String getWebKey() {
		return new Key<Contribution>(Contribution.class, id).getString();
	}
}
