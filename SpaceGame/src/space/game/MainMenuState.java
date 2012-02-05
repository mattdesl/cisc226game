package space.game;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import space.easing.Easing;
import space.easing.SimpleFX;
import space.util.Images;
import space.util.Utils;

public class MainMenuState extends SpaceGameState {
	
	static final int ID = 1;
	
	
	public MainMenuState(GameContext context) {
		super(context, ID);
	}
	
	Image bg, paper, alphaMap;
	
	private static final int FLICKER_DELAY_LOW = 20;
	private static final int FLICKER_DELAY_HIGH = 110;
	private static final float FLICKER_ADJUSTMENT = 0.002f;
	private static final float FLICKER_ALPHA_LOW = 0.8f;
	private static final float FLICKER_ALPHA_HIGH = 1f;
	
	private static final float SCALE_LOW = 0.84f;
	private static final float SCALE_HIGH = 0.93f;
	private static final float SCALE_DURATION = 1800f;
	private static final Easing SCALE_EASING = Easing.QUAD_IN_OUT;

	private SimpleFX lightScale = new SimpleFX(SCALE_LOW, SCALE_HIGH, SCALE_DURATION, SCALE_EASING);

	private static final float PAPER_ROT_START = -1;
	private static final float PAPER_ROT_END = 1f;
	private static final float PAPER_ROT_DURATION = 4000;
	private static final Easing PAPER_ROT_EASING = Easing.EXPO_OUT;
	
	private SimpleFX paperRot, paperAlpha;
	private Color paperFilter = new Color(1f,1f,1f,1f);
	
	private int flickerTime;
	private int nextFlickerTime = Utils.rnd(FLICKER_DELAY_LOW, FLICKER_DELAY_HIGH); 
	private Color lighting = new Color(1f,1f,1f,1f);
	private float nextFlickerAlpha = 1f;
	private boolean flickerAdj = false;
	
	private Image atmos;
	private Color atmosFilter = new Color(1f,1f,1f,.3f);
	
	private boolean hidePaper = false;
	
	private Font font;
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#init(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame)
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		bg = Images.get("menu.bg");
		paper = Images.get("menu.paper");
		alphaMap = Images.get("menu.alphaMap");
		atmos = Images.get("atmosphere");
		
		float s1 = Utils.rnd(PAPER_ROT_START, PAPER_ROT_END);
		float s2 = Utils.rnd(PAPER_ROT_START, PAPER_ROT_END);
		paperRot = new SimpleFX(s1, PAPER_ROT_END, PAPER_ROT_DURATION, PAPER_ROT_EASING);
		paperAlpha = new SimpleFX(0.5f, 1f, 550f, Easing.QUAD_OUT);
		
		updateOffscreen();
	}

	public void keyReleased(int k, char c) {
		if (paperAlpha.finished()) {
			hidePaper = !hidePaper;
			if (hidePaper)
				paperAlpha.setValue(0f);
			else
				entering();
		}
	}
	
	private void updateOffscreen() {
		Graphics g = context.getOffscreenGraphics();
		Graphics.setCurrent(g);
		g.clear();
		Image off = context.getOffscreenImage();
		float w = off.getWidth();
		float h = off.getHeight();
		Utils.texture(bg, 0, 0, w, h);
		g.flush();
	}
	
	
	public void entering() {
		float s1 = Utils.rnd(PAPER_ROT_START, PAPER_ROT_END);
		float s2 = Utils.rnd(PAPER_ROT_START, PAPER_ROT_END);
		paperRot.setStart(s1);
		paperRot.setEnd(s2);
		paperAlpha.restart();
		paperRot.restart();
		//lightScale.restart();
		updateOffscreen();
		flickerTime = 0;
		nextFlickerTime = Utils.rnd(FLICKER_DELAY_LOW, FLICKER_DELAY_HIGH);
		lighting.a = nextFlickerAlpha = 1f;
	}
	
	private void drawLines(Graphics g, float scale, float paperWidth, float yoff, String ... lines) {
		g.setColor(new Color(0f,0f,0f,paperFilter.a));
		float maxWidth = 0f;
		for (String l : lines) 
			maxWidth = Math.max(maxWidth, g.getFont().getWidth(l));
		g.translate(paperWidth/2f- maxWidth/2f*scale, 0);
		g.scale(scale, scale);
		for (String l : lines) {
			g.drawString(l, 0, yoff);
			yoff += g.getFont().getLineHeight();
		}
	}
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#render(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		Graphics.setCurrent(g);

		
		float sw = container.getWidth(), sh = container.getHeight();
		float IMGSIZE = Math.min(sh, 1024);
		
		////// ALPHA MAP RENDERING
		// If the height is < 1024, shrink to fit
		// Otherwise render fully
		
		float ls = lightScale.getValue();
		float w = IMGSIZE*ls, h = IMGSIZE*ls;
		
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		alphaMap.draw(sw/2f-w/2f, sh/2f-h/2f, w, h, lighting);
		
		g.setDrawMode(Graphics.MODE_ALPHA_BLEND);
		context.getOffscreenImage().draw(sw/2f-IMGSIZE/2f, sh/2f-IMGSIZE/2f);
		
		g.setDrawMode(Graphics.MODE_NORMAL);
		
		////// PAPER RENDERING
		// If height is < 1024, shrink to fit
		float ang = paperRot.getValue();
		float pw = paper.getWidth();
		float ph = paper.getHeight();
		float s = 1f;
		if (sh<1024)
			s = .9f*sh/ph;
		pw *= s;
		ph *= s;
		g.translate(sw/2f-pw/2f, sh/2f-ph/2f);
		g.rotate(pw/2f, ph/2f, ang);
		//g.scale(s, s);
		
		paperFilter.a = paperAlpha.getValue();
		paper.draw(0, 0, pw, ph, paperFilter);
		g.setAntiAlias(false);
		float textScale = 1f;
		if (s<1f)
			textScale = Math.min(1f, s*2);
		drawLines(g, textScale, pw, 150,
				"Hello, world.", "Press a key to show/hide me",
				"Best viewed in 1920x1080");
		
		g.resetTransform();
		atmos.draw(0, 0, sw, sh, atmosFilter);
		
