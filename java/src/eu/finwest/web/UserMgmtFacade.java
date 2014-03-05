package eu.finwest.web;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

import eu.finwest.dao.ObjectifyDatastoreDAO;
import eu.finwest.datamodel.Campaign;
import eu.finwest.datamodel.Campaign.Status;
import eu.finwest.datamodel.Listing;
import eu.finwest.datamodel.Listing.State;
import eu.finwest.datamodel.PricePoint;
import eu.finwest.datamodel.PricePoint.Group;
import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.SystemProperty;
import eu.finwest.datamodel.Transaction;
import eu.finwest.datamodel.UserStats;
import eu.finwest.datamodel.VoToModelConverter;
import eu.finwest.datamodel.PricePoint.Codes;
import eu.finwest.util.FacebookUser;
import eu.finwest.util.ImageHelper;
import eu.finwest.vo.BaseVO;
import eu.finwest.vo.CampaignVO;
import eu.finwest.vo.DtoToVoConverter;
import eu.finwest.vo.ErrorCodes;
import eu.finwest.vo.ListPropertiesVO;
import eu.finwest.vo.ListingTileVO;
import eu.finwest.vo.ListingVO;
import eu.finwest.vo.PricePointVO;
import eu.finwest.vo.UserAndUserVO;
import eu.finwest.vo.UserBasicVO;
import eu.finwest.vo.UserDataUpdatable;
import eu.finwest.vo.UserListVO;
import eu.finwest.vo.UserListingsForAdminVO;
import eu.finwest.vo.UserListingsForUsersVO;
import eu.finwest.vo.UserShortListVO;
import eu.finwest.vo.UserShortVO;
import eu.finwest.vo.UserVO;

public class UserMgmtFacade {
	private static final Logger log = Logger.getLogger(UserMgmtFacade.class.getName());
	
	public enum UpdateReason {BID_UPDATE, NEW_BID, NEW_COMMENT, NEW_LISTING, NEW_VOTE, NONE};
	
	private static UserMgmtFacade instance;
	
	private DateTimeFormatter timeStampFormatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss_SSS");
	
	public static UserMgmtFacade instance() {
		if (instance == null) {
			instance = new UserMgmtFacade();
		}
		return instance;
	}
	
	private UserMgmtFacade() {
	}
	
	private ObjectifyDatastoreDAO getDAO() {
		return ObjectifyDatastoreDAO.getInstance();
	}

