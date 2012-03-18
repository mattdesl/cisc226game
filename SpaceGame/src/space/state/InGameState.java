package space.state;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.World;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import space.GameContext;
import space.engine.SpriteBatch;
import space.engine.easing.Easing;
import space.engine.easing.SimpleFX;
import space.entities.Ship;
import space.sprite.StarfieldSprite;
import space.util.Utils;

public class InGameState extends AbstractState {

	public InGameState(GameContext context) {
		super(context, 1);
	}
	private World world; // the world!
	private Ship player;
	private StarfieldSprite starfield;
	private SimpleFX shakeFade = new SimpleFX(1f, 0f, 1000, Easing.QUAD_OUT);
	private boolean shake = false;
	private float shakeXAmt = 0f, shakeYAmt = 0f;
	private int shakeDelay = 0, shakeDelayMax = 30;
	
	public void keyPressed(int k, char c) {
		if (c=='1') {
			starfield.GRID_SIZE--;
			starfield.randomize(context);
		} else if (c=='2') {
			starfield.GRID_SIZE++;
			starfield.randomize(context);
		} else if (c=='3') {
			starfield.BIG_STAR_CHANCE--;
			starfield.randomize(context);
		} else if (c=='4') {
			starfield.BIG_STAR_CHANCE++;
			starfield.randomize(context);
		} else if (c=='5') {
			starfield.MEDIUM_STAR_CHANCE--;
			starfield.randomize(context);
		} else if (c=='6') {
			starfield.MEDIUM_STAR_CHANCE++;
			starfield.randomize(context);
		}
	}
	
	public void mouseMoved(int oldx, int oldy, int x, int y){
		player.setHeading(x, y);
	}
	
	@Override
	public void init(GameContext context) throws SlickException {
		starfield = new StarfieldSprite();
		starfield.randomize(context);
		world = new World(new Vector2f(0,0), 10);
		player = new Ship(new Image("res/ship.png"), 10f);
		player.setPosition(400,300);
		world.add(player.getBody());
	}
	
	@Override
	public void render(GameContext context, SpriteBatch batch, Graphics g)
			throws SlickException {		
		//apply the background which is clamped to the screen size
		starfield.drawBackground(context, batch, g);
		
		//apply a translation based on screen shake
		if (shake) {
			if (shakeXAmt!=0 && shakeYAmt!=0)
				batch.translate(shakeXAmt * shakeFade.getValue(), shakeYAmt * shakeFade.getValue());
		}
		
		//render our foreground elements that don't need to be clamped to the screen size
		starfield.drawStars(context, batch, g);
		float xOff = player.getVelX()/2.5f;
		float yOff = player.getVelY()/2.5f;
	// this isn't what i want. i want to just rotate the ship, but it rotates the entire background too.
    // how can we avoid this?
	//	g.rotate(player.getX(), player.getY(), (float)Math.toDegrees(player.getRotation())); 
		g.setAntiAlias(true); //necessary?
		player.draw(g, xOff, yOff, 1f);
		g.setAntiAlias(false);
	}
	
	
	@Override
	public void update(GameContext context, int delta) throws SlickException {
		starfield.update(context, delta);
		if (shake) {
			shakeDelay += delta;
			if (shakeDelay > shakeDelayMax) {
				float amt = 5f;
				shakeXAmt = Utils.rnd(-amt, amt);
				shakeYAmt = Utils.rnd(-amt, amt);
				shakeDelay -= shakeDelayMax;
			}
			shakeFade.update(delta);
			if (shakeFade.finished())
				shake = false;
		}
		
		//player controls
		if (context.getContainer().getInput().isKeyDown(Input.KEY_W)){
			player.thrustStraight(delta);
		}
		else {
			player.idling();
		}
		if (context.getContainer().getInput().isKeyDown(Input.KEY_A)){
			player.strafeLeft(delta);
		}
		else if (context.getContainer().getInput().isKeyDown(Input.KEY_D)){
			player.strafeRight(delta);
		}
	}
	
	public void shakeCamera(GameContext context) {
		shakeFade.restart();
		shake = true;
	}
	
	public String getDebugText() {
		return "\nStars: "+starfield.size()+"\n" +
				"Starfield Tiles: "+starfield.GRID_SIZE+" (use 1 and 2 keys to change)\n"+
				"Big star chance: "+starfield.BIG_STAR_CHANCE+" (3 and 4 to change)\n"+
				"Medium star chance: "+starfield.MEDIUM_STAR_CHANCE+" (5 and 6 to change)";
		
	}
	
}
