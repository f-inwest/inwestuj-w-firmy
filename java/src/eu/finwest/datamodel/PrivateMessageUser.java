/**
 * inwestujwfirmy.eu
 * Copyright 2012
 */
package eu.finwest.datamodel;

import java.util.Date;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@Unindexed
@Entity
@Cached(expirationSeconds=60*30)
public class PrivateMessageUser extends PrivateMessage {
	public int counter = 1;

	public PrivateMessageUser() {
	}
	public PrivateMessageUser(PrivateMessage msg) {
		this.direction = msg.direction;
		this.read = msg.read;
		this.text = msg.text;
		this.userA = msg.userA;
		this.userAEmail = msg.userAEmail;
		this.userANickname = msg.userANickname;
		this.userB = msg.userB;
		this.userBEmail = msg.userBEmail;
		this.userBNickname = msg.userBNickname;
		this.created = msg.created;
	}
	public String getWebKey() {
		return new Key<PrivateMessageUser>(PrivateMessageUser.class, id).getString();
	}
	public String toString() {
		return "MessageUser: " + userANickname + (direction == Direction.A_TO_B ? " -> " : " <- ") + userBNickname;
	}
}
