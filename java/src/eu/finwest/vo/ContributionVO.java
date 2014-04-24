package eu.finwest.vo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import eu.finwest.util.DateDeserializer;
import eu.finwest.util.ShortDateSerializer;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ContributionVO extends BaseVO {
	@JsonProperty("contribution_id") private String id;
	@JsonProperty("is_approved") private boolean approved;
	@JsonProperty("approved_on") @JsonSerialize(using=ShortDateSerializer.class)  @JsonDeserialize(using=DateDeserializer.class)
	private Date approvedOn;
	@JsonProperty("listing_id")	private String listing;
	@JsonProperty("listing_title") private String listingName;
	@JsonProperty("contributor_id") private String contributor;
	@JsonProperty("contributor_avatar") private String contributorAvatar;
	@JsonProperty("contributor_username") private String contributorName;
	@JsonProperty("contribution_date") @JsonSerialize(using=ShortDateSerializer.class)  @JsonDeserialize(using=DateDeserializer.class)
	private Date date;
	@JsonProperty("money") private String money;
	@JsonProperty("interestPerDay") private String interestPerDay;
	@JsonProperty("hours") private String hours;
	@JsonProperty("perHour") private String perHour;
	@JsonProperty("description") private String description;
	
	public ContributionVO() {
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getListing() {
		return listing;
	}

	public void setListing(String listing) {
		this.listing = listing;
	}

	public String getListingName() {
		return listingName;
	}

	public void setListingName(String listingName) {
		this.listingName = listingName;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public String getContributorAvatar() {
		return contributorAvatar;
	}

	public void setContributorAvatar(String contributorAvatar) {
		this.contributorAvatar = contributorAvatar;
	}

	public String getContributorName() {
		return contributorName;
	}

	public void setContributorName(String contributorName) {
		this.contributorName = contributorName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getInterestPerDay() {
		return interestPerDay;
	}

	public void setInterestPerDay(String interestPerDay) {
		this.interestPerDay = interestPerDay;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getPerHour() {
		return perHour;
	}

	public void setPerHour(String perHour) {
		this.perHour = perHour;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}
}
