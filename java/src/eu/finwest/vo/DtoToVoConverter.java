package eu.finwest.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.googlecode.objectify.Key;

import eu.finwest.datamodel.Bid;
import eu.finwest.datamodel.BidUser;
import eu.finwest.datamodel.Campaign;
import eu.finwest.datamodel.Comment;
import eu.finwest.datamodel.Listing;
import eu.finwest.datamodel.ListingDoc;
import eu.finwest.datamodel.Monitor;
import eu.finwest.datamodel.Notification;
import eu.finwest.datamodel.PrivateMessage;
import eu.finwest.datamodel.PrivateMessageUser;
import eu.finwest.datamodel.QuestionAnswer;
import eu.finwest.datamodel.SBUser;
import eu.finwest.datamodel.SystemProperty;
import eu.finwest.datamodel.Vote;
import eu.finwest.util.Translations;
import eu.finwest.web.FrontController;
import eu.finwest.web.LangVersion;
import eu.finwest.web.MemCacheFacade;

/**
 * Helper class which converts DTO objects to VO objects.
 *
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class DtoToVoConverter {
	private static final Logger log = Logger.getLogger(DtoToVoConverter.class.getName());

	private static String keyToString(Key<?> key) {
		if (key != null) {
			return key.getString();
		} else {
			return null;
		}
	}

	private static String keyToString666(Key<?> key) {
		if (key != null) {
			if (key.getId() == 666) {
				return "importing";
			}
			return key.getString();
		} else {
			return null;
		}
	}

	public static PrivateMessageVO convert(PrivateMessage msgDTO) {
		if (msgDTO == null) {
			return null;
		}
		PrivateMessageVO qa = new PrivateMessageVO();
		qa.setText(msgDTO.text);
		qa.setCreated(msgDTO.created);
		qa.setDirection(msgDTO.direction == PrivateMessage.Direction.A_TO_B ? "sent" : "received");
		qa.setRead(msgDTO.read);
		return qa;
	}

	public static PrivateMessageUserVO convert(PrivateMessageUser msgDTO) {
		if (msgDTO == null) {
			return null;
		}
		PrivateMessageUserVO qa = new PrivateMessageUserVO();
		qa.setText(msgDTO.text);
		qa.setLastDate(msgDTO.created);
		qa.setDirection(msgDTO.direction == PrivateMessage.Direction.A_TO_B ? "sent" : "received");
		qa.setUser(msgDTO.userB.getString());
		qa.setUserNickname(msgDTO.userBNickname);
		qa.setCounter(msgDTO.counter);
		qa.setRead(msgDTO.read);
		return qa;
	}

	public static BidVO convert(Bid bidDTO) {
		if (bidDTO == null) {
			return null;
		}
		BidVO bid = new BidVO();
		bid.setText(bidDTO.text);
		bid.setCreated(bidDTO.created);
		bid.setRead(bidDTO.read);
		bid.setAmount(bidDTO.amount);
		bid.setPercentage(bidDTO.percentage);
		bid.setValue(bidDTO.value);
		bid.setType(bidDTO.type.toString());
		return bid;
	}

	public static AnonBidVO convertAnonBid(Bid bidDTO) {
		if (bidDTO == null) {
			return null;
		}
		AnonBidVO bid = new AnonBidVO();
		bid.setCreated(bidDTO.created);
		bid.setAmount(bidDTO.amount);
		bid.setPercentage(bidDTO.percentage);
		bid.setValue(bidDTO.value);
		bid.setType(bidDTO.type.toString());
		return bid;
	}

	public static BidUserVO convert(BidUser bidDTO) {
		if (bidDTO == null) {
			return null;
		}
		BidUserVO bidUser = new BidUserVO();
		bidUser.setText(bidDTO.text);
		bidUser.setLastDate(bidDTO.created);
		bidUser.setUser(bidDTO.userB.getString());
		bidUser.setUserNickname(bidDTO.userBNickname);
		bidUser.setCounter(bidDTO.counter);
		bidUser.setRead(bidDTO.read);
		bidUser.setAmount(bidDTO.amount);
		bidUser.setPercentage(bidDTO.percentage);
		bidUser.setValue(bidDTO.value);
		bidUser.setType(bidDTO.type.toString());
		return bidUser;
	}

	public static QuestionAnswerVO convert(QuestionAnswer qaDTO) {
		if (qaDTO == null) {
			return null;
		}
		QuestionAnswerVO qa = new QuestionAnswerVO();
		qa.setId(qaDTO.id != null ? new Key<QuestionAnswer>(QuestionAnswer.class, qaDTO.id).getString() : null);
		qa.setAnswer(qaDTO.answer);
		qa.setAnswerDate(qaDTO.answerDate);
		qa.setCreated(qaDTO.created);
		qa.setListing(keyToString(qaDTO.listing));
		qa.setPublished(qaDTO.published);
		qa.setQuestion(qaDTO.question);
		qa.setUser(keyToString(qaDTO.user));
		qa.setUserNickname(qaDTO.userNickname);
		return qa;
	}

	public static ListingVO convert(Listing listingDTO) {
		if (listingDTO == null) {
			return null;
		}
		ListingVO listing = new ListingVO();
		listing.setId(new Key<Listing>(Listing.class, listingDTO.id).getString());
		listing.setLang(listingDTO.lang != null ? listingDTO.lang.toString() : LangVersion.EN.toString());
		listing.setModified(listingDTO.modified);
		listing.setCreated(listingDTO.created);
		listing.setClosingOn(listingDTO.closingOn);
		listing.setListedOn(listingDTO.listedOn);
		listing.setPostedOn(listingDTO.posted);
		listing.setName(listingDTO.name);
		listing.setOwner(keyToString(listingDTO.owner));
		listing.setContactEmail(listingDTO.contactEmail);
		listing.setFounders(listingDTO.founders);
		listing.setCurrency(listingDTO.currency.toString());
		listing.setHasBmc(listingDTO.hasBmc);
		listing.setHasIp(listingDTO.hasIp);
		listing.setAskedForFunding(listingDTO.askedForFunding);
		listing.setSuggestedValuation(listingDTO.suggestedValuation);
		listing.setSuggestedAmount(listingDTO.suggestedAmount);
		listing.setSuggestedPercentage(listingDTO.suggestedPercentage);
		listing.setState(listingDTO.state.toString());
		listing.setType(listingDTO.type.toString());
		listing.setPlatform(listingDTO.platform);
		listing.setStage(listingDTO.stage != null ? listingDTO.stage.toString() : null);
		listing.setPresentationId(keyToString(listingDTO.presentationId));
		listing.setPresentationGenId(keyToString(listingDTO.presentationGenId));
		listing.setBuinessPlanId(keyToString(listingDTO.businessPlanId));
		listing.setFinancialsId(keyToString(listingDTO.financialsId));
		listing.setLogo(listingDTO.logoBase64);
		listing.setVideo(listingDTO.videoUrl);
		listing.setPic1(keyToString666(listingDTO.pic1Id));
		listing.setPic2(keyToString666(listingDTO.pic2Id));
		listing.setPic3(keyToString666(listingDTO.pic3Id));
		listing.setPic4(keyToString666(listingDTO.pic4Id));
		listing.setPic5(keyToString666(listingDTO.pic5Id));
		listing.setSummary(listingDTO.summary);
		listing.setMantra(listingDTO.mantra);
		listing.setWebsite(listingDTO.website);
		listing.setCategory(listingDTO.category);
		listing.setCategoryValue(MemCacheFacade.instance().getCategoryLabel(listingDTO.category));

		listing.setAddress(listingDTO.address);
		listing.setLatitude(listingDTO.latitude);
		listing.setLongitude(listingDTO.longitude);

		listing.setBriefAddress(listingDTO.briefAddress);
		
		listing.setCampaign(listingDTO.campaign);

		// calculating days left and days ago
		if (listingDTO.listedOn != null) {
			DateMidnight listed = new DateMidnight(listingDTO.listedOn.getTime());
			if (listed.isAfterNow()) {
				listing.setDaysAgo(-new Interval(new DateTime(), listed).toPeriod().getDays());
			} else {
				listing.setDaysAgo(new Interval(listed, new DateTime()).toPeriod().getDays());
			}
		} else {
			listing.setDaysAgo(0);
		}
		if (listingDTO.closingOn != null) {
			DateMidnight closing = new DateMidnight(listingDTO.closingOn.getTime()).plusDays(1);
			if (closing.isAfterNow()) {
				listing.setDaysLeft(new Interval(new DateTime(), closing).toPeriod().getDays());
			} else {
				listing.setDaysLeft(-new Interval(closing, new DateTime()).toPeriod().getDays());
			}
		} else {
			listing.setDaysLeft(0);
		}

		listing.setAnswer1(listingDTO.answer1);
		listing.setAnswer2(listingDTO.answer2);
		listing.setAnswer3(listingDTO.answer3);
		listing.setAnswer4(listingDTO.answer4);
		listing.setAnswer5(listingDTO.answer5);
		listing.setAnswer6(listingDTO.answer6);
		listing.setAnswer7(listingDTO.answer7);
		listing.setAnswer8(listingDTO.answer8);
		listing.setAnswer9(listingDTO.answer9);
		listing.setAnswer10(listingDTO.answer10);
		listing.setAnswer11(listingDTO.answer11);
		listing.setAnswer12(listingDTO.answer12);
		listing.setAnswer13(listingDTO.answer13);
		listing.setAnswer14(listingDTO.answer14);
		listing.setAnswer15(listingDTO.answer15);
		listing.setAnswer16(listingDTO.answer16);
		listing.setAnswer17(listingDTO.answer17);
		listing.setAnswer18(listingDTO.answer18);
		listing.setAnswer19(listingDTO.answer19);
		listing.setAnswer20(listingDTO.answer20);
		listing.setAnswer21(listingDTO.answer21);
		listing.setAnswer22(listingDTO.answer22);
		listing.setAnswer23(listingDTO.answer23);
		listing.setAnswer24(listingDTO.answer24);
		listing.setAnswer25(listingDTO.answer25);
		listing.setAnswer26(listingDTO.answer26);
		listing.setAnswer27(listingDTO.answer27);
		
		listing.setPaidCode(listingDTO.paidCode);
		listing.setValuationData(listingDTO.valuationData);
		return listing;
	}

	public static ListingTileVO convertTile(Listing listingDTO) {
		if (listingDTO == null) {
			return null;
		}
		ListingTileVO listing = new ListingTileVO();
		listing.setId(new Key<Listing>(Listing.class, listingDTO.id).getString());
		listing.setLang(listingDTO.lang.toString());
		listing.setModified(listingDTO.modified);
		listing.setCreated(listingDTO.created);
		listing.setClosingOn(listingDTO.closingOn);
		listing.setListedOn(listingDTO.listedOn);
		listing.setPostedOn(listingDTO.posted);
		listing.setName(listingDTO.name);
		listing.setOwner(keyToString(listingDTO.owner));
		listing.setCurrency(listingDTO.currency.toString());
		listing.setAskedForFunding(listingDTO.askedForFunding);
		listing.setSuggestedValuation(listingDTO.suggestedValuation);
		listing.setSuggestedAmount(listingDTO.suggestedAmount);
		listing.setSuggestedPercentage(listingDTO.suggestedPercentage);
		listing.setState(listingDTO.state.toString());
		listing.setLogo(listingDTO.logoBase64);
		listing.setSummary(listingDTO.summary);
		listing.setMantra(listingDTO.mantra);
		listing.setWebsite(listingDTO.website);
		listing.setCategory(listingDTO.category);
		listing.setCategoryValue(MemCacheFacade.instance().getCategoryLabel(listingDTO.category));
		listing.setType(listingDTO.type.toString());
        listing.setPlatform(listingDTO.platform != null ? listingDTO.platform.toString() : null);
        listing.setStage(listingDTO.stage != null ? listingDTO.stage.toString() : null);

		listing.setLatitude(listingDTO.latitude);
		listing.setLongitude(listingDTO.longitude);

		listing.setBriefAddress(listingDTO.briefAddress);

		return listing;
	}

	public static CommentVO convert(Comment commentDTO) {
		if (commentDTO == null) {
			return null;
		}
		CommentVO comment = new CommentVO();
		comment.setId(new Key<Comment>(Comment.class, commentDTO.id).getString());
		comment.setComment(commentDTO.comment);
		comment.setCommentedOn(commentDTO.commentedOn);
		comment.setListing(keyToString(commentDTO.listing));
		comment.setListingName(commentDTO.listingName);
		comment.setUser(keyToString(commentDTO.user));
		comment.setUserName(commentDTO.userNickName);
		return comment;
	}

	public static VoteVO convert(Vote ratingDTO) {
		if (ratingDTO == null) {
			return null;
		}
		VoteVO rating = new VoteVO();
		rating.setMockData(ratingDTO.mockData);
		rating.setId(new Key<Vote>(Vote.class, ratingDTO.id).getString());
		rating.setListing(keyToString(ratingDTO.listing));
		rating.setUser(keyToString(ratingDTO.user));
		rating.setValue(ratingDTO.value);
		return rating;
	}

	public static ListingDocumentVO convert(ListingDoc docDTO) {
		if (docDTO == null) {
			return null;
		}
		ListingDocumentVO doc = new ListingDocumentVO();
		doc.setId(docDTO.id != 0 ? new Key<ListingDoc>(ListingDoc.class, docDTO.id).getString() : null);
		doc.setMockData(docDTO.mockData);
		doc.setBlob(docDTO.blob);
		doc.setCreated(docDTO.created);
		doc.setType(docDTO.type.toString());
		switch(docDTO.type) {
		case PRESENTATION_GENERATED:
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy_MM_dd_HH_mm");
			switch (FrontController.getLangVersion()) {
			case EN:
				doc.setFileName("project_presentation_" + fmt.print(docDTO.created.getTime()) + ".pptx");
				break;
			case PL:
				doc.setFileName("prezentacja_projektu_" + fmt.print(docDTO.created.getTime()) + ".pptx");
				break;
			}
			break;
		default:
			doc.setFileName(StringUtils.lowerCase(docDTO.type.toString()));
			break;
		}

		return doc;
	}

	public static UserVO convert(SBUser userDTO) {
		if (userDTO == null) {
			return null;
		}
		UserVO user = new UserVO();
		user.setId(new Key<SBUser>(SBUser.class, userDTO.id).getString());
		user.setMockData(userDTO.mockData);
		user.setAdmin(userDTO.admin);
		user.setAccreditedInvestor(userDTO.investor);
		user.setUserClass(userDTO.userClass);
		user.setEmail(userDTO.email);
		user.setName(userDTO.name);
		user.setJoined(userDTO.joined);
		user.setLastLoggedIn(userDTO.lastLoggedIn);
		user.setModified(userDTO.modified);
		user.setNickname(userDTO.nickname);
		user.setPhone(userDTO.phone);
		user.setLocation(userDTO.location);
		user.setStatus(userDTO.status.toString());
		user.setNotifyEnabled(userDTO.notifyEnabled);
		user.setEditedListing(keyToString(userDTO.editedListing));
		user.setAvatar(userDTO.avatarUrl);
		user.setPaidCode(userDTO.paidCode);
		user.setRecentDomain(userDTO.recentDomain);
		return user;
	}

	public static SystemPropertyVO convert(SystemProperty propertyDTO) {
		if (propertyDTO == null) {
			return null;
		}
		SystemPropertyVO prop = new SystemPropertyVO();
		prop.setName(propertyDTO.name);
		prop.setValue(propertyDTO.value);
		prop.setAuthor(propertyDTO.author);
		prop.setCreated(propertyDTO.created);
		prop.setModified(propertyDTO.modified);
		return prop;
	}

	public static NotificationVO convert(Notification notifDTO) {
		if (notifDTO == null) {
			return null;
		}
		NotificationVO notif = new NotificationVO();
		notif.setId(notifDTO.id != null ? new Key<Notification>(Notification.class, notifDTO.id).getString() : "");
		notif.setUser(notifDTO.user.getString());
		notif.setUserNickname(notifDTO.userNickname);
		notif.setUserEmail(notifDTO.userEmail);
		notif.setCreated(notifDTO.created);
		notif.setSentDate(notifDTO.sentDate);
		notif.setListing(notifDTO.listing != null ? notifDTO.listing.getString() : null);
		notif.setListingName(notifDTO.listingName);
		notif.setListingOwner(notifDTO.listingOwner);
		if (notifDTO.listingOwnerUser != null) {
			notif.setListingOwnerId(notifDTO.listingOwnerUser.getString());
		}
		notif.setListingMantra(notifDTO.listingMantra);
		notif.setListingCategory(notifDTO.listingCategory);
		notif.setListingBriefAddress(notifDTO.listingBriefAddress);
        if (notifDTO.type == null) { // guard against code not specifying type
            notifDTO.type = Notification.Type.PRIVATE_MESSAGE;
        }
		notif.setType(notifDTO.type.toString());
		notif.setRead(notifDTO.read);
		String listingLink = notifDTO.getTargetLink();
		notif.setLink(listingLink);
		// adding domain to the link
		listingLink = notif.getLink();
		String listingName = notifDTO.listingName;
		switch(notifDTO.type) {
		case NEW_LISTING:
			notif.setTitle(Translations.getText("notif_new_listing_title", listingName));
			String profileUrl = BaseVO.getServiceLocation() + "/profile-page.html?id=" + notifDTO.listingOwnerUser.getString();
			notif.setText1(Translations.getText("notif_new_listing_text", profileUrl, notifDTO.listingOwner));
			notif.setText2(notifDTO.message);
			notif.setText3(Translations.getText("notif_visit_listing", listingLink));
			break;
		case LISTING_ACTIVATED:
			notif.setTitle(Translations.getText("notif_listing_activated_title", listingName));
			notif.setText1(Translations.getText("notif_listing_activated_text", listingName));
			notif.setText2(notifDTO.message);
			notif.setText3(Translations.getText("notif_visit_listing", listingLink));
			break;
		case LISTING_FROZEN:
			notif.setTitle(Translations.getText("notif_listing_frozen_title", listingName));
			notif.setText1(Translations.getText("notif_listing_frozen_text", listingName));
            notif.setText2(notifDTO.message);
            notif.setText3(Translations.getText("notif_visit_listing", listingLink));
			break;
		case LISTING_WITHDRAWN:
			notif.setTitle(Translations.getText("notif_listing_withdrawn_title", listingName));
			notif.setText1(Translations.getText("notif_listing_withdrawn_text", listingName));
			notif.setText2(notifDTO.message);
			notif.setText3(Translations.getText("notif_visit_listing", listingLink));
			break;
        case LISTING_SENT_BACK:
            notif.setTitle(Translations.getText("notif_listing_sentback_title", listingName));
            notif.setText1(Translations.getText("notif_listing_sentback_text", listingName));
            notif.setText2(notifDTO.message);
            notif.setText3(Translations.getText("notif_visit_listing", listingLink));
            break;
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			notif.setTitle(Translations.getText("notif_comment_for_monitored_title", listingName));
			notif.setText1(Translations.getText("notif_comment_for_monitored_text", notifDTO.userNickname));
			notif.setText2(notifDTO.message);
			notif.setText3(Translations.getText("notif_comment_visit_listing", listingLink));
			break;
		case NEW_COMMENT_FOR_YOUR_LISTING:
			notif.setTitle(Translations.getText("notif_comment_for_your_listing_title", listingName));
			notif.setText1(Translations.getText("notif_comment_for_your_listing_text", notifDTO.userNickname));
			notif.setText2(notifDTO.message);
			notif.setText3(Translations.getText("notif_comment_visit_listing", listingLink));
			break;
		case ASK_LISTING_OWNER:
		    notif.setTitle(Translations.getText("notif_ask_owner_title", listingName));
            notif.setText1(Translations.getText("notif_ask_owner_text", listingName));
			notif.setText2(notifDTO.message);
			notif.setText3(Translations.getText("notif_visit_listing", listingLink));
			break;
		case PRIVATE_MESSAGE:
		    notif.setTitle(Translations.getText("notif_private_message_title"));
            notif.setText1(Translations.getText("notif_private_message_text", notifDTO.fromUserNickname));
		    notif.setText2(notifDTO.message);
		    try {
		    	String messagePageUrl = BaseVO.getServiceLocation() + "/messages-page.html?from_user_id=" + notifDTO.listingOwnerUser.getString()
		    		+ "&from_user_nickname=" + notifDTO.fromUserNickname;
		    	notif.setText3(Translations.getText("notif_visit_coversation_with", messagePageUrl, notifDTO.fromUserNickname));
		    } catch (Exception e) {
		    	notif.setText3(Translations.getText("notif_visit_coversations", BaseVO.getServiceLocation() + "/message-group-page.html"));
		    }
			break;
		case NEW_BID_FOR_YOUR_LISTING:
			notif.setTitle(Translations.getText("notif_new_bid_title", listingName));
			notif.setText1(Translations.getText("notif_new_bid_text", listingName, notifDTO.userNickname));
			notif.setText2("");
			notif.setText3(Translations.getText("notif_visit_bids", listingLink));
			break;
		case YOUR_BID_WAS_COUNTERED:
			notif.setTitle(Translations.getText("notif_counter_offer_title", listingName));
			notif.setText1(Translations.getText("notif_counter_offer_text", listingName, notifDTO.userNickname));
			notif.setText2("");
			notif.setText3(Translations.getText("notif_visit_bids", listingLink));
			break;
		case YOU_ACCEPTED_BID:
			notif.setTitle(Translations.getText("notif_accepted_offer_title", listingName));
			notif.setText1(Translations.getText("notif_accepted_offer_text", listingName, notifDTO.userNickname));
			notif.setText2("");
			notif.setText3(Translations.getText("notif_visit_bids", listingLink));
			break;
		case YOUR_BID_WAS_ACCEPTED:
			notif.setTitle(Translations.getText("notif_your_bid_accepted_title", listingName));
			notif.setText1(Translations.getText("notif_your_bid_accepted_text", listingName));
			notif.setText2("");
			notif.setText3(Translations.getText("notif_visit_bids", listingLink));
			break;
		case BID_WAS_WITHDRAWN:
			notif.setTitle(Translations.getText("notif_bid_withdrawn_title", listingName));
			notif.setText1(Translations.getText("notif_bid_withdrawn_text", listingName));
			notif.setText2("");
			notif.setText3(Translations.getText("notif_visit_bids", listingLink));
			break;
		case YOUR_BID_WAS_REJECTED:
			notif.setTitle(Translations.getText("notif_bid_rejected_title", listingName));
			notif.setText1(Translations.getText("notif_bid_rejected_text", listingName));
			notif.setText2("");
			notif.setText3(Translations.getText("notif_visit_bids", listingLink));
			break;
		case YOU_PAID_BID:
		case BID_PAID_FOR_YOUR_LISTING:
			// payments are not handled yet
			break;
		case ADMIN_REQUEST_TO_BECOME_DRAGON:
		case USER_PROMOTED_TO_INVESTOR:
			notif.setTitle(Translations.getText("notif_user_promoted_to_investor_title", notifDTO.listingOwner));
			notif.setText1(Translations.getText("notif_user_promoted_to_investor_text"));
			notif.setText2("");
			notif.setText3(Translations.getText("notif_visit_profile",
					BaseVO.getServiceLocation() + "/profile-page.html?id=" + notifDTO.listingOwnerUser.getString()));
			notif.setLink("/profile-page.html?id=" + notifDTO.listingOwnerUser.getString());
			break;
		}

		return notif;
	}

	public static ShortNotificationVO convertShortNotification(Notification notifDTO) {
		if (notifDTO == null) {
			return null;
		}
		ShortNotificationVO notif = new ShortNotificationVO();
		notif.setCreated(notifDTO.created);
		notif.setListing(notifDTO.listing != null ? notifDTO.listing.getString() : null);
		notif.setType(notifDTO.type.toString());
		notif.setRead(notifDTO.read);
		switch(notifDTO.type) {
		case NEW_COMMENT_FOR_MONITORED_LISTING:
			notif.setTitle("New comment for listing " + notifDTO.listingName);
			notif.setText1("Listing " + notifDTO.listingName + " has received a new comment.");
			break;
		case NEW_COMMENT_FOR_YOUR_LISTING:
			notif.setTitle("New comment for listing " + notifDTO.listingName);
			notif.setText1("Your listing \"" + notifDTO.listingName + "\" has received a new comment.");
			break;
		case NEW_LISTING:
			notif.setTitle("New listing " + notifDTO.listingName + " posted");
			notif.setText1("A new listing " + notifDTO.listingName + " has been posted by " + notifDTO.listingOwner + " on inwestujwfirmy.pl");
			break;
        case ASK_LISTING_OWNER:
		    notif.setTitle("A question from " + notifDTO.fromUserNickname + " concerning listing " + notifDTO.listingName);
            notif.setText1("Question about listing " + notifDTO.listingName + " has been posted by " + notifDTO.fromUserNickname + ":");
			break;
		case PRIVATE_MESSAGE:
		    notif.setTitle("Private message from " + notifDTO.fromUserNickname + " concerning listing " + notifDTO.listingName );
		    notif.setText1("Private message from user " + notifDTO.fromUserNickname + ":");
			break;
		}

		return notif;
	}

	public static MonitorVO convert(Monitor monitorDTO) {
		if (monitorDTO == null) {
			return null;
		}
		MonitorVO monitor = new MonitorVO();
		monitor.setId(new Key<Monitor>(Monitor.class, monitorDTO.id).getString());
		monitor.setMockData(monitorDTO.mockData);
		monitor.setCreated(monitorDTO.created);
		monitor.setDeactivated(monitorDTO.deactivated);
		monitor.setUser(keyToString(monitorDTO.user));
		monitor.setListingId(monitorDTO.monitoredListing.getString());
		monitor.setActive(monitorDTO.active);
		return monitor;
	}

	public static List<ListingVO> convertListings(List<Listing> bpDtoList) {
		if (bpDtoList == null) {
			return null;
		}
		List<ListingVO> bpVoList = new ArrayList<ListingVO>();
		for (Listing bpDTO : bpDtoList) {
			ListingVO bpVO = convert(bpDTO);
			bpVoList.add(bpVO);
		}
		return bpVoList;
	}

	public static List<ListingTileVO> convertListingTiles(List<Listing> bpDtoList) {
		if (bpDtoList == null) {
			return null;
		}
		List<ListingTileVO> bpVoList = new ArrayList<ListingTileVO>();
		for (Listing bpDTO : bpDtoList) {
			bpVoList.add(convertTile(bpDTO));
		}
		return bpVoList;
	}

	public static List<CommentVO> convertComments(List<Comment> commentDtoList) {
		if (commentDtoList == null) {
			return null;
		}
		List<CommentVO> commentVoList = new ArrayList<CommentVO>();
		for (Comment commentDTO : commentDtoList) {
			CommentVO commentVO = convert(commentDTO);
			commentVoList.add(commentVO);
		}
		return commentVoList;
	}

	public static List<UserVO> convertUsers(List<SBUser> userDtoList) {
		if (userDtoList == null) {
			return null;
		}
		List<UserVO> userVoList = new ArrayList<UserVO>();
		for (SBUser userDTO : userDtoList) {
			UserVO userVO = convert(userDTO);
			userVoList.add(userVO);
		}
		return userVoList;
	}

	public static List<UserShortVO> convertShortUsers(List<SBUser> userDtoList) {
		if (userDtoList == null) {
			return null;
		}
		List<UserShortVO> userVoList = new ArrayList<UserShortVO>();
		for (SBUser userDTO : userDtoList) {
			UserVO userVO = convert(userDTO);
			userVoList.add(new UserShortVO(userVO));
		}
		return userVoList;
	}

	public static List<VoteVO> convertVotes(List<Vote> votesDtoList) {
		if (votesDtoList == null) {
			return null;
		}
		List<VoteVO> votesVoList = new ArrayList<VoteVO>();
		for (Vote voteDTO : votesDtoList) {
			VoteVO voteVO = convert(voteDTO);
			votesVoList.add(voteVO);
		}
		return votesVoList;
	}

	public static List<SystemPropertyVO> convertSystemProperties(List<SystemProperty> propertiesDtoList) {
		if (propertiesDtoList == null) {
			return null;
		}
		List<SystemPropertyVO> propertyVoList = new ArrayList<SystemPropertyVO>();
		for (SystemProperty propertyDTO : propertiesDtoList) {
			SystemPropertyVO propertyVO = convert(propertyDTO);
			propertyVoList.add(propertyVO);
		}
		return propertyVoList;
	}

	public static List<ListingDocumentVO> convertListingDocuments(List<ListingDoc> docDtoList) {
		if (docDtoList == null) {
			return null;
		}
		List<ListingDocumentVO> docVoList = new ArrayList<ListingDocumentVO>();
		for (ListingDoc docDTO : docDtoList) {
			ListingDocumentVO docVO = convert(docDTO);
			docVoList.add(docVO);
		}
		return docVoList;
	}

	public static List<NotificationVO> convertNotifications(List<Notification> notifDtoList) {
		if (notifDtoList == null) {
			return null;
		}
		List<NotificationVO> notifVoList = new ArrayList<NotificationVO>();
		for (Notification notifDTO : notifDtoList) {
			NotificationVO notifVO = convert(notifDTO);
			notifVoList.add(notifVO);
		}
		return notifVoList;
	}

	public static List<ShortNotificationVO> convertShortNotifications(List<Notification> notifDtoList) {
		if (notifDtoList == null) {
			return null;
		}
		List<ShortNotificationVO> notifVoList = new ArrayList<ShortNotificationVO>();
		for (Notification notifDTO : notifDtoList) {
			ShortNotificationVO notifVO = convertShortNotification(notifDTO);
			notifVoList.add(notifVO);
		}
		return notifVoList;
	}

	public static List<MonitorVO> convertMonitors(List<Monitor> monitorDtoList) {
		if (monitorDtoList == null) {
			return null;
		}
		List<MonitorVO> monitorVoList = new ArrayList<MonitorVO>();
		for (Monitor monitorDTO : monitorDtoList) {
			MonitorVO monitorVO = convert(monitorDTO);
			monitorVoList.add(monitorVO);
		}
		return monitorVoList;
	}

	public static List<QuestionAnswerVO> convertQuestionAnswers(List<QuestionAnswer> qaDtoList) {
		if (qaDtoList == null) {
			return null;
		}
		List<QuestionAnswerVO> qaVoList = new ArrayList<QuestionAnswerVO>();
		for (QuestionAnswer qaDTO : qaDtoList) {
			QuestionAnswerVO qaVO = convert(qaDTO);
			qaVoList.add(qaVO);
		}
		return qaVoList;
	}

	public static List<PrivateMessageVO> convertPrivateMessages(List<PrivateMessage> msgDtoList) {
		if (msgDtoList == null) {
			return null;
		}
		List<PrivateMessageVO> msgVoList = new ArrayList<PrivateMessageVO>();
		for (PrivateMessage msgDTO : msgDtoList) {
			PrivateMessageVO msgVO = convert(msgDTO);
			msgVoList.add(msgVO);
		}
		return msgVoList;
	}

	public static List<PrivateMessageUserVO> convertPrivateMessageUsers(List<PrivateMessageUser> msgDtoList) {
		if (msgDtoList == null) {
			return null;
		}
		List<PrivateMessageUserVO> qaVoList = new ArrayList<PrivateMessageUserVO>();
		for (PrivateMessageUser msgDTO : msgDtoList) {
			PrivateMessageUserVO msgVO = convert(msgDTO);
			qaVoList.add(msgVO);
		}
		return qaVoList;
	}

	public static List<BidVO> convertBids(List<Bid> bidDtoList) {
		if (bidDtoList == null) {
			return null;
		}
		List<BidVO> bidVoList = new ArrayList<BidVO>();
		for (Bid bidDTO : bidDtoList) {
			BidVO bidVO = convert(bidDTO);
			bidVoList.add(bidVO);
		}
		return bidVoList;
	}

	public static List<AnonBidVO> convertAnonBids(List<Bid> bidDtoList) {
		if (bidDtoList == null) {
			return null;
		}
		List<AnonBidVO> bidVoList = new ArrayList<AnonBidVO>();
		for (Bid bidDTO : bidDtoList) {
			AnonBidVO bidVO = convertAnonBid(bidDTO);
			bidVoList.add(bidVO);
		}
		return bidVoList;
	}

	public static List<BidUserVO> convertBidUsers(List<BidUser> bidDtoList) {
		if (bidDtoList == null) {
			return null;
		}
		List<BidUserVO> bidVoList = new ArrayList<BidUserVO>();
		for (BidUser bidDTO : bidDtoList) {
			BidUserVO bidVO = convert(bidDTO);
			bidVoList.add(bidVO);
		}
		return bidVoList;
	}

	public static void updateBriefAddress(Listing listingDTO) {
		String briefAddress = "";
		if (!StringUtils.isEmpty(listingDTO.country)) {
			briefAddress = listingDTO.country;
    		listingDTO.country = listingDTO.country.toLowerCase();
		}
		if (!StringUtils.isEmpty(listingDTO.usState) && briefAddress.equals("USA")) {
			briefAddress = listingDTO.usState + (briefAddress.length() > 0 ? ", " : "") + briefAddress;
    		listingDTO.usState = listingDTO.usState.toLowerCase();
		}
		if (!StringUtils.isEmpty(listingDTO.city)) {
			briefAddress = listingDTO.city + (briefAddress.length() > 0 ? ", " : "") + briefAddress;
    		listingDTO.city = listingDTO.city.toLowerCase();
		}
		listingDTO.briefAddress = briefAddress;
	}

	public static CampaignVO convert(Campaign campaign) {
		if (campaign == null) {
			return null;
		}
		CampaignVO campaignVO = new CampaignVO();
		campaignVO.setId(campaign.id != null ? new Key<Campaign>(Campaign.class, campaign.id).getString() : "");
		campaignVO.setSpecial(false);
		campaignVO.setActiveFrom(campaign.activeFrom);
		campaignVO.setActiveTo(campaign.activeTo);
		campaignVO.setAdmins(campaign.admins);
		campaignVO.setAllowedLanguage(campaign.allowedLanguage.toString());
		campaignVO.setComment(campaign.comment);
		campaignVO.setCreator(campaign.creatorName);
		campaignVO.setCreated(campaign.created);
		campaignVO.setDescription(campaign.description);
		campaignVO.setName(campaign.name);
		campaignVO.setPublicBrowsing(campaign.publicBrowsing);
		campaignVO.setSubdomain(campaign.subdomain);
		campaignVO.setStatus(campaign.status.toString());
		campaignVO.setPaidCode(campaign.paidCode);
		return campaignVO;
	}

	public static List<CampaignVO> convertCampaigns(List<Campaign> userCampaignsDto) {
		if (userCampaignsDto == null) {
			return null;
		}
		List<CampaignVO> campaignVoList = new ArrayList<CampaignVO>();
		for (Campaign campaignDTO : userCampaignsDto) {
			CampaignVO campaignVO = convert(campaignDTO);
			campaignVoList.add(campaignVO);
		}
		return campaignVoList;
	}

}
