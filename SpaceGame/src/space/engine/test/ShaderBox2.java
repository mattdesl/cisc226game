package space.engine.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.TextureImpl;

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
public class ShaderBox2 extends BasicGame {
	
	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new ShaderBox2(), 800, 600, false).start();
	}
	
	public ShaderBox2() {
		super("shader test");
	}
	
	Image img;
	

    /*
    * if the shaders are setup ok we can use shaders, otherwise we just
    * use default settings
    */
    private boolean useShader = true;

//    public static final String VERTEX_SHADER = "" +
//            "varying vec4 vertColor;\n" +
//            "void main(){\n" +
//            "    gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;\n" +
//            "    vertColor = vec4(1.0, 0.0, 1.0, 1.0);\n" +
//            "}";

    
    public static final String VERTEX_SHADER = "uniform float waveTime;\n"+
    	    "uniform float waveWidth;\n"+
    	    "uniform float waveHeight;\n"+
    	    "void main(void){\n"+
    	    "    vec4 v = vec4(gl_Vertex);\n"+
    	    "    v.z = sin(waveWidth * v.x + waveTime) * cos(waveWidth * v.y + waveTime) * waveHeight;\n"+
    	    "    gl_Position = gl_ModelViewProjectionMatrix * v;\n"+
    	    "}";
    
//    public static final String FRAGMENT_SHADER = "" +
//            "varying vec4 vertColor;\n" +
//            "void main(){\n" +
//            "    gl_FragColor = vertColor;\n" +
//            "}";

    public static final String FRAGMENT_SHADER = "void main() {\n"+
    "    gl_FragColor[0] = gl_FragCoord[0] / 400.0;\n"+
    "    gl_FragColor[1] = gl_FragCoord[1] / 400.0;\n"+
    "    gl_FragColor[2] = 1.0;\n"+
    "}";

    
    
    /*
    * program shader, to which is attached a vertex and fragment shaders.
    * They are set to 0 as a check because GL will assign unique int
    * values to each
    */
    private int shader = 0;
    private int vertShader = 0;
    private int fragShader = 0;
    private int timer, colorMap, noiseMap;

    private SimpleFX red = new SimpleFX(0, 1f, 2000f, Easing.SINE_IN);
    
    boolean boom = false;
    float cx, cy;
    float elapsed = 0;
    float elapsed_max = 1500;
    
    Image noise;
    private Image offscreen;
    
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
		img = Resources.getFont1().getImage();
		noise = new Image("res/noise.jpg");
		
		offscreen = Image.createOffscreenImage(256, 256);
		Graphics g = offscreen.getGraphics();
		Graphics.setCurrent(g);
		SpriteBatch batch = new SpriteBatch(1000);
		batch.drawTextMultiLine(Resources.getFont2(), "this test uses a \nnoise-displacement\nshader", 10, 10);
		batch.flush();
		//g.drawString("This is a test of\nsome text affected\nby a noise displacement\nshader", 10, 10);
		g.flush();
		
        /*
        * create the shader program. If OK, create vertex
        * and fragment shaders
        */
        shader = GL20.glCreateProgram();
        
        if (shader != 0) {
            vertShader = createShader(GL20.GL_VERTEX_SHADER, source("res/vert.shader"));
            fragShader = createShader(GL20.GL_FRAGMENT_SHADER, source("res/frag.shader"));
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
            	colorMap = GL20.glGetUniformLocation(shader, "colorMap");
            	noiseMap = GL20.glGetUniformLocation(shader, "noiseMap");
            	timer = GL20.glGetUniformLocation(shader, "timer");
            }
        } else 
        	useShader = false;
	}
	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		g.clear();
		
        if (useShader) {
        	GL20.glUseProgram(shader);
        	GL20.glUniform1i(colorMap, 0);
    		GL20.glUniform1i(noiseMap, 1);
        	GL20.glUniform1f(timer, elapsed);

        	TextureImpl.unbind();

        	GL13.glActiveTexture(GL13.GL_TEXTURE1);
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, noise.getTexture().getTextureID());
        	GL11.glEnable(GL11.GL_TEXTURE_2D);
        	
        	GL13.glActiveTexture(GL13.GL_TEXTURE0);
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, offscreen.getTexture().getTextureID());
        	GL11.glEnable(GL11.GL_TEXTURE_2D);
        	
        	//GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);

        	
        	//if (redUniformLoc!=-1)
        	//	GL20.glUniform1f(redUniformLoc, red.getValue());
        	
        } else if (boom) {
        	boom = false;

        	GL13.glActiveTexture(GL13.GL_TEXTURE1);
        	GL11.glDisable(GL11.GL_TEXTURE_2D);
        	GL13.glActiveTexture(GL13.GL_TEXTURE0);
        	TextureImpl.bindNone();
        }
        //g.setColor(Color.white);
        //g.fillRect(0, 0, container.getWidth(), container.getHeight());
        g.setColor(Color.white);
        
        
        offscreen.draw(50, 50);
        
    	GL13.glActiveTexture(GL13.GL_TEXTURE1);
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL13.glActiveTexture(GL13.GL_TEXTURE0);
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
