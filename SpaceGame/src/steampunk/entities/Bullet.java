package steampunk.entities;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import steampunk.entities.StaticEntity;

public class Bullet extends AnimatedEntity {
	Body body; 
	float width = EntityConstants.LIGHTNING_WIDTH;
	float height = EntityConstants.LIGHTNING_HEIGHT;
	

	public Bullet (World world, float x, float y){
		body = createBody(width, height);
		body.setPosition(x, y);
		body.setMaxVelocity(70,70);
		body.adjustVelocity(new Vector2f(70f,0));
		body.setGravityEffected(false);
		world.add(body);
	}

	public Body createBody(float w, float h){
		return new Body(new Box(width, height),.5f);
	}
	
	public void draw(Graphics g){
		ROVector2f vector = body.getPosition();
		float vectorX = vector.getX()-width/2f;
		float vectorY = vector.getY()-height/2f;

		g.setColor(Color.red);
		g.drawRect(vectorX, vectorY, width, height);
	}

	public void update(GameContainer gameContainer, int delta){
	}

}
