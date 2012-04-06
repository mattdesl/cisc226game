package space.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class SimpleSpriteSheet {
	
	private HashMap<String, Image> sprites = new HashMap<String, Image>();
	private Image sheet;
	
	public SimpleSpriteSheet(String sheet, Image sheetImage) throws SlickException {
		this(Utils.getResource(sheet), sheetImage);
	}
	
	public SimpleSpriteSheet(URL sheet, Image sheetImage) throws SlickException {
		this.sheet = sheetImage;
		Properties p;
		try {
			InputStream in = sheet.openStream();
			p = new Properties();
			p.load(in);
			in.close();
		} catch (IOException e) {
			throw new SlickException("error loading sprite sheet "+e.getMessage(), e);
		}
		sprites = new HashMap<String, Image>();
		for (Map.Entry<Object, Object> e : p.entrySet()) {
			String k = String.valueOf(e.getKey());
			Object v = e.getValue();
			String[] s = v!=null ? v.toString().trim().split(",") : null; 
			sprites.put(k, toimg(k, s));
		}
	}
	
	private Image toimg(String key, String[] s) throws SlickException {
		if (s==null || s.length<4)
			throw new SlickException("invalid value for key "+key);
		try { 
			int x = Integer.parseInt(s[0]);
			int y = Integer.parseInt(s[1]);
			int w = Integer.parseInt(s[2]);
			int h = Integer.parseInt(s[3]);
			return sheet.getSubImage(x, y, w+1, h+1);
		} catch (NumberFormatException e) {
			throw new SlickException("invalid int values for key "+key+" "+Arrays.toString(s));
		}
	}
	
	public Image getSprite(String key) {
		return sprites.get(key);
	}
}
