package space.entities;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

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
	
	
	public Ship(Image image, float radius) {// create a body with the size of the image divided by 2
		this.shipSheet = image;
		this.radius = radius;
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
	public void draw(Graphics g) {
		draw(g, 0f, 0f, 1f);
	}
	// draw offset by shipwidth, because the body getPosition vector reports the position of the upperleft bounding box; 
	// therefore we draw the image on an offset. 
	// TODO: collision will probably be very off because of this
	public void draw(Graphics g, float xOff, float yOff, float scale) {
		currentImage.draw(getX()-shipWidth+xOff, getY()-shipWidth+yOff, scale, tint); 
	}
	
	public Ship copy() {
		Ship s = new Ship(this.shipSheet, this.radius);
		s.tint = new Color(tint);
		s.setPosition(getX(), getY());
		s.setRotation(getRotation());
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
	
	public void strafeLeft(float ang, int delta){	
		double r = Math.toRadians(ang - 45);
		float dirX = (float)Math.sin(r);
		float dirY = (float)-Math.cos(r);
		addForce(dirX * delta * Constants.PLAYER_STRAFE_SPEED, dirY * delta * Constants.PLAYER_STRAFE_SPEED);
		this.shipMoving = true;
		currentImage = shipStrafeLeft;
	}
	
	public void strafeRight(float ang, int delta){
		double r = Math.toRadians(ang + 45);
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
	
	public void setHeading(double r){
		this.dirX = (float) Math.sin(r);
		this.dirY = (float) -Math.cos(r); 
		setRotation((float)r);
		
	}
	
	public int getWidth(){
		return currentImage.getWidth();
	}
	
	public int getHeight(){
		return currentImage.getHeight();
	}
	//dirty
/*	public void setPosition(float x, float y){
		super.setPosition(x-19, y-19);
	}*/
	
/*	public float getX(){
		return super.getX() + 19;
	}
	
	public float getY(){
		return super.getY() + 19;
	}*/
	
	
}
