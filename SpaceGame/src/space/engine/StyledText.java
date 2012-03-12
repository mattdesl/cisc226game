package space.engine;

import java.util.ArrayList;
import java.util.List;

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
	
	public Group append(CharSequence text) {
		return append(text, null, null);
	}
	
	public Group append(CharSequence text, SpriteFont font) {
		return append(text, font, null);
	}
	
	public Group append(CharSequence text, Color color) {
		return append(text, null, color);
	}
	
	public Group append(CharSequence text, SpriteFont font, Color color) {
		Group g = new Group(text, font, color);
		groups.add(g);
		return g;
	}
	
	public void trimToSize() {
		groups.trimToSize();
	}
	
	public static class Group {
		
		public final CharSequence text;
		private SpriteFont font;
		private Color color;
		private float yoffset, height;
		private boolean yoffsetDirty = true, heightDirty = true;
		
		public Group(CharSequence text, SpriteFont font, Color color) {
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
		
		public CharSequence getText() {
			return text;
		}
		
		public SpriteFont getFont() {
			return font;
		}
		
		public void setFont(SpriteFont font) {
			if (this.font!=font)
				yoffsetDirty = true;
			this.font = font;
			
		}
	}
}