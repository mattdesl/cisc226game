package space.entities;

import org.newdawn.slick.Graphics;

import space.engine.SpriteBatch;

public interface RenderableEntity {
	public void draw(SpriteBatch b, Graphics g);
}
