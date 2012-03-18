package space.entities;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import space.engine.SpriteBatch;

public class Ship extends AbstractPhysicalEntity implements RenderableEntity {

	private float radius;
	private Image shipSheet;
	private Image shipIdle, shipStrafeLeft, shipStrafeLeft2, shipStrafeRight, shipStrafeRight2, shipThrust;
	private Image currentImage;
	private Color tint = new Color(1f,1f,1f,1f); //we can adjust alpha value of ship here
	private final int textureWidth = 38; // height of a single sprite on the spritesheet
	private final int textureHeight = 48;
	private int shipWidth, shipHeight;
	private float dirX, dirY; // direction the ship is currently facing
	private boolean shipMoving; // are we moving?
	private int blasterDamage = 50; // amount of hitpoint damage the blaster will deal
	private int shieldMax = 200; // the amount of damage the ship can take before taking damage on structure shields regenerate
	private int shields = shieldMax; // the current amound of shields
	private final int structureMax = 30; // the amount of damage the ship can take when shields are 0.
	private int structure = structureMax;
	private int upgradesPurchase;
//	private ArrayList<Blast> blasts;
	private double angle; // angle in radians to the mouse pointer
	
	public Ship(Image image, float radius) {// create a body with the size of the image divided by 2
		this.shipSheet = image;
		this.radius = radius;
//		this.blasts = new ArrayList<Blast>(3);
		init();
		this.body = createBody();
		this.shipMoving = false; //we aren't moving if we were just created
	}

	private Body createBody(){
		Body body = new Body(new Circle(shipIdle.getWidth()/2f),10f);
		body.setMaxVelocity(Constants.PLAYER_MAX_SPEED, Constants.PLAYER_MAX_SPEED);
		return body;		
	}
	
	// cut the image we're provided with into the needed dimensions
	private void init(){
		shipIdle = shipSheet.getSubImage(0, 0, textureWidth, textureHeight);
		shipStrafeRight = shipSheet.getSubImage(textureWidth, 0, textureWidth, textureHeight);
		shipStrafeLeft = shipSheet.getSubImage(textureWidth*2, 0, textureWidth, textureHeight);
		shipThrust = shipSheet.getSubImage(0, textureHeight, textureWidth, textureHeight);
		shipStrafeRight2 = shipSheet.getSubImage(textureWidth, textureHeight, textureWidth, textureHeight);
		shipStrafeLeft2 = shipSheet.getSubImage(textureWidth*2, textureHeight, textureWidth, textureHeight);
		this.shipWidth = shipIdle.getWidth()/2;
		this.shipHeight = shipIdle.getHeight()/2;
		currentImage = shipIdle;
	}
	
	/** Draw the ship at its current location. */
	public void draw(SpriteBatch b, Graphics g) {
		draw(b, g, 0f, 0f, 1f);
	}
	
	// draw offset by shipwidth, because the body getPosition vector reports the position of the upperleft bounding box; 
	// therefore we draw the image on an offset. 
	// TODO: collision will probably be very off because of this
	public void draw(SpriteBatch batch, Graphics g, float xOff, float yOff, float scale) {
		float newX = getX() - shipWidth + xOff;
		float newY = getY() - shipWidth + yOff;
		//set new filter which will be used to draw the image
		batch.setColor(tint);
		batch.drawImage(currentImage, newX, newY, (float)Math.toDegrees(angle));
		
//		drawBlasts(g, newX, newY, scale, tint); // if we have bullets, we draw them
	}
	
	public Ship copy() {
		Ship s = new Ship(this.shipSheet, this.radius);
		s.tint = new Color(tint);
		s.angle = this.angle;
		s.setPosition(getX(), getY());
		return s;
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
		int upgradeAmount = 25 + (wave * 5);
		this.blasterDamage+=upgradeAmount;
	}
	
	// upgrades shields based upon the wave
	public void upgradeShields(int wave){
		int upgradeAmount = 50 + (wave * 10);
		this.shieldMax+=upgradeAmount;
	}
	
	// deals damage to the player
	public void takeDamage(int damage){
		int newShields = this.shields - damage;
		if (newShields >= 0) { // our shields took all of the damage.
			this.shields = newShields;
		} 
		else { // we're taking structure damage
			this.shields = 0;
			this.structure+=newShields; // structure takes damage = amount through shields (will be negative, hence +=)
		}
		
		// after these calcs, we check if we're alive
		if (this.structure == 0){ // we're dead
			//this.die
		}
	}
	
	public void drawBlasts(Graphics g, float x, float y, float scale, Color tint){
//		for (Blast b : blasts){
//			b.draw(g);
//		}
	}
	
	public void fireBlaster(){
//		Blast blast = new Blast();
//		blast.addForce(this.dirX*100f, this.dirY*100f);
//		this.blasts.add(blast);
	}
	
}
