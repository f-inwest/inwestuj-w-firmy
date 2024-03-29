/**
 * inwestujwfirmy.eu
 * Copyright 2012
 */
package eu.finwest.datamodel;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.googlecode.objectify.Key;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public abstract class BaseObject<T> {
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public abstract Key<T> getKey();
}
