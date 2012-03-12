package space.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

public class ShaderProgram {

	// TODO: move to outer level ? 
	public static class Shader {
		
		private String source;
		private int type;
		private int id;
		private String log = "";
		
		public Shader(int type, String source) {
			if (source==null)
				throw new IllegalArgumentException("shader source code must not be null");
			this.source = source;
			this.type = type;
			id = createShader(type, source);
		}

		public boolean valid() {
			return id!=0;
		}
		
		private int createShader(int type, String source) {
			int shader = GL20.glCreateShader(type);
			if (shader==0) 
				return 0;
			
			ARBShaderObjects.glShaderSourceARB(shader, source);
			ARBShaderObjects.glCompileShaderARB(shader);
			int comp = ARBShaderObjects.glGetObjectParameteriARB(shader, GL20.GL_COMPILE_STATUS);
			if (comp==0) {
				int len = ARBShaderObjects.glGetObjectParameteriARB(shader, GL20.GL_INFO_LOG_LENGTH);
				log = ARBShaderObjects.glGetInfoLogARB(shader, len);
				return 0;
			}
			return shader;
		}
		
		public void release() {
			if (id!=0) {
				ARBShaderObjects.glDeleteObjectARB(id);
				id = 0;
			}
		}
		
		public String getLog() {
			return log;
		}
		
		public int getID() {
			return id;
		}
		
		public int getType() {
			return type;
		}
		
		public String getSource() {
			return source;
		}
	}
	
	private int program;
	
	private String log = "";
	
	protected HashMap<String, Integer> uniforms = new HashMap<String, Integer>();
	protected HashMap<String, Integer> attributes = new HashMap<String, Integer>();
	
	private String[] uniformNames, attributeNames;
	
	private Shader vert, frag;
	
	public static ShaderProgram loadProgram(String vertFile, String fragFile) throws SlickException {
		return new ShaderProgram(loadSource(vertFile), loadSource(fragFile));
	}
	
	public static String loadSource(String ref) throws SlickException {
		InputStream in = ResourceLoader.getResourceAsStream(ref);
		try { return loadSource(in); }
		catch (SlickException e) { 
			throw new SlickException("could not load source file: "+ref);
		}
	}
	
    public static String loadSource(InputStream in) throws SlickException{
    	try {
	    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    	String line = "";
	    	String txt = "";
	    	while ((line=br.readLine()) != null)
	    		txt += line + "\n";
	    	br.close();
	    	return txt.trim();
    	} catch (IOException e) {
    		throw new SlickException("could not load source file");
    	}
    }
	
    public ShaderProgram(String vertexShader, String fragShader) {
    	this(new Shader(GL20.GL_VERTEX_SHADER, vertexShader),
    		 new Shader(GL20.GL_FRAGMENT_SHADER, fragShader));
    }
    
	public ShaderProgram(Shader vertexShader, Shader fragShader) {
		if (vertexShader==null || fragShader==null)
			throw new IllegalArgumentException("shaders must be non-null");
		this.vert = vertexShader;
		this.frag = fragShader;
		log += vert.getLog() + frag.getLog();
		this.program = compile();
		if (valid()) {
			fetchUniforms();
			fetchAttributes();
		}
	}
	
	private int compile() {
		if (!vert.valid() || !frag.valid())
			return 0;
		int program = ARBShaderObjects.glCreateProgramObjectARB();
        ARBShaderObjects.glAttachObjectARB(program, vert.getID());
        ARBShaderObjects.glAttachObjectARB(program, frag.getID());
        ARBShaderObjects.glLinkProgramARB(program);
        //GL20.glValidateProgram(program);
        int comp = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_LINK_STATUS);
        if (comp==0) {
        	int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_INFO_LOG_LENGTH);
			log += ARBShaderObjects.glGetInfoLogARB(program, len);
			return 0;
        }
        return program;
	}
	
	/**
	 * Returns the log for this shader program as well as any shaders attached to this program.
	 * @return
	 */
	public String getLog() {
		return log;
	}
	
	public void unbind() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
	
	public void bind() {
		if (!valid())
			throw new IllegalStateException("trying to enable a program that is not valid");
		ARBShaderObjects.glUseProgramObjectARB(program);
	}
	
	public void release() {
		ARBShaderObjects.glDetachObjectARB(program, vert.getID());
		ARBShaderObjects.glDetachObjectARB(program, frag.getID());
		vert.release();
		frag.release();
		ARBShaderObjects.glDeleteObjectARB(program);
	}
	
	public boolean valid() {
		return program != 0;
	}
	
	private void fetchUniforms() {
		int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_UNIFORMS);
		//max length of all uniforms stored in program
		int strLen = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH); 
		uniformNames = new String[len];
		for (int i=0; i<len; i++) {
			uniformNames[i] = ARBShaderObjects.glGetActiveUniformARB(program, i, strLen);
			int id = ARBShaderObjects.glGetUniformLocationARB(program, uniformNames[i]);
			uniforms.put(uniformNames[i], id);
		}
	}
	
	private void fetchAttributes() {
		int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_ATTRIBUTES);
		//max length of all uniforms stored in program
		int strLen = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH); 
		attributeNames = new String[len];
		for (int i=0; i<len; i++) {
			attributeNames[i] = ARBShaderObjects.glGetActiveUniformARB(program, i, strLen);
			int id = ARBShaderObjects.glGetUniformLocationARB(program, attributeNames[i]);
			uniforms.put(attributeNames[i], id);
		}
	}
	
	public int getUniformID(String name) {
		return uniforms.get(name);
	}
	
	public int getAttributeID(String name) {
		return attributes.get(name);
	}
	
	public String[] getAttributes() {
		return attributeNames;
	}
	
	public String[] getUniformNames() {
		return uniformNames;
	}
	
	public Shader getVertexShader() {
		return vert;
	}
	
	public Shader getFragmentShader() {
		return frag;
	}

	public void getUniform(String name, FloatBuffer buf) {
		ARBShaderObjects.glGetUniformARB(program, getUniformID(name), buf);
	}
	
	public void getUniform(String name, IntBuffer buf) {
		ARBShaderObjects.glGetUniformARB(program, getUniformID(name), buf);
	}

	public void setUniform1f(String name, float f) {
		ARBShaderObjects.glUniform1fARB(getUniformID(name), f);
	}
	
	public void setUniform1i(String name, int i) {
		ARBShaderObjects.glUniform1iARB(getUniformID(name), i);
	}
	
	public void setUniform2f(String name, float a, float b) {
		ARBShaderObjects.glUniform2fARB(getUniformID(name), a, b);
	}
	
	public void setUniform3f(String name, float a, float b, float c) {
		ARBShaderObjects.glUniform3fARB(getUniformID(name), a, b, c);
	}
	
	public void setUniform4f(String name, float a, float b, float c, float d) {
		ARBShaderObjects.glUniform4fARB(getUniformID(name), a, b, c, d);
	}
	
	
	// TODO: include more setUniforms/getUniforms
}
