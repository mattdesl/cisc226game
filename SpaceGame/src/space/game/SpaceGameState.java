package space.game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class SpaceGameState extends BasicGameState {
	
	private int id;
	protected GameContext context;
	
	public SpaceGameState(GameContext context, int id) {
		this.id = id;
		this.context = context;
	}
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#getID()
	 */
	public int getID() {
		return id;
	}
	
	public void entering() {}
	public void entered() {}
	public void leaving() {}
	public void left() {}
	
	public void detailLevelChanged() {
	}
	
	public GameContext getContext() {
		return context;
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#init(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame)
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#render(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#update(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
	}
}