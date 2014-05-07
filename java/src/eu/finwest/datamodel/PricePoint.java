/**
 * 
 */
package eu.finwest.datamodel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

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
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
	fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class PricePoint extends BaseObject<PricePoint> implements Serializable {
	private static final long serialVersionUID = 5895345626437L;

	public enum Codes {NONE, INV_REG, PRJ_ACT, PRJ_BP, PRJ_PPT, PRJ_ALL, CMP_1MT, CMP_6MT, CMP_1Y};
	
	public enum Group {LISTING, CAMPAIGN, INVESTOR};
	public enum Type {LISTING_ACTIVATION, LISTING_PPTX, LISTING_BP, LISTING_VALUATION, LISTING_ALL,
		INVESTOR_REGISTRATION, CAMPAIGN_MONTH, CAMPAIGN_6MONTHS, CAMPAIGN_YEAR};
	
	public PricePoint() {
	}
	public Key<PricePoint> getKey() {
		return new Key<PricePoint>(PricePoint.class, id);
	}
	@Id public Long id;
	
	@JsonProperty("name") @Indexed public String name;
	@JsonProperty("amount")public int amount;
	@JsonProperty("currency")public Listing.Currency currency;
	@JsonProperty("group")public Group group;
	@JsonProperty("type")public Type type;
	@JsonProperty("descriptionPl") public String descriptionPl;
	@JsonProperty("descriptionEn")public String descriptionEn;
	@JsonProperty("buttonPl") public String buttonPl;
	@JsonProperty("buttonEn") public String buttonEn;
	@JsonProperty("freeButtonPl") public String freeButtonPl;
	@JsonProperty("freeButtonEn") public String freeButtonEn;
	@JsonProperty("successUrl")public String successUrl;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}

	public String getWebKey() {
		return new Key<PricePoint>(PricePoint.class, id).getString();
	}
}

