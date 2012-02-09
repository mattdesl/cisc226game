package space.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import space.easing.Easing;
import space.easing.SimpleFX;
import space.game.GameContext;
import space.game.SpaceGame;
import space.util.Resources;
import space.util.Utils;

/**
 * 
 * 
 * @author davedes
 */
public class MenuBackground {

	Image bg, alphaMap;
	
	//Constants for the background flicker
	protected static final int FLICKER_DELAY_LOW = 20;
	protected static final int FLICKER_DELAY_HIGH = 110;
	protected static final float FLICKER_ADJUSTMENT = 0.002f;
	protected static final float FLICKER_ALPHA_LOW = 0.8f;
	protected static final float FLICKER_ALPHA_HIGH = 1f;
	
	//Constants for scaling the background in/out
	protected static final float SCALE_LOW = 0.87f;
	protected static final float SCALE_HIGH = 0.95f;
	protected static final float SCALE_DURATION = 1800f;
	protected static final Easing SCALE_EASING = Easing.QUAD_IN_OUT;
	
	//an fx for scaling the background
	protected SimpleFX lightScale = new SimpleFX(SCALE_LOW, SCALE_HIGH, SCALE_DURATION, SCALE_EASING);
	
	private int flickerTime;
	private int nextFlickerTime = Utils.rnd(FLICKER_DELAY_LOW, FLICKER_DELAY_HIGH); 
	private Color lighting = new Color(1f,1f,1f,1f);
	private float nextFlickerAlpha = 1f;
	
	public int offscreenPtr;
	
	public MenuBackground(GameContext context, int offscreenPtr) throws SlickException {
		this.offscreenPtr = offscreenPtr;
		bg = Resources.getImage("menu.bg");
		alphaMap = Resources.getImage("menu.alphaMap");
		reinit(context);
	}
	
	/**
	 * Called upon entering the menu state -- updates the offscreen graphics and
	 * resets the flicker values. If the detail level changes, this should be
	 * called.
	 */
	public void reinit(GameContext context) {
		flickerTime = 0;
		nextFlickerTime = Utils.rnd(FLICKER_DELAY_LOW, FLICKER_DELAY_HIGH);
		lighting.a = nextFlickerAlpha = 1f;
		updateOffscreen(context);
	}
	
	private void drawBG(GameContext context, Graphics g, Color lighting, boolean isStatic) {
		float screenWidth = context.getWidth();
		float screenHeight = context.getHeight();
		
		float texsize = SpaceGame.OFFSCREEN_TEXSIZE;
		
		//the width/height of our background
		float width = Math.min(screenWidth, texsize);
		float height = Math.min(screenHeight, texsize);
		
		float ls = isStatic ? MenuBackground.SCALE_HIGH : lightScale.getValue();
		
		//the bounds of our alpha map
		float w = width * ls, h = height * ls;
		
		//dynamic mode: draw map in center of SCREEN
		//static mode: draw map in center of CONSTRAINED TEXTURE (800x600 or 1024x1024)
		float parentWidth = isStatic ? texsize : screenWidth;
		float parentHeight = isStatic ? texsize : screenHeight;

		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		alphaMap.draw(parentWidth/2f-w/2f, parentHeight/2f-h/2f, w, h, lighting);
		
		//dynamic mode: draw pattern in center of SCREEN
		//static mode: draw pattern from (0, 0) to CONSTRAINED width/height
		g.setDrawMode(Graphics.MODE_ALPHA_BLEND);
		if (isStatic) {
			Utils.texture(bg, 0, 0, parentWidth, parentHeight);
		} else {
			//we've cached the pattern for a slight performance gain
			//Utils.texture(bg, parentWidth/2f-texsize/2f, parentHeight/2f-texsize/2f, parentWidth, parentHeight);
			context.getOffscreenImage(offscreenPtr).draw(parentWidth/2f-texsize/2f, parentHeight/2f-texsize/2f);
		}
		
		g.setDrawMode(Graphics.MODE_NORMAL);
	}
	
	public void draw(GameContext context, Graphics g) throws SlickException {
		if (context.getDetailLevel()<=GameContext.DETAIL_LOWEST)
			return;
		if (context.getDetailLevel()==GameContext.DETAIL_LOW) {
			float texsize = SpaceGame.OFFSCREEN_TEXSIZE;
			//our whole scene is an offscreen image
			context.getOffscreenImage(offscreenPtr).draw(context.getWidth()/2f-texsize/2f, context.getHeight()/2f-texsize/2f, this.lighting);
		} else {
			//only the bg pattern is an offscreen image; the light map scales subtly
			drawBG(context, g, this.lighting, false);
		}
	}
	
	public void update(GameContext context, int delta) throws SlickException {
		flickerTime += delta;
		if (flickerTime >= nextFlickerTime) {
			flickerTime -= nextFlickerTime;
			nextFlickerTime = Utils.rnd(FLICKER_DELAY_LOW, FLICKER_DELAY_HIGH);
			nextFlickerAlpha = Utils.rnd(FLICKER_ALPHA_LOW, FLICKER_ALPHA_HIGH);
		}
		if (lighting.a > nextFlickerAlpha && lighting.a > FLICKER_ALPHA_LOW) {
			lighting.a = Math.max(lighting.a - FLICKER_ADJUSTMENT * delta, nextFlickerAlpha);
		} else if (lighting.a < nextFlickerAlpha && lighting.a < FLICKER_ALPHA_HIGH) {
			lighting.a = Math.min(lighting.a + FLICKER_ADJUSTMENT * delta, nextFlickerAlpha);
		}
		
		lightScale.update(delta);
		if (lightScale.finished()) {
			lightScale.flip();
			lightScale.restart();
		}
	}
	
	private void updateOffscreen(GameContext context) {
		if (context.getDetailLevel()<=GameContext.DETAIL_LOWEST)
			return;
		Graphics g = context.getOffscreenGraphics(offscreenPtr);
		Graphics.setCurrent(g);
		g.clear();
		
		float s = SpaceGame.OFFSCREEN_TEXSIZE;
		//if game detail is on LOW, then render the entire background ONCE to our FBO
		if (context.getDetailLevel()==GameContext.DETAIL_LOW) {
			drawBG(context, g, Color.white, true);
		} 
		//otherwise, render the repeating pattern ONCE to our FBO, and change the alpha map dynamically
		else {
			Utils.texture(bg, 0, 0, s, s);
		}
		g.flush();
	}
}
