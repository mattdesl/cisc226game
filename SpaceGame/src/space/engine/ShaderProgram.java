package space.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

public class ShaderProgram {

	private static boolean strict = true;
	
	public static boolean isSupported() {
		ContextCapabilities c = GLContext.getCapabilities();
		return c.GL_ARB_shader_objects && c.GL_ARB_vertex_shader && c.GL_ARB_fragment_shader;
	}
	
	public static void setStrictMode(boolean enabled) {
		strict = enabled;
	}
	
	public static boolean isStrictMode() {
		return strict;
	}
	
	/**
	 * Disables all shader use.
	 */
	public static void bindNone() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
	
	
	protected int program;
	
	protected String log = "";
	
	protected HashMap<String, Integer> uniforms = new HashMap<String, Integer>();
	protected HashMap<String, Integer> attributes = new HashMap<String, Integer>();
	
	
	protected Shader vert, frag;
	
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
	
    public static String loadSource(InputStream in) throws SlickException {
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
	
    public ShaderProgram(String vertexShaderSource, String fragShaderSource) throws SlickException {
    	this(new Shader(Shader.VERTEX_SHADER, vertexShaderSource),
    		 new Shader(Shader.FRAGMENT_SHADER, fragShaderSource));
    }
    
	public ShaderProgram(Shader vert, Shader frag) throws SlickException {
		//do some error checking before we tell OpenGL to create the program
		if (!isSupported())
			throw new SlickException("no shader support found; driver does not support extension GL_ARB_shader_objects");
		if (vert==null || frag==null || !vert.valid() || !frag.valid()) {
			throw new IllegalArgumentException("shaders must be non-null and valid before linking");
		}
		if (vert.getType()!=Shader.VERTEX_SHADER)
			throw new IllegalArgumentException("vertex shader not of type VERTEX_SHADER");
		if (frag.getType()!=Shader.FRAGMENT_SHADER)
			throw new IllegalArgumentException("vertex shader not of type FRAGMENT_SHADER");
		
		
		create();
		
		this.vert = vert;
		this.frag = frag;
		link();
	}
	
	/**
	 * Creates a program object with nothing attached to it; a vertex and fragment shader should
	 * be set before linking.
	 * 
	 * @throws SlickException
	 */
	public ShaderProgram() throws SlickException {
		create();
	}
	
	private void create() throws SlickException {
		if (!isSupported())
			throw new SlickException("no shader support found; driver does not support extension GL_ARB_shader_objects");
		program = ARBShaderObjects.glCreateProgramObjectARB();
		if (program == 0)
			throw new SlickException("could not create program; check ShaderProgram.isSupported()");
	}
	
	/**
	 * Compiles this program by attaching the current Frag/Vertex shaders,
	 * linking the program, and detaching the shaders (since they are now linked
	 * to the program).
	 * 
	 * @throws IllegalStateException
	 *             if we are trying to link an invalid (released) program, or if
	 *             the current shaders are null/invalid
	 * @throws SlickException
	 *             if the link was unsuccessful
	 */
	public void link() throws SlickException {
		if (!valid())
			throw new IllegalStateException("trying to link a released program");
		if (vert==null || frag==null || !vert.valid() || !frag.valid())
			throw new IllegalArgumentException("shaders must be non-null and valid before linking");
		
		uniforms.clear();
		attributes.clear();
		
		vert.attach(this);
		frag.attach(this);
        ARBShaderObjects.glLinkProgramARB(program);
        
        //GL20.glValidateProgram(program);
        int comp = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_LINK_STATUS);
        
        //shaders no longer need to be attached to the program
        vert.detach(this);
        frag.detach(this);
        int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_INFO_LOG_LENGTH);
		log += ARBShaderObjects.glGetInfoLogARB(program, len);
        if (comp==GL11.GL_FALSE) 
			throw new SlickException("ERROR: Error in linking shaders\n"+log);
        else if (log!=null&&log.length()!=0)
			Log.warn("GLSL link warning: "+log);
		fetchUniforms();
//		fetchAttributes();
	}
	
	/**
	 * Returns the log info 
	 * @return the log info for this program object
	 */
	public String getLinkLog() {
		return log;
	}
	
	/**
	 * Concats the shader's compile info and this program's link info and returns the result.
	 * @return the full log of this ShaderProgram
	 */
	public String getLog() {
		String s = "";
		if (vert!=null)
			s += vert.getCompileLog();
		if (frag!=null)
			s += frag.getCompileLog();
		return s + getLinkLog();
	}
	
	/**
	 * Sets the vertex shader to be used on next link. This will have no effect until
	 * link() is called, in which case it will be attached to the program before linking
	 * (and then detached from the program after linking).
	 * @param shader the new vertex shader
	 */
	public void setVertexShader(Shader shader) {
		if (shader.getType()!=Shader.VERTEX_SHADER)
			throw new IllegalArgumentException("vertex shader not of type VERTEX_SHADER");
		this.vert = shader;
	}
	
	public void setFragmentShader(Shader shader) {
		if (shader.getType()!=Shader.FRAGMENT_SHADER)
			throw new IllegalArgumentException("fragment shader not of type FRAGMENT_SHADER");
		this.frag = shader;
	}
	
	/**
	 * Enables this shader for use. Only one shader can be active at a time. 
	 * @throw IllegalStateException if this program is invalid
	 */
	public void bind() {
		if (!valid())
			throw new IllegalStateException("trying to enable a program that is not valid");
		ARBShaderObjects.glUseProgramObjectARB(program);
	}

	/**
	 * Unbinds this program and disables shaders via bindNone. This isn't necessary to
	 * call immediately before another shader bind(), as only one shader can be active
	 * at a time.
	 */
	public void unbind() {
		ShaderProgram.bindNone();
	}
	
	/**
	 * Releases this program and the current vertex/fragment shaders. If you wish to only release
	 * the program (i.e. not the shaders as well), then use releaseProgram(). Programs shouldn't 
	 * be used after being released.
	 */
	public void release() {
		if (program!=0) {
			vert.release();
			frag.release();
			releaseProgram();
		}
	}
	
	/**
	 * Releases this program and sets its ID to zero -- this will not release the current shaders.
	 * Programs shouldn't be used after being released.
	 */
	public void releaseProgram() {
		if (program!=0) {
			ARBShaderObjects.glDeleteObjectARB(program);
			program = 0;
		}
	}
	
	/**
	 * The ID of a shader, the value given by glCreateProgram.
	 * @return the program ID
	 */
	public int getID() {
		return program;
	}
	
	/**
	 * A shader program is "valid" if it's ID is not zero.
	 * @return whether this program is valid
	 */
	public boolean valid() {
		return program != 0;
	}
	
