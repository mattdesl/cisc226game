package space.entities;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import space.GameContext;
import space.engine.SpriteBatch;

public class Bullet extends AbstractEntity {
	
	private float dirX, dirY;
	private final float RADIUS = 10f;
	private boolean alive = true;
	
	public Bullet(float x, float y, float dirX, float dirY, boolean playerBullet) {
		this.dirX = dirX;
		this.dirY = dirY;
		body = new Body(new Circle(RADIUS),1f);
		body.setMaxVelocity(Constants.PLAYER_BLASTER_SPEED, Constants.PLAYER_BLASTER_SPEED);
		body.setPosition(x, y);
		body.addBit(playerBullet ? Constants.BIT_PLAYER_GROUP : Constants.BIT_ENEMY_GROUP);
		
		addForce(dirX * Constants.PLAYER_BLASTER_SPEED, dirY * Constants.PLAYER_BLASTER_SPEED);
	}
	
	public float getWidth() {
		return RADIUS*2f;
	}
	
	public float getHeight() {
		return RADIUS*2f;
	}
	
	public void update(GameContext context, int delta) {
		int width = context.getWidth();
		int height = context.getHeight();
		if (getX() < -getWidth() || getX() > width+getWidth()
				|| getY() < -getHeight() || getY() > height+getHeight())
			alive = false;
	}
	
	public boolean isActive() {
		return alive;
	}
	
	public void draw(GameContext context, SpriteBatch batch, Graphics g) {
		batch.flush();
		g.setColor(Color.red);
		g.fillOval(getX()-RADIUS, getY()-RADIUS, RADIUS*2f, RADIUS*2f);
	}
}
