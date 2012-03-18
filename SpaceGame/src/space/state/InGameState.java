package space.state;

import java.util.ArrayList;
import java.util.List;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import space.GameContext;
import space.engine.SpriteBatch;
import space.engine.easing.Easing;
import space.engine.easing.SimpleFX;
import space.entities.Constants;
import space.entities.Entity;
import space.entities.Kamikaze;
import space.entities.Ship;
import space.sprite.StarfieldSprite;
import space.util.Utils;

public class InGameState extends AbstractState implements CollisionListener {

	public InGameState(GameContext context) {
		super(context, 1);
	}
	private World world; // the world!
	private int worldUpdateInterval = 5;
	private int counter = 0;
	
	private Ship player;
	private StarfieldSprite starfield;
	private SimpleFX shakeFade = new SimpleFX(1f, 0f, 1000, Easing.QUAD_OUT);
	private boolean shake = false;
	private float shakeXAmt = 0f, shakeYAmt = 0f;
	private int shakeDelay = 0, shakeDelayMax = 30;
	
	private List<Entity> entities = new ArrayList<Entity>(1000);
	private List<Entity> entitiesBuffer = new ArrayList<Entity>(1000);
	
	private Kamikaze enemy;
	
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
		} else if (k==Input.KEY_RETURN){
			context.createShockwave((int)player.getX(), (int)player.getY());
		}
	}
	
	@Override
	public void init(GameContext context) throws SlickException {
//		context.getContainer().setMouseGrabbed(true);
		starfield = new StarfieldSprite();
		starfield.randomize(context);
		world = new World(new Vector2f(0,0), 10);
		world.addListener(this);
		
		
		player = new Ship(new Image("res/ship.png"), 10f);
		player.setPosition(context.getWidth()/2f, context.getHeight()/2f);
		world.add(player.getBody());
		player.player = true;
		
		enemy = new Kamikaze(1);
		enemy.setPosition(0, 0);
		enemy.getBody().setBitmask(Constants.BIT_ENEMY);
		addEntity(enemy);
		
		final int WALL_SIZE = 10;
		world.add(createWall(0, -WALL_SIZE*2, context.getWidth(), WALL_SIZE));
		world.add(createWall(0, context.getHeight(), context.getWidth(), WALL_SIZE));
		world.add(createWall(-WALL_SIZE*2, 0, WALL_SIZE, context.getHeight()));
		world.add(createWall(context.getWidth(), 0, WALL_SIZE, context.getHeight()));
		
		//context.getContainer().setMouseGrabbed(true);
	}
	
	private Body createWall(float x, float y, float width, float height) {
		Body walltop = new StaticBody(new Box(width, height));
		walltop.setRestitution(0.5f);
		walltop.setPosition(x+width/2f, y+height/2f);
		walltop.setBitmask(Constants.BITMASK_WALL);
		return walltop;
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
		
		for (Entity e : entities) {
			e.draw(context, batch, g);
		}
		
		if (!shake)
			player.draw(context, batch, g);
	}
	

	public void collisionOccured(CollisionEvent evt) {
		Object obj1 = evt.getBodyA().getUserData();
		Object obj2 = evt.getBodyB().getUserData();
		if (obj1 instanceof Entity && obj2 instanceof Entity) {
			Entity e1 = (Entity)obj1;
			Entity e2 = (Entity)obj2;
			System.out.println("Collision event "+e1+" "+e2);
			e1.collide(e2);
			e2.collide(e1);
		}
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
		if (e.getBody()!=null) {
			world.add(e.getBody());
		}
	}
	
	private void handleEntityDeath(Entity e) {
		if (e.getBody()!=null)
			world.remove(e.getBody());
	}
	
	@Override
	public void update(GameContext context, int delta) throws SlickException {
		//occasionally delta might be really huge, to be safe let's just cap it to 10 ms
		delta = Math.min(delta, 10);
		
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
		
		//use a "double buffered" list so that we are only updating entities that are active
		for (int i=0; i<entities.size(); i++) {
			Entity e = entities.get(i);
			if (e.isActive()) { //if the enemy is active
				e.update(context, delta);
				if (e.isActive()) {
					entitiesBuffer.add(e);
				} else { //the enemy died as a result of updating it
					handleEntityDeath(e);
				}
			} else { //it is "dead" and will no longer be part of entities list
				handleEntityDeath(e);
			}
		}
		
		//flip the buffer
		List<Entity> temp = entities;
		entities = entitiesBuffer;
		entitiesBuffer = temp;
		entitiesBuffer.clear();
		
		player.update(context, delta);
		
		//step the world so that the physics are updated
		counter += delta;
		while (counter > worldUpdateInterval) {
			world.step(worldUpdateInterval * 0.01f);
			counter -= worldUpdateInterval;
		}
		
		//bounds checking -- keep player within container
		//we do this after stepping the world
		player.ensureWithinBounds(context.getWidth(), context.getHeight());
	}
	
	public void shakeCamera(GameContext context) {
		shakeFade.restart();
		shake = true;
	}
	
	public String getDebugText() {
		return "Entities: "+entities.size();
		
	}
	
	public Ship getPlayer(){
		return player;
	}

	
}