//		float px = paperX.getValue()+sw/2f-paper.getWidth()/2f;
//		float py = paperY.getValue()+sh/2f-paper.getHeight()/2f;
//		float cx = paper.getWidth()/2f;
//		float cy = paper.getHeight()/2f;
//		float ang = paperRot.getValue();
//		
//		g.translate(px, py);
//		g.rotate(cx, cy, ang);
//		
//		paper.draw(0, 0);
//		g.setColor(Color.black);
//		g.drawString("This wuold be a menu page", paper.getWidth()/2f, 50);
//		
//		if (container.getHeight() > IMGSIZE) {
//			float amt = container.getHeight()/(float)IMGSIZE;
//			
//			IMGSIZE = container.getHeight();
//			
//		}
		
//		float sw = container.getWidth(), sh = container.getHeight();
//		float ls = lightScale.getValue();
//		float w=IMGSIZE*ls, h=IMGSIZE*ls;
//		
//		g.translate(sw/2f-w/2f, sh/2f-h/2f);
//		
//		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
//		alphaMap.draw(0f, 0f, w, h, lighting);
//		g.resetTransform();
//		
//		g.setDrawMode(Graphics.MODE_ALPHA_BLEND);
//		context.getOffscreenImage().draw(sw/2f-IMGSIZE/2f, sh/2f-IMGSIZE/2f);
//		
//		
//		g.setDrawMode(Graphics.MODE_NORMAL);
//		
		
//		
//		g.resetTransform();
//		
//		atmos.draw(0, 0, sw, sh, atmosFilter);
	}
	
	

	/**
	 * @see org.newdawn.slick.state.BasicGameState#update(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) {
		flickerTime += delta;
		if (flickerTime >= nextFlickerTime) {
			flickerTime -= nextFlickerTime;
			nextFlickerTime = Utils.rnd(FLICKER_DELAY_LOW, FLICKER_DELAY_HIGH);
			nextFlickerAlpha = Utils.rnd(FLICKER_ALPHA_LOW, FLICKER_ALPHA_HIGH);
			flickerAdj = true;
		}
		
		//System.out.println(lighting.a);
		if (lighting.a > nextFlickerAlpha && lighting.a > FLICKER_ALPHA_LOW) {
			lighting.a = Math.max(lighting.a - FLICKER_ADJUSTMENT * delta, nextFlickerAlpha);
		} else if (lighting.a < nextFlickerAlpha && lighting.a < FLICKER_ALPHA_HIGH) {
			lighting.a = Math.min(lighting.a + FLICKER_ADJUSTMENT * delta, nextFlickerAlpha);
		}
		
		paperRot.update(delta);
		paperAlpha.update(delta);
		lightScale.update(delta);
		if (lightScale.finished()) {
			lightScale.flip();
			lightScale.restart();
		}
	}

}