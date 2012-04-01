package space.entities;

import java.util.ArrayList;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import space.GameContext;
import space.engine.SpriteBatch;
import space.ui.HealthBarWidget;
import space.util.Resources;

public class Ship extends AbstractEntity {


	public boolean player = false;

	private float radius;
	private Image shipIdle, shipAfterImage, shipThrust;
	// this is dirty. help me make it better
	private Image shield1, shield2, shield3, shield4, shield5;
	private int shieldCounter; 
	private Image[] shieldArr; 
	private Animation shieldAnimation;
	private Image currentImage;
	private Color tint = new Color(1f,1f,1f,1f); //we can adjust alpha value of ship here

	private float dirX, dirY; // direction the ship is currently facing
	private boolean shipMoving; // are we moving?
	private boolean shipBoosting;
	private boolean takingDamage;
	private int shieldAnimSpeed = 250; // 250ms animation time for shield animation
	private int shieldTime = shieldAnimSpeed/5; //time per frame
	private int blasterDamage = 50; // initial amount of hitpoint damage the blaster will deal
	private int collisionDamage = 1; // amount of damage enemies take when colliding with the ship
	private double shieldMax = 200; // the amount of damage the ship can take before taking damage on structure shields regenerate
	private double shields = shieldMax; // the current amound of shields
	private final int structureMax = 330; // the amount of damage the ship can take when shields are 0.
	private int structure = structureMax;
	private int upgradesPurchased;
	private double angle; // angle in radians to the mouse pointer
	private int shootingInterval = Constants.PLAYER_SHOOTING_COOLDOWN; //ms
	private int shootingTime = shootingInterval;
	private int shieldRegenInterval = Constants.PLAYER_SHIELD_REGEN_COOLDOWN; // after 2 seconds, shields begen regeneration
	private int hitTime = shieldRegenInterval; // time since we were last hit
	private int boostCooldown = Constants.PLAYER_BOOST_COOLDOWN;
	private int boostTime = boostCooldown; // time since last dodge
	private int boostDuration = Constants.PLAYER_BOOST_DURATION;
	private int boostDurationTime = boostDuration;
	private int afterImageDuration = 30; // ms for polling the afterimage
	private int afterImageTime = afterImageDuration;
	private ArrayList<Position> oldPos;
	
	private HealthBarWidget healthBar, shieldBar;

