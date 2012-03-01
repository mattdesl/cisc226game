package steampunk.entities;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import steampunk.entities.StaticEntity;

public class Player extends AnimatedEntity {

	//animations
	Animation moveLeft;
	Animation moveRight;
	Animation jump;
	Animation idle;
	Animation attack;
	
	private final float SPEED = .05f;

	public Player(World world, float x, float y) {
		super(world, x, y, EntityConstants.PLAYER_WIDTH, EntityConstants.PLAYER_HEIGHT);
	}

	/**
	 * This method initializes all the animations we'll need, and also returns the one we should use when
	 * we intially create the player. 
	 * 
	 * @return
	 * @throws SlickException
	 */
	public Animation initAnimations() {
		try{
			SpriteSheet idleSS = new SpriteSheet(new Image("res/tex/playerIdle.png"), 45,45);
			SpriteSheet moveRightSS = new SpriteSheet(new Image("res/tex/playerMoveRight.png"), 45, 45);
			SpriteSheet moveLeftSS = new SpriteSheet(new Image("res/tex/playerMoveLeft.png"), 45, 45);
			SpriteSheet jumpSS = new SpriteSheet(new Image("res/tex/playerJump.png"), 75,51);
			SpriteSheet attackSS = new SpriteSheet(new Image("res/tex/playerAttack.png"), 45,45);
			moveLeft = new Animation(moveLeftSS, 100);
			moveRight = new Animation(moveRightSS, 100);
			idle = new Animation(idleSS, 100);
			jump = new Animation(jumpSS, 100);
			attack = new Animation(attackSS, 50);    
		}
		catch(Exception e){e.printStackTrace();}
		return idle;
	}


	public Body createBody(float w, float h) {
		Body retVal = new Body(new Box(w, h), 10f);
		retVal.setMaxVelocity(20,35);
		return retVal;
	}
	
	public void update(GameContainer c, int delta) {
		float vy = body.getVelocity().getY();
		float vx = body.getVelocity().getX();
		
		if (vy < 1 ) {
			if (c.getInput().isKeyDown(Input.KEY_A)) {
				// heading left
				currentAnimation = moveLeft;
				body.adjustVelocity(new Vector2f(-SPEED*delta, 0f));
			} else if (c.getInput().isKeyDown(Input.KEY_D)) {
				currentAnimation = moveRight;
				body.adjustVelocity(new Vector2f(SPEED*delta, 0f));
			} else 
				currentAnimation = idle;
			// we're on the ground
			if (c.getInput().isKeyDown(Input.KEY_SPACE) && vy == 0) {
				currentAnimation = jump;
				body.adjustVelocity(new Vector2f(0f, EntityConstants.PLAYER_JUMP_VELOCITY));

			} 
			
		}

	}
}
