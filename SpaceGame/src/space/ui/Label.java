package space.ui;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import space.engine.SpriteBatch;

public class Label extends Widget {

	private String text;
	private AngelCodeFont font;
	
	public static int ALIGN_TOP = 0;
	public static int ALIGN_RIGHT = 1;
	public static int ALIGN_BOTTOM = 2;
	public static int ALIGN_LEFT = 3;
	public static int ALIGN_CENTER = 4;
	

	private int hpad, vpad, hAlign=ALIGN_CENTER, vAlign=ALIGN_CENTER;
	private float textWidth = 0, textHeight = 0; 
	
	/**
	 * Sets label to image size.
	 * @param font
	 * @param text
	 * @param image
	 */
	public Label(AngelCodeFont font, String text, Image image) {
		this(image);
		this.font = font;
		this.text = text;
		textWidth = font.getWidth(text);
		textHeight = font.getHeight(text);
	}
	
	public Label(Image image) {
		super(image);
	}
	
	/**
	 * Sets label to text size + padding.
	 * @param font
	 * @param text
	 */
	public Label(AngelCodeFont font, String text, int hpad, int vpad) {
		super();
		this.font = font;
		this.text = text;
		this.hpad = hpad;
		this.vpad = vpad;
		setSize(font.getWidth(text)+hpad*2, font.getLineHeight()+vpad*2);
		textWidth = font.getWidth(text);
		textHeight = font.getHeight(text);
	}
	
	public Label(AngelCodeFont font, String text) {
		this(font, text, 0, 0);
	}
	
	public void setAlign(int hAlign, int vAlign) {
		this.hAlign = hAlign;
		this.vAlign = vAlign;
	}
	
	public AngelCodeFont getFont() {
		return font;
	}
	
	public String getText() {
		return text;
	}
	
	public void drawWidget(SpriteBatch b, Graphics g, float screenX, float screenY) {
		if (text!=null && text.length()!=0 && font!=null) {
			float x = screenX;
			float y = screenY;
			if (hAlign == ALIGN_LEFT)
				x += hpad;
			else if (hAlign == ALIGN_RIGHT)
				x += getWidth()-textWidth-hpad;
			else if (hAlign == ALIGN_CENTER)
				x += getWidth()/2f - textWidth/2f;
			if (vAlign == ALIGN_TOP)
				y += vpad;
			else if (vAlign == ALIGN_BOTTOM)
				y += getHeight()-textHeight-vpad;
			else if (vAlign == ALIGN_CENTER)
				y += getHeight()/2f - textHeight/2f;
			b.setColor(foreground);
			b.drawText(font, text, (int)x, (int)y);
		}
	}
}
