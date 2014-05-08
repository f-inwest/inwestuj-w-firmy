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
public class SmsPayment extends BaseObject<SmsPayment> {
	public enum Status {OK, ERROR};
	
	public SmsPayment() {
	}
	public Key<SmsPayment> getKey() {
		return new Key<SmsPayment>(SmsPayment.class, id);
	}
	@Id public Long id;
	
	public Key<SBUser> customer;
	public String customerName;
	public String email;
	public String sellerId;
	public String code;
	public String status;
	public String amount;
	@Indexed public Date date;
	public String description;
		
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}

	public String getWebKey() {
		return new Key<SmsPayment>(SmsPayment.class, id).getString();
	}
}

