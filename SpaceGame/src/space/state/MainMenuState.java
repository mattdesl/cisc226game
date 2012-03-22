package space.state;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import space.GameContext;
import space.engine.SpriteBatch;
import space.ui.Label;
import space.ui.Root;
import space.ui.Widget;
import space.ui.WidgetListener;
import space.util.Resources;

public class MainMenuState extends AbstractState implements WidgetListener {

	private Image background;
	
	private Widget title;
	private Label start, help, options, quit;
	
	private Widget panel;
	private Root root;
	
	private float lastY = 0;
	
	private static final int HPAD = 2, VPAD = 2, LINE_SPACE = 5;
	
	private Color hoverTint = new Color(1f, 1f, 1f, 1f);
	private Color normalTint = new Color(1f, 1f, 1f, 0.75f);
	
	private Label active;
	
	public MainMenuState(GameContext context){
		super(context, 2);
	}
	
	@Override
	public void init(GameContext context) throws SlickException {
		root = new Root(context, this);
		AngelCodeFont font = Resources.getSmallFont();
		
		background = Resources.getSprite("background");
		Image titleImg = Resources.getSprite("title");
		title = new Widget(titleImg);
		title.setPosition(context.getWidth()/2f-title.getWidth()/2f, context.getHeight()/2f-title.getHeight()*1.5f);
		root.add(title);
		
		int hpad = 2, vpad = 2;
		
		lastY = context.getHeight()/2f;
		
		start = nextMenuItem(root, font, "START", context);
		help = nextMenuItem(root, font, "HELP", context);
		options = nextMenuItem(root, font, "OPTIONS", context);
		quit = nextMenuItem(root, font, "QUIT", context);
		
		setActive(start);
	}
	
	private Label nextMenuItem(Widget parent, AngelCodeFont font, String text, GameContext ctx) {
		Label l = new Label(font, text, HPAD, VPAD);
		l.setAlign(Label.ALIGN_CENTER, Label.ALIGN_CENTER);
		l.setForeground(normalTint);
		l.setPosition(ctx.getWidth()/2f-l.getWidth()/2f, lastY);
		lastY += l.getHeight() + LINE_SPACE;
		parent.add(l);
		return l;
	}

	private void setActive(Label w) {
		
	}
	
	@Override
	public void render(GameContext context, SpriteBatch batch, Graphics g)
			throws SlickException {
		int cx = context.getContainer().getWidth();
		int cy = context.getContainer().getHeight();
		
		batch.setColor(Color.white); //white filter for image
		batch.drawImage(background, 0,0, cx, cy);
		root.draw(batch, g);
	}
	
	@Override
	public void update(GameContext context, int delta) throws SlickException {
	}
	
	
	public void onEnter(Widget widget) {
		widget.setForeground(hoverTint);
	}
	
	public void onExit(Widget widget) {
		widget.setForeground(normalTint);
	}
	
	public boolean onMouseClick(Widget widget, int button, int x, int y, int clickCount) {
		if (widget == start) {
			context.enterGame();
		} else if (widget == help) {
			
		} else if (widget == quit) {
			context.getContainer().exit();
		}
		return true;
	}
	
	public boolean onMousePress(Widget widget, int button, int x, int y) {
		return true;
	}
	
	public boolean onMouseRelease(Widget widget, int button, int x, int y) {
		return true;
	}
	
	public void onMouseMove(Widget widget, int oldx, int oldy, int newx,
			int newy) {
		
	}
}
