package space.tests.copy;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class FontTest extends BasicGame {


	FontTest() { super("FontTest!"); }
    public static void main(String[] args) throws SlickException {
        new AppGameContainer(new FontTest(), 800, 600, false).start();
    }
    
    UnicodeFont ttf;
	
    public void init(GameContainer container) throws SlickException {
		ttf = new UnicodeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 24));
		ColorEffect c = new ColorEffect(java.awt.Color.white);
		ttf.getEffects().add(c);
		ttf.addAsciiGlyphs();
	    ttf.loadGlyphs();
    	
	}
	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		ttf.drawString(50, 50, "test", Color.red);
	}
	
	public void update(GameContainer container, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}
    
    
}




