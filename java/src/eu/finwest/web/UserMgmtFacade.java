package eu.finwest.web;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
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
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
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
import eu.finwest.datamodel.PricePoint.Codes;
import eu.finwest.datamodel.PricePoint.Group;
import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.SystemProperty;
import eu.finwest.datamodel.Transaction;
import eu.finwest.datamodel.UserStats;
import eu.finwest.datamodel.VoToModelConverter;
import eu.finwest.util.FacebookUser;
import eu.finwest.util.ImageHelper;
import eu.finwest.util.Translations;
import eu.finwest.vo.BaseVO;
import eu.finwest.vo.CampaignVO;
import eu.finwest.vo.DtoToVoConverter;
import eu.finwest.vo.ErrorCodes;
import eu.finwest.vo.ListPropertiesVO;
import eu.finwest.vo.ListingTileVO;
import eu.finwest.vo.ListingVO;
import eu.finwest.vo.NewCampaignVO;
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
			result.setErrorMessage(Translations.getText("lang_error_user_not_found"));
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
		if (StringUtils.isNotEmpty(user.googleId)) {
			if (StringUtils.isEmpty(user.avatarUrl) || user.avatarUpdateDate == null
					|| user.avatarUpdateDate.getTime() < new DateTime().toDateMidnight().minusDays(7).getMillis()) {
				user.avatarUrl = ImageHelper.getGooglePlusAvatarUrl(googleUser);
				user.avatarUpdateDate = new Date();
				needsUpdate =  true;
			}
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
			log.warning("Password is too short: " + (password == null ? 0 : password.length()));
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
	public UserAndUserVO createUser(String email, String password, String name, String location, boolean investor) {
		UserAndUserVO result = new UserAndUserVO();
		if (!validateEmailAddress(email)) {
			result.setErrorCode(101);
			result.setErrorMessage(Translations.getText("lang_error_not_valid_email"));
			return result;
		}
		if (!validatePassword(email, password, name)) {
			result.setErrorCode(102);
			result.setErrorMessage(Translations.getText("lang_error_not_valid_password"));
			return result;
		}
		String encryptedPassword = encryptPassword(password);
		if (encryptedPassword == null) {
			result.setErrorCode(103);
			result.setErrorMessage(Translations.getText("lang_error_password_encryption"));
			return result;
		}
		String authCookie = encryptPassword(encryptedPassword + new Date().getTime());
        
		SBUser user = getDAO().getUserByEmail(email);
		if (user != null) {
            log.info("User with email '" + email + "' already exists: " + user);
            if (user.status == SBUser.Status.ACTIVE) {
            	// user already active, probably registered via google login
            	if (user.emailActivationDate != null) {
            		// user is already registered for email/pass usage
        			result.setErrorCode(104);
        			result.setErrorMessage(Translations.getText("lang_error_user_already_registered"));
        			return result;
            	}
            } else {
            	// user not yet active, probably previous registration was not finished
            }
        } else {
            user = new SBUser();
            user.email = email;
    		user.name = name;
    		user.status = SBUser.Status.CREATED;
    		user.investor = false;
        }
        if (name != null) {
            getDAO().generateNickname(user, name);
        }
        else if (email != null) {
            getDAO().generateNickname(user, email);
        }
        else {
            getDAO().generateRandomNickname(user);
        }

		user.password = encryptedPassword;
		user.authCookie = authCookie;
		user.location = location;
        user.modified = user.lastLoggedIn = user.joined = new Date();
		user.activationCode = "" + DigestUtils.md5Hex(email + user.joined.toString() + "25kj352025sfg");
		
		user = getDAO().saveUser(user);
		
		EmailService.instance().sendAccountActivation(user);
		
		UserVO userVO = DtoToVoConverter.convert(user);
		applyUserStatistics(userVO, userVO);
		result.setUser(userVO);
		return result;
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
			log.info("User not logged in or is not an admin");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage(Translations.getText("lang_error_user_not_admin"));
			return result;
		}
		SBUser user = getDAO().getUser(userId);
		if (user == null) {
			log.warning("User with id '" + userId + "' not found");
			result.setErrorCode(ErrorCodes.ENTITY_VALIDATION);
			result.setErrorMessage(Translations.getText("lang_error_user_not_found"));
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
			log.info("User not logged in");
			result.setErrorCode(ErrorCodes.NOT_LOGGED_IN);
			result.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
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
			result.setErrorMessage(Translations.getText("lang_error_user_already_requested_dragon"));
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
	
	public UserVO updateUserRecentData(UserVO loggedInUser, String currentDomain, LangVersion recentLang) {
		SBUser user = getDAO().getUser(loggedInUser.getId());
		if (user != null && !StringUtils.equalsIgnoreCase(user.recentDomain, currentDomain)) {
			user.recentDomain = currentDomain;
			user.recentLang = recentLang;
			getDAO().storeUser(user);
		}
		return loggedInUser;
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
	
	public Object findUser(UserVO loggedInUser, String query) {
		UserShortListVO userList = new UserShortListVO();
		if (loggedInUser == null) {
			log.warning("Only logged in user can search for users");
			userList.setUsers(new ArrayList<UserShortVO>());
			return userList;
		}
		
		List<SBUser> users = new ArrayList<SBUser>();
		List<SBUser> allUsers = getDAO().getAllUsers();
		int index = 1;
		for (SBUser user : allUsers) {
			if (StringUtils.startsWithIgnoreCase(user.nicknameLower, query)) {
				users.add(user);
			} else if (StringUtils.startsWithIgnoreCase(user.email, query)) {
				users.add(user);
			} else if (StringUtils.startsWithIgnoreCase(user.googleEmail, query)) {
				users.add(user);
			}
			if (index > 7) {
				break;
			}
		}
		
		userList.setUsers(DtoToVoConverter.convertShortUsers(users));
		userList.setUsersProperties(null);
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
		SBUser user = getDAO().getUserByActivationCode(activationCode);
		if (user != null) {
			if (user.status == SBUser.Status.ACTIVE || user.emailActivationDate != null) {
				return DtoToVoConverter.convert(user);
			}
			user.status = SBUser.Status.ACTIVE;
			user.emailActivationDate = new Date();
			getDAO().saveUser(user);

			UserVO userVO = DtoToVoConverter.convert(user);
			applyUserStatistics(userVO);
			return userVO;
		}
		return null;
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

	public NewCampaignVO storeCampaign(UserVO loggedInUser, CampaignVO campaign) {
		NewCampaignVO result = new NewCampaignVO();
		if (loggedInUser == null) {
			log.info("Not logged in or user is not admin/investor");
			result.setErrorCode(305);
			result.setErrorMessage(Translations.getText("lang_error_user_not_logged_in"));
			return result;
		}
		if (!(loggedInUser.isAccreditedInvestor() || loggedInUser.isAdmin())) {
			log.info("Not logged in or user is not admin/investor");
			result.setErrorCode(306);
			result.setErrorMessage(Translations.getText("lang_error_user_not_investor"));
			return result;
		}
		if (StringUtils.equalsIgnoreCase(campaign.getSubdomain(), "pl")
				|| StringUtils.equalsIgnoreCase(campaign.getSubdomain(), "en")) {
			log.info("User cannot update special campaigns");
			result.setErrorCode(306);
			result.setErrorMessage(Translations.getText("lang_error_user_not_admin"));
			return result;
		}
		Campaign existingCampaign = getDAO().getCampaignByDomain(campaign.getSubdomain());
		if (existingCampaign != null && (existingCampaign.creator.getId() != loggedInUser.toKeyId() || !loggedInUser.isAdmin())) {
			log.info("User is not an admin of the campaign, creator: " + existingCampaign.creatorName + ", logged in user: " + loggedInUser);
			result.setErrorCode(306);
			result.setErrorMessage(Translations.getText("lang_error_campaign_user_not_owner"));
			return result;
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
		result.setNewCampaign(campaign);
		MemCacheFacade.instance().cleanCampaingsCache();
		return result;
	}
	
	public List<PricePointVO> getPricePoints(UserVO loggedInUser, List<CampaignVO> campaigns) {
		List<PricePointVO> list = new ArrayList<PricePointVO>();
		if (loggedInUser == null) {
			log.info("User not logged in, returning empty pricepoints.");
		} else {
			PricePoint.Codes code = PricePoint.Codes.valueOf(loggedInUser.getPaidCode() != null ? loggedInUser.getPaidCode() : "NONE");
			if (code == Codes.NONE && !loggedInUser.isAccreditedInvestor()) {
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
		Collections.sort(list, PricePointVO.BY_ORDER);
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
		}
		Collections.sort(list, PricePointVO.BY_ORDER);
		log.info("Returning " + list.size() + " pricepoints for user " + loggedInUser.getName());
		return list;
	}
	
	private PricePointVO preparePricePointData(PricePoint pricePoint, UserVO loggedInUser, CampaignVO campaign, LangVersion portalLang) {
		PricePointVO pp = new PricePointVO();
		pp.setTransactionDescClient(Translations.getText(portalLang, "lang_payment_client_campaign_activation"));
		pp.setTransactionDescSeller(Translations.getText(portalLang, "lang_payment_seller_campaign_activation") + " " + campaign.getSubdomain());

		updateCommonFields(pricePoint, loggedInUser, portalLang, pp, campaign.getId());
		return pp;
	}

	private PricePointVO preparePricePointData(PricePoint pricePoint, UserVO loggedInUser, LangVersion portalLang) {
		PricePointVO pp = new PricePointVO();
		pp.setTransactionDescClient(Translations.getText(portalLang, "lang_payment_client_investor_registration"));
		pp.setTransactionDescSeller(Translations.getText(portalLang, "lang_payment_seller_investor_registration") + " " + loggedInUser.getEmail());
		
		updateCommonFields(pricePoint, loggedInUser, portalLang, pp, loggedInUser.getId());
		return pp;
	}

	private PricePointVO preparePricePointData(PricePoint pricePoint, UserVO loggedInUser, ListingVO listing, LangVersion portalLang) {
		PricePointVO pp = new PricePointVO();
		pp.setTransactionDescClient(Translations.getText(portalLang, "lang_payment_client_project_service"));
		pp.setTransactionDescSeller(Translations.getText(portalLang, "lang_payment_seller_project_service"));

		updateCommonFields(pricePoint, loggedInUser, portalLang, pp, listing.getId());
		return pp;
	}
	
	private void updateCommonFields(PricePoint pricePoint, UserVO loggedInUser, LangVersion portalLang, PricePointVO pp, String id) {
		boolean paymentFreeUsage = MemCacheFacade.instance().getSystemProperty(true, SystemProperty.PAYMENT_FREE_USAGE);
		boolean paymentFreeInvestorReg = MemCacheFacade.instance().getSystemProperty(true, SystemProperty.PAYMENT_FREE_INVESTOR_REG);
		boolean freeUsage = (pricePoint.type != PricePoint.Type.INVESTOR_REGISTRATION && paymentFreeUsage)
				|| (pricePoint.type == PricePoint.Type.INVESTOR_REGISTRATION && paymentFreeInvestorReg);
		
		boolean developEnv = com.google.appengine.api.utils.SystemProperty.environment.value() == com.google.appengine.api.utils.SystemProperty.Environment.Value.Development;
		String domain = null;
		String protocol = "https://";
		String subdomain = FrontController.getCampaign().getSubdomain();
		boolean devEnvironment = false;
		if (developEnv) {
			domain = subdomain + ".localhost:7777";
			protocol = "http://";
			devEnvironment = true;
		} else {
			domain = subdomain + ".inwestujwfirmy.pl";
		}
		
		pp.setDescription(portalLang == LangVersion.PL ? pricePoint.descriptionPl : pricePoint.descriptionEn);
		pp.setButtonText(Translations.getText(portalLang, freeUsage ? "lang_payment_free_usage_button" : "lang_payment_pay_button"));		
		pp.setPaymentLanguage(portalLang.name().toLowerCase());

		pp.setSellerId(MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_CUSTOMER_ID));
		updateAmounts(pp, pricePoint, portalLang, freeUsage);
		pp.setCrc(pricePoint.name + " " + id);

		String returnUrl = pricePoint.successUrl.replace("<domain>", domain);
		returnUrl = returnUrl.replace("<id>", id);
		pp.setReturnUrlSuccess(returnUrl);
		pp.setReturnUrlFailure(protocol + domain + "/error-page.html");
		
		if (devEnvironment || freeUsage) {
			pp.setActionUrl(protocol + domain + "/system/transaction_confirmation.html");
		} else {
			pp.setActionUrl(MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_ACTION_URL));
		}
		
		pp.setUserEmail(loggedInUser.getEmail());
		pp.setUserName(loggedInUser.getName());
		pp.setUserPhone(loggedInUser.getPhone());
		updateMd5(pp);
		
		pp.setOrder(pricePoint.type.ordinal());
	}
	
	private void updateMd5(PricePointVO pp) {
		String md5string = pp.getSellerId() + pp.getAmount() + pp.getCrc()
				+ MemCacheFacade.instance().getSystemProperty(SystemProperty.PAYMENT_SECURITY_CODE);
		pp.setMd5sum(DigestUtils.md5Hex(md5string));
	}
	
	private void updateAmounts(PricePointVO pp, PricePoint pricePoint, LangVersion portalLang, boolean freeUsage) {
		if (freeUsage) {
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
			user.investor = true;
			user.paidCode = user.paidCode == null ? code.toString() : user.paidCode + " " + code.toString();
			getDAO().storeUser(user);
			
			NotificationFacade.instance().scheduleUserDragonRequestNotification(user);
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

	public UserAndUserVO requestResetPassword(UserVO loggedInUser, String email) {
		UserAndUserVO result = new UserAndUserVO();
		result.setUser(loggedInUser);
		if (StringUtils.isBlank(email)) {
			log.info("Email is blank");
			result.setErrorCode(1001);
			result.setErrorMessage(Translations.getText("password_reset_request_blank_email"));
			return result;
		}
		
		SBUser user = getDAO().getUserByEmail(email);
		if (user == null) {
			log.info("User with email " + email + " not found");
			result.setErrorCode(1002);
			result.setErrorMessage(Translations.getText("password_reset_request_email_not_found"));
		} else if (user.status == SBUser.Status.CREATED) {
			log.info("User with email " + email + " is not active");
			result.setErrorCode(1003);
			result.setErrorMessage(Translations.getText("password_reset_request_activate_user_first"));
		} else if (user.status == SBUser.Status.DEACTIVATED) {
			log.info("User with email " + email + " is deactivated");
			result.setErrorCode(1004);
			result.setErrorMessage(Translations.getText("password_reset_request_user_deactivated"));
		} else {
			user.activationCode = "" + DigestUtils.md5Hex(email + new Date().toString() + "25kj352025sfg");
			log.info("New activationCode for " + user.email + " is: " + user.activationCode);
			getDAO().saveUser(user);
			if (!EmailService.instance().sendPasswordResetEmail(user)) {
				log.info("Error sending email for email " + email);
				result.setErrorCode(1005);
				result.setErrorMessage(Translations.getText("password_reset_request_email_not_sent"));
			}
		}
		return result;
	}
	
	public UserAndUserVO resetPassword(UserVO loggedInUser, String activationCode, String newPassword) {
		UserAndUserVO result = new UserAndUserVO();
		result.setUser(loggedInUser);
		if (StringUtils.isBlank(activationCode)) {
			log.info("Activation code has not been provided");
			result.setErrorCode(1001);
			result.setErrorMessage(Translations.getText("password_reset_blank_authcode"));
			return result;
		}
		
		SBUser user = getDAO().getUserByActivationCode(activationCode);
		if (user != null && user.status == SBUser.Status.ACTIVE && StringUtils.equals(activationCode, user.activationCode)) {
			if (!validatePassword(user.email, newPassword, user.name)) {
				result.setErrorCode(102);
				result.setErrorMessage(Translations.getText("lang_error_not_valid_password"));
				return result;
			}
			String encryptedPassword = encryptPassword(newPassword);
			if (encryptedPassword == null) {
				result.setErrorCode(103);
				result.setErrorMessage(Translations.getText("lang_error_password_encryption"));
				return result;
			}
			String authCookie = encryptPassword(encryptedPassword + new Date().getTime());
			user.password = encryptedPassword;
			user.authCookie = authCookie;
			user.activationCode = "";
			getDAO().saveUser(user);
		} else {
			if (user == null) {
				log.info("User for given activation code not found");
			} else if (user.status != SBUser.Status.ACTIVE) {
				log.info("User for given activation code is not active");
			} else if (!StringUtils.equals(activationCode, user.activationCode)) {
				log.info("Wrong activation code provided");
			}
			result.setErrorCode(1005);
			result.setErrorMessage(Translations.getText("password_reset_password_reset_cannot_be_done"));
		}
		
		return result;
	}

	public void updateUserListings(UserVO loggedInUser) {
		int count = getDAO().getUserListingsCount(loggedInUser.toKeyId());
		loggedInUser.setNumberOfListings(count);
	}

}
