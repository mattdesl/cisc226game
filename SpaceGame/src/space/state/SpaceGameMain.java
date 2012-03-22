package space.state;
import java.io.IOException;
import java.util.prefs.Preferences;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.pbuffer.GraphicsFactory;
import org.newdawn.slick.state.StateBasedGame;

import space.GameContext;
import space.engine.FBO;
import space.engine.ShaderProgram;
import space.engine.SpriteBatch;
import space.util.GameText;
import space.util.Resources;
import space.util.Utils;


public class SpaceGameMain extends StateBasedGame implements GameContext {
	public static final int MAINMENUSTATE = 0;
	public static final int INGAMESTATE = 1;
    public static final int MINIMUM_TEXSIZE = 1024;
	
	private GameContainer container;
	
	public static int detailLevel = DETAIL_HIGH;
	private static Preferences prefs = Preferences.userNodeForPackage(SpaceGameMain.class);

	private final static String[] DETAILS = new String[] { "lowest", "low", "medium", "highest" };
	
	private AbstractState currentState;
	private InGameState gameState;
	private MainMenuState menuState;
	private SpriteBatch spriteBatch;
	private AngelCodeFont defaultFont;
	private boolean showDebug = true;
	
	private boolean sceneEffectsEnabled = true;
	private Image sceneTex;
	private FBO sceneFBO;
	
	private boolean boom = false;
	private int elapsed, elapsed_max = 1000;
	private ShaderProgram shockShader;
	private float cx, cy;

	
	public static Preferences getPrefs() {
		return prefs;
	}
	
	public SpaceGameMain(String title) {
		super(title);
	}
	
	public GameContainer getContainer() {
		return container;
	}
	
