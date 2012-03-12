package space.entities;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Ship extends AbstractPhysicalEntity implements RenderableEntity {

	private float radius;
	private Image image;
	private Color tint = new Color(1f,1f,1f,1f); //we can adjust alpha value of ship here
	
	public Ship(Image image, float radius) {
		super(new Body(new Circle(radius), 10f));
		this.image = image;
		this.radius = radius;
	}
	
	/** Draw the ship at its current location. */
	public void draw(Graphics g) {
		draw(g, 0f, 0f, 1f);
	}
	
	public void draw(Graphics g, float xOff, float yOff, float scale) {
		image.draw(getX()+xOff, getY()+yOff, scale, tint); 
	}
	
	public Ship copy() {
		Ship s = new Ship(this.image, this.radius);
		s.tint = new Color(tint);
		s.setPosition(getX(), getY());
		s.setRotation(getRotation());
		return s;
	}
}
