package space.util;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import space.GameContext;
import space.engine.SpriteFont;

public class Resources {
	
	private static SimpleSpriteSheet sheet1; 
	private static SpriteFont font1, font2, font3, font4;
	
	public static void create() throws SlickException {
		Image img = new Image("res/sprites.png", false, Image.FILTER_NEAREST);
		sheet1 = new SimpleSpriteSheet("res/sprites.sheet", img);
		font1 = new SpriteFont("res/fonts/square.fnt", getSprite("font.square"), SpriteFont.CASE_INSENSITIVE);
		font2 = new SpriteFont("res/fonts/small.fnt", getSprite("font.small"), SpriteFont.CASE_INSENSITIVE);
		font3 = new SpriteFont("res/fonts/tiny.fnt", getSprite("font.tiny"));
		font4 = new SpriteFont("res/fonts/nice.fnt", getSprite("font.nice"));
	}
	
	public static Image getSprite(String key) {
		return sheet1.getSprite(key);
	}
	
	public static SpriteFont getFont1() {
		return font1;
	}
	
	public static SpriteFont getFont2() {
		return font2;
	}
	
	public static SpriteFont getMonospacedFont() {
		return font3;
	}
	
	public static SpriteFont getFont4() {
		return font4;
	}

}
