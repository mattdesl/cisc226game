package steampunk.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import net.phys2d.raw.Body;
import net.phys2d.raw.World;

public abstract class AbstractEntity {
	Body body;
	float width, height;
	
	public AbstractEntity(World world, float x, float y, float width, float height){
		this.width = width;
		this.height = height;
		body = createBody(width, height);
		body.setPosition(x,y);
		world.add(body);
	}
	
	public AbstractEntity(){}
	
	public abstract Body createBody(float width, float height);
	
	public abstract void update(GameContainer container, int delta);
	
	public abstract void draw(Graphics g);
}
