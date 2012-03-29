package space.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A simple wrapper utility for creating and reusing shaders and shader programs.
 * 
 * @author davedes
 */
public class ShaderProgram {

	private static boolean strict = true;
	
	/**
	 * Returns true if GLSL shaders are supported in hardware on this system. This checks for
	 * the following OpenGL extensions: GL_ARB_shader_objects, GL_ARB_vertex_shader,
	 * GL_ARB_fragment_shader
	 * 
	 * @return true if shaders are supported
	 */
	public static boolean isSupported() {
		ContextCapabilities c = GLContext.getCapabilities();
		return c.GL_ARB_shader_objects && c.GL_ARB_vertex_shader && c.GL_ARB_fragment_shader;
	}
	
	/**
	 * Whether shader programs are to use "strict" uniform/attribute name
	 * checking. That is, when strict mode is enabled, trying to modify or retrieve uniform/attribute
	 * data by name will fail and throw an IllegalArgumentException if there exists no
	 * 'active' uniforms/attributes by the given name. (In GLSL, declared uniforms might still be
	 * "inactive" if they are not used.) If strict mode is disabled, getting/setting uniform/attribute
	 * data will fail silently if the name is not found.
	 * @param enabled true to enable strict mode
	 */
	public static void setStrictMode(boolean enabled) {
		strict = enabled;
	}
	
	/**
	 * Returns <tt>true</tt> if shader programs are to use "strict" uniform/attribute name
	 * checking. That is, when strict mode is enabled, trying to modify or retrieve uniform/attribute
	 * data by name will fail and throw an IllegalArgumentException if there exists no
	 * 'active' uniforms/attributes by the given name. (In GLSL, declared uniforms might still be
	 * "inactive" if they are not used.) If strict mode is disabled, getting/setting uniform/attribute
	 * data will fail silently if the name is not found.
	 * @return true if strict mode is enabled
	 */
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
	
	/**
	 * A convenience method to load a ShaderProgram from two text files; this uses
	 * the loadSource method.
	 * @param vertFile the location of the vertex shader source
	 * @param fragFile the location of the frag shader source
	 * @return the compiled and linked ShaderProgram
	 * @throws SlickException if there was an issue reading the file, compiling the source,
	 * 				or linking the program
	 */
	public static ShaderProgram loadProgram(String vertFile, String fragFile) throws SlickException {
		return new ShaderProgram(loadSource(vertFile), loadSource(fragFile));
	}
	
	/**
	 * Loads the given text file into a source code string, with each line separated
	 * by new-line ('\n') characters.
	 * @param ref the location of the text file
	 * @return the resulting source code String 
	 * @throws SlickException if there was an issue reading the source
	 */
	public static String loadSource(String ref) throws SlickException {
		InputStream in = ResourceLoader.getResourceAsStream(ref);
		try { return loadSource(in); }
		catch (SlickException e) { 
			throw new SlickException("could not load source file: "+ref);
		}
	}
	
	/**
	 * Loads the given input stream into a source code string, with each line separated
	 * by new-line ('\n') characters.
	 * @param in the input stream
	 * @return the resulting source code String 
	 * @throws SlickException if there was an issue reading the source
	 */
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

    /**
     * Creates a new shader program with the given vertex and fragment shader
     * source code. The given source code is compiled, then the shaders attached
     * and linked. 
     * 
     * If one of the shaders does not compile successfully, a SlickException will be thrown.
     * 
     * If the shaders are null or invalid (i.e. have been released), then this
     * will throw an IllegalArgumentException. If shaders are not supported
     * on this system (isSupported returns false), a SlickException will be thrown.
     * If the specified <tt>vert</tt> shader is not of type VERTEX_SHADER, or 
     * <tt>frag</tt> not of type FRAGMENT_SHADER, an IllegalArgumentException
     * will be thrown.
     * @param vertexShaderSource the shader code to compile, attach and link
     * @param fragShaderSource the frag code to compile, attach and link
     * @throws SlickException if there was an issue
     * @throws IllegalArgumentException if there was an issue
     */
    public ShaderProgram(String vertexShaderSource, String fragShaderSource) throws SlickException {
    	if (!isSupported())
			throw new SlickException("no shader support found; driver does not support extension GL_ARB_shader_objects");
		if (vertexShaderSource==null || fragShaderSource==null) 
			throw new IllegalArgumentException("shader source must be non-null");
		
		
    }
    
