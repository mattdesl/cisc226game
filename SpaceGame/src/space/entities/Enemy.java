package space.entities;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import space.GameContext;
import space.engine.SpriteBatch;
import space.ui.HealthBarWidget;
import space.util.Resources;

public abstract class Enemy extends AbstractEntity {
	// only thing unique to enemies is the behaviour of their movement (update) and the 
	// graphic
	protected int health;
	protected int maxHealth;
	protected int weaponDamage; 
	protected int collisionDamage;
	protected Image enemyImage;
	protected Image explosion1, explosion2, explosion3, explosion4, explosion5, explosion6, explosion7;
	protected Animation deathAnimation;
	protected int explosionTime = 0;
	protected int explosionLength = 400; // 280 ms
	protected int explosionCounter;
	protected int enemyWidth;
	protected int enemyHeight;
	protected Color tint = new Color (1f,1f,1f,1f);
	protected double angle;
	protected float dirX, dirY;
	protected boolean dead;
	protected int pointValue;
	


	private HealthBarWidget healthBar;

	
	// enemy strength based upon wave
	public Enemy(Image image) {
		this.enemyImage = image;
		enemyWidth = enemyImage.getWidth()/2;
		enemyHeight = enemyImage.getHeight()/2;
		explosion1 = Resources.getSprite("explosion1");
		explosion2 = Resources.getSprite("explosion2");
		explosion3 = Resources.getSprite("explosion3");
		explosion4 = Resources.getSprite("explosion4");
		explosion5 = Resources.getSprite("explosion5");
		explosion6 = Resources.getSprite("explosion6");
		explosion7 = Resources.getSprite("explosion7");
		Image[] explArr = new Image[7];
		explArr[0] = explosion1;
		explArr[1] = explosion2;
		explArr[2] = explosion3;
		explArr[3] = explosion4;
		explArr[4] = explosion5;
		explArr[5] = explosion6;
		explArr[6] = explosion7;
		deathAnimation = new Animation(explArr, 40);
		deathAnimation.setLooping(false);
		Image bar = Resources.getSprite("healthbar");
		Image red = Resources.getSprite("healthbar.red");
		Image blue = Resources.getSprite("healthbar.blue");
		healthBar = new HealthBarWidget(bar, red, Resources.HEALTH_BAR_X_OFF, Resources.HEALTH_BAR_Y_OFF);
	}
	
	public abstract int getMaxHealth();
	
	public Body createBody(){
		Body body = new Body(new Circle(enemyImage.getWidth()/2f), 10f);
		body.addBit(Constants.BIT_ENEMY);
		body.setUserData(this);
		body.setRestitution(0.5f);
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
	
	public void setPointValue(int pointValue){
		this.pointValue = pointValue;
	}
	
	public int getPointValue(){
		return this.pointValue;
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
	public void thrust(int delta, float speed){
		float dirXAmt = dirX * delta * speed;
		float dirYAmt = dirY * delta * speed;
		addForce(dirXAmt, dirYAmt);
	}
	
	//reverse thrust, to keep the enemy at a more constant distance
	public void thrustReverse(int delta, float speed){
		float dirXAmt = -(dirX * delta * speed);
		float dirYAmt = -(dirY * delta * speed);
		addForce(dirXAmt, dirYAmt);
	}
	
	// thrust sideways. the boolean indicates the heading, false for right, true for left
	public void thrustSide(int delta, float speed, boolean left){
		double r;
		if (left){
			r = angle - Math.PI/2;
		} else {
			r = angle + Math.PI/2;
		}
		float dirX = (float)Math.sin(r);
		float dirY= (float)-Math.cos(r);
		addForce(dirX * delta * speed, dirY * delta * speed);
	}
	public void draw(GameContext context, SpriteBatch batch, Graphics g){
		float newX = getX() - enemyWidth;
		float newY = getY() - enemyWidth;
		
		batch.setColor(tint);
		
		if (dead) {// if we're dead
			body.setBitmask(Constants.BIT_UNCOLLIDABLE);
			batch.drawImage(deathAnimation.getImage(explosionCounter), newX, newY, getRotation());
			if (explosionCounter == 6){
				kill(); // animation is over
			}
		}
		else {
			batch.drawImage(enemyImage, newX, newY, getRotation());
			//draw shield + health bar
			float x = getX() - (healthBar.getWidth()/2f);
			float y = getY() + enemyImage.getHeight()/2f + 1;
			healthBar.setPosition(x, y+healthBar.getHeight());
			healthBar.setValue(health/(float)getMaxHealth());
			healthBar.draw(batch, g);
		}

	}
	
	public void takeDamage(int damage){
		int newHealth = getHealth() - damage;
		if (newHealth <= 0){ // dead
			setHealth(0);
			playDeath();
			System.out.println("Enemy destroyed");
		} else{
			setHealth(newHealth);
			System.out.println("Enemy damaged: "+getHealth()+" remaining health");
		}
	}
	
	public float getHealthPercentage(){
		return health / maxHealth;
	}
	
	public void setMaxHealth(int maxHealth){
		this.maxHealth = maxHealth;
	}
	
	// we play the death animation
	public void playDeath(){
		dead = true;
	}
}
