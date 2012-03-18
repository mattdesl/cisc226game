package space.entities;

import org.newdawn.slick.Image;

import space.GameContext;
import space.util.Resources;

public class Kamikaze extends Enemy {
	
	// a Kamikaze enemy

	public Kamikaze(int wave){
		super(Resources.getSprite("kamikaze"));
		setHealth(100 + (wave*10));
		setWeaponDamage(0); // kamikazes don't deal weapon damage
		setCollisionDamage(80 + (wave*20)); // quickly become very powerful
		setBody(createBody());		
		body.setMaxVelocity(Constants.ENEMY_KAMIKAZE_SPEED, Constants.ENEMY_KAMIKAZE_SPEED);
		enemyWidth = enemyImage.getWidth()/2;
		enemyHeight = enemyImage.getHeight()/2;
	}

	public void update(GameContext context, int delta){
		// move towards the player relentlessly
		// somehow give the enemies a reference to the player, or a reference to the InGameState
		Ship player = context.getInGameState().getPlayer();
		// kamikazes rotate to face the player
		setHeading(player.getX(), player.getY());
		thrust(delta);
	}

	public void collide(Entity other) {
		if (other instanceof Bullet){
			Bullet bullet = (Bullet)other;
			takeDamage(bullet.getDamage());
			bullet.kill();
		}
		else if (other instanceof Ship){
			kill();
		}
	}
}
