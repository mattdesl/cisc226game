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

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ParticleSystem;

import space.GameContext;
import space.engine.SpriteBatch;
import space.engine.easing.Easing;
import space.engine.easing.SimpleFX;
import space.entities.Constants;
import space.entities.Enemy;
import space.entities.Entity;
import space.entities.Kamikaze;
import space.entities.Ship;
import space.entities.Wingbat;
import space.sprite.StarfieldSprite;
import space.util.Resources;
import space.util.SpawnController;
import space.util.Utils;

public class InGameState extends AbstractState implements CollisionListener {

	public InGameState(GameContext context) {
		super(context, 1);
	}
	private World world; // the world!
	private int worldUpdateInterval = 5;
	private int counter = 0;
	private int score = 0;

	private Ship player;
	private StarfieldSprite starfield;
	private SimpleFX shakeFade = new SimpleFX(1f, 0f, 1000, Easing.QUAD_OUT);
	private boolean shake = false;
	private float shakeXAmt = 0f, shakeYAmt = 0f;
	private int shakeDelay = 0, shakeDelayMax = 30;
	private SpawnController spawner;
	private int spawnCounter = 0;

	private List<Entity> entities = new ArrayList<Entity>(1000);
	private List<Entity> entitiesBuffer = new ArrayList<Entity>(1000);
	private int enemies = 0;

	private int waveLevel = 0;
	private SimpleFX waveFadeFX = new SimpleFX(1f, 0f, Constants.WAVE_REST_TIME/2f, Easing.QUAD_OUT);
	private Color waveLevelColor = new Color(1f,1f,1f,0f);
	private boolean showWaveLevel = false;
	private float boomX, boomY;
	private boolean playerDeadDirty = true;

	public void keyPressed(int k, char c) {
		if (k==Input.KEY_ESCAPE) {
			context.enterMenu();
		}
	}

	@Override
	public void init(GameContext context) throws SlickException {
		starfield = new StarfieldSprite();
		starfield.randomize(context);
		world = new World(new Vector2f(0,0), 10);
		world.addListener(this);

		restart();
	}

	private Body createWall(float x, float y, float width, float height) {
		Body walltop = new StaticBody(new Box(width, height));
		walltop.setRestitution(0.5f);
		walltop.setPosition(x+width/2f, y+height/2f);
		walltop.setBitmask(Constants.BITMASK_WALL);
		return walltop;
	}

	public void restart() {
		playerDeadDirty = true;
		world.clear();

		player = new Ship(10f);
		player.setPosition(context.getWidth()/2f, context.getHeight()/2f);
		world.add(player.getBody());
		this.score = 0;
		player.player = true;

		spawner = new SpawnController();

		entities.clear();
		entitiesBuffer.clear();
		this.enemies = 0;
		waveLevel = 0;

		final int WALL_SIZE = 10;
		world.add(createWall(0, -WALL_SIZE*2, context.getWidth(), WALL_SIZE));
		world.add(createWall(0, context.getHeight(), context.getWidth(), WALL_SIZE));
		world.add(createWall(-WALL_SIZE*2, 0, WALL_SIZE, context.getHeight()));
		world.add(createWall(context.getWidth(), 0, WALL_SIZE, context.getHeight()));
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

		batch.setColor(Color.white);
		batch.drawTextMultiLine(Resources.getMonospacedFont(), "Wave: " + spawner.getWave() + "  Score: "+score, context.getContainer().getWidth()-150, 5);
		// is there a better way?
		batch.drawTextMultiLine(Resources.getMonospacedFont(), "Upgrades purchased | Shields: " + player.getShieldPurchased() + " | Weapons: " + player.getWeaponPurchased(), context.getContainer().getWidth()-270, context.getContainer().getHeight() - 20);
		batch.drawTextMultiLine(Resources.getMonospacedFont(), "Upgrade values | Shields: " + player.getShieldUpgradeValue(spawner.getWave()) + " | Weapons: " + player.getWeaponUpgradeValue(spawner.getWave()), 5, context.getContainer().getHeight() -20); 
		batch.flush(); // is this necessary?
		for (Entity e : entities) {
			e.draw(context, batch, g);
		}

		//check to see if the player died since last frame
		if (player.isDead() && playerDeadDirty) {
			playerDeadDirty = false; // we don't want explosion more than once...
			context.createShockwave((int)player.getX(), (int)player.getY());
		}


		if (!player.isDead())
			player.draw(context, batch, g);
		else if (shake) {
			ParticleSystem sys = Resources.getBoomParticle();
			if (sys!=null) {
				batch.flush();
				sys.render(boomX, boomY);
			}
		}

		if (showWaveLevel || waveFadeFX.getValue()>0f) {
			AngelCodeFont f = Resources.getSmallFont();
			String str = "Wave "+waveLevel;
			float w = f.getWidth(str);
			waveLevelColor.a = waveFadeFX.getValue();
			
			batch.setColor(waveLevelColor);
			batch.drawText(f, str, context.getWidth()/2f-w/2f, context.getHeight()/4f);
		}
	}


