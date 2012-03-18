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
	
	private int damage = 50;
	
	private int time = 0;
	private int life = 2500;
	
	
	public Bullet(float x, float y, float dirX, float dirY, boolean playerBullet) {
		this.dirX = dirX;
		this.dirY = dirY;
		body = new Body(new Circle(RADIUS),1f);
		body.setMaxVelocity(Constants.PLAYER_BLASTER_SPEED, Constants.PLAYER_BLASTER_SPEED);
		body.setPosition(x, y);
		body.addBit(playerBullet ? Constants.BIT_PLAYER_GROUP : Constants.BIT_ENEMY_GROUP);
		body.setUserData(this);
		
		addForce(dirX * Constants.PLAYER_BLASTER_SPEED, dirY * Constants.PLAYER_BLASTER_SPEED);
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void collide(Entity e) {
		//ignore...
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
			active = false;
		time += delta;
		if (time > life) {
			active = false;
		}
	}
	
	public void draw(GameContext context, SpriteBatch batch, Graphics g) {
		batch.flush();
		g.setColor(Color.red);
		g.fillOval(getX()-RADIUS, getY()-RADIUS, RADIUS*2f, RADIUS*2f);
	}
}
