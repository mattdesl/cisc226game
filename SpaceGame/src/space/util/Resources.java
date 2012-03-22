package space.util;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import space.GameContext;
import space.engine.SpriteFont;

public class Resources {
	
	private static SimpleSpriteSheet sheet1; 
	private static AngelCodeFont font1, font2, font3, font4;
	
	public static void create() throws SlickException {
		Image img = new Image("res/sprites.png", false, Image.FILTER_NEAREST);
		sheet1 = new SimpleSpriteSheet("res/sprites.sheet", img);
		font1 = new AngelCodeFont("res/fonts/square.fnt", getSprite("font.square"));
		font1.setSingleCase(true);
		font2 = new AngelCodeFont("res/fonts/small.fnt", getSprite("font.small"));
		font2.setSingleCase(true);
		font3 = new AngelCodeFont("res/fonts/tiny.fnt", getSprite("font.tiny"));
		font4 = new AngelCodeFont("res/fonts/nice.fnt", getSprite("font.nice"));
	}
	
	public static Image getSprite(String key) {
		return sheet1.getSprite(key);
	}
	
	public static AngelCodeFont getSquareFont() {
		return font1;
	}
	
	public static AngelCodeFont getSmallFont() {
		return font2;
	}
	
	public static AngelCodeFont getMonospacedFont() {
		return font3;
	}
	
	public static AngelCodeFont getNiceFont() {
		return font4;
	}

}