    /**
     * Creates a new shader program with the given vertex and fragment shaders.
     * The program is linked immediately. To create a program without immediately
     * linking shaders, use the empty ShaderProgram constructor.
     * 
     * If the shaders are null or invalid (i.e. have been released), then this
     * will throw an IllegalArgumentException. If shaders are not supported
     * on this system (isSupported returns false), a SlickException will be thrown.
     * If the specified <tt>vert</tt> shader is not of type VERTEX_SHADER, or 
     * <tt>frag</tt> not of type FRAGMENT_SHADER, an IllegalArgumentException
     * will be thrown.
     * @param vert the shader program to attach and link
     * @param frag the frag program to attach and link
     * @throws SlickException if there was an issue
     * @throws IllegalArgumentException if there was an issue
     */
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
	 * Creates a program object without linking any shaders. Vertex and fragment shaders should 
	 * be set before attempting to link() this program.
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
			throw new IllegalArgumentException("vertex and frag shaders must be non-null and valid before linking");
		
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
		fetchAttributes();
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
	
	/**
	 * Sets the fragment shader to be used on next link. This will have no effect until
	 * link() is called, in which case it will be attached to the program before linking
	 * (and then detached from the program after linking).
	 * @param shader the new fragment shader
	 */
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
	 * Releases this program and the current vertex/fragment shaders associated with it. 
	 * If you wish to only release the program (i.e. not the shaders as well), then use 
	 * releaseProgram(). Alternatively, to only release the associated shaders, use
	 * releaseShaders().
	 * Programs shouldn't be used after being released.
	 */
	public void release() {
		if (program!=0) {
			releaseShaders();
			releaseProgram();
		}
	}
	
	/**
	 * Releases the vertex/fragment shaders associated with this program. 
	 * If you wish to only release the program (i.e. not the shaders), then use 
	 * releaseProgram(). To release the program and its associated shaders, use
	 * release().
	 */
	public void releaseShaders() {
		vert.release();
		frag.release();
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
	 * A shader program is "valid" if it's ID is not zero. Upon
	 * releasing a program, the ID will be set to zero. 
	 * 
	 * @return whether this program is valid
	 */
	public boolean valid() {
		return program != 0;
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
	
	private void fetchAttributes() {
		int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_ATTRIBUTES);
		//max length of all uniforms stored in program
		int strLen = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH); 
		for (int i=0; i<len; i++) {
			String name = ARBVertexShader.glGetActiveAttribARB(program, i, strLen);
			int id = ARBVertexShader.glGetAttribLocationARB(program, name);
			uniforms.put(name, id);
		}
	}

