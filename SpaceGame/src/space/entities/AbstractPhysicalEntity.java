package space.entities;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;

/**
 * A new class
 * @author Matt
 */
public abstract class AbstractPhysicalEntity implements PhysicalEntity {

    protected Body body;
    
    protected AbstractPhysicalEntity(Body body) {
    	this.body = body;
    }
    
    protected AbstractPhysicalEntity(){
    	this.body = null;
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
    
    public void adjustRotation(float angle) {
    	getBody().adjustRotation(angle);
    }
    
    public void setRotation(float angle) {
    	getBody().setRotation(angle);
    }
    
    public float getRotation() {
    	return getBody().getRotation();
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
    
    public abstract PhysicalEntity copy();
}
