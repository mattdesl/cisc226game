package space.engine.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.TextureImpl;

import space.engine.FBO;
import space.engine.SpriteBatch;
import space.engine.easing.Easing;
import space.engine.easing.SimpleFX;
import space.util.Resources;
import space.util.Utils;


/**
 * A minor evolution of ShaderBox to use standard OpenGL 2.0 instead of extensions.  The vertex
 * color is different in order to differentiate it and the Box class had to be renamed, otherwise
 * little is changed from the original -- no matter how much I'd like to clean it up.
 */
public class ShaderBox3 extends BasicGame {
	
	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new ShaderBox3(), 1280, 600, false).start();
	}
	
	public ShaderBox3() {
		super("shader test");
	}
	
	Image img;
	

    /*
    * if the shaders are setup ok we can use shaders, otherwise we just
    * use default settings
    */
    private boolean useShader = true;

    
    /*
    * program shader, to which is attached a vertex and fragment shaders.
    * They are set to 0 as a check because GL will assign unique int
    * values to each
    */
    private int shader = 0;
    private int vertShader = 0;
    private int fragShader = 0;
    private int sceneTex, center, time, shockParams;

    private SimpleFX red = new SimpleFX(0, 1f, 2000f, Easing.SINE_IN);
    
    boolean boom = false;
    float cx, cy;
    float elapsed = 0;
    float elapsed_max = 1500;
    
    Image noise;
    private Image offscreen;
    SpriteBatch batch;
    FBO fbo;
    
    private String source(String str) throws SlickException{
    	try {
	    	BufferedReader br = new BufferedReader(new InputStreamReader(Utils.getResourceAsStream(str)));
	    	String line = "";
	    	String txt = "";
	    	while ((line=br.readLine()) != null)
	    		txt += line + "\n";
	    	br.close();
	    	return txt.trim();
    	} catch (IOException e) {
    		throw new SlickException("error loading source");
    	}
    }
    
	@Override
	public void init(GameContainer container) throws SlickException {
		container.setClearEachFrame(false);
		container.getGraphics().setBackground(Color.gray);
		
		Resources.create();
		//img = Resources.getFont1().getImage();
		batch = new SpriteBatch(5000);
		noise = new Image("res/clouds.jpg");

		//float mw = Math.max(container.getWidth(), container.getHeight());
		//float mh = M
		
		fbo = new FBO(container.getWidth(), container.getHeight());
		offscreen = new Image(fbo.getTexture());
		fbo.bind();
		batch.drawImage(noise, 0, 0, fbo.getWidth(), fbo.getHeight());
		batch.flush();
		//Graphics.setCurrent(g);
		//noise.draw(0, 0, offscreen.getWidth(), offscreen.getHeight());
		fbo.unbind();
		
        /*
        * create the shader program. If OK, create vertex
        * and fragment shaders
        */
        shader = GL20.glCreateProgram();
        
        if (shader != 0) {
            vertShader = createShader(GL20.GL_VERTEX_SHADER, source("res/vert2.shader"));
            fragShader = createShader(GL20.GL_FRAGMENT_SHADER, source("res/frag2.shader"));
        } else 
        	useShader = false;

        /*
        * if the vertex and fragment shaders setup sucessfully,
        * attach them to the shader program, link the sahder program
        * (into the GL context I suppose), and validate
        */
        if (vertShader != 0 && fragShader != 0) {
            GL20.glAttachShader(shader, vertShader);
            GL20.glAttachShader(shader, fragShader);
            GL20.glLinkProgram(shader);
            GL20.glValidateProgram(shader);
            useShader = printLogInfo(shader);
            if (useShader) {
            	sceneTex = GL20.glGetUniformLocation(shader, "sceneTex");
            	center = GL20.glGetUniformLocation(shader, "center");
            	time = GL20.glGetUniformLocation(shader, "time");
            	shockParams = GL20.glGetUniformLocation(shader, "shockParams");
            }
        } else 
        	useShader = false;
	}
	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		g.clear();
		
        if (useShader && boom && elapsed < elapsed_max) {
        	GL20.glUseProgram(shader);
        	GL20.glUniform1i(sceneTex, 0);
        	float cx = this.cx / container.getWidth();
        	float cy = this.cy / container.getHeight();
    		GL20.glUniform2f(center, cx, cy);
        	GL20.glUniform1f(time, elapsed*0.001f);
        	GL20.glUniform3f(shockParams, 10, 0.4f, .1f);

        	TextureImpl.unbind();

        	GL13.glActiveTexture(GL13.GL_TEXTURE0);
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, offscreen.getTexture().getTextureID());
        	GL11.glEnable(GL11.GL_TEXTURE_2D);
//        	
//        	GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, offscreen.getTexture().getTextureID());
//        	GL11.glEnable(GL11.GL_TEXTURE_2D);
        	
        	//GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);

        	
        	//if (redUniformLoc!=-1)
        	//	GL20.glUniform1f(redUniformLoc, red.getValue());
        	
        } else if (boom) {
        	boom = false;
//
//        	GL13.glActiveTexture(GL13.GL_TEXTURE1);
//        	GL11.glDisable(GL11.GL_TEXTURE_2D);
//        	GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        	TextureImpl.bindNone();
        }
        //g.setColor(Color.white);
        //g.fillRect(0, 0, container.getWidth(), container.getHeight());
        g.setColor(Color.white);
        
        float u = offscreen.getTextureOffsetX();
        float v = offscreen.getTextureOffsetY();
        float uw = 2.0f;
        float uh = 2.0f;
        //batch.drawImage(offscreen, 0, 0, offscreen.getWidth(), offscreen.getHeight(), u, v, uw, uh, null);
        batch.drawImage(offscreen);
        batch.flush();
        //offscreen.draw(0, 0);
//        
//    	GL13.glActiveTexture(GL13.GL_TEXTURE1);
//    	GL11.glDisable(GL11.GL_TEXTURE_2D);
//    	GL13.glActiveTexture(GL13.GL_TEXTURE0);
    	//TextureImpl.bindNone();
        
        //release the shader
        if (useShader)
        	GL20.glUseProgram(0);
	}

	public void mousePressed(int button, int x, int y) {
		boom = true;
		cx = x;
		cy = y;
		elapsed = 0;
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		red.update(delta);
		if (red.finished()) {
			red.flip();
			red.restart();
		}
		elapsed += delta;	
	}


	private int createShader(int type, String source) {
		int shader = GL20.glCreateShader(type);
		if (shader==0) return 0;
		GL20.glShaderSource(shader, source);
		GL20.glCompileShader(shader);
		return printLogInfo(shader) ? shader : 0;
	}
    

    private static boolean printLogInfo(int obj) {
        IntBuffer iVal = BufferUtils.createIntBuffer(1);
        GL20.glGetProgram(obj, GL20.GL_INFO_LOG_LENGTH, iVal);

        int length = iVal.get();
        if (length > 1) {
            // We have some info we need to output.
            ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
            iVal.flip();
            GL20.glGetProgramInfoLog(obj, iVal, infoLog);
            byte[] infoBytes = new byte[length];
            infoLog.get(infoBytes);
            String out = new String(infoBytes);
            System.out.println("Info log:\n" + out);
        } else 
        	return true;
        return false;
    }
	

}
