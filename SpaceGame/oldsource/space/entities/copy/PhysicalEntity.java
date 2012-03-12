package space.entities.copy;

import net.phys2d.raw.Body;

public interface PhysicalEntity extends Entity {

    public void addForce(float x, float y);
    public Body getBody();
    public float getVelX();
    public float getVelY();
    public Entity copy();

    /**
     * Returns the rotation in radians. 
     * @return rotation in radians
     */
    public float getRotation();
    public void setRotation(float angle);
    public void adjustRotation(float angle);
    public void adjustVelocity(float x, float y);
    public void setPosition(float x, float y);
}
