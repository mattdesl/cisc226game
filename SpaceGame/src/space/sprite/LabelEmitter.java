package space.sprite;

import java.util.ArrayList;
import java.util.Stack;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import space.GameContext;
import space.engine.SpriteBatch;
import space.engine.easing.Easing;
import space.engine.easing.SimpleFX;
import space.ui.Label;

public class LabelEmitter implements Sprite {
	
	private AngelCodeFont font;
	private float originX, originY;
	private float stepOffset;
	
	private int addDelay = 250;
	private int timer = addDelay;
	
	private ArrayList<LabelParticle> particles = new ArrayList<LabelParticle>();
	private Stack<LabelParticle> queue = new Stack<LabelParticle>();
	
	private float emitterWidth;
	static Color sharedColor = new Color(1f,1f,1f,1f);

	public LabelEmitter(AngelCodeFont font, float emittedWidth) {
		this.font = font;
		this.emitterWidth = emitterWidth;
	}
	
	public void setPosition(float x, float y) {
		originX = x;
		originY = y;
	}
	
	public void append(String text) {
		append(text, Color.white);
	}
	
	public void append(String text, Color color) {
		queue.add(new LabelParticle(text, color));
	}
	
	public void update(int delta) {
		timer += delta;
		if (!queue.isEmpty() && timer > addDelay) {
			timer = 0;
			particles.add(queue.pop());
		}
		for (int i=0; i<particles.size(); i++) {
			LabelParticle p = particles.get(i);
			if (p.alive()) {
				p.update(delta);
				if (!p.alive())
					particles.remove(i--);
			} else {
				particles.remove(i--);
			}
		}
	}
	
	public void draw(SpriteBatch b, Graphics g) {
		for (int i=0; i<particles.size(); i++) {
			LabelParticle p = particles.get(i);
			float x = originX + emitterWidth/2f - p.textWidth/2f;
			float y = originY - p.drift();
			p.draw(b, x, y);
			
		}
	}
	
	private class LabelParticle {
		
		private final float TIME = 2000f;
		private final float OFFSET = 100f;
		private boolean alive = true;
		private String text;
		private SimpleFX alpha = new SimpleFX(1f, 0f, TIME, Easing.SINE_OUT);
		private SimpleFX drift = new SimpleFX(0f, OFFSET, TIME, Easing.QUAD_OUT);
	    float textWidth;
		Color color;
		
		
		
		public LabelParticle(String text, Color color) {
			this.text = text;
			textWidth = font.getWidth(text);
			this.color = color;
		}
		
		public boolean alive() {
			return alive;
		}
		
		public float drift() {
			return drift.getValue();
		}
		
		public void draw(SpriteBatch b, float x, float y) {
			Color color = sharedColor;
			if (this.color!=null) {
				color.r = this.color.r;
				color.g = this.color.g;
				color.b = this.color.b;
			}
			color.a = alpha.getValue();
			b.setColor(color);
			b.drawText(font, text, (int)x, (int)y);
		}
		
		public void update(int delta) {
			if (!alive)
				return;
			alpha.update(delta);
			drift.update(delta);
			if (alpha.getValue() <= 0f) {
				alive = false;
			}
		}
	}
}
