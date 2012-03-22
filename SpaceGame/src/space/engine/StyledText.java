package space.engine;

import java.util.ArrayList;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;

public class StyledText {

	private ArrayList<Group> groups = new ArrayList<Group>();
	
	public StyledText() {
	}
	
	public Group getGroup(int index) {
		return groups.get(index);
	}
	
	public int getGroupCount() {
		return groups.size();
	}
	
	public Group append(String text) {
		return append(text, null, null);
	}
	
	public Group append(String text, AngelCodeFont font) {
		return append(text, font, null);
	}
	
	public Group append(String text, Color color) {
		return append(text, null, color);
	}
	
	public Group append(String text, AngelCodeFont font, Color color) {
		Group g = new Group(text, font, color);
		groups.add(g);
		return g;
	}
	
	public void trimToSize() {
		groups.trimToSize();
	}
	
	public static class Group {
		
		public final String text;
		private AngelCodeFont font;
		private Color color;
		private float yoffset, height;
		private boolean yoffsetDirty = true, heightDirty = true;
		
		public Group(String text, AngelCodeFont font, Color color) {
			this.text = text;
			this.font = font;
			this.color = color;
		}
		
		public float getHeight() {
			if (font==null)
				return 0;
			if (heightDirty) {
				height = font.getHeight(text);
				heightDirty = true;
			}
			return height;
		}
		
		public float getYOffset() {
			if (font==null)
				return 0;
			if (yoffsetDirty) {
				yoffset = font.getYOffset(text);
				yoffsetDirty = false;
			}
			return yoffset;
		}
		
		public Color getColor() {
			return color;
		}
		
		public void setColor(Color color) {
			this.color = color;
		}
		
		public String getText() {
			return text;
		}
		
		public AngelCodeFont getFont() {
			return font;
		}
		
		public void setFont(AngelCodeFont font) {
			if (this.font!=font)
				yoffsetDirty = true;
			this.font = font;
			
		}
	}
}