package space.engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.FastTrig;

/**
 * 
 * @author Matt DesLauriers (davedes)
 */
public class SpriteBatch {

	public static final int STRATEGY_DEFAULT = 2;
	public static final int STRATEGY_VBO = 4;
	
	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_RIGHT = 2;
	 
	public static boolean isVBOSupported() {
		return GLContext.getCapabilities().GL_ARB_vertex_buffer_object;
	}
	
	/**
	 * Whether to send the image data as GL_TRIANGLES
	 * or GL_QUADS. By default, GL_TRIANGLES is used.
	 * 
	 * @param b true to use triangle rendering
	 */
	public static void setUseTriangles(boolean b) {
		mode = b ? GL11.GL_TRIANGLES : GL11.GL_QUADS;
	}
	
	/**
	 * Returns whether to send the image data as GL_TRIANGLES
	 * or GL_QUADS. By default, GL_TRIANGLES is used.
	 * 
	 * @return true if we are using triangle rendering
	 */
	public static boolean isUseTriangles() {
		return mode==GL11.GL_TRIANGLES;
	}
	
	private static int mode = GL11.GL_TRIANGLES; 
	private final int TOLERANCE = 48; //we assume triangles is in use...
	
	private int idx = 0;
	private Texture texture;
	public int renderCalls = 0;
	
	private FloatBuffer vertices, colors, texcoords;
	private int maxVerts;
	private Color currentColor = Color.white;
	
	private int vboID, cboID, tboID;
	private int strategy = STRATEGY_DEFAULT;
	
	private float translateX, translateY;
	
	public SpriteBatch() {
		this(1000);
	}
	
	public SpriteBatch(int size) {
		this(size, STRATEGY_DEFAULT);
	}
	
	public SpriteBatch(int size, int strategy) {
		if (size<=0)
			throw new IllegalArgumentException("batch size must be larger than 0");
		this.strategy = strategy;
		this.maxVerts = size;
		vertices = BufferUtils.createFloatBuffer(size * 2);
		colors = BufferUtils.createFloatBuffer(size * 4);
		texcoords = BufferUtils.createFloatBuffer(size * 2);
//		
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);

    	// TODO: add VBO support
        if (strategy==STRATEGY_VBO) {
        	if (!isVBOSupported())
        		throw new UnsupportedOperationException("trying to use VBO with SpriteBatch when it's not supported");
        	vboID = GL15.glGenBuffers();
        	cboID = GL15.glGenBuffers();
        	tboID = GL15.glGenBuffers();
        	
        	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        	GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STREAM_DRAW);
        	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cboID);
        	GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colors, GL15.GL_STREAM_DRAW);
        	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tboID);
        	GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texcoords, GL15.GL_STREAM_DRAW);
        }
        
	}
	
	/**
	 * Returns the size of this ImageBatch as given in construction (default 1000).
	 * The internal array will have a capacity of size * 8.
	 * 
	 * A large internal array will require less calls to render(), but will take up
	 * more memory. 
	 * For example, an ImageBatch with a size of 6 would be ideal if we are only
	 * rendering a single image (made up of tris) within begin/end
	 * (six vertices, 8 bytes per vertex -- 2 for XY, 2 for texture UV, 4 for RGBA).
	 * 
	 * However, it's usually better to create a single large-size ImageBatch instance
	 * and re-use it throughout your game.
	 * 
	 * @return how many vertices to expect 
	 */
	public int getSize() {
		return maxVerts;
	}
	
	public void setColor(Color color) {
		this.currentColor = color;
	}
	
	public Color getColor() {
		return currentColor;
	}
	
	public void resetTranslation() {
		translateX = translateY = 0;
	}
	
	/** */
	public void translate(float x, float y) {
		translateX += x;
		translateY += y;
	}
	
	public void flush() {
		if (idx>0) 
			render();
		idx = 0;
		texture = null;
		
		vertices.clear();
		texcoords.clear();
		colors.clear();
	}
	
	/**
	 * Sends vertex, color and UV data to the GPU.
	 */
	protected void render() {
		if (idx==0) 
			return;
		renderCalls++;
		//bind the last texture
		if (texture!=null) 
			texture.bind();
		vertices.flip();
		colors.flip();
		texcoords.flip();

//
//	    vertices.rewind();
//	    colors.rewind();
//	    texcoords.rewind();
//	    
		
		if (strategy == STRATEGY_VBO) {
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        	GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STREAM_DRAW);
        	GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0L);
        	
        	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cboID);
        	GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colors, GL15.GL_STREAM_DRAW);
			GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0L);
        	
        	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tboID);
        	GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texcoords, GL15.GL_STREAM_DRAW);
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0L);
		} else {
			GL11.glVertexPointer(2, 0, vertices);
			GL11.glColorPointer(4, 0, colors);     
		    GL11.glTexCoordPointer(2, 0, texcoords);
		}
		
	    GL11.glDrawArrays(mode, 0, idx);
	    
	    if (strategy == STRATEGY_VBO)
	    	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		//GL31.glDrawArraysInstanced(mode, 0, idx, 1);
		
	    //texcoords2.clear();
