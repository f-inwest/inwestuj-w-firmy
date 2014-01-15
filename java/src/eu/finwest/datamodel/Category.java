package eu.finwest.datamodel;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
@Entity
@Indexed
@Cached(expirationSeconds=30*24*60*30)
public class Category implements Serializable {
	private static final long serialVersionUID = 21465473476765L;
	
	@Id public Long id;
	public String name;
	public String namePl;
	public int count = 0;
	public String campaign;
	
	public Category() {
	}
	
	public Category(long id, String name, String namePl) {
		this.id = id;
		this.name = name;
		this.namePl = namePl;
		this.campaign = "en";
	}
	
	public Category copyForCampaign(String campaign) {
		Category c = new Category();
		c.name = this.name;
		c.namePl = this.namePl;
		c.campaign = campaign;
		return c;
	}
	
	public String toString() {
		return "Category(id=" + id + ", name=" + name + ", namePl=" + namePl + ", count=" + count + ", campaign=" + campaign + ")";
	}
}
