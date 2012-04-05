package space.state;

import java.text.MessageFormat;
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

public class GameOverState extends AbstractState implements WidgetListener{
	
	private Image background;
	
	private Widget gameOver;
	private Label playAgain, mainMenu;
	private final List<Label> BUTTON_LIST = new ArrayList<Label>(2);
	private Root root;
	
	private float lastY = 0;
	
	private static final int HPAD = 0, VPAD = 0, LINE_SPACE = 5, ARROWPAD = 5;
	
	private Color hoverTint = new Color(1f,1f,1f,1f);
	private Color normalTint = new Color(1f,1f,1f,.75f);
	
	private Widget active;
	
	private Image arrow, arrowFlip;
	private SimpleFX arrowMotion = new SimpleFX(0f,7f,500,Easing.SINE_IN_OUT);
	
	private final String WAVE = "You made it to wave: {0}";
	private final String SCORE = "Your score was: {0}";
	private final String SHIELD = "You purchased {0} shield upgrades";
	private final String WEAPON = "You purchased {0} weapon upgrades";
	
	private Label wave, score, shield, weapon;
	
	public GameOverState(GameContext context){
		super(context,3);
	}
	
	public void init(GameContext context) throws SlickException {
		root = new Root(context,this);
		
		AngelCodeFont font = Resources.getSmallFont();
		AngelCodeFont font2 = Resources.getNiceFont();
		arrow = Resources.getSprite("arrow");
		arrowFlip = arrow.getFlippedCopy(true,false);
		background = Resources.getSprite("background");
		Image titleImg = Resources.getSprite("title");
		gameOver = new Widget(titleImg);
		float goX, goY;
		goX = context.getWidth()/2f - gameOver.getWidth()/2f;
		goY = context.getHeight()/2f-gameOver.getHeight()*1.5f;
		gameOver.setPosition(goX, goY);
		root.add(gameOver);
		
		lastY = context.getHeight()/2f;
		wave =  nextMenuItem(root, font2, "", context, false,0);
		score = nextMenuItem(root, font2, "", context, false,0);
		shield = nextMenuItem(root, font2, "", context, false,0);
		weapon = nextMenuItem(root, font2, "", context, false,0);
		playAgain = nextMenuItem(root, font, "PLAY AGAIN", context, true,30);
		mainMenu = nextMenuItem(root, font, "MAIN MENU", context, true, 5);
		updateText();
		
		setActive(playAgain);
	}
	
	public void updateText() {
		InGameState g = context.getInGameState();
		wave.setText(MessageFormat.format(WAVE, g.getWaveLevel()));
		
		centerText(wave, WAVE, g.getWaveLevel());
		centerText(score, SCORE, g.getScore());
		centerText(shield, SHIELD, g.getPlayer().getShieldPurchased());
		centerText(weapon, WEAPON, g.getPlayer().getWeaponPurchased());
	}
	
	private void centerText(Label label, String text, int obj) {
		label.setText(MessageFormat.format(text, obj));
		label.setPosition(context.getWidth()/2f-label.getWidth()/2f, label.getY());
	}
	
	private Label nextMenuItem(Widget parent, AngelCodeFont font, String text, GameContext ctx, boolean button, int yoff){
		Label l = new Label(font, text, HPAD, VPAD);
		l.setTextOffset(2f,1f);
		l.setAlign(Label.ALIGN_CENTER, Label.ALIGN_CENTER);
		l.setForeground(normalTint);
		lastY += yoff;
		l.setPosition(ctx.getWidth()/2f-l.getWidth()/2f, lastY);
		lastY += l.getHeight() + LINE_SPACE;
		parent.add(l);
		if (button)
			BUTTON_LIST.add(l);
		return l;
	}
	
	private void setActive(Widget w){
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
		
		batch.setColor(Color.white);
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
		int i = BUTTON_LIST.indexOf(active);
		if (i >= 0)
			n = i;
		if (key == Input.KEY_UP || key==Input.KEY_W) {
			if (n == 0)
				n = BUTTON_LIST.size() - 1;
			else
				n--;
			setActive(BUTTON_LIST.get(n));
		} else if (key == Input.KEY_DOWN || key==Input.KEY_S) {
			if (n == BUTTON_LIST.size() - 1)
				n = 0;
			else
				n++;
			setActive(BUTTON_LIST.get(n));
		} else if (key == Input.KEY_SPACE || key == Input.KEY_ENTER) {
			handleActivate();
		}
	}

	public void update(GameContext context, int delta) throws SlickException {
		arrowMotion.update(delta);
		if (arrowMotion.finished()) {
			arrowMotion.flip();
			arrowMotion.restart();
		}
	}
	
	private void handleActivate() {
		if (active == playAgain) {
			context.getInGameState().restart();
			context.enterGame();
		} else if (active == mainMenu) {
			context.enterMenu(); // this doesn't work for some reason :O 
		}
	}
	
	
	public void onEnter(Widget widget) {
		if (BUTTON_LIST.contains(widget) && widget!=root && widget!=gameOver)
			setActive(widget);
	}
	
	public void onExit(Widget widget) {
		if (BUTTON_LIST.contains(widget) && widget!=active)
			widget.setForeground(normalTint);
	}
	
	public boolean onMouseClick(Widget widget, int button, int x, int y, int clickCount) {
		if (BUTTON_LIST.contains(widget) && widget!=root && widget!=gameOver) {
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
		if (BUTTON_LIST.contains(widget) && widget!=root && widget!=gameOver && widget!=active)
			setActive(widget);
	}
}
