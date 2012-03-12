package space.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class GameText {
	
	static Properties i18n;
	public static final String TEXT_PATH = "res/text.properties";  
	
	public static void load() throws IOException {
		i18n = new Properties();
		InputStream f = Utils.getResourceAsStream(TEXT_PATH);
		if (f==null)
			throw new IOException("could not find text file at "+TEXT_PATH);
		i18n.load(f);
	}
	
	public static String get(String key, String def) {
		return i18n.getProperty(key, def);
	}
	
	public static String get(String key) {
		return i18n.getProperty(key);
	}

	public static float getFloat(String key, float def) {
		try {
			String s = get(key);
			if (s==null) return def;
			return Float.parseFloat(s); 
		} catch (NumberFormatException e) { return def; }
	}
	
	public static int getInteger(String key, int def) {
		try {
			String s = get(key);
			if (s==null) return def;
			return Integer.parseInt(s); 
		} catch (NumberFormatException e) { return def; }
	}
	
	public static boolean getBoolean(String key, boolean def) {
		String s = get(key);
		if (s==null) return def;
		return Boolean.parseBoolean(s);
	}
}
