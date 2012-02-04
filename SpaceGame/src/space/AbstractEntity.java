package space;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.DynamicShape;
import org.newdawn.slick.Graphics;

/**
 * A new class
 * @author Matt
 */
public abstract class AbstractEntity implements Entity {

    protected Body body;
    
    protected AbstractEntity(DynamicShape s, float m) {
        body = new Body(s, m);
        
    }
    
    public float getX() {
        return getBody().getPosition().getX();
    }

    public float getY() {
        return getBody().getPosition().getY();
    }
    
    public void apply(float x, float y) {
        getBody().addForce(new Vector2f(x, y));
    }

    public Body getBody() {
        return body;
    }

    public void draw(Graphics g) {
        
    }

    public abstract Entity copy();
}