	/**
	 * Returns the ID of the given uniform.
	 * @param name the uniform name
	 * @return the ID (location) in the shader program
	 */
	public int getUniformID(String name) {
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

	/**
	 * Returns the ID of the given attribute.
	 * @param name the attribute name
	 * @return the ID (location) in the shader program
	 */
	public int getAttributeID(String name) {
		int location = attributes.get(name);
		if (location!=-1)
			return location;
		location = ARBVertexShader.glGetAttribLocationARB(program, name);
		if (location == -1 && strict)
			throw new IllegalArgumentException("no active attribute by name '"+name+"'");
		attributes.put(name, location); 
		return location;
	}

	/**
	 * Returns the names of all active attributes that were found
	 * when linking the program.
	 * @return an array list of active attribute names
	 */
	public String[] getAttributes() {
		return attributes.keySet().toArray(new String[attributes.size()]);
	}
	
	/**
	 * Returns the names of all active uniforms that were found
	 * when linking the program.
	 * @return an array list of active uniform names
	 */
	public String[] getUniformNames() {
		return uniforms.keySet().toArray(new String[uniforms.size()]);
	}
	
	/**
	 * Returns the vertex shader last used by this shader program.
	 * Note that ShaderProgram does not hold on to shaders; they
	 * are detached after linking.
	 * @return the vertex shader
	 */
	public Shader getVertexShader() {
		return vert;
	}
	
	/**
	 * Returns the fragment shader last used by this shader program. 
	 * Note that ShaderProgram does not hold on to shaders; they
	 * are detached after linking.
	 * @return the fragment shader
	 */
	public Shader getFragmentShader() {
		return frag;
	}
	
	/**
	 * Enables the vertex array -- in strict mode, if the vertex attribute
	 * is not found (or it's inactive), an IllegalArgumentException will
	 * be thrown. If strict mode is disabled and the vertex attribute 
	 * is not found, this method will return <tt>false</tt> otherwise it
	 * will return <tt>true</tt>.
	 * 
	 * @param name the name of the vertex attribute to enable
	 * @return false if strict mode is disabled and this attribute couldn't be found
	 */
	public boolean enableVertexAttribute(String name) {
		int id = getAttributeID(name);
		if (id==-1) return false;
		ARBVertexShader.glEnableVertexAttribArrayARB(id);
		return true;
	}
	
	/**
	 * Disables the vertex array -- in strict mode, if the vertex attribute
	 * is not found (or it's inactive), an IllegalArgumentException will
	 * be thrown. If strict mode is disabled and the vertex attribute 
	 * is not found, this method will return <tt>false</tt> otherwise it
	 * will return <tt>true</tt>.
	 * 
	 * @param name the name of the vertex attribute to disable
	 * @return false if strict mode is disabled and this attribute couldn't be found
	 */
	public boolean disableVertexAttribute(String name) {
		int id = getAttributeID(name);
		if (id==-1) return false;
		ARBVertexShader.glDisableVertexAttribArrayARB(id);
		return true;
	}
	
//	public void setVertexAttribute(String name, int size, int type, boolean normalize, int stride, FloatBuffer buffer) {
//		ARBVertexShader.glVertexAttrib
//	}
	
	/**
	 * Sets the value of an RGBA vec4 uniform to the given color
	 * @param name the RGBA vec4 uniform
	 * @param color the color to assign
	 */
	public void setUniform4f(String name, Color color) {
		setUniform4f(name, color.r, color.g, color.b, color.a);
	}
	
	/**
	 * Sets the value of a vec2 uniform to the given Vector2f.
	 * @param name the vec2 uniform
	 * @param vec the vector to use
	 */
	public void setUniform2f(String name, Vector2f vec) {
		setUniform2f(name, vec.x, vec.y);
	}

	/**
	 * Retrieves data from a uniform and places it in the given buffer. If 
	 * strict mode is enabled, this will throw an IllegalArgumentException
	 * if the given uniform is not 'active' -- i.e. if GLSL determined that
	 * the shader isn't using it. If strict mode is disabled, this method will
	 * return <tt>true</tt> if the uniform was found, and <tt>false</tt> otherwise.
	 * 
	 * @param name the name of the uniform
	 * @param buf the buffer to place the data
	 * @return true if the uniform was found, false if there is no active uniform by that name
	 */
	public boolean getUniform(String name, FloatBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return false;
		ARBShaderObjects.glGetUniformARB(program, id, buf);
		return true;
	}
	
	/**
	 * Retrieves data from a uniform and places it in the given buffer. If 
	 * strict mode is enabled, this will throw an IllegalArgumentException
	 * if the given uniform is not 'active' -- i.e. if GLSL determined that
	 * the shader isn't using it. If strict mode is disabled, this method will
	 * return <tt>true</tt> if the uniform was found, and <tt>false</tt> otherwise.
	 * 
	 * @param name the name of the uniform
	 * @param buf the buffer to place the data
	 * @return true if the uniform was found, false if there is no active uniform by that name
	 */
	public boolean getUniform(String name, IntBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return false;
		ARBShaderObjects.glGetUniformARB(program, id, buf);
		return true;
	}
	
	/**
	 * Whether the shader program was linked with the active uniform by the given name. A
	 * uniform might be "inactive" even if it was declared at the top of a shader;
	 * if GLSL finds that a uniform isn't needed (i.e. not used in shader), then
	 * it will not be active.
	 * @param name the name of the uniform
	 * @return true if this shader program could find the active uniform
	 */
	public boolean hasUniform(String name) {
		return uniforms.containsKey(name);
	}
	
	/**
	 * Whether the shader program was linked with the active attribute by the given name. A
	 * attribute might be "inactive" even if it was declared at the top of a shader;
	 * if GLSL finds that a attribute isn't needed (i.e. not used in shader), then
	 * it will not be active.
	 * @param name the name of the attribute
	 * @return true if this shader program could find the active attribute
	 */
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	/**
	 * Sets the value of a float uniform.
	 * @param name the uniform by name
	 * @param f the float value
	 */
	public void setUniform1f(String name, float f) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform1fARB(id, f);
	}
	
