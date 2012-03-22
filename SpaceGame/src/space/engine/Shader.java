package space.engine;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public class Shader {
	
	public static final int VERTEX_SHADER = GL20.GL_VERTEX_SHADER;
	public static final int FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER;
	public static final int GEOMETRY_SHADER = GL32.GL_GEOMETRY_SHADER;
	
	protected String source;
	protected int type;
	protected int id;
	protected String log = "";
	
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

	public boolean valid() {
		return id!=0;
	}
	
	void attach(ShaderProgram program) {
        ARBShaderObjects.glAttachObjectARB(program.getID(), getID());
	}
	
	void detach(ShaderProgram program) {
		ARBShaderObjects.glDetachObjectARB(program.getID(), getID());
	}
	
	private String shaderTypeString(int type) {
		if (type==FRAGMENT_SHADER) return "FRAGMENT_SHADER";
		if (type==GEOMETRY_SHADER) return "GEOMETRY_SHADER";
		else if (type==VERTEX_SHADER) return "VERTEX_SHADER";
		else return "shader";
	}
	
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
	
	public String getCompileLog() {
		return log;
	}
	
	public void release() {
		if (id!=0) {
			ARBShaderObjects.glDeleteObjectARB(id);
			id = 0;
		}
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