	public Ship(float radius) {// create a body with the size of the image divided by 2
		this.radius = radius;
		init();
		setBody(createBody());
		this.shipMoving = false; //we aren't moving if we were just created
		this.shipBoosting = false;
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
	
	// class to keep track of old positions, so that we can do the trail effect

	private class Position{
		private float x,y;
		public Position(float x, float y){
			this.x = x;
			this.y = y;
		}
		public float getX(){
			return x;
		}
		public float getY(){
			return y;
		}
	}

	private void addOldPosition(float x, float y){
		Position p = new Position(x, y);
		if (oldPos.size() < 3){
			oldPos.add(p);
		} else {
			oldPos.remove(0);
			oldPos.add(p);
		}
	}
	// cut the image we're provided with into the needed dimensions
	private void init(){
		shipIdle = Resources.getSprite("playeridle");
		shipThrust = Resources.getSprite("playerthrust");
		shipAfterImage = Resources.getSprite("playerafterimage");
		shield1 = Resources.getSprite("shield1");
		shield2 = Resources.getSprite("shield2");
		shield3 = Resources.getSprite("shield3");
		shield4 = Resources.getSprite("shield4");
		shield5 = Resources.getSprite("shield5");
		shieldArr = new Image[5];
		shieldArr[0] = shield1;
		shieldArr[1] = shield2;
		shieldArr[2] = shield3;
		shieldArr[3] = shield4;
		shieldArr[4] = shield5;	
		shieldAnimation = new Animation(shieldArr, 50);
		this.oldPos = new ArrayList<Position>(3);
		currentImage = shipIdle;
		
		Image bar = Resources.getSprite("healthbar");
		Image red = Resources.getSprite("healthbar.red");
		Image blue = Resources.getSprite("healthbar.blue");
		healthBar = new HealthBarWidget(bar, red, Resources.HEALTH_BAR_X_OFF, Resources.HEALTH_BAR_Y_OFF);
		shieldBar = new HealthBarWidget(bar, blue, Resources.HEALTH_BAR_X_OFF, Resources.HEALTH_BAR_Y_OFF);
	}

	/** Draw the ship at its current location. */
	public void draw(GameContext context, SpriteBatch batch, Graphics g) {
		float newX = getX() - currentImage.getWidth()/2f;
		float newY = getY() - currentImage.getHeight()/2f;
		float shieldX = getX() - shield1.getWidth()/2f;
		float shieldY = getY() - shield1.getHeight()/2f;
		if (afterImageTime > afterImageDuration){
			addOldPosition(newX, newY);
			afterImageTime = 0;
		}
		//set new filter which will be used to draw the image
		batch.flush();
		batch.setColor(tint);
		
		if (shipBoosting){
			for (Position p : oldPos){
				batch.drawImage(shipAfterImage, p.getX(), p.getY(), getRotation());
			}
		} 
		
		batch.drawImage(currentImage, newX, newY, getRotation());
		
		if (takingDamage){
			if (shields > 0){
				batch.drawImage(shieldAnimation.getImage(shieldCounter), shieldX, shieldY, getRotation());
			}
			
			if (shieldCounter >= 4){
				shieldCounter = 0;	
				takingDamage = false;
			}
		}
		
		//draw shield + health bar
		float x = getX() - (healthBar.getWidth()/2f);
		float y = getY() + currentImage.getHeight()/2f + 1;
		shieldBar.setPosition(x,  y);
		healthBar.setPosition(x, y+healthBar.getHeight() - 3);
		
		shieldBar.setValue((float)shields/(float)shieldMax);
		healthBar.setValue(structure/(float)structureMax);
		shieldBar.draw(batch, g);
		healthBar.draw(batch, g);
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

		//increment time
		shootingTime += delta;
		hitTime += delta; // time since we were last hit.
		boostTime += delta;
		afterImageTime += delta;


		if (takingDamage){
			shieldTime += delta;
			if (shieldTime >= shieldAnimSpeed/5){
				shieldTime = 0;
				shieldCounter++;
			}			
		}


		// decide if we can regenerate shields (it's been more than 1.5 seconds since we were last hit)
		if (hitTime > shieldRegenInterval){ // if it's been 1500 ms since we were hit
			if (shields < shieldMax){ // if our shields are below maximum
				shields+=(delta*(shieldMax/Constants.PLAYER_SHIELD_REGEN_SPEED)); // increase shields by .04 * delta (.04 shields / ms)
				if (shields > shieldMax) 
					shields=shieldMax;	// make sure we can't regenerate past max shields			
				//System.out.println("Shields: "+shields); // logging for now
			}
		}

		if (shipBoosting){
			boostDurationTime += delta;
		}
		// decide if we want to end the afterimages
		if (boostDurationTime >= boostDuration){
			boostDurationTime = 0;
			shipBoosting=false;
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
		// if our boost is off cooldown
		if (boostTime >= boostCooldown){
			if (input.isKeyDown(Input.KEY_D)) {

				shipBoosting = true;
				boostTime = 0;
				body.setMaxVelocity(10000f,10000f); // modify the max speed while we dodge
				burstRight(delta);
				body.setMaxVelocity(Constants.PLAYER_MAX_SPEED, Constants.PLAYER_MAX_SPEED);
			}

			else if (input.isKeyDown(Input.KEY_A)) {
				shipBoosting = true;
				boostTime = 0;
				body.setMaxVelocity(10000f,10000f); // modify the max speed while we dodge
				burstLeft(delta);
				body.setMaxVelocity(Constants.PLAYER_MAX_SPEED, Constants.PLAYER_MAX_SPEED);				
			}	

			else if (input.isKeyDown(Input.KEY_LSHIFT) || input.isKeyDown(Input.KEY_RSHIFT)){
				shipBoosting = true;
				boostTime = 0;
				body.setMaxVelocity(10000f, 10000f);
				burstStraight(delta);
				body.setMaxVelocity(Constants.PLAYER_MAX_SPEED, Constants.PLAYER_MAX_SPEED);
			}
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

	public void burstStraight(int delta){
		float dirXAmt = dirX * delta * Constants.PLAYER_BOOST_SPEED;
		float dirYAmt = dirY * delta * Constants.PLAYER_BOOST_SPEED;
		addForce(dirXAmt, dirYAmt);
		this.shipMoving = true;
	}

	public void burstLeft(int delta){
		double r = angle - Math.PI/2;
		float dirX = (float)Math.sin(r);
		float dirY = (float)-Math.cos(r);
		addForce(dirX * delta * Constants.PLAYER_BOOST_SPEED, dirY * delta * Constants.PLAYER_BOOST_SPEED);
		this.shipMoving = true;
	}

	public void burstRight(int delta){
		double r = angle + Math.PI/2;
		float dirX = (float)Math.sin(r);
		float dirY = (float)-Math.cos(r);
		addForce(dirX * delta * Constants.PLAYER_BOOST_SPEED, dirY * delta * Constants.PLAYER_BOOST_SPEED);
		this.shipMoving = true;
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
		takingDamage = true;
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
	
	public double getShieldPercentage(){
		return shields / shieldMax;
	}

}
