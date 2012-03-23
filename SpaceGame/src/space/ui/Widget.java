package space.ui;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import space.GameContext;
import space.engine.SpriteBatch;


/**
 * A new class
 * @author Matt
 */
public class Widget {
	
    protected float x, y;
    protected float width, height;
    protected ArrayList<Widget> children;
    
    protected Widget parent;
    protected boolean visible = true;
    
    protected Image image;
    
    protected Color foreground = new Color(1f,1f,1f,1f);
    protected Color imageFilter = new Color(1f,1f,1f,1f);
    boolean mouseInside = false;
	
    public Widget(Image image) {
        this.image = image;
        if (image!=null) {
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
    }
    
    public Widget() {
    }
    
    public Image getImage() {
        return image;
    }
    
    public void setImage(Image image) {
        this.image = image;
    }
    
    public float getViewX() { 
        return 0; 
    }
    
    public float getViewY() { 
        return 0; 
    }
    
    public float getAbsoluteX() {
        return parent!=null ? x+parent.getAbsoluteX()+parent.getViewX() : x;
    }
    
    public float getAbsoluteY() {
        return parent!=null ? y+parent.getAbsoluteY()+parent.getViewY() : y;
    }
    
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public void setWidth(float width) {
        this.width = width;
    }
    
    public void setHeight(float height) {
        this.height = height;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    
    public void translate(float x, float y) {
        setPosition(getX()+x, getY()+y);
    }
    
    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void draw(SpriteBatch batch, Graphics g) {
        if (!isShowing())
            return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
            
        drawBackground(batch, g, absX, absY);
        drawWidget(batch, g, absX, absY);
        drawChildren(batch, g);
        drawForeground(batch, g, absX, absY);
    }
    
    public boolean inside(int x, int y) {
    	float xp = getAbsoluteX();
    	float yp = getAbsoluteY();
    	float w = getWidth();
    	float h = getHeight();
    	return x > xp && y > yp && x <= xp+w && y <= yp+h;  
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isShowing() {
        return getWidth()==0||getHeight()==0 //no size
                || (visible && (parent==null || parent.isShowing())); //not visible
    }
    
    protected void drawChildren(SpriteBatch batch, Graphics g) {
        if (children!=null) {
            for (int i=0; i<childCount(); i++) {
                getChild(i).draw(batch, g);
            }
        }
    }
    
    /**
     * Renders the background image and color if they have been specified. 
     * @param g
     * @param screenX
     * @param screenY 
     */
    protected void drawBackground(SpriteBatch batch, Graphics g, float screenX, float screenY) {
        if (image!=null) {
        	batch.setColor(imageFilter);
            batch.drawImage(image, screenX, screenY);
        }
    }
    
    protected void drawForeground(SpriteBatch batch, Graphics g, float screenX, float screenY) {
//    	batch.flush();
//    	g.drawRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
    }
    
    /** 
     * Classes should override this for rendering ther widgets. 
     * @param g
     * @param screenX
     * @param screenY 
     */
    protected void drawWidget(SpriteBatch batch, Graphics g, float screenX, float screenY) {
        
    }
    
    public void update(GameContext context, int delta) {
    }
    
    public Widget getParent() {
        return parent;
    }
    
    public void add(Widget child) {
        if (child.parent!=null && child.parent!=this)
            throw new IllegalArgumentException("children can only have one parent");
        if (child.parent==this)
            return; //we've already added this child
        if (children==null)
            children = new ArrayList<Widget>();
        children.add(child);
        child.parent = this;
    }
    
    public boolean remove(Widget child) {
        if (children!=null) {
            boolean b = children.remove(child);
            if (b)
                child.parent = null;
        } 
        return false;
    }
	
	public Color getForeground() {
		return foreground;
	}
	
	public void setForeground(Color color) {
		this.foreground = color;
	}
    
    public int childCount() {
        return children!=null ? children.size() : 0;
    }
    
    public Widget getChild(int i) {
        return children!=null ? children.get(i) : null;
    }
    
    public Widget remove(int i) {
        if (children!=null) {
            Widget child = children.remove(i);
            if (child!=null)
                child.parent = null;
            return child;
        }
        return null;
    }
}