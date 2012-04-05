package space.entities;

import java.io.IOException;

import org.newdawn.slick.Image;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import space.GameContext;
import space.util.Resources;
import space.util.Utils;

public class Wingbat extends Enemy {

	private boolean left; // are we heading left?
	private int shootingInterval; // how often the firebat fires
	private int shootingTime = 0;
	private int maxHealth;
	private static Audio gunshot;

	public Wingbat(int wave) {
		super(Resources.getSprite("wingbat"));
		maxHealth = 40 + (wave*10);
		setHealth(maxHealth);
		setWeaponDamage(25 + (wave*10));
		int adjustedInterval = Constants.ENEMY_WINGBAT_SHOOTING_COOLDOWN - (wave*50); // ships will shoot faster as waves go on. 
		shootingInterval = (adjustedInterval > 300) ? adjustedInterval : 300; // can't shoot faster than 3x / second
		setCollisionDamage(5); // no collision damage increase for wingbats
		setPointValue(50+(wave*50));
		setBody(createBody());
		body.setMaxVelocity(Constants.ENEMY_WINGBAT_SPEED, Constants.ENEMY_WINGBAT_SPEED);
		// decide if this wingbat strafes left or right when orbiting the player,
		// to add some variety
		int random = Utils.rnd(0,2);
		if (random == 0){
			left = true;
		} else{
			left = false;
		}
		if (gunshot == null){
			try {
				gunshot = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sounds/wingbatGunshot.wav"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// gunshot delay

		random = Utils.rnd(50, 400);
		shootingTime-=random;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void update(GameContext context, int delta){
		// move toward a player until we're within 400 or so units. then we start orbiting the player
		// TODO: use player velocity to predict position, aim / orbit there instead
		if (!context.getInGameState().isPlayerAlive()) {
			//do nothing
		} else if (!dead){
			Ship player = context.getInGameState().getPlayer();
			setHeading(player.getX(), player.getY());
			double px = player.getX();
			double py = player.getY();
			double distance = Math.sqrt(Math.pow(getX()-px, 2) + Math.pow(getY()-py, 2));
			shootingTime += delta;
			if (distance <= 200){ // we orbit
				thrustReverse(delta, Constants.ENEMY_WINGBAT_REV_SPEED);
				thrustSide(delta, Constants.ENEMY_WINGBAT_SPEED, left);
			} else{
				thrust(delta, Constants.ENEMY_WINGBAT_SPEED); // we close the distance			
			}

			if (shootingTime > shootingInterval){
				shootingTime = 0;
				gunshot.playAsSoundEffect(1f,.7f,false);
				context.getInGameState().addEntity(new Bullet(getX(), getY(), dirX, dirY, getRotation(), getWeaponDamage(), false));
			}
		} else { // we're dead; keep all forces the same, no shooting, no new forces, just updating the explosion frame counter
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
			Ship ship = (Ship) other;
			takeDamage(ship.getCollisionDamage());
		}
	}
}
