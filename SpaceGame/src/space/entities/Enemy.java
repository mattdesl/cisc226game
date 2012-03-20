package space.entities;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import space.GameContext;
import space.engine.SpriteBatch;

public abstract class Enemy extends AbstractEntity {
	// only thing unique to enemies is the behaviour of their movement (update) and the 
	// graphic
	protected int health;
	protected int weaponDamage; 
	protected int collisionDamage;
	protected Image enemyImage;
	protected int enemyWidth;
	protected int enemyHeight;
	protected Color tint = new Color (1f,1f,1f,1f);
	protected double angle;
	protected float dirX, dirY;
	// private Image enemyThrust, enemyExplosion, enemyExplosion2
	
	// enemy strength based upon wave
	public Enemy(Image image) {
		this.enemyImage = image;
		enemyWidth = enemyImage.getWidth()/2;
		enemyHeight = enemyImage.getHeight()/2;
	}
	
	public Body createBody(){
		Body body = new Body(new Circle(enemyImage.getWidth()/2f), 10f);
		body.addBit(Constants.BIT_ENEMY);
		body.setUserData(this);
		return body;
	}
	
	public int getWidth(){
		return enemyImage.getWidth();
	}
	
	public int getHeight(){
		return enemyImage.getHeight();
	}
	
	public void setImage(Image image){
		enemyImage = image;
	}
	
	public void setHealth(int health){
		this.health = health;
	}
	
	public int getHealth(){
		return this.health;
	}
	
	public void setWeaponDamage(int damage){
		this.weaponDamage = damage;
	}
	
	public int getWeaponDamage(){
		return this.weaponDamage;
	}
	
	public void setCollisionDamage(int damage){
		this.collisionDamage = damage;
	}
	
	public int getCollisionDamage(){
		return this.collisionDamage;
	}
		
	public float getRotation(){
		return (float)Math.toDegrees(this.angle);
	}
	
	public void setRotation(float deg){
		double r = Math.toRadians(deg);
		this.dirX = (float) Math.sin(r);
		this.dirY = (float) -Math.cos(r);
		this.angle = r;
	}
	
	// sets rotation based on a location in the game, such as the player's location
	public void setHeading(float playerX, float playerY){
		double r = -Math.atan2((getX()-playerX), (getY()-playerY));
		setRotation((float)Math.toDegrees(r));
	}
	
	// thrust straight ahead	
	public void thrust(int delta){
		float dirXAmt = dirX * delta * Constants.ENEMY_KAMIKAZE_SPEED;
		float dirYAmt = dirY * delta * Constants.ENEMY_KAMIKAZE_SPEED;
		addForce(dirXAmt, dirYAmt);
	}
	
	//reverse thrust, to keep the enemy at a more constant distance
	public void thrustReverse(int delta){
		float dirXAmt = -(dirX * delta * Constants.ENEMY_WINGBAT_REV_SPEED);
		float dirYAmt = -(dirY * delta * Constants.ENEMY_WINGBAT_REV_SPEED);
		addForce(dirXAmt, dirYAmt);
	}
	
	// thrust sideways. the boolean indicates the heading, false for right, true for left
	public void thrustSide(int delta, boolean left){
		double r;
		if (left){
			r = angle - Math.PI/2;
		} else {
			r = angle + Math.PI/2;
		}
		float dirX = (float)Math.sin(r);
		float dirY= (float)-Math.cos(r);
		addForce(dirX * delta * Constants.ENEMY_WINGBAT_SPEED, dirY * delta * Constants.ENEMY_WINGBAT_SPEED);
	}
	public void draw(GameContext context, SpriteBatch batch, Graphics g){
		float newX = getX() - enemyWidth;
		float newY = getY() - enemyWidth;
		
		batch.setColor(tint);
		batch.drawImage(enemyImage, newX, newY, getRotation());
	}
	
	public void takeDamage(int damage){
		int newHealth = getHealth() - damage;
		if (newHealth <= 0){ // dead
			setHealth(0);
			kill();
			System.out.println("Enemy destroyed");
		} else{
			setHealth(newHealth);
			System.out.println("Enemy damaged: "+getHealth()+" remaining health");
		}
	}
	
	// we play the death animation
	public void playDeath(){
		kill(); 		
	}
}
