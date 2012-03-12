package space.game;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.pbuffer.GraphicsFactory;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import space.tests.AbstractMenuState;
import space.util.GameText;
import space.util.NullTexture;
import space.util.Resources;
import space.util.Utils;

import com.eekboom.utils.Strings;

public class SpaceGame extends StateBasedGame implements GameContext {

	public static final int OFFSCREEN_TEXTURES = 1;
	public static final int OFFSCREEN_TEXSIZE = 1024;
	
	private GameContainer container;
	
	private MainMenu menuState;
	private GameExample gameState;
	
	private Graphics[] offscreenGraphics = new Graphics[OFFSCREEN_TEXTURES];
	private Image[] offscreen = new Image[OFFSCREEN_TEXTURES];
	
	private SpaceGameState currentState;
	public static int detailLevel = DETAIL_HIGH;
	private static Preferences prefs = Preferences.userNodeForPackage(SpaceGame.class);

	String[] detailStr = new String[] { "lowest", "low", "medium", "highest" };
	
	public static Preferences getPrefs() {
		return prefs;
	}
	
	public SpaceGame(String title) {
		super(title);
	}
	
	public GameContainer getContainer() {
		return container;
	}
	
	public int getWidth() {
		return container.getWidth();
	}
	
	public int getHeight() {
		return container.getHeight();
	}
	
	public int getDetailLevel() {
		return detailLevel;
	}
	
	public SpaceGameState getCurrentState() {
		return currentState;
	}
	
	public void enterGame() {
		enterState(gameState);
	}
	
	public void enterMainMenu() {
		enterState(menuState);
	}
	
	public void enterState(SpaceGameState state) {
		SpaceGameState old = currentState;
		if (old!=null) {
			old.leaving();
		}
		currentState = state;
		state.entering();
		this.enterState(state.getID());
		state.entered();
		old.left();
	}
	
	
	private Image createImageGraphics(int ptr) throws SlickException {
		Image offscreen = new Image(new NullTexture(OFFSCREEN_TEXSIZE, OFFSCREEN_TEXSIZE));
		Graphics offscreenGraphics = offscreen.getGraphics();
		this.offscreen[ptr] = offscreen;
		this.offscreenGraphics[ptr] = offscreenGraphics;
		return offscreen;
	}
	
	/**
	 * Called to release the offscreen image texture; attempts to later
	 * retrieve the image at this pointer will throw a null pointer.
	 * 
	 * @param ptr the index of the image 
	 * @throws SlickException if we couldn't release the image
	 */
	public void releaseOffscreenImage(int ptr) throws SlickException {
		if (offscreen[ptr]!=null) {
			offscreen[ptr].destroy();
			offscreenGraphics[ptr] = null;
			offscreen[ptr] = null;
		}
	}
	
	public int getOffscreenImageCount() {
		return OFFSCREEN_TEXTURES;
	}
	
	public Image getOffscreenImage(int ptr) {
		return offscreen[ptr];
	}
	
	public Graphics getOffscreenGraphics(int ptr) {
		return offscreenGraphics[ptr];
	}
	
	/**
	 * @see org.newdawn.slick.state.StateBasedGame#initStatesList(org.newdawn.slick.GameContainer)
	 */
	public void initStatesList(GameContainer c) throws SlickException {
		this.container = c;
		//container.setSmoothDeltas(true);
		
		//checks capabilities
		if (notPlayable()) {
			Log.error("System requirements do not meet those needed to run this game");
			c.exit();
		}
		
		Resources.create(this); //create the images, which may each be deferred
		
		//imagine each image is loaded individually in a progress state
		
		Resources.initSprites(this); //fill the map
		
		//would be done in pre-loading
		for (int i=0; i<SpaceGame.OFFSCREEN_TEXTURES; i++)
			createImageGraphics(i);
		
		addState(menuState = new MainMenu(this));
		addState(gameState = new GameExample(this));
		currentState = menuState;
	}
	
