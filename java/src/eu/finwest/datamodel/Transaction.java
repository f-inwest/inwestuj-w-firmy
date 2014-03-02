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
public class Transaction extends BaseObject<Transaction> {
	public enum Status {OK, ERROR};
	
	public Transaction() {
	}
	public Key<Transaction> getKey() {
		return new Key<Transaction>(Transaction.class, id);
	}
	@Id public Long id;
	
	public String seller_id;
	public Status status;
	public String transactionId;
	public String amountStr;
	public double amount;
	public String paidStr;
	public double paid;
	public String error;
	public String date;
	public String description;
	public String crc;
	public String email;
	public String md5sum;
	
	public String pricePointName;
	public Key<PricePoint> pricePoint;
	
	@Indexed public Key<Listing> listing;
	@Indexed public Key<Listing> user;
	@Indexed public Key<Listing> campaign;
	
	public Date modified;
	@PrePersist void updateModifiedDate() {
		this.modified = new Date();
	}

	public String getWebKey() {
		return new Key<Transaction>(Transaction.class, id).getString();
	}
}