	public void collisionOccured(CollisionEvent evt) {
		Object obj1 = evt.getBodyA().getUserData();
		Object obj2 = evt.getBodyB().getUserData();
		if (obj1 instanceof Entity && obj2 instanceof Entity) {
			Entity e1 = (Entity)obj1;
			Entity e2 = (Entity)obj2;
			e1.collide(e2);
			e2.collide(e1);
		}
	}

	public void addEntity(Entity e) {
		entities.add(e);
		if (e.getBody()!=null) {
			world.add(e.getBody());
		}
		if (e instanceof Enemy) {
			enemies++;
		}
	}

	public void handleEntityDeath(Entity e) {
		if (e.getBody()!=null) {
			if (e instanceof Enemy){
				Enemy enemy = (Enemy) e;
				score += enemy.getPointValue();
				enemies--;
			}
			world.remove(e.getBody());
		}
	}

	public void killPlayer() {
		context.createShockwave((int)player.getX(), (int)player.getY());
		handleEntityDeath(player);
		System.out.println("DEAD!");
	}

	@Override
	public void update(GameContext context, int delta) throws SlickException {
		//occasionally delta might be really huge, to be safe let's just cap it to 10 ms
		delta = Math.min(delta, 10);
		starfield.update(context, delta);
		if (shake) {
			ParticleSystem sys = Resources.getBoomParticle();
			if (sys!=null) {
				sys.update(delta);
			}
			shakeDelay += delta;
			if (shakeDelay > shakeDelayMax) {
				float amt = 5f;
				shakeXAmt = Utils.rnd(-amt, amt);
				shakeYAmt = Utils.rnd(-amt, amt);
				shakeDelay -= shakeDelayMax;
			}
			shakeFade.update(delta);
			if (shakeFade.finished()){
				// end the game here, because we only have this shaking effect if we're dead. for now just go to menu lol
				//context.enterMenu();
				shake = false;
				context.enterGameOver();
			}

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


		if (enemies==0){
			if (!showWaveLevel) {
				waveLevel++;
				waveFadeFX.setStart(waveLevelColor.a);
				waveFadeFX.setEnd(1f);
				waveFadeFX.restart();
				waveFadeFX.setEasing(Easing.EXPO_OUT);
			}
			showWaveLevel = true;
			spawnCounter += delta;
			if (spawnCounter >= Constants.WAVE_REST_TIME){
				showWaveLevel = false;
				waveFadeFX.setStart(waveLevelColor.a);
				waveFadeFX.setEnd(0f);
				waveFadeFX.restart();
				waveFadeFX.setEasing(Easing.EXPO_IN);
				spawner.spawnWave(context);
				player.addUpgrade();
				spawnCounter = 0;
			}
		}

		waveFadeFX.update(delta);
		if (!player.isDead()){
			player.update(context, delta);	
		}

		//step the world so that the physics are updated
		counter += delta;
		while (counter > worldUpdateInterval) {
			world.step(worldUpdateInterval * 0.01f);
			counter -= worldUpdateInterval;
		}
	}

	public boolean isPlayerAlive(){ 
		return !player.isDead();
	}

	public void shakeCamera(GameContext context, int x, int y) {
		shakeFade.restart();
		ParticleSystem sys = Resources.getBoomParticle();
		boomX = x;
		boomY = y;
		if (sys!=null) {
			sys.reset();
		}
		shake = true;
	}

	public String getDebugText() {
		return "Entities: "+entities.size()+"\nEnemies: "+enemies;

	}

	public Ship getPlayer(){
		return player;
	}

	public int getScore(){
		return score;
	}

	public void adjustScore(int amount){
		score-=amount;
	}

	public SpawnController getSpawner(){
		return spawner;
	}

	public int getWaveLevel(){
		return this.waveLevel;
	}

}
