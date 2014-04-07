package eu.finwest.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.api.client.json.JsonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusRequestInitializer;
import com.google.api.services.plus.model.PeopleFeed;
import com.google.api.services.plus.model.Person;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import eu.finwest.datamodel.SystemProperty;
import eu.finwest.web.MemCacheFacade;

public class ImageHelper {
	private static final Logger log = Logger.getLogger(ImageHelper.class.getName());
	
	private final static byte[] JPEG = {(byte)0xff, (byte)0xd8 };
	private final static byte[] BMP = {(byte)0x42, (byte)0x4d };
	private final static byte[] GIF = {0x47, 0x49, 0x46, 0x38};
	private final static byte[] PNG = {(byte)0x89, 0x50, 0x4e, 0x47};
	private final static byte[] TIFF_II = {(byte)0x49, 0x49, 0x2a, 0x00};
	private final static byte[] TIFF_MM = {(byte)0x4d, 0x4d, 0x00, 0x2a};
	private final static byte[] WMF = {(byte)0x9a, (byte)0xc6, (byte)0xcd, (byte)0xd7};
	
	@SuppressWarnings("serial")
	public static class ImageFormatException extends Exception {
		public ImageFormatException(String message) {
			super(message);
		}
	}

	public static String checkMagicNumber(byte[] logo) throws ImageFormatException {
		byte[] premagicNumber = ArrayUtils.subarray(logo, 0, 2);
		byte[] magicNumber = ArrayUtils.subarray(logo, 0, 4);
		String format = "";
		if (ArrayUtils.isEquals(premagicNumber, JPEG)) {
			format = "image/jpeg";
		} else if (ArrayUtils.isEquals(premagicNumber, BMP)) {
			format = "image/bmp";
		} else if (ArrayUtils.isEquals(magicNumber, GIF)) {
			format = "image/gif";
		} else if (ArrayUtils.isEquals(magicNumber, PNG)) {
			format = "image/png";
		} else if (ArrayUtils.isEquals(magicNumber, TIFF_II)) {
			format = "image/tiff";
		} else if (ArrayUtils.isEquals(magicNumber, TIFF_MM)) {
			format = "image/tiff";
		} else if (ArrayUtils.isEquals(magicNumber, WMF)) {
			format = "image/wmf";
		} else {
			log.warning("Image not recognized as JPG, GIF or PNG. Magic number was: " + toHexString(magicNumber));
            throw new ImageFormatException("Image not recognized as JPG, GIF or PNG. Magic number was: " + toHexString(magicNumber));
		}
		return format;
	}
	
	public static String printStringAsHex(String text) {
		text = StringUtils.substring(text, 0, 32);
		StringBuffer buf = new StringBuffer();
		buf.append("Hex output:\n");
		for (int i = 0; i < text.length(); i++) {
			buf.append(text.charAt(i)).append("    ");
		}
		buf.append("\n");
		buf.append(toHexString(text.getBytes()));
		return buf.toString();
	}
	
	public static String toHexString(byte[] magicNumber) {
		StringBuffer buf = new StringBuffer();
		for (byte b : magicNumber) {
			buf.append("0x").append(Integer.toHexString((0xF0 & b) >>> 4)).append(Integer.toHexString(0x0F & b)).append(" ");
		}
		return buf.toString();
	}
	
	public static String toByteDefinition(byte[] magicNumber) {
		StringBuffer buf = new StringBuffer();
		buf.append("new byte[] {");
		int index = 0;
		for (byte b : magicNumber) {
			if ((index++) > 0) {
				buf.append(", ");
			}
			buf.append("(byte)0x").append(Integer.toHexString((0xF0 & b) >>> 4)).append(Integer.toHexString(0x0F & b)).append(" ");
		}
		buf.append("}");
		return buf.toString();
	}

	public static String getMimeTypeFromFileName(String propValue) {
		propValue = StringUtils.lowerCase(propValue);
		if (propValue.endsWith(".gif")) {
			return "image/gif";
		} else if (propValue.endsWith(".jpg")) {
			return "image/jpeg";
		} else if (propValue.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (propValue.endsWith(".png")) {
			return "image/png";
		} else if (propValue.endsWith(".bmp")) {
			return "image/bmp";
		} else if (propValue.endsWith(".tif")) {
			return "image/tiff";
		} else if (propValue.endsWith(".tiff")) {
			return "image/tiff";
		} else if (propValue.endsWith(".wmf")) {
			return "image/wmf";
		} else if (propValue.endsWith(".doc")) {
			return "application/msword";
		} else if (propValue.endsWith(".docx")) {
			return "application/msword";
		} else if (propValue.endsWith(".ppt")) {
			return "application/vnd.ms-powerpoint";
		} else if (propValue.endsWith(".xls")) {
			return "application/vnd.ms-excel";
		} else if (propValue.endsWith(".pdf")) {
			return "application/pdf";
		}
		return "";
	}
	
	private static String fetchUrl(String url) {
		try {
			log.info("Fetching Google Plus avatar url from " + url);
			return IOUtils.toString(new InputStreamReader(new URL(url).openStream()));
		} catch (Exception e) {
			log.log(Level.WARNING, "Error fetching avatar url from " + url, e);
			return null;
		}
	}

	public static String getGooglePlusAvatarUrl(com.google.appengine.api.users.User user) {
		String apiKey = MemCacheFacade.instance().getSystemProperty(SystemProperty.GOOGLE_API_KEY);
		if (StringUtils.isBlank(apiKey)) {
			apiKey = "AIzaSyCyUJqBsLWYgxQwnS6uOUC5yX8ASSN0P7o";
		}
		
		try {
			Plus plus = new Plus.Builder(new UrlFetchTransport(), new JacksonFactory(), (com.google.api.client.http.HttpRequestInitializer)null)
		    	.setApplicationName("").setGoogleClientRequestInitializer(new PlusRequestInitializer(apiKey)).build();
			Plus.People.Search searchPeople = plus.people().search(user.getNickname());
			searchPeople.setMaxResults(5L);
			PeopleFeed peopleFeed = searchPeople.execute();
			List<Person> people = peopleFeed.getItems();
			if (people == null || people.size() == 0) {
				return null;
			}
			String avatarJson = fetchUrl("https://www.googleapis.com/plus/v1/people/" + people.get(0).getId()
					+ "?fields=image&key=" + apiKey);
			log.info("Fetched Google Plus avatar json: " + avatarJson);
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode avatar = mapper.readValue(avatarJson, JsonNode.class);
			if (avatar.has("image") && avatar.get("image").has("url")) {
				String avatarUrl = avatar.get("image").get("url").getValueAsText();
				log.info("Fetched Google Plus avatar url: " + avatarUrl);
				return avatarUrl;
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Error fetching Google Plus avatar", e);
		}
		return null;
	}
	
	public static String getFacebookAvatarUrl(String facebookId) {
		String avatarUrl = fetchUrl("http://graph.facebook.com/" + facebookId + "/picture");
		log.info("Fetched Facebook avatar url: " + avatarUrl);
		return avatarUrl;
	}
	
	public static byte[] getBytesFromBlob(BlobKey blobKey) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

        // Start reading
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        long inxStart = 0;
        long inxEnd = 10240;
        boolean flag = false;

        do {
            try {
                byte[] b = blobstoreService.fetchData(blobKey,inxStart,inxEnd);
                out.write(b);

                if (b.length < 10240) {
                    flag = true;
                }
                inxStart = inxEnd + 1;
                inxEnd += 10250;
            } catch (Exception e) {
                flag = true;
            }

        } while (!flag);

        return out.toByteArray();
	}
}
