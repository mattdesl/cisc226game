package space.entities;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;

import org.newdawn.slick.Graphics;

import space.GameContext;
import space.engine.SpriteBatch;

/**
 * A new class
 * @author Matt
 */
public abstract class AbstractEntity implements Entity {

    protected Body body;
    protected boolean active = true;
    
    protected AbstractEntity(){
    }
    
	public boolean isActive() {
		return active;
	}
	
    public void setPosition(float x, float y) {
    	getBody().setPosition(x, y);
    }
    
    // returns the X position of the body 
    public float getX() {
        return getBody().getPosition().getX();
    }

    // returns the Y position of the body
    public float getY() {
        return getBody().getPosition().getY();
    }
    
    public void addForce(float x, float y) {
        getBody().addForce(new Vector2f(x, y));
    }
    
    public void adjustVelocity(float x, float y) {
    	getBody().adjustVelocity(new Vector2f(x, y));
    }

    // gets the X Velocity of the Body
    public float getVelX() {
    	return getBody().getVelocity().getX();
    }
    
    // gets the Y velocity of the body
    public float getVelY() {
    	return getBody().getVelocity().getY();
    }
    
    public Body getBody() {
        return body;
    }
    
    public void setBody(Body body){
    	this.body = body;    	
    }
  
    public void kill() {
    	active = false;
    }
    
	public void draw(GameContext context, SpriteBatch b, Graphics g) {
		
	}
	
	public void update(GameContext context, int delta) {
		
	}
}
