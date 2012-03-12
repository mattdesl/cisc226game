package space.engine.test;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class GameTest2 extends BasicGame {
	

    public GameTest2() {
        super("slick test");
    }

    public static void main(String[] args) throws SlickException {
    	new AppGameContainer(new GameTest2(), 800, 600, false).start();
    }
	
	private Image sv, hue;
	private Color hueColor = new Color(Color.red);
	private Color output = new Color(Color.red);
	private boolean over = false;
	private float[] hsv = { 0f, 1f, 1f };
	private float hueVal = 1f;
	
	public void init(GameContainer c) throws SlickException {
		c.setShowFPS(false);
		//c.setClearEachFrame(false);
		c.getGraphics().setBackground(Color.lightGray);
		
		sv = new Image("res/svPane.png");
		hue = new Image("res/huePane.png");
	}
	
	public void render(GameContainer c, Graphics g) throws SlickException {
		g.setColor(hueColor);
		g.fillRect(50, 50, sv.getWidth(), sv.getHeight());
		sv.draw(50, 50);
		hue.draw(225, 50);
		
		if (over) {
			g.setColor(output);
			g.fillRect(50, 250, 50, 50);
		}
	}
	
	public void update(GameContainer c, int delta) throws SlickException {
		int mx = c.getInput().getMouseX();
		int my = c.getInput().getMouseY();
		over = false;
		if (mx >= 225 && mx <= 225+hue.getWidth() && my >= 50 && my <=50+hue.getHeight()) {
			float perc = 1 - ((my-50) / (float)hue.getHeight());
			System.out.println(perc*360);
			hsv[0] = hueVal = perc * 360;
			hsv[1] = hsv[2] = 1f;
			HSVtoRGB(hsv, hueColor);
		} else if (mx >= 50 && mx <= 50+sv.getWidth() && my >= 50 && my <= 50+sv.getHeight()) {
			float sat = ((mx-50) / (float)sv.getWidth());
			float val = 1 - ((my-50) / (float)sv.getHeight());
			hsv[0] = hueVal;
			hsv[1] = sat;
			hsv[2] = val;
			//System.out.println();
			HSVtoRGB(hsv, output);
			over = true;
		}
	}
	
	
	
    public static void HSVtoRGB(float[] hsv, Color rgbOut) {
        //http://www.cs.rit.edu/~ncs/color/t_convert.html
        float h=hsv[0], s=hsv[1], v=hsv[2];
        
        int i;
        float r, g, b;
        float f, p, q, t;
        if (s==0) {
            // achromatic (grey)
            r = g = b = v;
        } else {
            h /= 60; // sector 0 to 5
            i = (int)h;
            f = h - i; // factorial part of h
            p = v * (1 - s);
            q = v * (1 - s * f);
            t = v * (1 - s * (1-f));
            switch (i) {
                case 0:
                    r = v;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = v;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = v;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = v;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = v;
                    break;
                default:
                    r = v;
                    g = p;
                    b = q;
                    break;
            }
        }
        rgbOut.r = r;
        rgbOut.g = g;
        rgbOut.b = b;
    }
}
