package eu.finwest.vo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import eu.finwest.datamodel.Notification;
import eu.finwest.util.DateSerializer;
import eu.finwest.util.LowecaseSerializer;
import eu.finwest.web.LangVersion;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserVO extends BaseVO {
	@JsonProperty("num") private int orderNumber;
	@JsonProperty("profile_id") private String id;
	@JsonProperty("username") private String nickname;
	@JsonProperty("name") private String name;
	@JsonProperty("email") private String email;
	@JsonProperty("location") private String location;
	@JsonProperty("phone") private String phone;
	@JsonProperty("investor") private boolean accreditedInvestor;
	@JsonProperty("user_class") private String userClass;
	@JsonProperty("notify_enabled") private boolean notifyEnabled;
	@JsonProperty("edited_listing") private String editedListing;
	@JsonProperty("edited_status")	@JsonSerialize(using=LowecaseSerializer.class) private String editedStatus;
	@JsonProperty("joined_date")
	@JsonSerialize(using=DateSerializer.class) private Date   joined;
	@JsonProperty("last_login")
	@JsonSerialize(using=DateSerializer.class) private Date   lastLoggedIn;
	@JsonProperty("modified")
	@JsonSerialize(using=DateSerializer.class) private Date   modified;
	@JsonProperty("num_listings") private long numberOfListings;
	@JsonProperty("num_bids") private long numberOfBids;
	@JsonProperty("num_accepted_bids") private long numberOfAcceptedBids;
	private long numberOfFundedBids;
	@JsonProperty("num_comments") private long numberOfComments;
	private long numberOfVotes;
	@JsonProperty("num_notifications") private long numberOfNotifications;
	@JsonProperty("status")
	@JsonSerialize(using=LowecaseSerializer.class) private String status;
	private boolean votable;
	private boolean mockData;
	@JsonProperty("admin") private boolean admin;
	@JsonProperty("avatar") private String avatar;
	private String paidCode;
	private Map<String, String> locationHeaders;
	private String recentDomain;
	private LangVersion recentLang;
	
	public UserVO() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAccreditedInvestor() {
		return accreditedInvestor;
	}

	public void setAccreditedInvestor(boolean accreditedInvestor) {
		this.accreditedInvestor = accreditedInvestor;
	}

	public Date getJoined() {
		return joined;
	}

	public void setJoined(Date joined) {
		this.joined = joined;
	}

	public Date getLastLoggedIn() {
		return lastLoggedIn;
	}

	public void setLastLoggedIn(Date lastLoggedIn) {
		this.lastLoggedIn = lastLoggedIn;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	public long getNumberOfListings() {
		return numberOfListings;
	}
	public void setNumberOfListings(long numberOfListings) {
		this.numberOfListings = numberOfListings;
	}
	public long getNumberOfBids() {
		return numberOfBids;
	}
	public void setNumberOfBids(long numberOfBids) {
		this.numberOfBids = numberOfBids;
	}
	public long getNumberOfComments() {
		return numberOfComments;
	}
	public void setNumberOfComments(long numberOfComments) {
		this.numberOfComments = numberOfComments;
	}
	public long getNumberOfVotes() {
		return numberOfVotes;
	}
	public void setNumberOfVotes(long numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public boolean isVotable() {
		return votable;
	}
	public void setVotable(boolean votable) {
		this.votable = votable;
	}
	public long getNumberOfAcceptedBids() {
		return numberOfAcceptedBids;
	}
	public void setNumberOfAcceptedBids(long numberOfAcceptedBids) {
		this.numberOfAcceptedBids = numberOfAcceptedBids;
	}
	public long getNumberOfFundedBids() {
		return numberOfFundedBids;
	}
	public void setNumberOfFundedBids(long numberOfFundedBids) {
		this.numberOfFundedBids = numberOfFundedBids;
	}
	public boolean isMockData() {
		return mockData;
	}
	public void setMockData(boolean mockData) {
		this.mockData = mockData;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public long getNumberOfNotifications() {
		return numberOfNotifications;
	}
	public void setNumberOfNotifications(long numberOfNotifications) {
		this.numberOfNotifications = numberOfNotifications;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public boolean isNotifyEnabled() {
		return notifyEnabled;
	}
	public void setNotifyEnabled(boolean notifyEnabled) {
		this.notifyEnabled = notifyEnabled;
	}
	public List<Notification.Type> getNotifications() {
		return Arrays.asList(Notification.Type.values());
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEditedListing() {
		return editedListing;
	}
	public void setEditedListing(String editedListing) {
		this.editedListing = editedListing;
	}
	public String getEditedStatus() {
		return editedStatus;
	}
	public void setEditedStatus(String editedStatus) {
		this.editedStatus = editedStatus;
	}
	public String getUserClass() {
		return userClass;
	}
	public void setUserClass(String userClass) {
		this.userClass = userClass;
	}
	public Map<String, String> getLocationHeaders() {
		return locationHeaders;
	}
	public void setLocationHeaders(Map<String, String> locationHeaders) {
		this.locationHeaders = locationHeaders;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getPaidCode() {
		return paidCode;
	}
	public void setPaidCode(String paidCode) {
		this.paidCode = paidCode;
	}
	public String getRecentDomain() {
		return recentDomain;
	}
	public void setRecentDomain(String recentDomain) {
		this.recentDomain = recentDomain;
	}
	public LangVersion getRecentLang() {
		return recentLang;
	}
	public void setRecentLang(LangVersion recentLang) {
		this.recentLang = recentLang;
	}
}
