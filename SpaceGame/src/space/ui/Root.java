package space.ui;

import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;

import space.GameContext;

/**
 * A new class
 * @author Matt
 */
public class Root extends Widget implements InputListener {
    
	Input input;
	Widget lastWidget;
	WidgetListener listener;
	
    public Root(GameContext context, WidgetListener listener) {
    	this.input = context.getInput();
    	input.addListener(this);
    	this.width = context.getWidth();
        this.height = context.getHeight();
        this.listener = listener;
    }
    
    public Widget getDeepestWidgetAt(int x, int y) {
    	return getDeepestWidgetAt(this, x, y);
    }
    
    public static Widget getDeepestWidgetAt(Widget parent, int x, int y) {
    	if (!parent.inside(x,  y))
    		return null;
    	
        for (int i=parent.childCount()-1; i>=0; i--) {
            Widget comp = parent.getChild(i);
            
            if (comp!=null && comp.isShowing()) {
                if (comp.inside(x, y)) {                    
                    return getDeepestWidgetAt(comp, x, y);
                }
            }
        }
    	return parent;
    }

	public void mouseWheelMoved(int change) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(int button, int x, int y, int clickCount) {
		if (lastWidget!=null)
			lastWidget.mouseInside = false;
		lastWidget = getDeepestWidgetAt(x, y);
		if (lastWidget!=null) {
			lastWidget.mouseInside = true;
			boolean consume = listener.onMouseClick(lastWidget, button, x, y, clickCount);
			if (consume)
				input.consumeEvent();
		}
	}

	public void mousePressed(int button, int x, int y) {
		if (lastWidget!=null)
			lastWidget.mouseInside = false;
		lastWidget = getDeepestWidgetAt(x, y);
		if (lastWidget!=null){
			lastWidget.mouseInside = true;
			boolean consume = listener.onMousePress(lastWidget, button, x, y);
			if (consume)
				input.consumeEvent();
		}
	}

	public void mouseReleased(int button, int x, int y) {
		if (lastWidget!=null)
			lastWidget.mouseInside = false;
		lastWidget = getDeepestWidgetAt(x, y);
		if (lastWidget!=null) {
			lastWidget.mouseInside = true;
			boolean consume = listener.onMouseRelease(lastWidget, button, x, y);
			if (consume)
				input.consumeEvent();
		}
	}

	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Widget oldWidget = lastWidget;
		Widget newWidget = getDeepestWidgetAt(newx, newy);
		if (oldWidget!=newWidget && oldWidget!=null) {
			if (oldWidget.mouseInside)
				listener.onExit(oldWidget);
			oldWidget.mouseInside = false;
		}
		lastWidget = newWidget;
		if (lastWidget!=null) {
			if (!lastWidget.mouseInside)
				listener.onEnter(lastWidget);
			lastWidget.mouseInside = true;
			listener.onMouseMove(lastWidget, oldx, oldy, newx, newy);
		}
	}

	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub
		
	}

	public void setInput(Input input) {
		this.input = input;
	}

	public boolean isAcceptingInput() {
		return true;
	}

	public void inputEnded() {
		// TODO Auto-generated method stub
		
	}

	public void inputStarted() {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(int key, char c) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(int key, char c) {
		// TODO Auto-generated method stub
		
	}

	public void controllerLeftPressed(int controller) {
		// TODO Auto-generated method stub
		
	}

	public void controllerLeftReleased(int controller) {
		// TODO Auto-generated method stub
		
	}

	public void controllerRightPressed(int controller) {
		// TODO Auto-generated method stub
		
	}

	public void controllerRightReleased(int controller) {
		// TODO Auto-generated method stub
		
	}

	public void controllerUpPressed(int controller) {
		// TODO Auto-generated method stub
		
	}

	public void controllerUpReleased(int controller) {
		// TODO Auto-generated method stub
		
	}

	public void controllerDownPressed(int controller) {
		// TODO Auto-generated method stub
		
	}

	public void controllerDownReleased(int controller) {
		// TODO Auto-generated method stub
		
	}

	public void controllerButtonPressed(int controller, int button) {
		// TODO Auto-generated method stub
		
	}

	public void controllerButtonReleased(int controller, int button) {
		// TODO Auto-generated method stub
		
	}
    
    
    
}
