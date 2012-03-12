package space.engine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import space.util.Utils;



/**
 * A font implementation that will parse BMFont format font files. The font
 * files can be output by Hiero, which is included with Slick, and also the
 * AngelCode font tool available at:
 * 
 * <a href="http://www.angelcode.com/products/bmfont/">http://www.angelcode.com/
 * products/bmfont/</a>
 * 
 * This implementation copes with both the font display and kerning information
 * allowing nicer looking paragraphs of text. Note that this utility only
 * supports the text BMFont format definition file.
 * 
 * @author kevin
 * @author davedes various modifications
 * @author Nathan Sweet <misc@n4te.com>
 */
public class SpriteFont {

	/** The highest character that BitmapFont will support. */
	private static final int MAX_CHAR = 255;
	
	/** The characters building up the font */
	private Glyph[] chars;
	/** The height of a line */
	private int lineHeight;
	private Image fontImage;
	
	private int hint = NO_HINT;
	private short ascent, descent, leading;
	
	public static final int NO_HINT = 0;
	public static final int CASE_INSENSITIVE = 1;
	
	public SpriteFont(String fontFile, Image fontImage) throws SlickException {
		this(fontFile, fontImage, NO_HINT);
	}
	
	public SpriteFont(String fontFile, Image fontImage, int hint) throws SlickException {
		this(Utils.getResourceAsStream(fontFile), fontImage, hint);
	}
	
	public SpriteFont(InputStream fontFile, Image fontImage) throws SlickException {
		this(fontFile, fontImage, NO_HINT);
	}
	
	
	/**
	 * Create a new font based on a font definition from AngelCode's tool and
	 * the font image generated from the tool.
	 * 
	 * @param name
	 *            The name to assign to the font image in the image store
	 * @param fntFile
	 *            The stream of the font defnition file
	 * @param imgFile
	 *            The stream of the font image
	 * @throws SlickException
	 *             Indicates a failure to load either file
	 */
	public SpriteFont(InputStream fontFile, Image fontImage, int hint) throws SlickException {
		this.fontImage = fontImage;
		this.hint = hint;
		try {
			// now parse the font file
			BufferedReader in = new BufferedReader(new InputStreamReader(
					fontFile));
			String info = in.readLine();
			String common = in.readLine();
			//baseline = parseMetric(common, "base="); //not used apparently
			ascent = parseMetric(common, "ascent=");
			descent = parseMetric(common, "descent=");
			leading = parseMetric(common, "leading=");
			
			String page = in.readLine();

			Map<Short, List<Short>> kerning = new HashMap<Short, List<Short>>(64);
			List<Glyph> charDefs = new ArrayList<Glyph>(MAX_CHAR);
			int maxChar = 0;
			boolean done = false;
			while (!done) {
				String line = in.readLine();
				if (line == null) {
					done = true;
				} else {
					if (line.startsWith("chars c")) {
						// ignore
					} else if (line.startsWith("char")) {
						Glyph def = parseChar(line);
						if (def != null) {
							maxChar = Math.max(maxChar, def.id);
							charDefs.add(def);
						}
					}
					if (line.startsWith("kernings c")) {
						// ignore
					} else if (line.startsWith("kerning")) {
						StringTokenizer tokens = new StringTokenizer(line, " =");
						tokens.nextToken(); // kerning
						tokens.nextToken(); // first
						short first = Short.parseShort(tokens.nextToken()); // first
																			// value
						tokens.nextToken(); // second
						int second = Integer.parseInt(tokens.nextToken()); // second
																			// value
						tokens.nextToken(); // offset
						int offset = Integer.parseInt(tokens.nextToken()); // offset
																			// value
						List<Short> values = kerning.get(first);
						if (values == null) {
							values = new ArrayList<Short>();
							kerning.put(first, values);
						}
						// Pack the character and kerning offset into a short.
						values.add((short) ((offset << 8) | second));
					}
				}
			}

			chars = new Glyph[maxChar + 1];
			for (Glyph def : charDefs) {
				chars[def.id] = def;
			}

			// Turn each list of kerning values into a short[] and set on the
			// chardef.
			for (Entry<Short, List<Short>> entry : kerning.entrySet()) {
				short first = entry.getKey();
				List<Short> valueList = entry.getValue();
				short[] valueArray = new short[valueList.size()];
				for (int i=0; i<valueList.size(); i++)
					valueArray[i] = valueList.get(i);
				chars[first].kerning = valueArray;
			}
		} catch (Exception e) {
			Log.error(e);
			throw new SlickException("Failed to parse font file: " + fontFile, e);
		}
	}

	private short parseMetric(String str, String sub) {
		int ind = str.indexOf(sub);
		if (ind!=-1) {
			String subStr = str.substring(ind+sub.length());
			ind = subStr.indexOf(' ');
			return Short.parseShort(subStr.substring(0, ind!=-1 ? ind : subStr.length()));
		}
		return -1;
	}
	
	/**
	 * @see org.newdawn.slick.Font#getLineHeight()
	 */
	public int getLineHeight() {
		return lineHeight;
	}
	
	public int getDescent() {
		return descent;
	}
	
	public int getAscent() {
		return ascent;
	}
	
	public int getLeading() {
		return leading;
	}
	
	public float getHeight(CharSequence text) {
		return getHeight(text, 0, text.length());
	}
	
