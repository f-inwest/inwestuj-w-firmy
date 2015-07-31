package eu.finwest;

import java.util.regex.*;

/**
 * Created by grzegorznittner on 26.06.2014.
 */
public class TestClass {
    public static final String ANDROID_UA = "Mozilla/5.0 (Linux; U; Android 4.3; en-gb; SAMSUNG GT-I9300/I9300XXUGMJ9 Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
    public static final String IOS_UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_0_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Mobile/9A405";
    public static final String WP_UA = "Mozilla/5.0 (Windows Phone 8.1; ARM; Trident/7.0; Touch; rv:11.0; IEMobile/11.0; NOKIA; id313) like Gecko";

    public static final String ANDROID_VER_REGEXP = "android (\\d+(?:\\.\\d+)+)";
    public static final String IOS_VER_REGEXP = "os \\b([0-9]+_[0-9]+(?:_[0-9]+)?)\\b";
    public static final String WINDOWS_VER_REGEXP = "windows phone (\\d+(?:\\.\\d+)+)";

    public static void main(String arg[]) {
        Pattern p = null;
        Matcher m = null;

        System.out.println("android check: " + getOsVersion(ANDROID_UA));
        System.out.println("ios check: " + getOsVersion(IOS_UA));
        System.out.println("wp check: " + getOsVersion(WP_UA));
    }

    private static String getOsVersion(String userAgent) {
        Pattern p;
        Matcher m;
        if (userAgent.toLowerCase().contains("android")) {
            p = Pattern.compile(ANDROID_VER_REGEXP);
            m = p.matcher(userAgent.toLowerCase());
            if (m.find()) {
                return m.group();
            }
        }

        if (userAgent.toLowerCase().contains("iphone")) {
            p = Pattern.compile(IOS_VER_REGEXP);
            m = p.matcher(userAgent.toLowerCase());
            if (m.find()) {
                String version = m.group();
                return version.replace("_", ".");
            }
        }

        if (userAgent.toLowerCase().contains("windows phone")) {
            p = Pattern.compile(WINDOWS_VER_REGEXP);
            m = p.matcher(userAgent.toLowerCase());
            if (m.find()) {
                return m.group();
            }
        }
        return "invalid";
    }
}
