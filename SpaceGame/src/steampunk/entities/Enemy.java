package steampunk.entities;

import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;

import steampunk.entities.StaticEntity;

public class Enemy extends StaticEntity{
	private final float SPEED = .05f;
	private int hitpoints = 25;

	public Enemy (World world, Image img, float x, float y){
		super(world,img,x,y);
	}

	public Body createBody(float width, float height){
		return new Body(new Box(width, height),10f);
	}

	public int getHp(){
		return hitpoints;
	}

	public void update(GameContainer container, int delta){
		ROVector2f enemyVel = body.getVelocity();
		boolean isHit = false; //place holder
		// if the enemy is hit by a bullet, slow him down based on hp damage dealt
		// needs some complex stuff. need to know which side of him the bullet hit, to reverse the 
		// velocity for a suitable time when he's hit
		// this is actually probably done for us already by the handy physics engine
	}
}