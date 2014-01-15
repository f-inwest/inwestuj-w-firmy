/**
 * 
 */
package eu.finwest.datamodel;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * This entity is not cached separately.
 * All location list is stored as one entity in memcache.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@Unindexed
@Entity
public class ListingLocation extends BaseObject<ListingLocation> {
	public ListingLocation() {
	}
	public ListingLocation(Listing listing) {
		this.id = listing.id;
		this.campaign = listing.campaign;
		this.latitude = listing.latitude;
		this.longitude = listing.longitude;
	}
	public Key<ListingLocation> getKey() {
		return new Key<ListingLocation>(ListingLocation.class, id);
	}
	@Id public Long id;
	public String campaign;
	public Double latitude;
	public Double longitude;
	
	/**
	 * CAUTION!
	 * This method returns listing's web key!
	 */
	public String getWebKey() {
		return new Key<Listing>(Listing.class, id).getString();
	}
	
}

