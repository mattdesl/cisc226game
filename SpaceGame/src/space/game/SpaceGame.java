package space.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.StateBasedGame;

import space.util.GameText;
import space.util.Images;
import space.util.Utils;

import com.eekboom.utils.Strings;

public class SpaceGame extends StateBasedGame implements GameContext {

	private GameContainer container;
	
	private MainMenuState menuState;
	
	private Graphics offscreenGraphics;
	private Image offscreen;
	
	private SpaceGameState currentState;
	
	public SpaceGame(String title) {
		super(title);
	}
	
	public int getWidth() {
		return container.getWidth();
	}
	
	public int getHeight() {
		return container.getHeight();
	}
	
	public void enterGame() {}
	
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
	
	public Image getOffscreenImage() {
		return offscreen;
	}
	
	public Graphics getOffscreenGraphics() {
		return offscreenGraphics;
	}
	
	/**
	 * @see org.newdawn.slick.state.StateBasedGame#initStatesList(org.newdawn.slick.GameContainer)
	 */
	public void initStatesList(GameContainer c) throws SlickException {
		this.container = c;
		
		Images.create(); //create the images, which may each be deferred
		
		//imagine each image is loaded individually in a progress state
		
		Images.initSprites(); //fill the map
		
		offscreen = new Image(1024, 1024);
		Texture old = offscreen.getTexture();
		offscreenGraphics = offscreen.getGraphics();
		old.release(); //bug with slick: ensure that we release unused texture
		
		addState(menuState = new MainMenuState(this));
	}
	
	public void update(GameContainer c) throws SlickException {
		this.container = c;
	}
	
	public void keyPressed(int k, char c) {
		if (k == Input.KEY_ESCAPE)
			container.exit();
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
			Object res = JOptionPane.showInputDialog(null, "Choose your display size:", "Display", 
					JOptionPane.INFORMATION_MESSAGE, 
					null, a, best);
			if (res==null)
				return;
			String display = res.toString();
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