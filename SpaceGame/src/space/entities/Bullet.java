package space.entities;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import space.GameContext;
import space.engine.SpriteBatch;
import space.util.Resources;

public class Bullet extends AbstractEntity {
	
	private float dirX, dirY;
	private final float RADIUS = 4f;
	
	private int damage = 50;
	
	private int time = 0;
	private int life = 2500;
	private Image image;
	private float angle = 0;
	
	public Bullet(float x, float y, float dirX, float dirY, float angle, int damage, boolean playerBullet) {
		this.dirX = dirX;
		this.dirY = dirY;
		this.angle = angle;
		this.damage = damage;
		image = Resources.getSprite("playerbullet");
		setBody(createBody(playerBullet));
		setPosition(x, y);
		if (playerBullet){
			addForce(dirX * Constants.PLAYER_BLASTER_SPEED, dirY * Constants.PLAYER_BLASTER_SPEED);
		} else {
			// an enemy bullet
			addForce(dirX * Constants.ENEMY_BLASTER_SPEED, dirY * Constants.ENEMY_BLASTER_SPEED);
		}
	}
	
	public int getDamage() {
		return damage;
	}
	
	public Body createBody(boolean playerBullet){
		Body body = new Body(new Circle(RADIUS),1f);
		if (playerBullet){
			body.setMaxVelocity(Constants.PLAYER_BLASTER_SPEED, Constants.PLAYER_BLASTER_SPEED);
		} else{
			body.setMaxVelocity(Constants.ENEMY_BLASTER_SPEED, Constants.ENEMY_BLASTER_SPEED);
		}
		body.addBit((playerBullet ? Constants.BIT_PLAYER : Constants.BIT_ENEMY) | Constants.BIT_BULLET);
		body.setUserData(this);
		return body;
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
				|| getY() < -getHeight() || getY() > height+getHeight()) // die if we leave the playable area
			active = false;
		time += delta;
		if (time > life) {
			active = false;
		}
	}
	
	public void draw(GameContext context, SpriteBatch batch, Graphics g) {
		batch.drawImage(image, getX()-image.getWidth()/2f, getY()-image.getHeight()/2f, angle);
		//batch.flush();
		//g.setColor(Color.red);
		//g.fillOval(getX()-RADIUS, getY()-RADIUS, RADIUS*2f, RADIUS*2f);
	}
}
