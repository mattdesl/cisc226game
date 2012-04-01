package space.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import space.engine.SpriteBatch;

public class HealthBarWidget extends Widget {

	private Image progress;
	private float perc;
	private float xoff, yoff;
	
	public HealthBarWidget(Image bar, Image progress, int xoff, int yoff) {
		super(bar);
		this.progress = progress;
		this.xoff = xoff;
		this.yoff = yoff;
		imageFilter.a = 0.8f;
	}
	
	public void setValue(float perc) {
		this.perc = perc;
	}
	
	public float getValue() {
		return perc;
	}
	
	public void drawBackground(SpriteBatch batch, Graphics g, float screenX, float screenY) {
		if (progress!=null) {
			float newWidth = progress.getWidth() * perc;
			batch.setColor(Color.white);
			batch.drawImage(progress, (int)(screenX+xoff), (int)(screenY+yoff), (int)newWidth+1, (int)progress.getHeight()+1);
		}
		super.drawBackground(batch, g, screenX, screenY);
	}
	
}
