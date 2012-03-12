package space.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import space.GameContext;
import space.engine.SpriteBatch;

public abstract class AbstractState extends BasicGameState {
	
	private int id;
	protected GameContext context;
	
	public AbstractState(GameContext context, int id) {
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
	
	public String getDebugText() {
		return "";
	}
	
	public void detailLevelChanged() {
	}
	
	public GameContext getContext() {
		return context;
	}

	/**
	 * Initialise the state. It should load any resources it needs at this stage
	 * 
	 * @param container The container holding the game
	 * @param game The game holding this state
	 * @throws SlickException Indicates a failure to initialise a resource for this state
	 */
	public final void init(GameContainer container, StateBasedGame game) throws SlickException {
		init(context);
	}
	
	/**
	 * Render this state to the game's graphics context
	 * 
	 * @param container The container holding the game
	 * @param game The game holding this state
	 * @param g The graphics context to render to
	 * @throws SlickException Indicates a failure to render an artifact
	 */
	public final void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		render(context, context.getSpriteBatch(), g);
	}
	
	/**
	 * Update the state's logic based on the amount of time thats passed
	 * 
	 * @param container The container holding the game
	 * @param game The game holding this state
	 * @param delta The amount of time thats passed in millisecond since last update
	 * @throws SlickException Indicates an internal error that will be reported through the
	 * standard framework mechanism
	 */
	public final void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		update(context, delta);
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#init(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame)
	 */
	public abstract void init(GameContext context) throws SlickException;

	/**
	 * @see org.newdawn.slick.state.BasicGameState#render(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public abstract void render(GameContext context, SpriteBatch batch, Graphics g) throws SlickException;

	/**
	 * @see org.newdawn.slick.state.BasicGameState#update(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, int)
	 */
	public abstract void update(GameContext context, int delta) throws SlickException;
}