//	    
	    vertices.clear();
	    colors.clear();
	    texcoords.clear();
	    idx = 0;
	}
	
	public void drawText(SpriteFont defaultFont, StyledText text, float x, float y) {
		SpriteFont.Glyph lastDef = null;
		SpriteFont lastFont = null;
		Color old = currentColor;
		
		float maxLineHeight = defaultFont.getLineHeight();
		float minY = text.getGroupCount()>0 ? Integer.MAX_VALUE : 0;
		float maxBaseline = 0;
		for (int gc=0; gc<text.getGroupCount(); gc++) {
			StyledText.Group g = text.getGroup(gc);
			if (g.getFont()!=null) {
				maxLineHeight = Math.max(maxLineHeight, g.getFont().getLineHeight());
				minY = Math.min(minY, g.getYOffset());
				maxBaseline = Math.max(maxBaseline, g.getFont().getAscent());
			} else {
				minY = Math.min(minY, defaultFont.getYOffset(g.getText()));
				maxBaseline = Math.max(maxBaseline, defaultFont.getAscent());
			}
			
		}
		
		for (int gc=0; gc<text.getGroupCount(); gc++) {
			StyledText.Group g = text.getGroup(gc);
			SpriteFont newFont = g.getFont()!=null ? g.getFont() : defaultFont;
			Color newColor = g.getColor()!=null ? g.getColor() : old;
			CharSequence newStr = g.getText();
			//TODO: clean up this method
			float minYOff = g.getFont()==null ? defaultFont.getYOffset(newStr) : g.getYOffset();
			float height = g.getFont()==null ? defaultFont.getHeight(newStr) : g.getHeight();
			float baseline = g.getFont()==null ? defaultFont.getAscent() : g.getFont().getAscent();
			float descent = g.getFont()==null ? defaultFont.getDescent() : g.getFont().getDescent();
			float yoff = maxBaseline - baseline;
			
			if (newFont!=lastFont) { //reset the last glyph
				lastDef = null;
			}
			
			for (int i=0; i<newStr.length(); i++) {
				char c = newStr.charAt(i);
				SpriteFont.Glyph def = newFont.getGlyph(c);
				if (def==null)
					continue;
				if (lastDef!=null) 
					x += lastDef.getKerning(c);
				lastDef = def;
				setColor(newColor);
				drawImage(def.image, x + def.xoffset, y + def.yoffset + yoff - minY);
				x += def.xadvance;
			}
		}
		setColor(old);
	}
	
	private void drawTextImpl(SpriteFont font, CharSequence text, float x, float y, 
			int startIndex, int endIndex, boolean multiLine) {
		SpriteFont.Glyph lastDef = null;
		
		float startX = x;
		for (; startIndex < endIndex; startIndex++) {
			char c = text.charAt(startIndex);
			if (multiLine && c=='\n') {
				y += font.getLineHeight();
				x = startX;
			}
			SpriteFont.Glyph def = font.getGlyph(c);
			if (def==null)
				continue;
			if (lastDef!=null) 
				x += lastDef.getKerning(c);
			lastDef = def;
			drawImage(def.image, x + def.xoffset, y + def.yoffset);
			x += def.xadvance;
		}
	}
	
	public void drawTextMultiLine(SpriteFont font, CharSequence text, float x, float y) {
		drawTextImpl(font, text, x, y, 0, text.length(), true);
	}
	
	public void drawTextMultiLine(SpriteFont font, CharSequence text, 
			float x, float y, int startIndex, int endIndex) {
		drawTextImpl(font, text, x, y, startIndex, endIndex, true);
	}
	
	public void drawText(SpriteFont font, CharSequence text, float x, float y) {
		drawTextImpl(font, text, x, y, 0, text.length(), false);
	}
	
	public void drawText(SpriteFont font, CharSequence text, float x, float y, int startIndex, int endIndex) {
		drawTextImpl(font, text, x, y, startIndex, endIndex, false);
	}
	
	public void drawImageScaled(Image image, float x, float y, float scale) {
		drawImage(image, x, y, image.getWidth()*scale, image.getHeight()*scale);
	}
	
	public void drawImage(Image image) {
		drawImage(image, 0, 0);
	}
	
	public void drawImage(Image image, float x, float y) {
		drawImage(image, x, y, null);
	}

	public void drawImage(Image image, float x, float y, Color[] corners) {
		drawImage(image, x, y, image.getWidth(), image.getHeight(), corners);
	}
	
	public void drawImage(Image image, float x, float y, float w, float h) {
		drawImage(image, x, y, w, h, null);
	}

	public void drawImage(Image image, float x, float y, float rotation) {
		drawImage(image, x, y, rotation, image.getWidth(), image.getHeight(), null);
	}
	
	public void drawImage(Image image, float x, float y, float rotation, float w, float h, Color[] corners) {
		if (rotation==0) {
			drawImage(image, x, y, w, h, corners);
			return;
		}
		
		checkRender(image);
		
		float scaleX = w/image.getWidth();
		float scaleY = h/image.getHeight();
		
		float cx = image.getCenterOfRotationX()*scaleX;
		float cy = image.getCenterOfRotationY()*scaleY;

		float p1x = -cx;
		float p1y = -cy;
		float p2x = w - cx;
		float p2y = -cy;
		float p3x = w - cx;
		float p3y = h - cy;
		float p4x = -cx;
		float p4y = h - cy;

		double rad = Math.toRadians(rotation);
		final float cos = (float) FastTrig.cos(rad);
		final float sin = (float) FastTrig.sin(rad);

		float tx = image.getTextureOffsetX();
		float ty = image.getTextureOffsetY();
		float tw = image.getTextureWidth();
		float th = image.getTextureHeight();

		float x1 = (cos * p1x - sin * p1y) + cx; // TOP LEFT
		float y1 = (sin * p1x + cos * p1y) + cy;
		float x2 = (cos * p2x - sin * p2y) + cx; // TOP RIGHT
		float y2 = (sin * p2x + cos * p2y) + cy;
		float x3 = (cos * p3x - sin * p3y) + cx; // BOTTOM RIGHT
		float y3 = (sin * p3x + cos * p3y) + cy;
		float x4 = (cos * p4x - sin * p4y) + cx; // BOTTOM LEFT
		float y4 = (sin * p4x + cos * p4y) + cy;
		drawQuadElement(x+x1, y+y1, tx, ty, corners!=null ? corners[0] : null,
				 		x+x2, y+y2, tx+tw, ty, corners!=null ? corners[1] : null,
				 		x+x3, y+y3, tx+tw, ty+th, corners!=null ? corners[2] : null,
				 		x+x4, y+y4, tx, ty+th, corners!=null ? corners[3] : null);
	}
	
	public void drawImage(Image image, float x, float y, float w, float h, Color[] corners) {
		checkRender(image);
		float tx = image.getTextureOffsetX();
		float ty = image.getTextureOffsetY();
		float tw = image.getTextureWidth();
		float th = image.getTextureHeight();
		drawImage(image, x, y, w, h, tx, ty, tw, th, corners);
	}

	public void drawSubImage(Image image, float srcx, float srcy,
			float srcwidth, float srcheight, float x, float y) {
		drawSubImage(image, srcx, srcy, srcwidth, srcheight, x, y, srcwidth, srcheight);
	}
	
	public void drawSubImage(Image image, float srcx, float srcy,
			float srcwidth, float srcheight, float x, float y, float w, float h) {
		drawSubImage(image, srcx, srcy, srcwidth, srcheight, x, y, w, h, null);
	}

	public void drawSubImage(Image image, float srcx, float srcy,
			float srcwidth, float srcheight, float x, float y, float w,
			float h, Color[] corners) {
		checkRender(image);
		float iw = image.getWidth();
		float ih = image.getHeight();
		float tx = (srcx / iw * image.getTextureWidth()) + image.getTextureOffsetX();
		float ty = (srcy / ih * image.getTextureHeight()) + image.getTextureOffsetY();
		float tw = w / iw * image.getTextureWidth();
		float th = h / ih * image.getTextureHeight();
		drawQuadElement(x, y, tx, ty, corners != null ? corners[0] : null, x
				+ w, y, tx + tw, ty, corners != null ? corners[1] : null,
				x + w, y + h, tx + tw, ty + th, corners != null ? corners[2]
						: null, x, y + h, tx, ty + th,
				corners != null ? corners[3] : null);
	}
	
	public void drawImage(Image image, float x, float y, float width, float height, 
					float u, float v, float uWidth, float vHeight, Color[] corners) {
		checkRender(image);
		drawQuadElement(x, y, u, v, corners!=null ? corners[0] : null,
				 		x+width, y, u+uWidth, v, corners!=null ? corners[1] : null,
				 		x+width, y+height, u+uWidth, v+vHeight, corners!=null ? corners[2] : null,
				 		x, y+height, u, v+vHeight, corners!=null ? corners[3] : null);
	}
	
	/**
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param points
	 * @param texcoords a texcoord for each vertex (8 elements 
	 * @param offset
	 * @param corners
	 */
	public void drawImage(Image image, float x, float y, float[] points, 
			float[] texcoords, int offset, int texcoordsOffset, Color[] corners) {
		checkRender(image);
		float x1 = points[offset++];
		float y1 = points[offset++];
		float x2 = points[offset++];
		float y2 = points[offset++];
		float x3 = points[offset++];
		float y3 = points[offset++];
		float x4 = points[offset++];
		float y4 = points[offset++];
		
		float u1 = texcoords[texcoordsOffset++];
		float v1 = texcoords[texcoordsOffset++];
		float u2 = texcoords[texcoordsOffset++];
		float v2 = texcoords[texcoordsOffset++];
		float u3 = texcoords[texcoordsOffset++];
		float v3 = texcoords[texcoordsOffset++];
		float u4 = texcoords[texcoordsOffset++];
		float v4 = texcoords[texcoordsOffset++];
		drawQuadElement(x+x1, y+y1, u1, v1, corners!=null ? corners[0] : null,
				 		x+x2, y+y2, u2, v2, corners!=null ? corners[1] : null,
				 		x+x3, y+y3, u3, v3, corners!=null ? corners[2] : null,
				 		x+x4, y+y4, u4, v4, corners!=null ? corners[3] : null);
	}
	
	private void checkRender(Image image) {
		if (image==null || image.getTexture()==null)
			throw new NullPointerException("null texture");
		
		//we need to bind a different texture. this is
		//for convenience; ideally the user should order
		//their rendering wisely to minimize texture binds	
		if (image.getTexture()!=texture) {
			//apply the last texture
			render();
			texture = image.getTexture();
		} else if (idx >= maxVerts - 4) 
			render();
	}
	
	/**
	 * Specifies vertex data.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @param u the U texcoord
	 * @param v the V texcoord
	 * @param color the color for this vertex
	 */
	protected void vertex(float x, float y, float u, float v, Color color) {
		vertices.put(x);
		vertices.put(y);
		texcoords.put(u);
		texcoords.put(v);
		Color c = color!=null ? color : currentColor;
		colors.put(c.r);
		colors.put(c.g);
		colors.put(c.b);
		colors.put(c.a);
		idx++;
	}
	
	/**
	 * Draws a quad-like element using either GL_QUADS or GL_TRIANGLES, depending
	 * on this batch's configuration.
	 */
	protected void drawQuadElement(
						float x1, float y1, float u1, float v1, Color c1,   //TOP LEFT 
						float x2, float y2, float u2, float v2, Color c2,   //TOP RIGHT
						float x3, float y3, float u3, float v3, Color c3,   //BOTTOM RIGHT
						float x4, float y4, float u4, float v4, Color c4) { //BOTTOM LEFT
		x1 += translateX;
		y1 += translateY;
		x2 += translateX;
		y2 += translateY;
		x3 += translateX;
		y3 += translateY;
		x4 += translateX;
		y4 += translateY;
		if (mode == GL11.GL_TRIANGLES) {
			//top left, top right, bottom left
			vertex(x1, y1, u1, v1, c1);
			vertex(x2, y2, u2, v2, c2);
			vertex(x4, y4, u4, v4, c4);
			//top right, bottom right, bottom left
			vertex(x2, y2, u2, v2, c2);
			vertex(x3, y3, u3, v3, c3);
			vertex(x4, y4, u4, v4, c4);
		} else {
			//quads: top left, top right, bottom right, bottom left
			vertex(x1, y1, u1, v1, c1);
			vertex(x2, y2, u2, v2, c2);
			vertex(x3, y3, u3, v3, c3);
			vertex(x4, y4, u4, v4, c4);
		}
	}
}