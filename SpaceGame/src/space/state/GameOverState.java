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
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;

import space.GameContext;
import space.engine.SpriteBatch;
import space.engine.easing.Easing;
import space.engine.easing.SimpleFX;
import space.ui.Label;
import space.ui.Root;
import space.ui.Widget;
import space.ui.WidgetListener;
import space.util.HighScores;
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
	
	private Label wave, score, shield, weapon, name;
	
	private TextField tf;
	private boolean tfVisible = true;
	
	public GameOverState(GameContext context){
		super(context,3);
	}
	
	public void entering() {
		updateText();
		tf.setText("");
		setActive(playAgain);
		playAgain.setVisible(false);
		mainMenu.setVisible(false);
		tfVisible = true;
		name.setVisible(true);
	}
	
	public Root getRootUI() {
		return root;
	}
	
	public void init(final GameContext context) throws SlickException {
		root = new Root(context,this);
		
		AngelCodeFont font = Resources.getSmallFont();
		AngelCodeFont font2 = Resources.getNiceFont();
		arrow = Resources.getSprite("arrow");
		arrowFlip = arrow.getFlippedCopy(true,false);
		background = Resources.getSprite("background");
		Image titleImg = Resources.getSprite("title");
		gameOver = new Widget(titleImg);
		lastY = context.getHeight()*.25f;
		float goX, goY;
		goX = context.getWidth()/2f - gameOver.getWidth()/2f;
		goY = lastY-gameOver.getHeight()*1.5f;
		gameOver.setPosition(goX, goY);
		root.add(gameOver);
		
		wave =  nextMenuItem(root, font2, "", context, false,0);
		score = nextMenuItem(root, font2, "", context, false,0);
		shield = nextMenuItem(root, font2, "", context, false,0);
		weapon = nextMenuItem(root, font2, "", context, false,0);
		playAgain = nextMenuItem(root, font, "PLAY AGAIN", context, true,30);
		mainMenu = nextMenuItem(root, font, "MAIN MENU", context, true, 5);
		updateText();
		
		name = new Label(font, "Enter your name:", HPAD, VPAD);
		name.setForeground(Color.white);
		name.setPosition(context.getWidth()/2f - name.getWidth()/2f, playAgain.getY());
		root.add(name);
		
		playAgain.setVisible(false);
		mainMenu.setVisible(false);
		
		HighScores.load();
		
		tf = new TextField(context.getContainer(), font2, (int)name.getX(), (int)(name.getY()+name.getHeight()),
					(int)name.getWidth(), font2.getLineHeight(), new ComponentListener() {
						
			
			public void componentActivated(AbstractComponent source) {
				
				if (tfVisible && tf.getText()!=null && tf.getText().trim().length()!=0) {
					InGameState g = context.getInGameState();
					addHighscore(tf.getText().trim(), g.getWaveLevel(), g.getScore(), 
							g.getPlayer().getShieldPurchased()+g.getPlayer().getWeaponPurchased());
					name.setVisible(false);
					tfVisible = false;
					playAgain.setVisible(true);
					mainMenu.setVisible(true);
					HighScores.store();
					tf.setFocus(false);
				}
			}
		}) {
			
			public void mouseReleased(int button, int x, int y) {
				if (tfVisible)
					super.mouseReleased(button, x, y);
			}
		};
		tf.setBorderColor(null);
		tf.setFocus(true);
		tf.setTextColor(Color.white);
		tf.setBackgroundColor(null);
		
		
		
		setActive(playAgain);
	}
	
	private void addHighscore(String name, int wave, int score, int upgrades) {
		HighScores.place(name, wave, score, upgrades);
	}
	
	public void updateText() {
		InGameState g = context.getInGameState();
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
		if (active.isVisible()) {
			batch.drawImage(arrow, ax-amt, ay+acy);
			batch.drawImage(arrowFlip, ax+active.getWidth()+arrow.getWidth()+amt, ay+acy);
		}
		
		if (tfVisible) {
			batch.flush();
			g.setColor(normalTint);
			g.drawRect(tf.getX(), tf.getY(), tf.getWidth(), tf.getHeight());
			g.setColor(Color.white);
			tf.render(context.getContainer(), g);
		}

		AngelCodeFont font2 = Resources.getSmallFont();
		AngelCodeFont font = Resources.getNiceFont();
		
		String str = HighScores.list();
		float w = font.getWidth(str);
		
		float x = context.getWidth()/2f-w/2f;
		float y = mainMenu.getY()+mainMenu.getHeight()+55;
		batch.setColor(Color.white);
		batch.drawText(font2, "SCORES", x, y);
		
		batch.setColor(normalTint);
		batch.drawTextMultiLine(font, str, x, y+font.getLineHeight());
		
		
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
		if (tfVisible)
			return;
		if (active == playAgain) {
			context.getInGameState().restart();
			context.enterGame();
		} else if (active == mainMenu) {
			context.enterMenu();
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
