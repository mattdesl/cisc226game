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
	

	protected int padLeft, padRight, padTop, padBottom, hAlign=ALIGN_CENTER, vAlign=ALIGN_CENTER;
	private float textWidth = 0, textHeight = 0; 
	private float xoff, yoff;
	
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
	public Label(AngelCodeFont font, String text) {
		super();
		this.font = font;
		this.text = text;
		textWidth = font.getWidth(text);
		textHeight = font.getLineHeight();//font.getHeight(text);
		setSize(textWidth, textHeight);
		
	}
	
	public void setText(String text) {
		setText(text, true);
	}
	
	public float getTextWidth() {
		return textWidth;
	}
	
	public float getTextHeight() {
		return textHeight;
	}
	
	public void setText(String text, boolean resizeAfter) {
		this.text = text;
		this.textWidth = font.getWidth(text);
		this.textHeight = font.getHeight(text);
		if (resizeAfter) {
			setSize(textWidth+padLeft+padRight, textHeight+padTop+padBottom);
		}
	}
	
	public void setTextOffset(float xoff, float yoff) {
		this.xoff = xoff;
		this.yoff = yoff;
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
				x += padLeft;
			else if (hAlign == ALIGN_RIGHT)
				x += getWidth()-textWidth-padRight;
			else if (hAlign == ALIGN_CENTER)
				x += getWidth()/2f - textWidth/2f;
			if (vAlign == ALIGN_TOP)
				y += padTop;
			else if (vAlign == ALIGN_BOTTOM)
				y += getHeight()-textHeight-padBottom;
			else if (vAlign == ALIGN_CENTER)
				y += getHeight()/2f - textHeight/2f;
			b.setColor(foreground);
			b.drawText(font, text, (int)x+xoff, (int)y+yoff);
		}
	}
}
