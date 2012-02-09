package space.game;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import space.easing.Easing;
import space.easing.SimpleFX;
import space.ui.MenuBackground;
import space.ui.MenuPaper;
import space.util.Resources;

public class MainMenu extends SpaceGameState {
	
	public MainMenu(GameContext c) { 
		super(c, 2); 
	}
    
    private MenuBackground bg;
    private MenuPaper paper;
    private Image atmos;
    private Color atmosFilter = new Color(1f,1f,1f,0.25f);
    public boolean isOverPaper = false;

    private final float TEXT_NOHOVER = 0.75f;
    private Color textFilter = new Color(0f,0f,0f,TEXT_NOHOVER);
    
    public void init(GameContainer c, StateBasedGame game) throws SlickException {
    	bg = new MenuBackground(context, 0);
    	paper = new MenuPaper(context);
    	atmos = Resources.getImage("atmosphere");
    }
    
    private void drawText(Graphics g, String text, float scale, float pw, float yOff) {
    	int w = g.getFont().getWidth(text);
    	g.scale(scale, scale);
		g.drawString(text, pw/2f-(scale*w/2f), yOff);
    }
    
    public void detailLevelChanged() {
    	bg.reinit(context);
    }
    
    public void entering() {
    	bg.reinit(context);
    	paper.reinit(context);
    }
    
    public void render(GameContainer c, StateBasedGame game, Graphics g) throws SlickException {
    	bg.draw(context, g);
    	//if (context.getDetailLevel()>=GameContext.DETAIL_HIGH) {
    	//	atmos.draw(0, 0, c.getWidth(), c.getHeight(), atmosFilter);
    	//}
    	paper.begin(context, g);
    	paper.draw(context, g);
    	g.setFont(Resources.getFont(Resources.BOLD));
    	g.setColor(textFilter);
    	drawText(g, "click me to start", paper.getTextScale(), paper.getWidth(), 50);
    	paper.end(context, g);
    }
    
    public void update(GameContainer c, StateBasedGame game, int delta) throws SlickException {
    	isOverPaper = paper.contains(c.getInput().getMouseX(), c.getInput().getMouseY());
    	textFilter.a = isOverPaper ? 1f : TEXT_NOHOVER;
    	bg.update(context, delta);
    	paper.update(context, delta);
    	
    }
    
    public void mouseClicked(int button, int x, int y, int c) {
    	if (paper.contains(x, y))
    		context.enterGame();
    }
}
