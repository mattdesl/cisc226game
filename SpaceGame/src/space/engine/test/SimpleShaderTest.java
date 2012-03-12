package space.engine.test;

import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

import space.engine.ShaderProgram;

public class SimpleShaderTest extends BasicGame {
	
	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new SimpleShaderTest(), 800, 600, false).start();
	}
	
	public SimpleShaderTest() {
		super("shader test");
	}

	Image clouds, noise;
	ShaderProgram program;
	private HashMap<Integer, String> idMap = new HashMap<Integer, String>();
	
	private boolean shaderWorks = false;
	
	private HashMap<String, Integer> map = new HashMap<String, Integer>();
	
	
	
	@Override
	public void init(GameContainer container) throws SlickException {
		clouds = new Image("res/clouds.jpg");
		noise = new Image("res/noise.jpg");
		
		//often its wise to hard-code the shader... but for now we'll use a file
		program = ShaderProgram.loadProgram("res/vert.shader", "res/frag.shader");
		shaderWorks = program.valid();
		
		//System.out.println(GLContext.getCapabilities().OpenGL32);
		//System.out.println(GLContext.getCapabilities().GL_EXT_geometry_shader4);
		
		try {
			SoundStore.get().init();
			Audio a = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/speech.ogg"));
			int src = SoundStore.get().getSource(0);
			AL10.alSourcei(src, AL10.AL_BUFFER, a.getBufferID());
			
			AL10.alSourcef(src, AL10.AL_PITCH, 1f);
			AL10.alSourcef(src, AL10.AL_GAIN, 1f); 
		    AL10.alSourcei(src, AL10.AL_LOOPING, AL10.AL_TRUE);
		    System.out.println(AL10.alGetSourcei(src, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING);
			AL10.alSourcePlay(src);
			System.out.println(AL10.alGetSourcei(src, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING);
			System.out.println(AL10.alGetError());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		map.put("blah", 2);
		map.put("bloo", 3);
		map.put("bahh", 6);
	}

	//@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		
//		if (shaderWorks) {
//			
//		} else {
//			clouds.draw(0, 0, container.getWidth(), container.getHeight());
//			g.drawString("Shader error: "+program.getLog(), 50, 50);
//		}
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		
	}

}
