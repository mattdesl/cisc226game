package space;

import net.phys2d.raw.Body;
import org.newdawn.slick.Graphics;

/**
 * A new class
 * @author Matt
 */
public interface Entity {
    
    public float getX();
    public float getY();
    public void apply(float x, float y);
    public Body getBody();
    public void draw(Graphics g);
    public Entity copy();
}
