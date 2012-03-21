package space.entities;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import space.GameContext;
import space.engine.SpriteBatch;
import space.util.Resources;

public class Ship extends AbstractEntity {

	
	public boolean player = false;
	
	private float radius;
	private Image shipSheet;
	private Image shipIdle, shipStrafeLeft, shipStrafeLeft2, shipStrafeRight, shipStrafeRight2, shipThrust;
	private Image currentImage;
	private Color tint = new Color(1f,1f,1f,1f); //we can adjust alpha value of ship here
	private final int textureWidth = 38; // height of a single sprite on the spritesheet
	private final int textureHeight = 48;

	private float dirX, dirY; // direction the ship is currently facing
	private boolean shipMoving; // are we moving?
	private int blasterDamage = 50; // initial amount of hitpoint damage the blaster will deal
	private int collisionDamage = 50; // amount of damage enemies take when colliding with the ship
	private double shieldMax = 200; // the amount of damage the ship can take before taking damage on structure shields regenerate
	private double shields = shieldMax; // the current amound of shields
	private final int structureMax = 30; // the amount of damage the ship can take when shields are 0.
	private int structure = structureMax;
	private int upgradesPurchase;
	private double angle; // angle in radians to the mouse pointer
	
	private boolean shooting = false;
	private int shootingInterval = Constants.PLAYER_SHOOTING_COOLDOWN; //ms
	private int shootingTime = shootingInterval;
	private int shieldRegenInterval = Constants.PLAYER_SHIELD_REGEN_SPEED; // after 2 seconds, shields begen regeneration
	private int hitTime = shieldRegenInterval;
	
	public Ship(float radius) {// create a body with the size of the image divided by 2
		this.radius = radius;
		init();
		setBody(createBody());
		this.shipMoving = false; //we aren't moving if we were just created
		setRotation(0);
	}
	
	public void collide(Entity other) {
		// we got shot! take damage equal to the bullet's damage attribute
		hitTime = 0; // set the hit time to 0, since we got hit
		if (other instanceof Bullet) {
			Bullet bullet = ((Bullet)other);
			takeDamage(bullet.getDamage());
			bullet.kill();
		}
		// if we collide with an enemy, we take their collision damage
		if (other instanceof Enemy){
			Enemy enemy = (Enemy)other;
			takeDamage(enemy.getCollisionDamage());
		}
	}
	
	public int getCollisionDamage(){
		return this.collisionDamage;
	}
	
	protected Body createBody(){
		Body body = new Body(new Circle(shipIdle.getWidth()/2f),1000f);
		body.setMaxVelocity(Constants.PLAYER_MAX_SPEED, Constants.PLAYER_MAX_SPEED);
		body.setUserData(this);
		body.setRestitution(0.5f);
		body.addBit(Constants.BIT_PLAYER);
		return body;		
	}
	
	// cut the image we're provided with into the needed dimensions
	private void init(){
//		shipIdle = shipSheet.getSubImage(0, 0, textureWidth, textureHeight);
//		shipStrafeRight = shipSheet.getSubImage(textureWidth, 0, textureWidth, textureHeight);
//		shipStrafeLeft = shipSheet.getSubImage(textureWidth*2, 0, textureWidth, textureHeight);
//		shipThrust = shipSheet.getSubImage(0, textureHeight, textureWidth, textureHeight);
//		shipStrafeRight2 = shipSheet.getSubImage(textureWidth, textureHeight, textureWidth, textureHeight);
//		shipStrafeLeft2 = shipSheet.getSubImage(textureWidth*2, textureHeight, textureWidth, textureHeight);
		
		shipIdle = Resources.getSprite("player2");
		shipStrafeRight = shipStrafeLeft = shipThrust = shipStrafeRight2 = shipStrafeLeft2 = shipIdle;
		
		currentImage = shipIdle;
	}
	
	/** Draw the ship at its current location. */
	public void draw(GameContext context, SpriteBatch batch, Graphics g) {
		float newX = getX() - currentImage.getWidth()/2f;
		float newY = getY() - currentImage.getHeight()/2f;
		//set new filter which will be used to draw the image
		batch.setColor(tint);
		batch.drawImage(currentImage, newX, newY, getRotation());
	}
	
	// deprecated
	public void ensureWithinBounds(int width, int height) {
		if (getX() < -getWidth()) {
			setPosition(width, getY());
		} else if (getX() > width+getWidth())
			setPosition(-getWidth(), getY());
		
		if (getY() < -getHeight()) 
			setPosition(getX(), height);
		else if (getY() > height+getHeight())
			setPosition(getX(), -getHeight());
	}
	
