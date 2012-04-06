package space.state;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.pbuffer.GraphicsFactory;
import org.newdawn.slick.opengl.shader.ShaderProgram;
import org.newdawn.slick.state.StateBasedGame;

import space.GameContext;
import space.engine.FBO;
import space.engine.SpriteBatch;
import space.engine.easing.Easing;
import space.engine.easing.SimpleFX;
import space.util.Resources;
import space.util.Strings;
import space.util.Utils;


public class SpaceGameMain extends StateBasedGame implements GameContext {
	public static final int MAINMENUSTATE = 0;
	public static final int INGAMESTATE = 1;
    public static final int MINIMUM_TEXSIZE = 1024;
	
	private GameContainer container;
	
	public static int detailLevel = DETAIL_HIGH;
	public static Preferences prefs = Preferences.userNodeForPackage(SpaceGameMain.class);

	private final static String[] DETAILS = new String[] { "lowest", "low", "medium", "highest" };
	
	private AbstractState currentState;
	private InGameState gameState;
	private MainMenuState menuState;
	private HelpState helpState;
	private GameOverState gameOver;
	private SpriteBatch spriteBatch;
	private OptionsState optionsState;
	private AngelCodeFont defaultFont;
	private boolean showDebug = false;
	
	private boolean sceneEffectsEnabled = true;
	private Image sceneTex;
	private FBO sceneFBO;
	
	private boolean boom = false;
	private int elapsed, elapsed_max = 1000;
	private ShaderProgram shockShader;
	private float cx, cy;
	
	private boolean useFades = true;
	private SimpleFX fadeFX = new SimpleFX(1, 0f, 500f, Easing.QUAD_OUT);
	private AbstractState nextState = null;
	private boolean fadeStart = false;
	private Color fadeColor = new Color(Color.black);
	
	private static boolean vsync = false;
	private boolean displayPanel = false;
	
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
	
	public void enterHelp() {
		enterState(helpState);
	}
	
	public void enterGameOver(){
		enterState(gameOver);
	}
	
	boolean doSceneEffect() {
		return sceneEffectsEnabled && boom && elapsed < elapsed_max;
	}
	
	public void enterOptions() {
		enterState(optionsState);
	}
	

	public void setVSyncEnabled(boolean b) {
		vsync = b;
		if (b) {
			container.setVSync(b);
			container.setTargetFrameRate(60);
		} else {
			container.setVSync(false);
			container.setTargetFrameRate(-1);
		}
	}

	public void setShowDisplayPanel(boolean b) { 
		displayPanel = b;
		prefs.putBoolean("displayPanel", b);
	}
	
	public boolean isVSyncEnabled() { return vsync; }
	public boolean isShowDisplayPanel() { return displayPanel; }
	public boolean isSoundOn() { return container.isSoundOn(); }
	
	public void setSoundOn(boolean b) {
		container.setSoundOn(b);
		prefs.putBoolean("sound", b);
	}

	public void enterState(AbstractState state) {
		if (state==currentState)
			return;
		
		if (useFades) {
			if (fadeStart && !fadeFX.finished())
				return;
			nextState = state;
			fadeStart = true;
			fadeFX.setStart(fadeFX.getValue());
			fadeFX.setEnd(1f);
			fadeFX.setEasing(Easing.QUAD_OUT);
			fadeFX.restart();
			if (currentState!=null) {
				if (currentState.getRootUI()!=null)
					getInput().removeListener(currentState.getRootUI());
			}
		} else {
			handleStateSwitch(state);
		}
	}
	
