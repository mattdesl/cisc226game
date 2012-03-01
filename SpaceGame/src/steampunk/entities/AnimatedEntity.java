package steampunk.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;

/** 
 * This class denotes an entity with an associated animation
 * @author TomW7
 *
 */

public abstract class AnimatedEntity extends AbstractEntity {

	Animation currentAnimation;  // the animation for the current action being performed

	public AnimatedEntity (World world, float x, float y, float width, float height){
		super(world, x, y, width, height);
		currentAnimation = initAnimations();
	}

	public AnimatedEntity(World world, Animation anim, float x, float y){
		this(world, x, y, anim.getWidth(), anim.getHeight());
	}

	// this method will give us the correct initial animation to use
	public abstract Animation initAnimations();

	public void draw(Graphics g){
		ROVector2f vector = body.getPosition();
		currentAnimation.draw(vector.getX(),vector.getY());
		g.setColor(Color.green);
		g.drawRect(vector.getX(), vector.getY(), width, height);
	}


}
