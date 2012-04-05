package space.entities;

import java.io.IOException;

import org.newdawn.slick.Image;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import space.GameContext;
import space.util.Resources;

public class Kamikaze extends Enemy {
	
	// a Kamikaze enemy
	private int maxHealth;
	
	
	public Kamikaze(int wave){
		super(Resources.getSprite("kamikaze2"));
		maxHealth = 140 + (wave*10); // kamikazes are tough
		setHealth(maxHealth);
		setWeaponDamage(0); // kamikazes don't deal weapon damage
		setCollisionDamage(5 + (wave*5)); // quickly become very powerful
		setPointValue(100+(wave*100)); // desired: divide this value by #upgrades purchased.
		setBody(createBody());		
		body.setMaxVelocity(Constants.ENEMY_KAMIKAZE_SPEED, Constants.ENEMY_KAMIKAZE_SPEED);
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}

	public void update(GameContext context, int delta){
		// move towards the player relentlessly
		// somehow give the enemies a reference to the player, or a reference to the InGameState
		if (!dead){
			Ship player = context.getInGameState().getPlayer();
			// 	kamikazes rotate to face the player
			setHeading(player.getX(), player.getY());
			thrust(delta, Constants.ENEMY_KAMIKAZE_SPEED);
		} else {
			explosionTime += delta;
			if (explosionTime >= explosionLength/7){
				explosionTime = 0;
				explosionCounter++;				
			}		
		}
			
	}

	public void collide(Entity other) {
		if (other instanceof Bullet){
			Bullet bullet = (Bullet)other;
			takeDamage(bullet.getDamage());
			bullet.kill();
		}
		else if (other instanceof Ship){
			playDeath();
		}
	}
}