	private boolean notPlayable() throws SlickException {
		IntBuffer buffer = BufferUtils.createIntBuffer(16);
		GL11.glGetInteger(SGL.GL_MAX_TEXTURE_SIZE, buffer);
		//fatal error
		if (buffer.get(0)<OFFSCREEN_TEXSIZE) {
			Utils.error("Your system does not support textures\ngreater than "+OFFSCREEN_TEXSIZE+
					"x"+OFFSCREEN_TEXSIZE+", which is required for this game.\n" +
					"Try updating your drivers or installing a newer graphics card.");
			return true;
		}
		//not a fatal error
		if (!GraphicsFactory.usingFBO()) {
			//TODO: place this on in-game menu with 'do not show again'
			boolean b = prefs.getBoolean("fbo.err.show", true);
			if (b) {
				Utils.error("Your system does not support FBO rendering,\nwhich means some" +
					"of the graphics and effects may\nnot display corerctly.\n");
				prefs.putBoolean("fbo.err.show", false);
			}
		}
		return false;
	}
	
	public boolean ingame() {
		return currentState==gameState;
	}
	
	public boolean closeRequested() {
		prefs.putInt("spacegame.detail", detailLevel);
		return true;
	}
	
	public void update(GameContainer c) throws SlickException {
		this.container = c;
	}
	
	public void keyPressed(int k, char c) {
		if (k == Input.KEY_ESCAPE) {
			if (ingame())
				enterMainMenu();
			else
				container.exit();
		}
		
		//DEBUG: change detail with 1-4
		else if (k == Input.KEY_1) {
			detailLevel = GameContext.DETAIL_LOWEST;
			currentState.detailLevelChanged();
		} else if (k == Input.KEY_2) {
			detailLevel = GameContext.DETAIL_LOW;
			currentState.detailLevelChanged();
		} else if (k == Input.KEY_3) {
			detailLevel = GameContext.DETAIL_MEDIUM;
			currentState.detailLevelChanged();
		} else if (k == Input.KEY_4){
			detailLevel = GameContext.DETAIL_HIGH;
			currentState.detailLevelChanged();
		}
	}
	
	public void postRenderState(GameContainer c, Graphics g) throws SlickException {
		
		
		g.setColor(Color.white);
		g.drawString("Total textures: "+InternalTextureLoader.totalTextures, 10, 25);
		g.drawString("Detail Level: "+detailStr[detailLevel], 10, 40);
	}
	
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		//load game text
		try {
			GameText.load();
		} catch (IOException e) {
			Utils.error("Game text file at '"+GameText.TEXT_PATH+"' could not be loaded", e);
		}

		int width = 800;
		int height = 600;
		boolean fullscreen = false;
		
		detailLevel = prefs.getInt("spacegame.detail", GameContext.DETAIL_HIGH);
		System.out.println("detail"+detailLevel);
		
		try {
			DisplayMode[] ds = Display.getAvailableDisplayModes();
			List<String> s = new ArrayList<String>(ds.length);
			for (DisplayMode d : ds) {
				String str = d.getWidth()+"x"+d.getHeight();
				String str2 = str+" (fullscreen)";  
				if (!s.contains(str2)) {
					s.add(str);
					s.add(str2);
				}
			}
			Collections.sort(s, Strings.getNaturalComparator());
			String[] a = s.toArray(new String[s.size()]);
			String best = a[a.length>2 ? a.length-2 : 0];
			best = prefs.get("spacegame.display", best);
			Object res = JOptionPane.showInputDialog(null, "Choose your display size:", "Display", 
					JOptionPane.INFORMATION_MESSAGE, 
					null, a, best);
			if (res==null)
				return;
			String display = res.toString();
			prefs.put("spacegame.display", display);
			String[] split = display.split("x");
			int i = split[1].indexOf(' ');
			if (i!=-1) {
				split[1] = split[1].substring(0, i);
				fullscreen = true;
			}
			width = Integer.parseInt(split[0]);
			height = Integer.parseInt(split[1]);
		} catch (LWJGLException e) {
			Utils.error("Game text file at '"+GameText.TEXT_PATH+"' could not be loaded", e);
		}
		
		//create the display and start the game
		try {
			AppGameContainer container = new AppGameContainer(new SpaceGame(GameText.get("title", "")+" - "+width+"x"+height));
			container.setDisplayMode(width,height,fullscreen);
			container.start();
		} catch (SlickException e) {
			Utils.error("There was a problem creating the display: "+e.getMessage(), e);
		}
	}
}