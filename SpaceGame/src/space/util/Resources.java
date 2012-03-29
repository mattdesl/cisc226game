package space.util;

import java.io.IOException;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;

public class Resources {
	
	public static final int HEALTH_BAR_X_OFF = 5;
	public static final int HEALTH_BAR_Y_OFF = 5;
	
	private static SimpleSpriteSheet sheet1; 
	private static AngelCodeFont font1, font2, font3, font4;
	private static ParticleSystem shockBoom;
	
	public static void create() throws SlickException {
		Image img = new Image("res/sprites.png", false, Image.FILTER_NEAREST);
		sheet1 = new SimpleSpriteSheet("res/sprites.sheet", img);
		font1 = new AngelCodeFont("res/fonts/square.fnt", getSprite("font.square"));
		font1.setSingleCase(true);
		font2 = new AngelCodeFont("res/fonts/small.fnt", getSprite("font.small"));
		font2.setSingleCase(true);
		font3 = new AngelCodeFont("res/fonts/tiny.fnt", getSprite("font.tiny"));
		font4 = new AngelCodeFont("res/fonts/nice.fnt", getSprite("font.nice"));
		

		try {
			shockBoom = ParticleIO.loadConfiguredSystem("res/particle/shockboom.xml");
			shockBoom.setRemoveCompletedEmitters(true);
			shockBoom.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		} catch (IOException e) {
			throw new SlickException("error loading particle", e);
		}
	}
	
	public static ParticleSystem getBoomParticle() {
		return shockBoom;
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
