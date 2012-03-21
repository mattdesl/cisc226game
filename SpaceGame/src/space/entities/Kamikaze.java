package space.entities;

import org.newdawn.slick.Image;

import space.GameContext;
import space.util.Resources;

public class Kamikaze extends Enemy {
	
	// a Kamikaze enemy

	public Kamikaze(int wave){
		super(Resources.getSprite("kamikaze2"));
		setHealth(140 + (wave*10)); // kamikazes are tough
		setWeaponDamage(0); // kamikazes don't deal weapon damage
		setCollisionDamage(80 + (wave*20)); // quickly become very powerful
		setPointValue(100+(wave*100)); // desired: divide this value by #upgrades purchased.
		setBody(createBody());		
		body.setMaxVelocity(Constants.ENEMY_KAMIKAZE_SPEED, Constants.ENEMY_KAMIKAZE_SPEED);

	}

	public void update(GameContext context, int delta){
		// move towards the player relentlessly
		// somehow give the enemies a reference to the player, or a reference to the InGameState
		Ship player = context.getInGameState().getPlayer();
		// kamikazes rotate to face the player
		setHeading(player.getX(), player.getY());
		thrust(delta, Constants.ENEMY_KAMIKAZE_SPEED);
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
