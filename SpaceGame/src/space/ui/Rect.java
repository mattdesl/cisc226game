package space.ui;

/**
 * A new class
 * @author Matt
 */
public class Rect {
    public float x, y, w, h;
    
    public Rect(float x, float y, float w, float h) {
        set(x, y, w, h);
    }
    
    public void set(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    public String toString() {
        return "["+x+", "+y+", "+w+", "+h+"]";
    }
}
