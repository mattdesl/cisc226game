package space.ui;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;


/**
 * A new class
 * @author Matt
 */
public class Widget {
	
	protected static ClipStack clipStack;

    protected static void clearClipStack() {
        clipStack.clear();
    }
    
    protected static void pushClip(Graphics g, Rect c) {
        clipStack.push(c);
        g.clearWorldClip();
        g.setWorldClip(c.x, c.y, c.w, c.h);
    }
    
    protected static Rect popClip(Graphics g) {
        clipStack.pop();
        Rect c = clipStack.peek();
        g.clearWorldClip();
        if (c!=null)
            g.setWorldClip(c.x, c.y, c.w, c.h);
        return c;
    }
    
	
    
    protected float x, y;
    protected float width, height;
    protected float hpad, vpad;
    protected ArrayList<Widget> children;
    protected float scaleX, scaleY, rotation;
    protected Rect clip = new Rect(0f,0f,0f,0f);
    
    protected String name;
    
    protected Widget parent;
    
    protected boolean visible = true;
    
    protected Image image;
    protected Color background;
    
    private boolean needsReorder = false;
    private boolean bringToFront = false;
    private ArrayList<Widget> widgetsToFront;
            
    public Widget(Color background) {
        this.background = background;
    }
    
    public Widget(Image image) {
        this.image = image;
        if (image!=null) {
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
    }
    
    public Widget() {
    }
    
    public Color getBackground() {
        return background;
    }
    
    public void setBackground(Color background) {
        this.background = background;
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
    
    protected float getScreenX() {
        return parent!=null ? x+parent.getScreenX()+parent.getViewX() : x;
    }
    
    protected float getScreenY() {
        return parent!=null ? y+parent.getScreenY()+parent.getViewY() : y;
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
    
    public Rect getClipBounds() {
        return clip;
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
    
    public void setPadding(float hpad, float vpad) {
        this.hpad = hpad;
        this.vpad = vpad;
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
    
    public boolean isClipping() {
        return true;
    }
    
    public void toFront() {
        if (parent!=null) {
            parent.needsReorder = true;
            if (parent.widgetsToFront==null)
                parent.widgetsToFront = new ArrayList<Widget>();
            parent.widgetsToFront.add(this);
        }
    }
    
    public void draw(Graphics g) {
        if (!isShowing())
            return;
        
        if (needsReorder) {
            for (int i=0; i<widgetsToFront.size(); i++) {
                Widget c = widgetsToFront.get(i);
                if (children.remove(c)) 
                    children.add(c);
            }
            widgetsToFront.clear();
            needsReorder = false;
        }
        
        float absX = getScreenX();
        float absY = getScreenY();
            
        boolean isClip = isClipping();
        clip.set(absX, absY, width, height);
        
        if (isClip) {
            //shrink the clip bounds based on parent
            if (parent!=null && parent.isClipping()) {
                Rect pclip = parent.clip;
                if (clip.x < pclip.x) {
                    clip.w = Math.max(0, clip.w + clip.x - pclip.x);
                    clip.x = pclip.x;
                } 
                if (clip.y < pclip.y) {
                    clip.h = Math.max(0, clip.h + clip.y - pclip.y);
                    clip.y = pclip.y;
                }
                if (clip.x+clip.w > pclip.x + pclip.w) {
                    clip.w = Math.max(0, pclip.x+pclip.w - clip.x);
                    clip.x = Math.min(clip.x, pclip.x+pclip.w);
                }
                if (clip.y+clip.h > pclip.y + pclip.h) {
                    clip.h = Math.max(0, pclip.y+pclip.h - clip.y);
                    clip.y = Math.min(clip.y, pclip.y+pclip.h);
                }
                //do another clip check now that it's changed
                if (clip.w==0||clip.h==0)
                    return;
            }
            pushClip(g, clip);
        }
        drawBackground(g, absX, absY);
        drawWidget(g, absX, absY);
        drawChildren(g);
        drawForeground(g, absX, absY);
        if (isClip)
            popClip(g);
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isShowing() {
        return getWidth()==0||getHeight()==0 //no size
                || (isClipping()&&(clip.w==0||clip.h==0)) //clipped completely
                || (visible && (parent==null || parent.isShowing())); //not visible
    }
    
    protected void drawChildren(Graphics g) {
        if (children!=null) {
            for (int i=0; i<childCount(); i++) {
                getChild(i).draw(g);
            }
        }
    }
    
    /**
     * Renders the background image and color if they have been specified. 
     * @param g
     * @param screenX
     * @param screenY 
     */
    protected void drawBackground(Graphics g, float screenX, float screenY) {
        if (background!=null) {
            g.setColor(background);
            g.fillRect(screenX, screenY, getWidth(), getHeight());
        }
        if (image!=null) {
            g.drawImage(image, screenX, screenY);
        }
    }
    
    protected void drawForeground(Graphics g, float screenX, float screenY) {
        g.setColor(new Color(1f, 1f, 1f, 0.75f));
        g.drawRect(screenX, screenY, getWidth(), getHeight());
    }
    
    /** 
     * Classes should override this for rendering ther widgets. 
     * @param g
     * @param screenX
     * @param screenY 
     */
    protected void drawWidget(Graphics g, float screenX, float screenY) {
        
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