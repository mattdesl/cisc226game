package space.tests.copy;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import space.easing.Easing;
import space.easing.SimpleFX;
import space.game.GameContext;
import space.game.SpaceGameState;
import space.util.Resources;
import space.util.Utils;

public class AbstractMenuState extends SpaceGameState {
	
	static final int ID = 1;
	
	
	public AbstractMenuState(GameContext context) {
		super(context, ID);
	}
	
	Image bg, paper, alphaMap;
	
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
	
	//Constants for the spinning effect
	protected static final float PAPER_SPIN_LOW = -1f;
	protected static final float PAPER_SPIN_HIGH = 1f;
	protected static final float PAPER_SPIN_DURATION = 2500;
	protected static final Easing PAPER_SPIN_EASING = Easing.EXPO_OUT;
	
	//Constants for the fade-in effect on the paper
	protected static final float PAPER_FADE_START = 0.5f;
	protected static final float PAPER_FADE_DURATION = 550f;
	protected static final Easing PAPER_FADE_EASING = Easing.QUAD_OUT;
	
	//some fx for the paper spinning/fading values
	private SimpleFX paperSpin, paperAlpha;
	
	private Color paperFilter = new Color(1f,1f,1f,1f);
	
	private int flickerTime;
	private int nextFlickerTime = Utils.rnd(FLICKER_DELAY_LOW, FLICKER_DELAY_HIGH); 
	private Color lighting = new Color(1f,1f,1f,1f);
	private float nextFlickerAlpha = 1f;
	private boolean flickerAdj = false;
	
	private Image atmos;
	private Color atmosFilter = new Color(1f,1f,1f,.3f);
	
	private boolean hidePaper = false;
	
	private float textScale;
	private float paperWidth, paperHeight;
	
	private final Color BG_COLOR = new Color(0f,0f,0f,0f);
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#init(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame)
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		bg = Resources.getImage("menu.bg");
		paper = Resources.getImage("menu.paper");
		alphaMap = Resources.getImage("menu.alphaMap");
		atmos = Resources.getImage("atmosphere");
		
		float s1 = Utils.rnd(PAPER_SPIN_LOW, PAPER_SPIN_HIGH);
		paperSpin = new SimpleFX(s1, 0, PAPER_SPIN_DURATION, PAPER_SPIN_EASING);
		paperAlpha = new SimpleFX(PAPER_FADE_START, 1f, PAPER_FADE_DURATION, PAPER_FADE_EASING);
		
		container.setDefaultFont(Resources.getFont());
		
		//g.setAntiAlias(true);
		
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
		if (context.getDetailLevel()==0)
			return;
		Graphics g = context.getOffscreenGraphics(0);
		Graphics.setCurrent(g);
		g.clear();
		Image off = context.getOffscreenImage(0);
		float w = off.getWidth();
		float h = off.getHeight();
		Utils.texture(bg, 0, 0, w, h);
		g.flush();
	}
	
	
	public void entering() {
		float s1 = Utils.rnd(PAPER_SPIN_LOW, PAPER_SPIN_HIGH);
		float s2 = Utils.rnd(PAPER_SPIN_LOW, PAPER_SPIN_HIGH);
		paperSpin.setStart(s1);
		//paperRot.setEnd(s2);
		paperAlpha.restart();
		paperSpin.restart();
		//lightScale.restart();
		context.getContainer().getGraphics().setBackground(BG_COLOR);
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
		container.getGraphics().setFont(container.getDefaultFont());
	
		float sw = container.getWidth(), sh = container.getHeight();
		if (context.getDetailLevel()>GameContext.DETAIL_LOWEST) {
			float IMGSIZE = Math.min(sh, 1024);
			
			////// ALPHA MAP RENDERING
			// If the height is < 1024, shrink to fit
			// Otherwise render fully
			
			float ls = lightScale.getValue();
			float w = IMGSIZE*ls, h = IMGSIZE*ls*.9f;
			
			g.setDrawMode(Graphics.MODE_ALPHA_MAP);
			alphaMap.draw(sw/2f-w/2f, sh/2f-h/2f, w, h, lighting);
			
			g.setDrawMode(Graphics.MODE_ALPHA_BLEND);
			context.getOffscreenImage(0).draw(sw/2f-IMGSIZE/2f, sh/2f-IMGSIZE/2f);
			g.setDrawMode(Graphics.MODE_NORMAL);
		}
		
		////// PAPER RENDERING
		// If height is < 1024, shrink to fit
		float ang = paperSpin.getValue();
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
		float textScale = 1f;
		if (s<1f)
			textScale = Math.min(1f, s*2);
		drawLines(g, textScale, pw, 150,
				"Hello, world.", "Press a key to show/hide me",
				"Best viewed in 1920x1080");
		
		g.resetTransform();
//		if (context.getDetailLevel()>=GameContext.DETAIL_MEDIUM)
//			atmos.draw(0, 0, sw, sh, atmosFilter);
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
		
		lightScale.update(delta);
		if (lightScale.finished()) {
			lightScale.flip();
			lightScale.restart();
		}
		
		paperSpin.update(delta);
		paperAlpha.update(delta);
	}

}