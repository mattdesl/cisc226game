package space.engine.test;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;

import space.engine.FBO;
import space.engine.Shader;
import space.engine.ShaderProgram;

public class SimpleShaderTest extends BasicGame {
	
	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new SimpleShaderTest(), 800, 600, false).start();
	}
	
	public SimpleShaderTest() {
		super("shader test");
	}

	Image tex0, logo;
	ShaderProgram program;
	String log;
	private float elapsed;
	
	private boolean shaderWorks, useShader=true;
	
	private int x=100, y=100;
	
	public static final String VERT_FILE = "res/hellovert.shader";
	public static final String FRAG_FILE = "res/hellofrag.shader";
	
	private GameContainer container;
	
	private FBO fbo;
	private Image fboImg;
	private float rot;
	
	@Override
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		logo = new Image("res/logo.png");
		tex0 = new Image("res/tex0.jpg");
		
		//try to load the program
		try {
			ShaderProgram.setStrictMode(false);
			program = new ShaderProgram();
			reload(0);
		} catch (SlickException ex) {
			//no shader support...
			log = ex.getMessage();
			Log.error(ex.getMessage());
		}
		
		fbo = new FBO(256, 256);
		fboImg = new Image(fbo.getTexture()).getFlippedCopy(false, true);
	}
	
	public void reload(int type) {
		if (program==null) //if the program wasn't created
			return;
		log = "";
		
		Shader vert=null, frag=null;
		try {
			//an exception here means we have a compiler error
			
			//load the vertex shader...
			if (type==Shader.VERTEX_SHADER || type==0) {
				if (program.getVertexShader()!=null)
					program.getVertexShader().release();
				vert = new Shader(Shader.VERTEX_SHADER, ShaderProgram.loadSource(VERT_FILE));
				program.setVertexShader(vert);
			} 

			if (type==Shader.FRAGMENT_SHADER || type==0) {
				if (program.getFragmentShader()!=null)
					program.getFragmentShader().release();
				frag = new Shader(Shader.FRAGMENT_SHADER, ShaderProgram.loadSource(FRAG_FILE));
				program.setFragmentShader(frag);
			}
			
			//an exception here means we have a link error
			program.link();
			
			log = program.getLog();
			
			shaderWorks = true;
		} catch (SlickException ex) {
			shaderWorks = false;
			
			//incase we reach an error at any point, release everything that may have been created
			if (vert!=null)
				vert.release();
			if (frag!=null)
				frag.release();
			
			log = ex.getMessage();
			Log.error(log);
		}
		
		if (shaderWorks) {
			//set up our uniforms...
			program.bind();
			//strict mode is disabled, so these will work regardless of whether the uniform is active / exists
			program.setUniform1i("tex0", 0); //texture 0
			program.setUniform2f("resolution", 256, 256);
			program.setUniform1f("time", elapsed);
			
			program.unbind();
		}
		
		
		
		
	}
	
	//@Override
	public void render(GameContainer container, Graphics g) throws SlickException {		
		fbo.bind();
		if (shaderWorks && useShader) {
			program.bind();
			float mx = Math.min(1f, (container.getInput().getMouseX()-x)/(float)tex0.getTextureWidth());
			float my = Math.min(1f, (container.getInput().getMouseY()-y)/(float)tex0.getTextureHeight());
			program.setUniform2f("mouse", (mx/(float)tex0.getTextureWidth()), (my/(float)tex0.getTextureHeight()));
			program.setUniform1f("time", elapsed);
			
			tex0.bind();
		}
		
		tex0.draw(0, 0, container.getWidth(), container.getHeight());
		
		if (shaderWorks && useShader)
			program.unbind();
		
		g.scale(1f, -1f);
		g.translate(0, -fbo.getHeight());
		g.setColor(Color.black);
		
		
		if (GLContext.getCapabilities().OpenGL14) {
			GL11.glEnable(GL14.GL_COLOR_SUM);
			Color col = Color.red;
			GL14.glSecondaryColor3f(col.r, col.g, col.b);
			GL11.glTexEnvi(SGL.GL_TEXTURE_ENV, SGL.GL_TEXTURE_ENV_MODE, SGL.GL_MODULATE);
		}
		
		//... draw image ...
		
		if (GLContext.getCapabilities().OpenGL14) {
			GL11.glDisable(GL14.GL_COLOR_SUM);
		}
		
		g.drawString("Some slick text...", 0, 0);
		g.resetTransform();
		
		fbo.unbind();
		
		fboImg.setRotation(rot);
		fboImg.draw(100, 100);
		
		g.setColor(Color.white);
		if (shaderWorks)
			g.drawString("Space to toggle shader\nR to reload shader from text file", 10, 25);
		else
			g.drawString("Shader did not load... try again", 10, 25);
		if (log.length()!=0)
			g.drawString("Log:\n"+log, 10, 75);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		elapsed += delta*0.001f;
		rot += delta*0.03f;
		
		if (container.getInput().isKeyPressed(Input.KEY_SPACE)) 
			useShader = !useShader;
		else if (container.getInput().isKeyPressed(Input.KEY_F))
			reload(Shader.FRAGMENT_SHADER);
		else if (container.getInput().isKeyPressed(Input.KEY_V))
			reload(Shader.VERTEX_SHADER);
		else if (container.getInput().isKeyPressed(Input.KEY_R))
			reload(0);
	}

}