	public float getHeight(CharSequence text, int startIndex, int endIndex) {
        int maxHeight = 0;
        for (int i = 0; i < text.length(); i++) {
                char id = text.charAt(i);
                if (id == '\n' || id == ' ') {
                    continue;
                }
                Glyph g = getGlyph(id);
                if (g==null)
                	continue;
                maxHeight = Math.max(g.height + g.yoffset, maxHeight);
        }
        return maxHeight;
	}
	
	public float getYOffset(CharSequence text) {
		return getYOffset(text, 0, text.length());
	}
	
	public float getYOffset(CharSequence text, int startIndex, int endIndex) {
		if (endIndex-startIndex==0) return 0;
		
		float minYOffset = Integer.MAX_VALUE;
		for ( ; startIndex < endIndex; startIndex++) {
			char id = text.charAt(startIndex);
			Glyph g = id==' '||id=='\n' ? null : getGlyph(id);
			if (g==null)
				continue;
			minYOffset = Math.min(g.yoffset, minYOffset);
		}
		return minYOffset;
	}
	
	/**
	 * Parse a single character line from the definition
	 * 
	 * @param line
	 *            The line to be parsed
	 * @return The character definition from the line
	 * @throws SlickException
	 *             Indicates a given character is not valid in an angel code
	 *             font
	 */
	private Glyph parseChar(String line) throws SlickException {
		StringTokenizer tokens = new StringTokenizer(line, " =");

		tokens.nextToken(); // char
		tokens.nextToken(); // id
		short id = Short.parseShort(tokens.nextToken()); // id value
		if (id < 0) {
			return null;
		}
		if (id > MAX_CHAR) {
			throw new SlickException("Invalid character '" + id
					+ "': SpriteFont does not support characters above "
					+ MAX_CHAR);
		}

		tokens.nextToken(); // x
		short x = Short.parseShort(tokens.nextToken()); // x value
		tokens.nextToken(); // y
		short y = Short.parseShort(tokens.nextToken()); // y value
		tokens.nextToken(); // width
		short width = Short.parseShort(tokens.nextToken()); // width value
		tokens.nextToken(); // height
		short height = Short.parseShort(tokens.nextToken()); // height value
		tokens.nextToken(); // x offset
		short xoffset = Short.parseShort(tokens.nextToken()); // xoffset value
		tokens.nextToken(); // y offset
		short yoffset = Short.parseShort(tokens.nextToken()); // yoffset value
		tokens.nextToken(); // xadvance
		short xadvance = Short.parseShort(tokens.nextToken()); // xadvance
		
		if (id != ' ') {
			lineHeight = Math.max(height + yoffset, lineHeight);
		}
		Image img = fontImage.getSubImage(x, y, width, height);
		Glyph def = new Glyph(id, x, y, width, height, xoffset, yoffset, xadvance, img);
		
		return def;
	}

	/**
	 * Returns the character definition for the given character. 
	 * 
	 * @param c the desired character
	 * @return the CharDef with glyph info
	 */
	public Glyph getGlyph(char c) {
		Glyph g = c<0 || c>= chars.length ? null : chars[c];
		if (g!=null)
			return g;
		
		if (g==null && hint==CASE_INSENSITIVE) {
			if (c>=65 && c<=90)
				c += 32;
			else if (c>=97 && c<=122)
				c -= 32;
		}
		return c<0 || c>= chars.length ? null : chars[c];
	}
	
	public Image getImage() {
		return fontImage;
	}
	
	/**
	 * The definition of a single character as defined in the AngelCode file
	 * format
	 * 
	 * @author kevin
	 */
	public static class Glyph {
		/** The id of the character */
		public final short id;
		/** The x location on the sprite sheet */
		public final short x;
		/** The y location on the sprite sheet */
		public final short y;
		/** The width of the character image */
		public final short width;
		/** The height of the character image */
		public final short height;
		/** The amount the x position should be offset when drawing the image */
		public final short xoffset;
		/** The amount the y position should be offset when drawing the image */
		public final short yoffset;
		/** The amount to move the current position after drawing the character */
		public final short xadvance;
		/** The sub image; will be null if fontImage was null at creation time. */
		public final Image image;

		/** The kerning info for this character */
		private short[] kerning;
		
		protected Glyph(short id, short x, short y, short width, short height,
				short xoffset, short yoffset, short xadvance, Image image) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.xoffset = xoffset;
			this.yoffset = yoffset;
			this.xadvance = xadvance;
			this.image = image;
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "[CharDef id=" + id + " x=" + x + " y=" + y + "]";
		}

		/**
		 * Get the kerning offset between this character and the specified
		 * character.
		 * 
		 * @param otherCodePoint
		 *            The other code point
		 * @return the kerning offset
		 */
		public int getKerning(int otherCodePoint) {
			if (kerning == null)
				return 0;
			int low = 0;
			int high = kerning.length - 1;
			while (low <= high) {
				int midIndex = (low + high) >>> 1;
				int value = kerning[midIndex];
				int foundCodePoint = value & 0xff;
				if (foundCodePoint < otherCodePoint)
					low = midIndex + 1;
				else if (foundCodePoint > otherCodePoint)
					high = midIndex - 1;
				else
					return value >> 8;
			}
			return 0;
		}
	}

}