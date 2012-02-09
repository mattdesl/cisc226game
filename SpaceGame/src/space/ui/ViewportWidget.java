package space.ui;

/**
 * A new class
 * @author Matt
 */
public class ViewportWidget extends Widget {
    
    private float vx, vy;
    
    public void setViewX(float x) {
        this.vx = x;
    }
    
    public void setViewY(float y) {
        this.vy = y;
    }
    
    public void setViewPosition(float x, float y) {
        setViewX(x);
        setViewY(y);
    }
    
    /**
     * Adds the given position to the current position.
     * 
     * @param x
     * @param y 
     */
    public void translateView(float x, float y) {
        setViewPosition(getViewX()+x, getViewY()+y);
    }
    
    public float getViewX() { 
        return vx; 
    }
    
    public float getViewY() { 
        return vy; 
    }
}
