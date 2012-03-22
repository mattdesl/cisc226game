package space.ui;

public interface WidgetListener {
	public void onEnter(Widget widget);
	public void onExit(Widget widget);
	public boolean onMouseClick(Widget widget, int button, int x, int y, int clickCount);
	public boolean onMousePress(Widget widget, int button, int x, int y);
	public boolean onMouseRelease(Widget widget, int button, int x, int y);
	public void onMouseMove(Widget widget, int oldx, int oldy, int newx, int newy);
}
