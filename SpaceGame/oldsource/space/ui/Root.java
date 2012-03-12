package space.ui;

import org.newdawn.slick.Graphics;

/**
 * A new class
 * @author Matt
 */
public class Root extends Widget {
    
    private Widget tooltip;
    
    public Root(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public boolean isClipping() {
        return false;
    }
    
    public void draw(Graphics g) {
        clearClipStack();
        super.draw(g);
    }
}
