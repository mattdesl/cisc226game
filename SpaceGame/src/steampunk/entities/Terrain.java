package steampunk.entities;

import org.newdawn.slick.Image;

import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

/**
 * This class stands for a piece of the game world :D
 * @author TomW7
 *
 */

public class Terrain extends StaticEntity {
	
	public Terrain(World w, Image img, float x, float y, float width, float height){
		super (w, img, x, y, width, height);
	}

	@Override
	public Body createBody(float width, float height) {
		Body retVal = new StaticBody(new Box(width, height)); 
		retVal.setRotatable(false);
		return retVal;
	}

}