	public void update(GameContext context, int delta) {
		if (!player)
			return;
		
		Input input = context.getInput();
		
		float mx = context.getInput().getMouseX(), my = context.getInput().getMouseY();
		float dx = Mouse.getDX();
//		rotate(dx * Constants.PLAYER_TURN_SPEED);
		
		setHeading(mx, my);
		
		//player controls
		
		
		shootingTime += delta;
		hitTime += delta; // time since we were last hit.
		
		if (hitTime > shieldRegenInterval){ // if it's been 1500 ms since we were hit
			if (shields < shieldMax){ // if our shields are below maximum
				shields+=(delta*.04); // increase shields by .04 * delta (.04 shields / ms)
				if (shields > shieldMax) 
					shields=shieldMax;	// make sure we can't regenerate past max shields			
				//System.out.println("Shields: "+shields); // logging for now
			}
		}
		
		if (input.isKeyDown(Input.KEY_W)){
			thrustStraight(delta);
		}
		else {
			idling();
		}
		if (input.isKeyDown(Input.KEY_LEFT)){
			rotate(delta * -Constants.PLAYER_TURN_SPEED);
			
		}
		else if (input.isKeyDown(Input.KEY_RIGHT)){
			rotate(delta * Constants.PLAYER_TURN_SPEED);
		}
		if (input.isKeyDown(Input.KEY_SPACE) || input.isMouseButtonDown(0)) {
			if (shootingTime > shootingInterval) {
				shootingTime = 0;
				float x = getX();
				float y = getY();
				context.getInGameState().addEntity(new Bullet(x, y, dirX, dirY, getRotation(), blasterDamage, true));
			}
		}
		
		if (input.isKeyDown(Input.KEY_D)) {
			strafeRight(delta);
		} 
		else if (input.isKeyDown(Input.KEY_A)) {
			strafeLeft(delta);
		}
		
	}
	
	// rename for clarity	
	public void thrustStraight(int delta){
		float dirXAmt = dirX * delta * Constants.PLAYER_MOVE_SPEED;
		float dirYAmt = dirY * delta * Constants.PLAYER_MOVE_SPEED;
		addForce(dirXAmt, dirYAmt);
		this.shipMoving = true;
		currentImage = shipThrust;
	}
	
	public void strafeLeft(int delta){	
		double r = angle - Math.PI/4;
		float dirX = (float)Math.sin(r);
		float dirY = (float)-Math.cos(r);
		addForce(dirX * delta * Constants.PLAYER_STRAFE_SPEED, dirY * delta * Constants.PLAYER_STRAFE_SPEED);
		this.shipMoving = true;
		currentImage = shipStrafeLeft;
	}
	
	public void strafeRight(int delta){
		double r = angle + Math.PI/4;
		float dirX = (float)Math.sin(r);
		float dirY = (float)-Math.cos(r);
		addForce(dirX * delta * Constants.PLAYER_STRAFE_SPEED, dirY * delta * Constants.PLAYER_STRAFE_SPEED);
		this.shipMoving = true;
		currentImage = shipStrafeRight;
	}
	
	// no thrust being applied. 
	public void idling(){
		float xDecay = -getVelX() * .05f;
		float yDecay = -getVelY() * .05f;
		if (xDecay != 0 || yDecay != 0){
			addForce(xDecay, yDecay);
		}
		currentImage = shipIdle;
	}
	
	public boolean isShipMoving(){
		return this.shipMoving;
	}
	
	/** Rotates the ship image and direction by the given degrees amount. */
	public void rotate(float degAmt) {
		setRotation(getRotation() + degAmt);
	}
	
	public float getRotation() {
		return (float)Math.toDegrees(this.angle);
	}
	
	public void setRotation(float deg) {
		double a = Math.toRadians(deg);
		this.dirX = (float)Math.sin(a);
		this.dirY = (float)-Math.cos(a);
		this.angle = a;
	}
	
	//deprecated
	public void setHeading(float mouseX, float mouseY){
		double r = -Math.atan2((getX()-mouseX), (getY()-mouseY));	
		this.dirX = (float) Math.sin(r);
		this.dirY = (float) -Math.cos(r); 
		this.angle = r;
	}
	
	public int getWidth(){
		return currentImage.getWidth();
	}
	
	public int getHeight(){
		return currentImage.getHeight();
	}
	
	// upgrades the blaster's damage by an amount relevant to the wave the user is on.
	// and how many upgrades are already purchased
	public void upgradeBlaster(int wave){
		int upgradeAmount = 20 + (wave * 10);
		this.blasterDamage+=upgradeAmount;
	}
	
	// upgrades shields based upon the wave
	public void upgradeShields(int wave){
		int upgradeAmount = 40 + (wave * 10);
		this.shieldMax+=upgradeAmount;
	}
	
	// deals damage to the player
	public void takeDamage(int damage){
		double newShields = this.shields - damage;
		if (newShields >= 0) { // our shields took all of the damage.
			this.shields = newShields;
		} 
		else { // we're taking structure damage
			this.shields = 0;
			this.structure+=newShields; // structure takes damage = amount through shields (will be negative, hence +=)
		}
		
		// after these calcs, we check if we're alive
		if (this.structure <= 0){ // we're dead
			this.structure = 0;
			kill();
			System.out.println("Dead.");
		} else {
			System.out.println("Taking dmg - shield: "+shields+" struc: "+structure);
		}
	}

	
	
}