//	protected int findAttributeLocation(String name) {
//		int location = attributes.get(name);
//		if (location!=-1)
//			return location;
//		location = ARBVertexShader.glGetAttribLocationARB(program, name);
//		if (location == -1 && strict)
//			throw new IllegalArgumentException("no active attribute by name '"+name+"'");
//		attributes.put(name, location); 
//		return location;
//	}
	
	protected int findUniformLocation(String name) {
		Integer locI = uniforms.get(name);
		int location = locI==null ? -1 : locI.intValue();
		if (location!=-1)
			return location;
		location = ARBShaderObjects.glGetUniformLocationARB(program, name);
		if (location == -1 && strict)
			throw new IllegalArgumentException("no active uniform by name '"+name+"' (disable strict compiling to suppress warnings)");
		uniforms.put(name, location); 
		return location;
	}
	
	private void fetchUniforms() {
		int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_UNIFORMS);
		//max length of all uniforms stored in program
		int strLen = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
		
		for (int i=0; i<len; i++) {
			String name = ARBShaderObjects.glGetActiveUniformARB(program, i, strLen);
			int id = ARBShaderObjects.glGetUniformLocationARB(program, name);
			uniforms.put(name, id);
		}
	}
	
//	private void fetchAttributes() {
//		int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_ATTRIBUTES);
//		//max length of all uniforms stored in program
//		int strLen = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH); 
//		for (int i=0; i<len; i++) {
//			String name = ARBVertexShader.glGetActiveAttribARB(program, i, strLen);
//			int id = ARBVertexShader.glGetAttribLocationARB(program, name);
//			uniforms.put(name, id);
//		}
//	}
	
	public int getUniformID(String name) {
		return findUniformLocation(name);
	}
	
//	public int getAttributeID(String name) {
//		return findAttributeLocation(name);
//	}
	
//	public String[] getAttributes() {
//		return attributes.keySet().toArray(new String[attributes.size()]);
//	}
	
	public String[] getUniformNames() {
		return uniforms.keySet().toArray(new String[uniforms.size()]);
	}
	
	public Shader getVertexShader() {
		return vert;
	}
	
	public Shader getFragmentShader() {
		return frag;
	}

	public boolean getUniform(String name, FloatBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return false;
		ARBShaderObjects.glGetUniformARB(program, id, buf);
		return true;
	}
	
	public boolean getUniform(String name, IntBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return false;
		ARBShaderObjects.glGetUniformARB(program, id, buf);
		return true;
	}
	
	public boolean hasUniform(String name) {
		return uniforms.containsKey(name);
	}
	
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	public void setUniform1f(String name, float f) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform1fARB(id, f);
	}
	
	public void setUniform1i(String name, int i) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform1iARB(id, i);
	}
	
	public void setUniform2f(String name, float a, float b) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform2fARB(id, a, b);
	}
	
	public void setUniform3f(String name, float a, float b, float c) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform3fARB(id, a, b, c);
	}
	
	public void setUniform4f(String name, float a, float b, float c, float d) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform4fARB(id, a, b, c, d);
	}
	
	
	// TODO: include more setUniforms/getUniforms
}
