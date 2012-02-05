package space.util;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Images {
	
	public static final String SHEET_1_PATH = "res/tex/sprites.png"; 
	public static final String ATMOSPHERE_PATH = "res/tex/atmos.png";
	
	static final int FILMGRAIN_WIDTH = 320;
	static final int FILMGRAIN_HEIGHT = 238;
	static final int FILMGRAIN_SPACING = 1;
	private static Image sheet1, atmosphere;
	
	private static HashMap<String, Image> images = new HashMap<String, Image>();
	
	public static void create() throws SlickException {
		
		images.put("sheet", sheet1=new Image(SHEET_1_PATH));
		images.put("atmosphere", atmosphere=new Image(ATMOSPHERE_PATH));
	}
	
	public static Image get(String key) {
		return images.get(key);
	}
	
	/**
	 * Once the sheets are loaded, call this to initialize the individual sprites.
	 */
	public static void initSprites() {
		images.put("menu.paper", sheet1.getSubImage(0, 0, 686, 954));
		images.put("menu.bg", sheet1.getSubImage(690, 0, 150, 150));
		images.put("menu.alphaMap", sheet1.getSubImage(690, 153, 256, 256));
		
	}
}
