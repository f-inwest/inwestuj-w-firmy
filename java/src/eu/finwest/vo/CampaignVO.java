package eu.finwest.vo;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import eu.finwest.util.DateSerializer;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class CampaignVO extends BaseVO implements Serializable {
	private static final long serialVersionUID = 4545997896432456L;
	
	@JsonProperty("campaign_id") private String id;
	@JsonProperty("special") private boolean special;
	@JsonProperty("creator") private String creator;
	@JsonProperty("subdomain") private String subdomain;
	@JsonProperty("create_date") @JsonSerialize(using=DateSerializer.class) private Date created;
	@JsonProperty("name") private String name;
	@JsonProperty("description") private String description;
	@JsonProperty("comment") private String comment;
	@JsonProperty("active_from") @JsonSerialize(using=DateSerializer.class) private Date   activeFrom;
	@JsonProperty("active_to") @JsonSerialize(using=DateSerializer.class) private Date   activeTo;
	@JsonProperty("public_browsing") private boolean	publicBrowsing;
	@JsonProperty("admins") private String admins;
	@JsonProperty("allowed_languages") private String allowedLanguage;
	@JsonProperty("status") private String status;
	
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getSubdomain() {
		return subdomain;
	}
	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getActiveFrom() {
		return activeFrom;
	}
	public void setActiveFrom(Date activeFrom) {
		this.activeFrom = activeFrom;
	}
	public Date getActiveTo() {
		return activeTo;
	}
	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}
	public boolean isPublicBrowsing() {
		return publicBrowsing;
	}
	public void setPublicBrowsing(boolean publicBrowsing) {
		this.publicBrowsing = publicBrowsing;
	}
	public String getAdmins() {
		return admins;
	}
	public void setAdmins(String admins) {
		this.admins = admins;
	}
	public String getAllowedLanguage() {
		return allowedLanguage;
	}
	public void setAllowedLanguage(String allowedLanguage) {
		this.allowedLanguage = allowedLanguage;
	}
	public boolean isSpecial() {
		return special;
	}
	public void setSpecial(boolean special) {
		this.special = special;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
