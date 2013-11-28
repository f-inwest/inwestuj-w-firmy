package eu.finwest.datamodel;

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
public class Category {
	@Id public Long id;
	public String name;
	public String namePl;
	public int count = 0;
	public int countPl = 0;
	
	public Category() {
	}
	
	public Category(long id, String name, String namePl) {
		this.id = id;
		this.name = name;
		this.namePl = namePl;
	}
	
	public String toString() {
		return "Category(id=" + id + ", name=" + name + ", namePl=" + namePl + ", count=" + count + ", countPl=" + countPl + ")";
	}
}
