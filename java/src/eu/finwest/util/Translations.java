/**
 * 
 */
package eu.finwest.util;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.finwest.web.FrontController;
import eu.finwest.web.LangVersion;

/**
 * @author grzegorznittner
 *
 */
public class Translations {
	private static final Logger log = Logger.getLogger(Translations.class.getName());
	
	private static Translations instance;
	
	public static Translations instance() {
		if (instance == null) {
			instance = new Translations();
		}
		return instance;
	}
	
	private ResourceBundle plTranslations, enTranslations;
	
	private Translations() {
		try {
			plTranslations = new PropertyResourceBundle(new InputStreamReader(new FileInputStream("pl.properties"), "UTF-8"));
			enTranslations = new PropertyResourceBundle(new InputStreamReader(new FileInputStream("en.properties"), "UTF-8"));
		} catch (Exception e) {
			log.log(Level.SEVERE, "Problem loading translations files", e);
		}
	}
	
	public static String getText(LangVersion lang, String key, Object... params) {
		return Translations.instance().getTranslation(lang, key, params);
	}

	public static String getText(LangVersion lang, String key) {
		return Translations.instance().getTranslation(lang, key);
	}

	public static String getText(String key, Object... params) {
		return Translations.instance().getTranslation(key, params);
	}

	public static String getText(String key) {
		return Translations.instance().getTranslation(FrontController.getLangVersion(), key);
	}

	public String getTranslation(String key) {
		return getTranslation(FrontController.getLangVersion(), key);
	}

	public String getTranslation(LangVersion lang, String key, Object... params) {
		return MessageFormat.format(getTranslation(lang, key), params);
	}
	
	public String getTranslation(String key, Object... params) {
		return MessageFormat.format(getTranslation(FrontController.getLangVersion(), key), params);
	}

	public String getTranslation(LangVersion lang, String key) {
		try {
			if (lang == LangVersion.PL) {
				return plTranslations.getString(key);
			} else if (lang == LangVersion.EN) {
				return enTranslations.getString(key);
			}
			return key;
		} catch (java.util.MissingResourceException e) {
			log.log(Level.SEVERE, "Missing translation key: " + key + " for " + lang);
			return key;
		}
	}
}
