/**
 * 
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
public class ListingToImport extends BaseObject<ListingToImport> {
	public enum Type {ANDROID, IOS};
	
	public ListingToImport() {
	}
	public Key<ListingToImport> getKey() {
		return new Key<ListingToImport>(ListingToImport.class, id);
	}
	@Id public Long id;
	@Indexed public boolean imported = false;
	@Indexed public String sellerId;
	
	@Indexed public String identity;
	public String creator;
	public String name;
	public String description;
	public String logoUrl;
	public String screenshotUrl1;
	public String screenshotUrl2;
	public String screenshotUrl3;
	public String screenshotUrl4;
	public String screenshotUrl5;
	public String videoUrl;
	public Type type = Type.ANDROID;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}

	public String getWebKey() {
		return new Key<ListingToImport>(ListingToImport.class, id).getString();
	}
}

