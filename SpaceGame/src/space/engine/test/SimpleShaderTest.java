package space.engine.test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

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
	
	private Image fboImg;
	private float rot;
	
	private Graphics offG;
	

	public static final String readFile(String file) throws IOException {
	   BufferedInputStream in = new BufferedInputStream(ResourceLoader.getResourceAsStream(file));
	   ByteBuffer2 buffer = new ByteBuffer2();
	   byte[] buf = new byte[1024];
	   int len;
	   while ((len = in.read(buf)) != -1) {
	      buffer.put(buf, len);
	   }
	   in.close();
	   return new String(buffer.buffer, 0, buffer.write);
	}

	static class ByteBuffer2 {

	   public byte[] buffer = new byte[256];

	   public int write;

	   public void put(byte[] buf, int len) {
	      ensure(len);
	      System.arraycopy(buf, 0, buffer, write, len);
	      write += len;
	   }

	   private void ensure(int amt) {
	      int req = write + amt;
	      if (buffer.length <= req) {
	         byte[] temp = new byte[req * 2];
	         System.arraycopy(buffer, 0, temp, 0, write);
	         buffer = temp;
	      }
	   }

	}
	
	@Override
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		container.setClearEachFrame(false);
		logo = new Image("res/logo.png");
		
		long m = System.currentTimeMillis();
		tex0 = new Image("res/tex0.jpg");
		System.out.println("Loading tex "+(System.currentTimeMillis()-m));
		
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
		
		fboImg = Image.createOffscreenImage(256, 256).getFlippedCopy(false, true);
		offG = fboImg.getGraphics();
	}
	
	public boolean closeRequested() {
		program.release();
		return true;
	}
	
	
	
	public void reload(int type) {
		if (program==null) //if the program wasn't created
			return;
		log = "";
		System.out.println(GLContext.getCapabilities().GL_ARB_get_program_binary);
		long m2 = 0;
		Shader vert=null, frag=null;
		try {
			//an exception here means we have a compiler error

//			String str = ShaderProgram.loadSource("res/frag.shader");
//			System.out.println(str.length());
			m2 = System.currentTimeMillis();
			long m = System.currentTimeMillis();
			
			
			
			String src1 = ShaderProgram.loadSource(VERT_FILE);
			System.out.println("Vert read "+(System.currentTimeMillis()-m));
			m = System.currentTimeMillis();
			String src2 = readFile(FRAG_FILE);
			System.out.println("Frag read "+(System.currentTimeMillis()-m));
			
			m = System.currentTimeMillis();
			//load the vertex shader...
			if (type==Shader.VERTEX_SHADER || type==0) {
				if (program.getVertexShader()!=null)
					program.getVertexShader().release();
				vert = new Shader(Shader.VERTEX_SHADER, src1);
				program.setVertexShader(vert);
			} 
			System.out.println("Shader vert "+(System.currentTimeMillis()-m));
			m = System.currentTimeMillis();
			if (type==Shader.FRAGMENT_SHADER || type==0) {
				if (program.getFragmentShader()!=null)
					program.getFragmentShader().release();
				frag = new Shader(Shader.FRAGMENT_SHADER, src2);
				program.setFragmentShader(frag);
			}
			System.out.println("Shader frag "+(System.currentTimeMillis()-m));
			m = System.currentTimeMillis();
			
			//an exception here means we have a link error
			program.link();
			System.out.println("Shader link "+(System.currentTimeMillis()-m));
			m = System.currentTimeMillis();
			
			log = program.getLog();
			
			shaderWorks = true;
		} catch (Exception ex) {
			shaderWorks = false;
			
			//incase we reach an error at any point, release everything that may have been created
			if (vert!=null)
				vert.release();
			if (frag!=null)
				frag.release();
			
			log = ex.getMessage();
			Log.error(log);
		}
		System.out.println("Full load: "+(System.currentTimeMillis()-m2));
		if (shaderWorks) {
			//set up our uniforms...
			program.bind();
			//strict mode is disabled, so these will work regardless of whether the uniform is active / exists
			program.setUniform1i("tex0", 0); //texture 0
			program.setUniform2f("resolution", 800, 600);
			program.setUniform2f("surfacePosition", 0.0f, 0.0f);
			program.setUniform2f("surfaceSize", 0.1f, 0.1f);
			program.setUniform1f("time", elapsed);
			program.setUniform2f("mouse", 0f, 0f);
			program.unbind();
		}
		
		
		
		
	}
	
	//@Override
	public void render(GameContainer container, Graphics g) throws SlickException {	
		g.clear();
		
		if (shaderWorks && useShader) {
			program.bind();
			float mx = Math.min(1f, (container.getInput().getMouseX()-x)/(float)tex0.getTextureWidth());
			float my = Math.min(1f, (container.getInput().getMouseY()-y)/(float)tex0.getTextureHeight());
			program.setUniform2f("mouse", (mx/(float)tex0.getTextureWidth()), (my/(float)tex0.getTextureHeight()));
			program.setUniform1f("time", elapsed);
			
			tex0.bind();
		}
		
		//draw the shader stuff
		tex0.draw(0, 0, container.getWidth(), container.getHeight());
		
		if (shaderWorks && useShader)
			program.unbind();
		
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
