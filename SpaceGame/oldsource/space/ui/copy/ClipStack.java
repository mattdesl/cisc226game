package space.ui.copy;

import java.util.ArrayList;

/**
 * A new class
 * @author Matt
 */
public class ClipStack {
    
    private ArrayList<Rect> stack = new ArrayList<Rect>(50);
    
    private int next = 0;
    
    public Rect push(float x, float y, float w, float h) {
        return push(new Rect(x, y, w, h));
    }
    
    public Rect push(Rect r) {
        if (next > stack.size()-1)
            stack.add(r);
        else
            stack.set(next, r);
        next++;
        return r;
    }
    
    public Rect pop() {
        if (next==0)
            return null;
        return stack.get(--next);
    }
    
    public Rect peek() {
        if (next==0)
            return null;
        return stack.get(next-1);
    }
    
    public void clear() {
        next = 0;
    }
    
    public boolean empty() {
        return next == 0;
    }
    
    public int size() {
        return next;
    }
    
    public String toString() {
        String s = "";
        for (int i=0; i<size(); i++) {
            s += String.valueOf(stack.get(i)) + ", ";
        }
        return s;
    }
    
    public static void main(String[] args) {
        System.out.println("new stack");
        ClipStack s = new ClipStack();
        s.push(0, 0, 50, 50);
        
        s.push(50, 50, 50, 50);
        System.out.println(s);
        s.push(25, 25, 50, 50);
        System.out.println(s);
        s.clear();
        System.out.println(s.empty());
        System.out.println(s.peek());
    }
}
