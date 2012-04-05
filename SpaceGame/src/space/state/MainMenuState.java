package space.state;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import space.GameContext;
import space.engine.SpriteBatch;
import space.engine.easing.Easing;
import space.engine.easing.SimpleFX;
import space.ui.Label;
import space.ui.Root;
import space.ui.Widget;
import space.ui.WidgetListener;
import space.util.Resources;

public class MainMenuState extends AbstractState implements WidgetListener {

	private Image background;
	
	private Widget title;
	private Label start, help, options, quit;
	private List<Label> MENU_LIST = new ArrayList<Label>(5);
	private Widget panel;
	private Root root;
	
	private float lastY = 0;
	
	private static final int HPAD = 0, VPAD = 0, LINE_SPACE = 5, ARROWPAD = 5;
	
	private Color hoverTint = new Color(1f, 1f, 1f, 1f);
	private Color normalTint = new Color(1f, 1f, 1f, 0.75f);
	
	private Widget active;
	
	private Image arrow, arrowFlip;
	private SimpleFX arrowMotion = new SimpleFX(0f, 7f, 500, Easing.SINE_IN_OUT);
	
	public MainMenuState(GameContext context){
		super(context, 2);
	}
	
	@Override
	public void init(GameContext context) throws SlickException {
		root = new Root(context, this);
		AngelCodeFont font = Resources.getSmallFont();
		arrow = Resources.getSprite("arrow");
		arrowFlip = arrow.getFlippedCopy(true, false);
		background = Resources.getSprite("background");
		Image titleImg = Resources.getSprite("title");
		title = new Widget(titleImg);
		title.setPosition(context.getWidth()/2f-title.getWidth()/2f, context.getHeight()/2f-title.getHeight()*1.5f);
		root.add(title);
		
		lastY = context.getHeight()/2f;
		
		start = nextMenuItem(root, font, "PLAY", context);
		help = nextMenuItem(root, font, "HELP", context);
		options = nextMenuItem(root, font, "OPTIONS", context);
		quit = nextMenuItem(root, font, "QUIT", context);
		
		setActive(start);
	}
	
	private Label nextMenuItem(Widget parent, AngelCodeFont font, String text, GameContext ctx) {
		Label l = new Label(font, text, HPAD, VPAD);
		l.setTextOffset(2f, 1f);
		l.setAlign(Label.ALIGN_CENTER, Label.ALIGN_CENTER);
		l.setForeground(normalTint);
		l.setPosition(ctx.getWidth()/2f-l.getWidth()/2f, lastY);
		lastY += l.getHeight() + LINE_SPACE;
		parent.add(l);
		MENU_LIST.add(l);
		return l;
	}

	private void setActive(Widget w) {
		if (active!=null)
			active.setForeground(normalTint);
		active = w;
		active.setForeground(hoverTint);
	}
	
	@Override
	public void render(GameContext context, SpriteBatch batch, Graphics g)
			throws SlickException {
		int cx = context.getContainer().getWidth();
		int cy = context.getContainer().getHeight();
		
		batch.setColor(Color.white); //white filter for image
		batch.drawImage(background, 0,0, cx, cy);
		root.draw(batch, g);
		
		batch.setColor(normalTint);
		float ax = active.getAbsoluteX()-arrow.getWidth();
		float ay = active.getAbsoluteY();
		float acy = active.getHeight()/2f-arrow.getHeight()/2f;
		
		float amt = ARROWPAD + arrowMotion.getValue();
		
		batch.drawImage(arrow, ax-amt, ay+acy);
		batch.drawImage(arrowFlip, ax+active.getWidth()+arrow.getWidth()+amt, ay+acy);
	}
	
	public void keyPressed(int key, char c) {
		int n = 0;
		int i = MENU_LIST.indexOf(active);
		if (i >= 0)
			n = i;
		if (key == Input.KEY_UP || key==Input.KEY_W) {
			if (n == 0)
				n = MENU_LIST.size() - 1;
			else
				n--;
			setActive(MENU_LIST.get(n));
		} else if (key == Input.KEY_DOWN || key==Input.KEY_S) {
			if (n == MENU_LIST.size() - 1)
				n = 0;
			else
				n++;
			setActive(MENU_LIST.get(n));
		} else if (key == Input.KEY_SPACE || key == Input.KEY_ENTER) {
			handleActivate();
		}
//		else if (key == Input.KEY_ESCAPE)
//			context.getContainer().exit();

	}
	
	@Override
	public void update(GameContext context, int delta) throws SlickException {
		arrowMotion.update(delta);
		if (arrowMotion.finished()) {
			arrowMotion.flip();
			arrowMotion.restart();
		}
			
	}
	
	private void handleActivate() {
		if (active == start) {
			context.enterGame();
		} else if (active == help) {
			
		} else if (active == quit) {
			context.getContainer().exit();
		}
	}
	
	
	public void onEnter(Widget widget) {
		if (widget!=root && widget!=title)
			setActive(widget);
	}
	
	public void onExit(Widget widget) {
		if (widget!=active)
			widget.setForeground(normalTint);
	}
	
	public boolean onMouseClick(Widget widget, int button, int x, int y, int clickCount) {
		if (widget!=root && widget!=title) {
			setActive(widget);
			handleActivate();
		}
		return true; //returning true means "consume the input event" so that it wont' be sent to the game state
	}
	
	public boolean onMousePress(Widget widget, int button, int x, int y) {
		return true; 
	}
	
	public boolean onMouseRelease(Widget widget, int button, int x, int y) {
		return true;
	}
	
	public void onMouseMove(Widget widget, int oldx, int oldy, int newx, int newy) {
		if (widget!=root && widget!=title && widget!=active)
			setActive(widget);
	}
}