	/**
	 * Sets the value of a sampler2D uniform.
	 * @param name the uniform by name
	 * @param i the integer / active texture (e.g. 0 for TEXTURE0)
	 */
	public void setUniform1i(String name, int i) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform1iARB(id, i);
	}
	
	/**
	 * Sets the value of a vec2 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / tex.s
	 * @param b vec.y / tex.t
	 */
	public void setUniform2f(String name, float a, float b) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform2fARB(id, a, b);
	}
	
	/**
	 * Sets the value of a vec3 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / color.r / tex.s
	 * @param b vec.y / color.g / tex.t
	 * @param c vec.z / color.b / tex.p
	 */
	public void setUniform3f(String name, float a, float b, float c) {
		int id = getUniformID(name);
		if (id==-1) return;
		
		ARBShaderObjects.glUniform3fARB(id, a, b, c);
	}

	/**
	 * Sets the value of a vec4 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / color.r
	 * @param b vec.y / color.g
	 * @param c vec.z / color.b 
	 * @param d vec.w / color.a 
	 */
	public void setUniform4f(String name, float a, float b, float c, float d) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform4fARB(id, a, b, c, d);
	}
	
	/**
	 * Sets the value of a ivec2 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / tex.s
	 * @param b vec.y / tex.t
	 */
	public void setUniform2i(String name, int a, int b) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform2iARB(id, a, b);
	}

	/**
	 * Sets the value of a ivec3 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / color.r
	 * @param b vec.y / color.g
	 * @param c vec.z / color.b 
	 */
	public void setUniform3i(String name, int a, int b, int c) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform3iARB(id, a, b, c);
	}
	
	/**
	 * Sets the value of a ivec4 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / color.r
	 * @param b vec.y / color.g
	 * @param c vec.z / color.b 
	 * @param d vec.w / color.a 
	 */
	public void setUniform4i(String name, int a, int b, int c, int d) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform4iARB(id, a, b, c, d);
	}
	
	public void setMatrix2(String name, boolean transpose, FloatBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniformMatrix2ARB(id, transpose, buf);
	}
	
	public void setMatrix3(String name, boolean transpose, FloatBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniformMatrix3ARB(id, transpose, buf);
	}

	public void setMatrix4(String name, boolean transpose, FloatBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniformMatrix4ARB(id, transpose, buf);
	}
	// TODO: include more setUniforms/getUniforms
}