	private void handleStateSwitch(AbstractState state) {
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
    
    public void postUpdateState(GameContainer c, int delta) throws SlickException {
    	fadeFX.update(delta);
    	if (fadeFX.finished() && fadeStart) {
    		fadeFX.setStart(fadeFX.getValue());
    		fadeFX.setEnd(0f);
    		fadeFX.setEasing(Easing.QUAD_IN);
    		fadeFX.restart();
    		fadeStart = false;
    		handleStateSwitch(nextState);
    	}
    }
	
	/**
	 * @see org.newdawn.slick.state.StateBasedGame#initStatesList(org.newdawn.slick.GameContainer)
	 */
	public void initStatesList(GameContainer c) throws SlickException {
		this.container = c;
		SoundStore.get().init();
		
		c.setClearEachFrame(false);
		c.setShowFPS(false);
		c.getGraphics().setBackground(Color.black);
		c.setAlwaysRender(true);
		
		setVSyncEnabled(vsync);
		setShowDisplayPanel(prefs.getBoolean("displayPanel", false));
		setSoundOn(prefs.getBoolean("sound", true));
		
		if (!meetsSystemRequirements())
			c.exit();
		sceneEffectsEnabled = true;
		try {
			shockShader = ShaderProgram.loadProgram("res/vert2.shader", "res/frag2.shader");
			if (sceneEffectsEnabled) {
				sceneFBO = new FBO(c.getWidth(), c.getHeight());
				sceneTex = new Image(sceneFBO.getTexture());
				sceneFBO.setPushAttrib(FBO.NO_BITS); //don't bother pushing bits...
			} else {
				System.out.println("Shader Log "+shockShader.getLog());
			}
		} catch (SlickException e) {
			sceneEffectsEnabled = false;
		}
			
		
		//DO LOADING HERE... generally we would move this into a separate state
		//i.e. with a progress bar
		Resources.create();
		defaultFont = Resources.getMonospacedFont();
		
		spriteBatch = new SpriteBatch(1000);

		addState(menuState = new MainMenuState(this));
		addState(gameState = new InGameState(this));
		addState(helpState = new HelpState(this));
		addState(optionsState = new OptionsState(this));
		addState(gameOver = new GameOverState(this));
		
		
		currentState = menuState;
		
		//enterGame();
//		enterMenu();
		//enterGameOver();
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
		gameState.shakeCamera(this, x, y);
	}
	
	public void preRenderState(GameContainer c, Graphics g) throws SlickException {
		g.clear();
		
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
        	//GL13.glActiveTexture(GL13.GL_TEXTURE0);
        	sceneTex.bind();
        	spriteBatch.drawImage(sceneTex);
        	spriteBatch.flush();
    		
        	shockShader.unbind();
		} else if (boom) {
			boom = false;
		}
		
		fadeColor.a = fadeFX.getValue();
		if (fadeColor.a > 0f) {
			g.setColor(fadeColor);
			g.fillRect(0f, 0f, c.getWidth(), c.getHeight());
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
	
	private static void findMode() {
		
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		if (argv.length >= 1 && "vsync=".contains(argv[0]) && argv[0].length()>6) {
			vsync = Boolean.parseBoolean(argv[0].substring(6));
		}
		
		//Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);

		//load game text
		
		int width = 1024;
		int height = 768;
		boolean fullscreen = false;
		
		detailLevel = prefs.getInt("spacegame.detail", GameContext.DETAIL_HIGH);
//		System.out.println("Detail level: "+DETAILS[detailLevel]);
//
		boolean b = prefs.getBoolean("displayPanel", false);
		
		if (b) {
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
				e.printStackTrace();
			}
		}
		
//		System.out.println(Constants.BIT_PLAYER & (Constants.BIT_BULLET | Constants.BIT_ENEMY));
//		System.out.println(Constants.BIT_PLAYER & (Constants.BIT_BULLET | Constants.BIT_PLAYER));
//		System.out.println(Constants.BIT_BULLET & Constants.BIT_ENEMY);
//		System.out.println(Constants.BIT_BULLET & Constants.BIT_BULLET);
//		System.out.println(Constants.BIT_ENEMY & Constants.BIT_ENEMY);
//		System.exit(0);
		//create the display and start the game
		
		try {
			SpaceGameMain g = new SpaceGameMain("Space Game"+" - "+width+"x"+height);
			AppGameContainer container = new AppGameContainer(g);
			//System.out.println(g.getTitle());
			container.setDisplayMode(width,height,fullscreen);
			container.start();
		} catch (SlickException e) {
			Utils.error("There was a problem creating the display: "+e.getMessage(), e);
		}

	}
}