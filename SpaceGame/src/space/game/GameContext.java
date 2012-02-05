package space.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * Holds some info about the game and its main states.
 * @author Matt
 */
public interface GameContext {
	public int getWidth();
	public int getHeight();
	
	/**
	 * Returns an offscreen buffer that is shared between game states; the size is 1024x1024.
	 * @return
	 */
	public Image getOffscreenImage();
	public Graphics getOffscreenGraphics();
	
	public void enterMainMenu();
	public void enterGame();
}
