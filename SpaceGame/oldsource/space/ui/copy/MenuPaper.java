package space.ui.copy;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import space.easing.Easing;
import space.easing.SimpleFX;
import space.game.GameContext;
import space.util.Resources;
import space.util.Utils;

public class MenuPaper {

	//Constants for the spinning effect
	protected static final float PAPER_SPIN_LOW = -2f;
	protected static final float PAPER_SPIN_HIGH = 2f;
	protected static final float PAPER_SPIN_DURATION = 2500;
	protected static final Easing PAPER_SPIN_EASING = Easing.EXPO_OUT;
	
	//Constants for the fade-in effect on the paper
	protected static final float PAPER_FADE_START = 0.5f;
	protected static final float PAPER_FADE_DURATION = 550f;
	protected static final Easing PAPER_FADE_EASING = Easing.QUAD_OUT;
	
	//some fx for the paper spinning/fading values
	private SimpleFX paperSpin, paperAlpha;
	
	private Color paperFilter = new Color(1f,1f,1f,1f);
	
	private Image paper;
	
	private float paperScale = 1f;
	private float textScale = 1f;

    private Rectangle rolloverRect = new Rectangle(0f,0f,0f,0f);
    private GameContext context;
    
	public MenuPaper(GameContext context) throws SlickException {
		paper = Resources.getImage("menu.paper");
		paperSpin = new SimpleFX(0f, 0f, PAPER_SPIN_DURATION, PAPER_SPIN_EASING);
		paperAlpha = new SimpleFX(PAPER_FADE_START, 1f, PAPER_FADE_DURATION, PAPER_FADE_EASING);
		reinit(context);
	}
	
	/**
	 * Called upon entering the state -- randomizes the initial spin amount of the paper.
	 */
	public void reinit(GameContext context) {
		//paper texture might be larger than display height
		if (context.getHeight()<1024) {
			paperScale = .9f*context.getHeight()/paper.getHeight();
			textScale = Math.min(1f, paperScale*2f);
		} else
			paperScale = textScale = 1f;
		float s1 = Utils.rnd(PAPER_SPIN_LOW, PAPER_SPIN_HIGH);
		paperSpin.setStart(s1);
		paperAlpha.restart();
		paperSpin.restart();
		
	}

    public boolean contains(int x, int y) {
    	return rolloverRect.contains(x, y);
    }
    
	public float getWidth() {
		return paper.getWidth() * paperScale;
	}
	
	public float getHeight() {
		return paper.getHeight() * paperScale;
	}
	
	public float getTextScale() {
		return textScale;
	}
	
	public void begin(GameContext context, Graphics g) throws SlickException {
		float sw = context.getWidth();
		float sh = context.getHeight(); 
		float ang = paperSpin.getValue();
		float pw = getWidth();
		float ph = getHeight();
		g.pushTransform();
		g.translate(sw/2f-pw/2f, sh/2f-ph/2f);
		g.rotate(pw/2f, ph/2f, ang);
	}
	
	public void end(GameContext context, Graphics g) throws SlickException {
		g.popTransform();
	}
	
	public void update(GameContext context, int delta) throws SlickException {
		rolloverRect.setBounds(context.getWidth()/2f-getWidth()/2f, context.getHeight()/2f-getHeight()/2f,
							getWidth(), getHeight());
		
		paperSpin.update(delta);
		paperAlpha.update(delta);
	}
	
	public void draw(GameContext context, Graphics g) throws SlickException {
		paperFilter.a = paperAlpha.getValue();
		paper.draw(0, 0, paper.getWidth()*paperScale, paper.getHeight()*paperScale, paperFilter);
	}
}
