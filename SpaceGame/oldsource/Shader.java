package space.engine;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

/**
 * An individual Shader object. Currently, only vertex and fragment shaders are supported. 
 * 
 * @author davedes
 */
public class Shader {
	
	/** The vertex shader type (GL20.GL_VERTEX_SHADER). */
	public static final int VERTEX_SHADER = GL20.GL_VERTEX_SHADER;
	/** The fragment shader type (GL20.GL_FRAGMENT_SHADER). */
	public static final int FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER;
	/** The geometry shader type (GL32.GL_GEOMETRY_SHADER). */
	public static final int GEOMETRY_SHADER = GL32.GL_GEOMETRY_SHADER;
	
	/** Shader source code. */
	protected String source;
	/** The shader type. */
	protected int type;
	/** The id of the shader. */
	protected int id;
	/** The log string, updated after compilation. */
	protected String log = "";
	
	/**
	 * Creates a new shader object with the given type and source code, then
	 * attempts to compile it. If compilation was successful, users should still
	 * double-check the resulting log via getCompileLog(). Occasionally GLSL
	 * will print warning/info messages even on successful compilation. 
	 * 
	 * @param type the type of shader; generally VERTEX_SHADER or FRAGMENT_SHADER
	 * @param source the source code
	 * @throws SlickException if there was a problem compiling the shader
	 */
	public Shader(int type, String source) throws SlickException {
		if (source==null)
			throw new IllegalArgumentException("shader source code must not be null");
		this.source = source;
		this.type = type;
		id = compileShader(type, source);
	}
	
	/**
	 * Implementations may wish to override Shader for specific functionality; e.g. creating
	 * a tesselation shader or creating a single vertex shader made up of multiple 
	 * mix-and-matched Shader objects.
	 */
	protected Shader() { }
	
	/**
	 * Returns true if this shader's ID is non-zero. If a shader is released, its ID 
	 * will be set to zero.
	 * @return whether this shader is usable
	 */
	public boolean valid() {
		return id!=0;
	}
	
	/**
	 * Called by a ShaderProgram to have this shader attach itself to the program. Override
	 * for functionality (i.e. attaching multiple shader objects that make up a single
	 * vertex shader)
	 * @param program the calling program
	 */
	void attach(ShaderProgram program) {
        ARBShaderObjects.glAttachObjectARB(program.getID(), getID());
	}

	/**
	 * Called by a ShaderProgram to have this shader detach itself from the program. Override
	 * for functionality (i.e. attaching multiple shader objects that make up a single
	 * vertex shader)
	 * @param program the calling program
	 */
	void detach(ShaderProgram program) {
		ARBShaderObjects.glDetachObjectARB(program.getID(), getID());
	}
	
	private String shaderTypeString(int type) {
		if (type==FRAGMENT_SHADER) return "FRAGMENT_SHADER";
		if (type==GEOMETRY_SHADER) return "GEOMETRY_SHADER";
		else if (type==VERTEX_SHADER) return "VERTEX_SHADER";
		else return "shader";
	}
	
	/**
	 * Compiles the shader.
	 * @param type the type to use in compilation
	 * @param source the source code to compile
	 * @return the resulting ID
	 * @throws SlickException if compilation was unsuccessful
	 */
	protected int compileShader(int type, String source) throws SlickException {
		int shader = ARBShaderObjects.glCreateShaderObjectARB(type);
		if (shader==0) 
			throw new SlickException("could not create shader object; check ShaderProgram.isSupported()");
		ARBShaderObjects.glShaderSourceARB(shader, source);
		ARBShaderObjects.glCompileShaderARB(shader);
		int comp = ARBShaderObjects.glGetObjectParameteriARB(shader, GL20.GL_COMPILE_STATUS);
		int len = ARBShaderObjects.glGetObjectParameteriARB(shader, GL20.GL_INFO_LOG_LENGTH);
		log = ARBShaderObjects.glGetInfoLogARB(shader, len);
		if (comp==GL11.GL_FALSE)
			throw new SlickException("ERROR: Compiler error in "+shaderTypeString(type)+"\n"+log);
		else if (log!=null&&log.length()!=0)
			Log.warn("GLSL shader compile warning: "+log);
		return shader;
	}
	
	/**
	 * Returns the log which might have useful error/warning/info messages during GLSL compilation.
	 * @return the shader's info log
	 */
	public String getCompileLog() {
		return log;
	}
	
	/**
	 * Releases this shader and frees up any memory it may be using. 
	 */
	public void release() {
		if (id!=0) {
			ARBShaderObjects.glDeleteObjectARB(id);
			id = 0;
		}
	}
	
	/**
	 * Returns the ID of this shader.
	 * @return the id
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the type of this shader, e.g. VERTEX_SHADER or FRAGMENT_SHADER.
	 * @return the type of shader
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Returns the source code of this shader.
	 * @return the source code
	 */
	public String getSource() {
		return source;
	}
}