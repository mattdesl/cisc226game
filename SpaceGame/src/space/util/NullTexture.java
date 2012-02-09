package space.util;

import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;

/**
 * A "null" texture which holds no image data and does not
 * create or bind any OpenGL textures. This is useful
 * for creating off-screen images where there is no need
 * to keep hold of the original image (i.e. if the off-screen
 * image you are creating is empty).
 * 
 * @author davedes
 */
public class NullTexture implements Texture {
	
	private int width, height;
	private int texWidth, texHeight;
	
	public NullTexture(int width, int height) {
		this.width = width;
		this.height = height;
		this.texWidth = InternalTextureLoader.get2Fold(width);
		this.texHeight = InternalTextureLoader.get2Fold(height);
	}

	public boolean hasAlpha() {
		return true;
	}

	public String getTextureRef() {
		return null;
	}

	public void bind() {
	}

	public int getImageHeight() {
		return width;
	}

	public int getImageWidth() {
		return height;
	}

	public float getHeight() {
		return width/(float)texWidth;
	}

	public float getWidth() {
		return height/(float)texHeight;
	}

	public int getTextureHeight() {
		return texWidth;
	}

	public int getTextureWidth() {
		return texHeight;
	}

	public void release() {
	}

	public int getTextureID() {
		return 0;
	}

	public byte[] getTextureData() {
		return null;
	}

	public void setTextureFilter(int textureFilter) {
	}
}
