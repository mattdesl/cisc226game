package steampunk.entities;

import net.phys2d.math.ROVector2f;
import net.phys2d.raw.World;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * This class denotes any static entity, with no animation. good for terrain / obstacles. 
 * 
 * @author TomW7
 *
 */


public abstract class StaticEntity extends AbstractEntity {
	// static entities have an image, not an animation 
	Image img;

	public StaticEntity (World world, Image img, float x, float y, float width, float height){
		super(world, x, y, width, height);
		this.img = img;
	}
	
	public StaticEntity(World world, Image img, float x, float y){
		this(world, img, x, y, img.getWidth(), img.getHeight());
	}

	public void draw(Graphics g){
		ROVector2f vector = body.getPosition();
		float vectorX = vector.getX()-width/2f;
		float vectorY = vector.getY()-height/2f;

		img.startUse();
		for (int i = 0; i < width; i+=EntityConstants.TILESIZE){
			for (int j = 0; j < height; j+=EntityConstants.TILESIZE){
				img.drawEmbedded(vectorX+i, vectorY+j, img.getWidth(), img.getHeight());
			}
		}
		img.endUse();
		g.setColor(Color.red);
		g.drawRect(vectorX, vectorY, width, height);
	}

	public void update(GameContainer container, int delta){}
}