	/**
	 * 
	 */
	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}
	
	public AngelCodeFont getDefaultFont() {
		return defaultFont;
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
	
	public void enterGame() {
		enterState(gameState);
	}
	
	public void enterMenu() {
		enterState(menuState);
	}
	
	boolean doSceneEffect() {
		return sceneEffectsEnabled && boom && elapsed < elapsed_max;
	}

	public void enterState(AbstractState state) {
		AbstractState old = currentState;
		if (old!=null)
			old.leaving();
		currentState = state;
		state.entering();
		this.enterState(state.getID());
		state.entered();
		if (old!=null)
			old.left();
	}
    
    public Input getInput() {
    	return container.getInput();
    }
	
    public void preUpdateState(GameContainer c, int delta) throws SlickException {
		//used by our shockwave shader
    	elapsed += delta;
    }
	
	/**
	 * @see org.newdawn.slick.state.StateBasedGame#initStatesList(org.newdawn.slick.GameContainer)
	 */
	public void initStatesList(GameContainer c) throws SlickException {
		this.container = c;
		c.setClearEachFrame(false);
		c.setShowFPS(false);
		c.getGraphics().setBackground(Color.black);
		
		sceneEffectsEnabled = getDetailLevel() != GameContext.DETAIL_LOWEST;
		if (!meetsSystemRequirements())
			c.exit();
		shockShader = ShaderProgram.loadProgram("res/vert2.shader", "res/frag2.shader");
		sceneEffectsEnabled = shockShader.valid();
		if (sceneEffectsEnabled) {
			sceneFBO = new FBO(c.getWidth(), c.getHeight());
			sceneTex = new Image(sceneFBO.getTexture());
			sceneFBO.setPushAttrib(FBO.NO_BITS); //don't bother pushing bits...
		} else {
			System.out.println(shockShader.getLog());
		}
		
		//DO LOADING HERE... generally we would move this into a separate state
		//i.e. with a progress bar
		Resources.create();
		defaultFont = Resources.getMonospacedFont();
		
		spriteBatch = new SpriteBatch(4000);

		addState(menuState = new MainMenuState(this));
		addState(gameState = new InGameState(this));
		//currentState = gameState;
		//enterGame();
		enterMenu();
	}
	
	private boolean meetsSystemRequirements() throws SlickException {
		ContextCapabilities caps = GLContext.getCapabilities();
		//fatal errors...
		if (GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE)<MINIMUM_TEXSIZE) {
			Utils.error("Your system does not support textures\ngreater than "+MINIMUM_TEXSIZE+
					"x"+MINIMUM_TEXSIZE+", which is required for this game.\n" +
					"Try updating your drivers or installing a newer graphics card.");
			return false;
		}
		
		//not a fatal error
		if (!GraphicsFactory.usingFBO()) {
			//TODO: place this on in-game menu with 'do not show again'
			boolean b = prefs.getBoolean("fbo.err.show", true);
			if (b) {
				Utils.error("Your system does not support Frame Buffer Objects,\nwhich means some" +
					"of the graphics and effects may\nnot display corerctly.\n");
				prefs.putBoolean("fbo.err.show", false);
				sceneEffectsEnabled = false;
			}
		}
		return true;
	}
	
	public boolean isDebugEnabled() {
		return showDebug;
	}
	
	public boolean closeRequested() {
		prefs.putInt("spacegame.detail", detailLevel);
		return true;
	}
	
	public void keyPressed(int k, char c) {
		if (k == Input.KEY_0) {
			showDebug = !showDebug;
		} else if (k == Input.KEY_9) {
			Runtime.getRuntime().gc();
		}
		super.keyPressed(k, c);
	}
	
	public InGameState getInGameState() {
		return gameState;
	}
	
	public void createShockwave(int x, int y) {
		boom = true;
		cx = x;
		cy = y;
		elapsed = 0;
		gameState.shakeCamera(this);
	}
	
	public void preRenderState(GameContainer c, Graphics g) throws SlickException {
		if (doSceneEffect()) {
			sceneFBO.bind();
			g.clear();
		}
		spriteBatch.renderCalls = 0;
		spriteBatch.setColor(Color.white);
	}
	
	public void postRenderState(GameContainer c, Graphics g) throws SlickException {
		spriteBatch.flush();
		spriteBatch.resetTranslation();
		
		if (doSceneEffect()) {
			sceneFBO.unbind();
			
			shockShader.bind();
			
			float cx = this.cx / sceneTex.getTexture().getTextureWidth();
        	float cy = this.cy / sceneTex.getTexture().getTextureHeight();
    		shockShader.setUniform1i("sceneTex", 0);
			shockShader.setUniform2f("center", cx, cy);
			shockShader.setUniform1f("time", elapsed*0.0015f);
			shockShader.setUniform3f("shockParams", 10, 0.7f, .1f);
			
        	//TextureImpl.unbind();
        	GL13.glActiveTexture(GL13.GL_TEXTURE0);
        	sceneTex.bind();
        	spriteBatch.drawImage(sceneTex);
        	spriteBatch.flush();
    		
        	shockShader.unbind();
		} else if (boom) {
			boom = false;
		}
		
		int r = spriteBatch.renderCalls;
		if (showDebug) {
			spriteBatch.setColor(Color.white);
			
			String debug = getDebugText();
			spriteBatch.drawTextMultiLine(defaultFont, debug, 5, 5);
			spriteBatch.flush();
		}
	}
	
	public String getDebugText() {
		String debug = "Debugging (press 0 to toggle)\nFPS:"+container.getFPS()
				+"\nRender calls: "+spriteBatch.renderCalls
				+"\nTexture count: "+InternalTextureLoader.getTextureCount()
				+"\n"+(doSceneEffect()?"(full-screen shader)":"")+"\n"+currentState.getDebugText();
		return debug;
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		//Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);
		
		//load game text
		try {
			GameText.load();
		} catch (IOException e) {
			Utils.error("Game text file at '"+GameText.TEXT_PATH+"' could not be loaded", e);
		}

		int width = 1024;
		int height = 768;
		boolean fullscreen = false;
		
		detailLevel = prefs.getInt("spacegame.detail", GameContext.DETAIL_HIGH);
//		System.out.println("Detail level: "+DETAILS[detailLevel]);
//
//		try {
//			DisplayMode[] ds = Display.getAvailableDisplayModes();
//			List<String> s = new ArrayList<String>(ds.length);
//			for (DisplayMode d : ds) {
//				String str = d.getWidth()+"x"+d.getHeight();
//				String str2 = str+" (fullscreen)";  
//				if (!s.contains(str2)) {
//					s.add(str);
//					s.add(str2);
//				}
//			}
//			Collections.sort(s, Strings.getNaturalComparator());
//			String[] a = s.toArray(new String[s.size()]);
//			String best = a[a.length>2 ? a.length-2 : 0];
//			best = prefs.get("spacegame.display", best);
//			Object res = JOptionPane.showInputDialog(null, "Choose your display size:", "Display", 
//					JOptionPane.INFORMATION_MESSAGE, 
//					null, a, best);
//			if (res==null)
//				return;
//			String display = res.toString();
//			prefs.put("spacegame.display", display);
//			String[] split = display.split("x");
//			int i = split[1].indexOf(' ');
//			if (i!=-1) {
//				split[1] = split[1].substring(0, i);
//				fullscreen = true;
//			}
//			width = Integer.parseInt(split[0]);
//			height = Integer.parseInt(split[1]);
//		} catch (LWJGLException e) {
//			Utils.error("Game text file at '"+GameText.TEXT_PATH+"' could not be loaded", e);
//		}
		
		//create the display and start the game
		try {
			AppGameContainer container = new AppGameContainer(new SpaceGameMain(GameText.get("title", "")+" - "+width+"x"+height));
			container.setDisplayMode(width,height,fullscreen);
			container.start();
		} catch (SlickException e) {
			Utils.error("There was a problem creating the display: "+e.getMessage(), e);
		}

	}
}
