package space.game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Holds some info about the game and its main states.
 * @author Matt
 */
public interface GameContext {
	
	public static final int DETAIL_LOWEST = 0;
	public static final int DETAIL_LOW = 1;
	public static final int DETAIL_MEDIUM = 2;
	public static final int DETAIL_HIGH = 3;
	
	public int getWidth();
	public int getHeight();
	public GameContainer getContainer();
	
	/**
	 * To increase performance, the game uses a number of 
	 * @return
	 */
	public Image getOffscreenImage(int ptr);
	public Graphics getOffscreenGraphics(int ptr);
	public int getOffscreenImageCount();
	
	public void enterMainMenu();
	public void enterGame();
	
	public int getDetailLevel();
}
