package space;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import space.engine.SpriteBatch;
import space.engine.SpriteFont;
import space.state.InGameState;

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

	public int getDetailLevel();
	public boolean isDebugEnabled();

	public SpriteFont getDefaultFont();

	public Input getInput();

	public SpriteBatch getSpriteBatch();

	public void createShockwave(int x, int y);

	public InGameState getInGameState();

	public void enterGame();
	public void enterMenu(); //TODO
}