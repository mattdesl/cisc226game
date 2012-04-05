package space.sprite;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import space.GameContext;
import space.engine.SpriteBatch;
import space.engine.easing.Easing;
import space.engine.easing.SimpleFX;
import space.util.Resources;
import space.util.Utils;

/**
 * The starfield is broken up into a grid; say we use a gridSize of 2, 
 * that means that there will be four grid tiles and a total of four stars placed
 * on the screen.
 * 
 * @author Matt
 */
public class StarfieldSprite implements Sprite {

	static class Star {
		float tilex, tiley;
		float x, y;
		Image star;
		float drift;
		float alpha;
		private static Color sharedColor = new Color(1f,1f,1f,1f);
		
		public Star(Image star, float tilex, float tiley, float x, float y) {
			this.star = star;
			this.tilex = tilex;
			this.tiley = tiley;
			this.x = x; 
			this.y = y;
		}
		
		public void draw(GameContext context, SpriteBatch batch, Graphics g) {
			float tileW = context.getWidth() / GRID_SIZE;
			float tileH = context.getHeight() / GRID_SIZE;
			sharedColor.a = alpha;
			batch.setColor(sharedColor);
			batch.drawImage(star, tilex+x - star.getWidth()/2f, tiley+y - star.getHeight()/2f);
		}
	}
	
	Image starSmall, starMed, starBig;
	Image bg;
	
	public static float GRID_SIZE = 10;
	public static float BIG_STAR_CHANCE = 1;
	public static float MEDIUM_STAR_CHANCE = 30;
	
	private ArrayList<Star> stars = new ArrayList<Star>();
	
	public StarfieldSprite() {
		starSmall = Resources.getSprite("star1");
		starMed = Resources.getSprite("star2");
		starBig = Resources.getSprite("star3");
		bg = Resources.getSprite("background");
	}
	
	public int size() {
		return stars.size();
	}
	
	private Image rndStar() {
		int i = Utils.rnd(0, 100);
		if (i<BIG_STAR_CHANCE)
			return starBig;
		else if (i<MEDIUM_STAR_CHANCE)
			return starMed;
		return starSmall;
	}
	
	private void randomizeStar(Star s) {
		Image img = s.star;
		float drift = Utils.rndFloat() / 200f;
		if (img==starSmall)
			drift *= 0.5f;
		else if (img==starMed)
			drift *= 0.25f;
		else if (img==starBig)
			drift *= 0.05f;
		s.drift = drift;
		s.alpha = (img==starBig) ? 1f : Utils.rnd(0.10f, 1f);
	}
	
	
	public void randomize(GameContext context) {
		float tileW = context.getWidth() / GRID_SIZE;
		float tileH = context.getHeight() / GRID_SIZE;
		int grid = (int)GRID_SIZE;
		stars.clear();
		stars.ensureCapacity(grid*grid);
		
		for (int x=0; x<grid; x++) {
			for (int y=0; y<grid; y++) {
				Image star = rndStar();
				Star s = new Star(star,
						x*tileW, y*tileH,
						Utils.rnd(0, tileW), Utils.rnd(0, tileH));
				randomizeStar(s);
				stars.add(s);	
			}
		}
	}
	
	public void update(GameContext context, int delta) {
		float tileW = context.getWidth() / GRID_SIZE;
		float tileH = context.getHeight() / GRID_SIZE;
		
		for (int i=0; i<stars.size(); i++) {
			Star s = stars.get(i);
			s.x += s.drift*delta;
			if (s.x+s.tilex > context.getWidth()) {
				s.x = s.tilex = 0;
				s.star = rndStar();
				randomizeStar(s);
				s.y = Utils.rnd(0, tileH);
			}
		}
	}
	
	public void drawBackground(GameContext context, SpriteBatch batch, Graphics g) {
		float cw = context.getWidth();
		float ch = context.getHeight();
		batch.drawImage(bg, 0, 0,cw,ch);
	}
	
	public void drawStars(GameContext context, SpriteBatch batch, Graphics g) {
		for (int i=0; i<stars.size(); i++) {
			stars.get(i).draw(context, batch, g);
		}
	}
}
