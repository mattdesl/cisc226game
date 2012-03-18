package space.entities;

import net.phys2d.raw.Body;

import org.newdawn.slick.Graphics;

import space.GameContext;
import space.engine.SpriteBatch;

/**
 * A new class
 * @author Matt
 */
public interface Entity {
    
    public float getX();
    public float getY();
    
    public void addForce(float x, float y);
    public Body getBody();
    public float getVelX();
    public float getVelY();

    public void adjustVelocity(float x, float y);
    public void setPosition(float x, float y);
    
	public void draw(GameContext context, SpriteBatch b, Graphics g);
	public void update(GameContext context, int delta);
	
	public boolean isActive();
}