	public UserVO getLoggedInUser(User loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		String email = loggedInUser.getEmail();
		SBUser userDTO = null;
		
		if (StringUtils.isNotEmpty(email)) {
			userDTO = getDAO().getUserByEmail(email);
		} else {
			userDTO = getDAO().getUserByEmail(loggedInUser.getUserId());
		}
		if (userDTO == null) {
			return null;
		}
		userDTO = updateUserGoogleData(loggedInUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		
		if (StringUtils.isEmpty(user.getName())) {
			String parts[] = StringUtils.split(user.getEmail(), "@");
			if (parts != null && parts.length > 0) {
				user.setName(parts[0]);
			} else {
				user.setName("not provided");
			}
		}
		applyUserStatistics(user, user);
		return user;
	}
	
	public UserVO getLoggedInUser(String email) {
		if (email == null) {
			return null;
		}
		UserVO user = DtoToVoConverter.convert(getDAO().getUserByEmail(email));
		if (user == null) {
			return null;
		}
		applyUserStatistics(user, user);
		return user;
	}

	public UserVO getLoggedInUser(FacebookUser fbUser) {
		if (fbUser == null) {
			return null;
		}
		SBUser userDTO = getDAO().getUserByEmail(fbUser.getEmail());
		if (userDTO == null) {
			return null;
		}
		userDTO = updateUserFacebookData(fbUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}

	public UserVO getLoggedInUser(twitter4j.User twitterUser) {
		if (twitterUser == null) {
			return null;
		}
		SBUser userDTO = getDAO().getUserByTwitter(twitterUser.getId());
		if (userDTO == null) {
			return null;
		}
		userDTO = updateUserTwitterData(twitterUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}
	
	public UserVO getLoggedInUser(SBUser userDTO) {
		if (userDTO == null) {
			return null;
		}		
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}

	public UserVO requestEmailUpdate(twitter4j.User twitterUser, String email) {
		if (twitterUser == null) {
			return null;
		}
		SBUser userByTwitter = getDAO().getUserByTwitter(twitterUser.getId());
		if (userByTwitter == null) {
			log.warning("User with twitter id " + twitterUser.getId() + " doesn't exist!");
			return null;
		}
		if (!StringUtils.isEmpty(userByTwitter.email)) {
			log.warning("User with twitter id " + twitterUser.getId() + " has already set email. User: " + userByTwitter);
			return null;
		}
		userByTwitter = getDAO().prepareUpdateUsersEmailByTwitter(userByTwitter, email);
		EmailService.instance().sendEmailVerification(userByTwitter);
		
		return DtoToVoConverter.convert(userByTwitter);
	}

	public UserVO confirmEmailUpdate(String twitterIdString, String token) {
		if (StringUtils.isEmpty(twitterIdString) || StringUtils.isEmpty(token)) {
			return null;
		}
		long twitterId = Long.parseLong(twitterIdString);
		SBUser twitterUser = getDAO().getUserByTwitter(twitterId);
		if (twitterUser != null) {
			if (StringUtils.equals(token, twitterUser.activationCode)) {
				twitterUser = getDAO().updateUsersEmailByTwitter(twitterUser, twitterUser.twitterEmail);
				return DtoToVoConverter.convert(twitterUser);
			} else {
				log.warning("Confirmation token provided for Twitter user is not valid. Token: " + token
						+ " User: " + twitterUser);
			}
		} else {
			log.warning("Twitter user " + twitterId + " not found in datastore!");
		}
		return null;
	}

	/**
	 * Returns user data object by userId
	 * 
	 * @param userId User identifier
	 * @return User data as JsonNode
	 */
	public UserListingsForAdminVO getUser(UserVO loggedInUser, String userId) {
		UserListingsForAdminVO result = new UserListingsForUsersVO();
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			result = new UserListingsForAdminVO();
		}

		UserVO user = DtoToVoConverter.convert(getDAO().getUser(userId));
		if (user == null) {
			result.setErrorCode(ErrorCodes.APPLICATION_ERROR);
			result.setErrorMessage("User with id '" + userId + "' doesn't exist!");
			log.info("User with id '" + userId + "' doesn't exist!");
			return result;
		}
		ListPropertiesVO props = new ListPropertiesVO();
		
		List<ListingTileVO> allListings = new ArrayList<ListingTileVO>();

		props.setMaxResults(4);
		List<ListingTileVO> activeListings = ListingFacade.instance().prepareListingList(
				getDAO().getUserListings(user.toKeyId(), Listing.State.ACTIVE, props));
		if (activeListings.size() > 0) {
			result.setActiveListings(activeListings);
			allListings.addAll(activeListings);
		}
		
		List<CampaignVO> ownedCampaings = DtoToVoConverter.convertCampaigns(getDAO().getUserCampaigns(user.toKeyId(), new ListPropertiesVO(10)));
		result.setOwnedCampaigns(ownedCampaings);

		if (loggedInUser != null && (loggedInUser.isAdmin() || user.toKeyId() == loggedInUser.toKeyId())) {
			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<ListingTileVO> withdrawnListings = ListingFacade.instance().prepareListingList(
					getDAO().getUserListings(user.toKeyId(), Listing.State.WITHDRAWN, props));
			if (withdrawnListings.size() > 0) {
				result.setWithdrawnListings(withdrawnListings);
				allListings.addAll(withdrawnListings);
			}

			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<ListingTileVO> frozenListings = ListingFacade.instance().prepareListingList(
					getDAO().getUserListings(user.toKeyId(), Listing.State.FROZEN, props));
			if (frozenListings.size() > 0) {
				result.setFrozenListings(frozenListings);
				allListings.addAll(frozenListings);
			}
			
			props = new ListPropertiesVO();
			props.setMaxResults(4);
			List<ListingTileVO> closedListings = ListingFacade.instance().prepareListingList(
					getDAO().getUserListings(user.toKeyId(), Listing.State.CLOSED, props));
			if (closedListings.size() > 0) {
				result.setClosedListings(closedListings);
				allListings.addAll(closedListings);
			}

			if (user.getEditedListing() != null) {
				Listing editedListing = getDAO().getListing(BaseVO.toKeyId(user.getEditedListing()));
				result.setEditedListing(DtoToVoConverter.convert(editedListing));
				allListings.add(result.getEditedListing());
			}
			applyUserStatistics(loggedInUser, loggedInUser);
			
			result.setPricePoints(getPricePoints(user, MemCacheFacade.instance().getUserCampaigns(user)));
		} else {
			user.setEmail("");
			user.setLocation("");
			user.setPhone("");
			user.setEditedListing("");
			user.setEditedStatus("");
		}
		
		if (loggedInUser != null && loggedInUser.isAdmin()) {
			result.setUser(user);
			result.setOwnedCampaigns(MemCacheFacade.instance().getUserCampaigns(user));
		} else {
			((UserListingsForUsersVO)result).setUserBasic(new UserBasicVO(user));
		}
		return result;
	}
	
	/**
	 * Creates new user based on Google user object.
	 * Should only be used to log in via google account.
	 */
	public UserVO createUser(User loggedInUser) {
		if (loggedInUser == null) {
			return null;
		}
		
		SBUser userDTO = getDAO().createUser(loggedInUser.getEmail(), loggedInUser.getNickname());
		userDTO = updateUserGoogleData(loggedInUser, userDTO);
		
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}
	
	private SBUser updateUserGoogleData(User googleUser, SBUser user) {
		boolean needsUpdate = false;
		if (StringUtils.isEmpty(user.googleId)) {
			user.googleId = googleUser.getUserId();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.googleEmail)) {
			user.googleEmail = googleUser.getEmail();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.googleName)) {
			user.googleName = googleUser.getNickname();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.avatarUrl) && StringUtils.isNotEmpty(user.googleId)) {
			user.avatarUrl = ImageHelper.getGooglePlusAvatarUrl(user.googleId, user.googleEmail);
			needsUpdate =  true;
		}
		if (needsUpdate) {
			user = getDAO().updateUser(user);
		}
		return user;
	}
	
	/**
	 * Creates new user based on Facebook user object.
	 */
	public UserVO createUser(FacebookUser fbUser) {
		if (fbUser == null) {
			return null;
		}
		
		String fullName = fbUser.getFirstName() + " " + fbUser.getLastName();
		SBUser userDTO = getDAO().createUser(fbUser.getEmail(), fbUser.getFirstName(), fullName.trim());
		
		userDTO = updateUserFacebookData(fbUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}
	
	private SBUser updateUserFacebookData(FacebookUser facebookUser, SBUser user) {
		boolean needsUpdate = false;
		if (StringUtils.isEmpty(user.facebookId)) {
			user.facebookId = facebookUser.getId();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.facebookEmail)) {
			user.facebookEmail = facebookUser.getEmail();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.facebookName)) {
			user.facebookName = facebookUser.getFirstName() + " " + facebookUser.getLastName();
			needsUpdate = true;
		}
		if (StringUtils.isEmpty(user.avatarUrl) && StringUtils.isNotEmpty(user.facebookId)) {
			user.avatarUrl = ImageHelper.getFacebookAvatarUrl(user.facebookId);
			needsUpdate =  true;
		}
		if (needsUpdate) {
			user = getDAO().updateUser(user);
		}
		return user;
	}
	
	/**
	 * Creates new user based on Twitter user object.
	 * Should only be used to log in via twitter account.
	 */
	public UserVO createUser(twitter4j.User twitterUser) {
		if (twitterUser == null) {
			return null;
		}
		
		SBUser userDTO = getDAO().createUser(twitterUser.getId(), twitterUser.getScreenName());
		userDTO = updateUserTwitterData(twitterUser, userDTO);
		UserVO user = DtoToVoConverter.convert(userDTO);
		applyUserStatistics(user, user);
		return user;
	}

	private SBUser updateUserTwitterData(twitter4j.User twitterUser, SBUser user) {
		boolean needsUpdate = false;
		if (StringUtils.isEmpty(user.avatarUrl) && twitterUser.getProfileImageURL() != null) {
			user.avatarUrl = twitterUser.getProfileImageURL().toString();
			needsUpdate =  true;
		}
		if (needsUpdate) {
			user = getDAO().updateUser(user);
		}
		return user;
	}
	
	private boolean validateEmailAddress(String email) {
		if (StringUtils.isEmpty(email)) {
			log.warning("Email is empty");
			return false;
		}
		String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			log.warning("Invalid email address: " + email);
			return false;
		}
		return true;
	}
	
	private boolean validatePassword(String email, String password, String name) {
		if (password == null || password.length() < 6) {
			log.warning("Password is too short: " + password.length());
			return false;
		}
		if (email.contains(password)) {
			log.warning("Password is part of email: " + email);
			return false;
		}
		if (name != null && name.contains(password)) {
			log.warning("Password is part of name: " + name);
			return false;
		}
		int digits = 0;
		int letters = 0;
		for (int i = 0; i < password.length(); i++) {
			if (StringUtils.isAlpha("" + password.charAt(i))) {
				letters++;
			}
			if (StringUtils.isNumeric("" + password.charAt(i))) {
				digits++;
			}
		}
		if (digits < 2 || letters < 2) {
			log.warning("Password must have at least 2 digits (" + digits + ") and 2 letters (" + letters + ")");
			return false;
		}
		return true;
	}
	
	private boolean validateName(String name) {
		if (name != null && name.length() < 6) {
			log.warning("Name is too short: " + name);
			return false;
		}
		return true;
	}
	
	/**
	 * Creates new user based on Google user object.
	 * Should only be used to log in via google account.
	 */
	public UserVO createUser(String email, String password, String name, String location, boolean investor) {
		UserVO user = null;
		if (!validateEmailAddress(email) || !validateName(name) || !validatePassword(email, password, name)) {
			return null;
		}
		String encryptedPassword = encryptPassword(password);
		if (encryptedPassword == null) {
			return null;
		}
		String authCookie = encryptPassword(encryptedPassword + new Date().getTime());
		
		SBUser userDAO = getDAO().registerUser(email, encryptedPassword, authCookie, name, location, investor);
		log.warning("************************************************");
		log.warning("************* EMAIL NEEDS TO BE SEND ***********");
		log.warning("************* cofirmation code: " + userDAO.activationCode);
		log.warning("************************************************");
		
		user = DtoToVoConverter.convert(userDAO);
		applyUserStatistics(user, user);
		return user;
	}
	
	public SBUser authenticateUser(String email, String password) {
		SBUser user = getDAO().getUserByEmail(email);
		if (user == null) {
			return null;
		}
		if (!StringUtils.equals(user.password, encryptPassword(password))) {
			log.warning("Not valid password for user: " + user.email);
			return null;
		}
		if (StringUtils.isEmpty(user.authCookie)) {
			user.authCookie = encryptPassword(user.password + new Date().getTime());
		}
		return user;
	}

	private String encryptPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return new String(md.digest(password != null ? password.getBytes("UTF-8") : new byte[0])); 
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while encrypting password", e);
			return null;
		}
	}

	/**
	 * Verifies user/password, returns authCookie which should be set in the browser
	 */
	public String checkUserCredentials(String email, String password) {
		SBUser user = getDAO().getUserByEmail(email);
		if(StringUtils.equals(user.password, encryptPassword(password))) {
			return user.authCookie;
		} else {
			return null;
		}
	}

	/**
	 * Returns user for provided authCookie.
	 * Make sure that authCookie expires on the browser.
	 */
	public UserVO checkUserCredentials(String authCookie) {
		SBUser user = getDAO().getUserByAuthCookie(authCookie);
		return DtoToVoConverter.convert(user);
	}
	
	public String changePassword(String email, String oldPassword, String newPassword) {
		SBUser user = getDAO().getUserByEmail(email);
		if(StringUtils.equals(user.password, encryptPassword(oldPassword))) {
			user.password = encryptPassword(newPassword);
			user.authCookie = encryptPassword(user.password + new Date().getTime());
			log.info("User '" + email + "' is going to update password");
			user =  getDAO().updateUser(user);
			return user != null ? user.authCookie : null;
		} else {
			log.warning("User '" + email + "' tried to change password but provided wrong existing password");
			return null;
		}
	}
	
	public UserAndUserVO promoteToDragon(UserVO loggedInUser, String userId) {
		UserAndUserVO result = new UserAndUserVO();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.warning("User not logged in or is not an admin");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("User not logged in or is not an admin");
			return result;
		}
		SBUser user = getDAO().getUser(userId);
		if (user == null) {
			log.warning("User with id '" + userId + "' not found");
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage("User not found");
			return result;
		}
		user.userClass = "dragon";
		user.dragon = true;
		user = getDAO().updateUser(user);
		log.info("Promoted to Dragon: " + user);
		
		result.setUser(DtoToVoConverter.convert(user));
		return result;
	}
	
	public UserAndUserVO requestDragon(UserVO loggedInUser) {
		UserAndUserVO result = new UserAndUserVO();
		if (loggedInUser == null) {
			log.warning("User not logged in");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage("User not logged in or is not an admin");
			return result;
		}
		SBUser user = getDAO().getUser(loggedInUser.getId());
		if (!StringUtils.contains(user.userClass, "dragon")) {
			user.userClass = "requested_dragon";
			user = getDAO().updateUser(user);
			loggedInUser.setUserClass(user.userClass);
		
			NotificationFacade.instance().scheduleUserDragonRequestNotification(user);
		} else {
			log.warning("User has already requested dragon badge. " + user);
			result.setErrorCode(ErrorCodes.APPLICATION_ERROR);
			result.setErrorMessage("User has already requested dragon badge");
			return result;

		}
		result.setUser(DtoToVoConverter.convert(user));
		return result;
	}
	
	/**
	 * Updates user data. Field validation is done before.
	*/
	public UserVO updateUser(UserVO loggedInUser, String name, String nickname, String location, String phone, Boolean investor, Boolean notifyEnabled) {
		SBUser oldUser = getDAO().getUser(loggedInUser.getId());
		if (oldUser == null) {
			log.warning("User '" + loggedInUser.getEmail() + "' doesn't exist!");
			return null;
		}
		if (oldUser.status != SBUser.Status.ACTIVE) {
			log.warning("User '" + loggedInUser.getEmail() + "' is not active!");
			return null;
		}
		if (StringUtils.isNotEmpty(nickname)) {
            if (nickname.length() < 3) {
                log.warning("New nickname '" + name + "' must be at least 3 characters");
                return null;
            }
            else if (nickname.length() > 30) {
                log.warning("New nickname '" + name + "' must be no more than 30 characters");
                return null;
            }
            else if (!checkUserNameIsValid(loggedInUser, nickname)) {
				log.warning("Nickname '" + nickname + "' for user is not unique!");
				return null;
			}
            else {
				oldUser.nickname = nickname;
			}
        }
		if (StringUtils.isNotEmpty(name)) {
			if (name.length() < 3) {
				log.warning("New user name '" + name + "' must be at least 3 characters");
				return null;
			}
            else if (name.length() > 100) {
				log.warning("New user name '" + name + "' must be no more than 100 characters");
				return null;
			}
            else {
                oldUser.name = name;
			}
		}
		// @FIXME Implement proper location verifier
		if (StringUtils.isNotEmpty(location)) {
			if (location.length() < 10) {
				log.warning("New user location '" + name + "' is too short!");
				return null;
			} else {
				oldUser.location = location;
			}
		}
		// @FIXME User regexp for phone number validation
		if (StringUtils.isNotEmpty(phone)) {
			if (phone.length() < 7) {
				log.warning("New phone '" + phone + "' is too short!");
				return null;
			} else {
				oldUser.phone = phone;
			}
		}
		if (investor != null) {
			oldUser.investor = investor;
		}
		if (notifyEnabled != null) {
			oldUser.notifyEnabled = notifyEnabled;
		}
		UserVO user = DtoToVoConverter.convert(getDAO().updateUser(oldUser));
		if (user != null) {
			loggedInUser.setEmail(user.getEmail());
			loggedInUser.setNickname(user.getNickname());
			loggedInUser.setName(user.getName());
			loggedInUser.setLocation(user.getLocation());
			loggedInUser.setPhone(user.getPhone());
			loggedInUser.setAccreditedInvestor(user.isAccreditedInvestor());
			loggedInUser.setNotifyEnabled(user.isNotifyEnabled());
			applyUserStatistics(loggedInUser, user);
			//ServiceFacade.instance().createNotification(user.getId(), user.getId(), Notification.Type.YOUR_PROFILE_WAS_MODIFIED, "");
		}
		return user;
	}

	/**
	 * Returns list of all registered users
	 * @return List of users
	 */
	public UserListVO getAllUsers(UserVO loggedInUser) {
		UserListVO userList = new UserListVO();
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			log.warning("Only admins can retrieve all users list");
			userList.setUsers(new ArrayList<UserVO>());
			return userList;
		}
		
		List<UserVO> users = DtoToVoConverter.convertUsers(getDAO().getAllUsers());
		int index = 1;
		for (UserVO user : users) {
			applyUserStatistics(loggedInUser, user);
			user.setOrderNumber(index++);
		}

		userList.setUsers(users);
		return userList;
	}

	/**
	 * Returns list of all dragons
	 */
	public UserShortListVO getDragons(UserVO loggedInUser, ListPropertiesVO userProperties) {
		UserShortListVO userList = new UserShortListVO();
		
		List<UserShortVO> users = DtoToVoConverter.convertShortUsers(getDAO().getDragons(userProperties));
		userList.setUsers(users);
		if (loggedInUser != null) {
			userList.setUser(loggedInUser);
		}
		userList.setUsersProperties(userProperties);
		return userList;
	}

	/**
	 * Returns list of all listers
	 */
	public UserShortListVO getListers(UserVO loggedInUser, ListPropertiesVO userProperties) {
		UserShortListVO userList = new UserShortListVO();
		
		List<UserShortVO> users = DtoToVoConverter.convertShortUsers(getDAO().getListers(userProperties));
		userList.setUsers(users);
		userList.setUsersProperties(userProperties);
		if (loggedInUser != null) {
			userList.setUser(loggedInUser);
		}
		return userList;
	}

    public void applyUserStatistics(UserVO user) {
        applyUserStatistics(null, user);
    }

    public void applyUserStatistics(UserVO loggedInUser, UserVO user) {
		if (user != null && user.getId() != null) {
			UserStats userStats = getUserStatistics(user.getId());
			if (userStats != null) {
				//user.setNumberOfBids(userStats.numberOfBids);
				//user.setNumberOfComments(userStats.numberOfComments);
				//user.setNumberOfListings(userStats.numberOfListings);
				//user.setNumberOfVotes(userStats.numberOfVotes);
				//user.setNumberOfAcceptedBids(userStats.numberOfAcceptedBids);
				//user.setNumberOfFundedBids(userStats.numberOfFundedBids);
				user.setNumberOfNotifications(userStats.numberOfNotifications);
			} else {
				log.info("User statistics not available for user '" + user.getEmail() + "'");
			}
		}
	}
	
	public UserVO activateUser(String activationCode) {
		UserVO user = DtoToVoConverter.convert(getDAO().activateUser(activationCode));
		if (user != null) {
			applyUserStatistics(user);
		}
		return user;
	}

	public UserVO deactivateUser(UserVO loggedInUser, String userId) {
		if (loggedInUser == null || !loggedInUser.getId().equals(userId)) {
			if (UserServiceFactory.getUserService().isUserLoggedIn() && UserServiceFactory.getUserService().isUserAdmin()) {
				log.info("Admin user '" + loggedInUser.getEmail() + "' is deactivating user " + userId);
			} else {
				return null;
			}
		}
		UserVO user = DtoToVoConverter.convert(getDAO().deactivateUser(BaseVO.toKeyId(userId)));
		applyUserStatistics(loggedInUser, user);
		return user;
	}

	public Boolean checkUserNameIsValid(UserVO loggedInUser, String nickName) { // true if nickname is a valid username in use, false otherwise
        if (StringUtils.isEmpty(nickName)) { // empty nickname not allowed
            return false;
        }
        if (!StringUtils.isEmpty(loggedInUser.getNickname()) && loggedInUser.getNickname().equalsIgnoreCase(nickName)) { // keeping my existing name is okay
            return true;
        }
        if (getDAO().checkNickNameInUse(nickName)) { // same as existing nickname not allowed
            return false;
        }
        return true;
	}
	
	public void scheduleUpdateOfUserStatistics(String userId, UpdateReason reason) {
		log.log(Level.INFO, "Scheduling user stats update for '" + userId + "', reason: " + reason);
//		UserStats userStats = (UserStats)cache.get(USER_STATISTICS_KEY + userId);
//		if (userStats != null)
//			switch(reason) {
//			case NEW_BID:
//				userStats.numberOfBids = userStats.numberOfBids + 1;
//				break;
//			case NEW_COMMENT:
//				userStats.numberOfComments = userStats.numberOfComments + 1;
//				break;
//			case NEW_LISTING:
//				userStats.numberOfListings = userStats.numberOfListings + 1;
//				break;
//			case NEW_VOTE:
//				userStats.numberOfVotes = userStats.numberOfVotes + 1;
//				break;
//			default:
//				// reason can be also null
//				break;
//			}
//			cache.put(USER_STATISTICS_KEY + userId, userStats);
//		}
		String taskName = timeStampFormatter.print(new Date().getTime()) + "user_stats_update_" + reason + "_" + userId;
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/task/calculate-user-stats").param("id", userId)
				.taskName(taskName));
	}
	
	public UserStats calculateUserStatistics(String userId) {
		log.log(Level.INFO, "Calculating user stats for '" + userId + "'");
		UserStats userStats = getDAO().updateUserStatistics(BaseVO.toKeyId(userId));
		return userStats;
	}
	
	private UserStats getUserStatistics(String userId) {
		UserStats userStats = getDAO().getUserStatistics(BaseVO.toKeyId(userId));
		if (userStats == null) {
			// calculating user stats here may be disabled here
			userStats = calculateUserStatistics(userId);
		}
		log.log(Level.INFO, "User stats for '" + userId + "' : " + userStats);
		return userStats;
	}
	
	public List<UserStats> updateAllUserStatistics() {
		List<UserStats> list = new ArrayList<UserStats>();
		for (SBUser user : getDAO().getAllUsers()) {
			list.add(calculateUserStatistics("" + user.id));
		}
		
		return list;
	}

	public Object verifyEmail(UserVO loggedInUser, String email) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object requestEmailAccess(UserVO loggedInUser, String email, String url) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void updateUserData(UserDataUpdatable item) {
		if (item == null) {
			return;
		}
		SBUser user = getDAO().getUser(item.getUser());
		item.setAvatar(user.avatarUrl);
		item.setUserClass(user.userClass);
		item.setUserNickname(user.nickname);
	}
	
	public void updateUserData(List<? extends UserDataUpdatable> items) {
		if (items == null || items.size() == 0) {
			return;
		}
		Set<Key<Object>> userKeys = new HashSet<Key<Object>>();
		for (UserDataUpdatable item : items) {
			if (item.getUser() != null) {
				userKeys.add(Key.create(item.getUser()));
			}
		}
		if (userKeys.size() > 0) {
			log.info("Getting user's data for " + userKeys.size() + " users");
			Map<String, SBUser> users = getDAO().getUsers(userKeys);
			for (UserDataUpdatable item : items) {
				SBUser user = users.get(item.getUser());
				if (user != null) {
					item.setAvatar(user.avatarUrl);
					item.setUserClass(user.userClass);
					item.setUserNickname(user.nickname);
				}
			}
		}
	}

	public CampaignVO storeCampaign(UserVO loggedInUser, CampaignVO campaign) {
		if (loggedInUser == null || !(loggedInUser.isAccreditedInvestor() || loggedInUser.isAdmin())) {
			log.info("Not logged in or user is not admin/investor");
			return null;
		}
		if (StringUtils.equalsIgnoreCase(campaign.getSubdomain(), "pl")
				|| StringUtils.equalsIgnoreCase(campaign.getSubdomain(), "en")) {
			log.info("User cannot update special campaigns");
			return null;
		}
		Campaign existingCampaign = getDAO().getCampaignByDomain(campaign.getSubdomain());
		if (existingCampaign != null && (existingCampaign.creator.getId() != loggedInUser.toKeyId() || !loggedInUser.isAdmin())) {
			log.info("User is not an admin of the campaign, creator: " + existingCampaign.creatorName + ", logged in user: " + loggedInUser);
			return null;
		}
		if (StringUtils.isBlank(campaign.getStatus())) {
			campaign.setStatus(Campaign.Status.NEW.toString());
		}
		Campaign newCampaign = VoToModelConverter.convert(campaign);
		log.info("Updating campaign " + newCampaign + " with new data " + campaign + " by " + loggedInUser.getName());
		if (existingCampaign == null) {
			newCampaign.creator = new Key<SBUser>(loggedInUser.getId());
			newCampaign.creatorName = loggedInUser.getName();
			newCampaign.subdomain = campaign.getSubdomain();
			newCampaign.status = Campaign.Status.NEW;
		} else if (newCampaign.status != Campaign.Status.NEW && !loggedInUser.isAdmin()){
			// only admins can change campaign status to ACTIVE or CLOSED
			newCampaign.status = Campaign.Status.NEW;
		}
		campaign = DtoToVoConverter.convert(getDAO().storeCampaign(newCampaign));
		MemCacheFacade.instance().cleanCampaingsCache();
		return campaign;
	}
	
	public List<PricePointVO> getPricePoints(UserVO loggedInUser, List<CampaignVO> campaigns) {
		List<PricePointVO> list = new ArrayList<PricePointVO>();
		if (loggedInUser == null) {
			log.info("User not logged in, returning empty pricepoints.");
		} else {
			PricePoint.Codes code = PricePoint.Codes.valueOf(loggedInUser.getPaidCode() != null ? loggedInUser.getPaidCode() : "NONE");
			if (code == Codes.NONE) {
				List<PricePoint> pricePoints = MemCacheFacade.instance().getPricePoints(Group.INVESTOR);
				LangVersion portalLang = FrontController.getLangVersion();
				for (PricePoint pp : pricePoints) {
					list.add(preparePricePointData(pp, loggedInUser, portalLang));
				}
				log.info("Returning " + list.size() + " pricepoints for user " + loggedInUser.getName());
			} else {
				log.info("User " + loggedInUser.getName() + " has already registered as investor.");
				
				if (loggedInUser.isAccreditedInvestor()) {
					List<PricePoint> pricePoints = MemCacheFacade.instance().getPricePoints(Group.CAMPAIGN);
					LangVersion portalLang = FrontController.getLangVersion();
					for (CampaignVO campaign : campaigns) {
						PricePoint.Codes campaignPaidCode = PricePoint.Codes.valueOf(campaign.getPaidCode() != null ? campaign.getPaidCode() : "NONE");
						if (StringUtils.equalsIgnoreCase(Campaign.Status.NEW.toString(), campaign.getStatus())
								&& campaignPaidCode == PricePoint.Codes.NONE) {
							for (PricePoint pp : pricePoints) {
								list.add(preparePricePointData(pp, loggedInUser, campaign, portalLang));
							}
						}
					}
				}
			}
		}
		log.info("Returning " + list.size() + " pricepoints for user " + loggedInUser.getName());
		return list;
	}

	public List<PricePointVO> getPricePoints(UserVO loggedInUser, ListingVO listing) {
		List<PricePointVO> list = new ArrayList<PricePointVO>();
		if (loggedInUser == null || listing == null) {
			log.info("User not logged in or empty listing, returning empty pricepoints.");
		} else if (!StringUtils.equalsIgnoreCase(Listing.State.NEW.toString(), listing.getState())
				&& !StringUtils.equalsIgnoreCase(Listing.State.POSTED.toString(), listing.getState())
				&& !StringUtils.equalsIgnoreCase(Listing.State.ACTIVE.toString(), listing.getState())) {
			log.info("Only listings in NEW, POSTED and ACTIVE state will return pricepoints, returning empty pricepoints.");
		} else {
			LangVersion portalLang = FrontController.getLangVersion();
			List<PricePoint> pricePoints = MemCacheFacade.instance().getPricePoints(Group.LISTING);			
			if (StringUtils.equals(State.ACTIVE.name(), listing.getState())) {
				for (PricePoint pp : pricePoints) {
					if (pp.name.equals(Codes.PRJ_BP.toString()) || pp.name.equals(Codes.PRJ_PPT.toString())) {
						list.add(preparePricePointData(pp, loggedInUser, listing, portalLang));
					}
				}
			} else if (StringUtils.contains(listing.getPaidCode(), Codes.PRJ_ALL.toString())) {
				// listing has all options already purchased
			} else if (StringUtils.contains(listing.getPaidCode(), Codes.PRJ_BP.toString())) {
				for (PricePoint pp : pricePoints) {
					if (pp.name.equals(Codes.PRJ_PPT.toString())) {
						list.add(preparePricePointData(pp, loggedInUser, listing, portalLang));
					}
				}
			} else if (StringUtils.contains(listing.getPaidCode(), Codes.PRJ_PPT.toString())) {
				for (PricePoint pp : pricePoints) {
					if (pp.name.equals(Codes.PRJ_BP.toString())) {
						list.add(preparePricePointData(pp, loggedInUser, listing, portalLang));
					}
				}
			} else if (StringUtils.contains(listing.getPaidCode(), Codes.PRJ_ACT.toString())) {
				for (PricePoint pp : pricePoints) {
					if (pp.name.equals(Codes.PRJ_BP.toString()) || pp.name.equals(Codes.PRJ_PPT.toString())) {
						list.add(preparePricePointData(pp, loggedInUser, listing, portalLang));
					}
				}
			} else {
				for (PricePoint pp : pricePoints) {
					if (pp.name.equals(Codes.PRJ_ACT.toString()) || pp.name.equals(Codes.PRJ_ALL.toString())) {
						list.add(preparePricePointData(pp, loggedInUser, listing, portalLang));
					}
				}
			}
			log.info("Returning " + list.size() + " pricepoints for user " + loggedInUser.getName());
		}		
		return list;
	}
	
	private PricePointVO preparePricePointData(PricePoint pricePoint, UserVO loggedInUser, CampaignVO campaign, LangVersion portalLang) {
		PricePointVO pp = new PricePointVO();
		if (portalLang == LangVersion.PL) {
			pp.setDescription(pricePoint.descriptionPl);
			pp.setButtonText("Przejdź do zapłaty");
			
			pp.setPaymentLanguage("pl");
			pp.setTransactionDescClient("Opłata aktywacyjna kampanii");
			pp.setTransactionDescSeller("Opłata aktywacyjna kampanii");
		} else {
			pp.setDescription(pricePoint.descriptionEn);
			pp.setButtonText("Proceed to pay");

			pp.setPaymentLanguage("en");
			pp.setTransactionDescClient("Campaign activation fee");
			pp.setTransactionDescSeller("Opłata aktywacyjna kampanii");
		}

		updateCommonFields(pricePoint, loggedInUser, portalLang, pp, campaign.getId());
		return pp;
	}

	private PricePointVO preparePricePointData(PricePoint pricePoint, UserVO loggedInUser, LangVersion portalLang) {
		PricePointVO pp = new PricePointVO();
		if (portalLang == LangVersion.PL) {
			pp.setDescription(pricePoint.descriptionPl);
			pp.setButtonText("Przejdź do zapłaty");
			
			pp.setPaymentLanguage("pl");
			pp.setTransactionDescClient("Opłata rejestracyjna dla inwestora");
			pp.setTransactionDescSeller("Opłata rejestracyjna dla inwestora");
		} else {
			pp.setDescription(pricePoint.descriptionEn);
			pp.setButtonText("Proceed to pay");

			pp.setPaymentLanguage("en");
			pp.setTransactionDescClient("Investor registration fee");
			pp.setTransactionDescSeller("Opłata rejestracyjna dla inwestora");
		}
		
		updateCommonFields(pricePoint, loggedInUser, portalLang, pp, loggedInUser.getId());
		return pp;
	}

	private PricePointVO preparePricePointData(PricePoint pricePoint, UserVO loggedInUser, ListingVO listing, LangVersion portalLang) {
		PricePointVO pp = new PricePointVO();
		if (portalLang == LangVersion.PL) {
			pp.setDescription(pricePoint.descriptionPl);
			pp.setButtonText("Przejdź do zapłaty");
			
			pp.setPaymentLanguage("pl");
			pp.setTransactionDescClient("Opłata za usługę " + pricePoint.name);
			pp.setTransactionDescSeller("Opłata za usługę " + pricePoint.name);
		} else {
			pp.setDescription(pricePoint.descriptionEn);
			pp.setButtonText("Proceed to pay");

			pp.setPaymentLanguage("en");
			pp.setTransactionDescClient("Payment for service " + pricePoint.name);
			pp.setTransactionDescSeller("Opłata za usługę " + pricePoint.name);
		}
		
		updateCommonFields(pricePoint, loggedInUser, portalLang, pp, listing.getId());
		return pp;
	}
	
	private void updateCommonFields(PricePoint pricePoint, UserVO loggedInUser, LangVersion portalLang, PricePointVO pp, String id) {
		pp.setSellerId(MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_CUSTOMER_ID));
		updateAmounts(pp, pricePoint, portalLang);
		pp.setCrc(pricePoint.name + " " + id);
		
		String domain = null;
		String subdomain = FrontController.getCampaign().getSubdomain();
		boolean devEnvironment = false;
		if (com.google.appengine.api.utils.SystemProperty.environment.value() == com.google.appengine.api.utils.SystemProperty.Environment.Value.Development) {
			domain = subdomain + ".localhost:7777";
			devEnvironment = true;
		} else {
			domain = subdomain + ".inwestujwfirmy.pl";
		}
		String returnUrl = pricePoint.successUrl.replace("<domain>", domain);
		returnUrl = returnUrl.replace("<id>", id);
		pp.setReturnUrlSuccess(returnUrl);
		pp.setReturnUrlFailure("http://" + domain + "/error-page.html");
		
		if (devEnvironment || StringUtils.equals("true", MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_FREE_USAGE))) {
			pp.setActionUrl("http://" + domain + "/system/transaction_confirmation.html");
		} else {
			pp.setActionUrl(MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_ACTION_URL));
		}
		
		pp.setUserEmail(loggedInUser.getEmail());
		pp.setUserName(loggedInUser.getName());
		pp.setUserPhone(loggedInUser.getPhone());
		updateMd5(pp);
	}
	
	private void updateMd5(PricePointVO pp) {
		String md5string = pp.getSellerId() + pp.getAmount() + pp.getCrc()
				+ MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_SECURITY_CODE);
		pp.setMd5sum(DigestUtils.md5Hex(md5string));
	}
	
	private void updateAmounts(PricePointVO pp, PricePoint pricePoint, LangVersion portalLang) {
		if (StringUtils.equals("true", MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_FREE_USAGE))) {
			pp.setValueDisplayed(null);
			pp.setAmount("0.00");
		} else {
			String value = "" + (pricePoint.amount / 100);
			value += portalLang == LangVersion.PL ? "," : ".";
			value += pricePoint.amount % 100 > 9 ? (pricePoint.amount % 100) : "0" + (pricePoint.amount % 100);
			value += " " + pricePoint.currency;
			pp.setValueDisplayed(value);
			
			value = "" + (pricePoint.amount / 100) + ".";
			value += pricePoint.amount % 100 > 9 ? (pricePoint.amount % 100) : "0" + (pricePoint.amount % 100);
			pp.setAmount(value);
		}
	}

	public void storeTransaction(Transaction trans) {
		trans = getDAO().storeTransaction(trans);
		String[] crcData = trans.crc.split(" ");
		PricePoint.Codes code = PricePoint.Codes.valueOf(crcData[0]);
		
		SBUser user = null;
		switch(code) {
		case INV_REG:
			user = getDAO().getUser(crcData[1]);
			log.info("User '" + user.email + "' has requested investor badge with transaction: " + trans);
			requestDragon(DtoToVoConverter.convert(user));
			break;
		case CMP_1MT:
		case CMP_6MT:
		case CMP_1Y:
			Campaign campaign = MemCacheFacade.instance().getCampaignById(crcData[1]);
			user = getDAO().getUser(campaign.creator.getString());
			campaign.paidCode = code.toString();
			campaign.status = Status.ACTIVE;
			if (code == Codes.CMP_1MT) {
				campaign.activeTo = DateUtils.addDays(campaign.activeFrom, 31);
			} else if (code == Codes.CMP_6MT) {
				campaign.activeTo = DateUtils.addDays(campaign.activeFrom, 183);
			} else {
				campaign.activeTo = DateUtils.addDays(campaign.activeFrom, 365);
			}
			campaign.comment += "Activated by transaction " + trans.id + " done by user " + trans.email;
			getDAO().storeCampaign(campaign);
			log.info("Campaign '" + campaign.subdomain + "' has been activated by user " + user.email + " with transaction: " + trans);
			break;
		case PRJ_ACT:
		case PRJ_BP:
		case PRJ_PPT:
		case PRJ_ALL:
			Listing listing = getDAO().getListing(crcData[1]);
			user = getDAO().getUser(listing.owner.getString());
			listing.paidCode = listing.paidCode == null ? code.toString() : listing.paidCode + " " + code.toString();
			listing.notes += "User " + user.email + " has paid " + code + " for listing " + new Date() + ", transaction " + trans.id + ".\n";
			getDAO().storeListing(listing);
			if (listing.state == State.NEW) {
				ListingFacade.instance().postListing(DtoToVoConverter.convert(user), listing.getWebKey());
			}
			break;
		case NONE:
			// nothing will happen
		}
	}
	
	public PricePoint storePricepoint(PricePoint pp) {
		pp = getDAO().storePricePoint(pp);
		MemCacheFacade.instance().cleanPricePointsCache();
		return pp;
	}

}
