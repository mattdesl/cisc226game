package space.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import net.phys2d.math.ROVector2f;

import org.lwjgl.Sys;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

import space.game.GameContext;

public class Utils {
	public static final File ROOT = new File(".");
	
    private static File createFile(String ref) {
        File file = new File(ROOT, ref);
        if (!file.exists()) {
            file = new File(ref);
        }
        
        return file;
    }
    

    private static final Random RND = new Random();
    
    public static int rnd() {
    	return RND.nextInt();
    }
    
    public static int rnd(int high) {
    	return RND.nextInt(high);
    }
    
    public static float rndFloat() {
    	return RND.nextFloat();
    }
    
    public static int rnd(int low, int high) {
        if (low==high)
            return low;
        return RND.nextInt(high - low) + low;
    }
    
    public static float rnd(float low, float high) {
        if (low==high)
            return low;
        return low + (RND.nextFloat() * (high - low));
    }
    
    public static float dist(ROVector2f a, ROVector2f b) {
        float dx = b.getX()-a.getX();
        float dy = b.getY()-a.getY();
        return (float)Math.sqrt( dx*dx + dy*dy );
    }
    
    public static void drawCentered(Image img, GameContext screen) {
        drawCentered(img, screen, 0, 0);
    }
    
    public static void drawCentered(Image img, GameContext screen, float x, float y) {
        drawCentered(img, screen.getWidth(), screen.getHeight(), x, y);
    }
    
    public static void drawCentered(Image img, float parentWidth, float parentHeight, float x, float y) {
        img.draw(x + parentWidth/2f-img.getWidth()/2f, y + parentHeight/2f-img.getHeight()/2f);
    }
    
    /** 
     * Tiles an image across a rectangular area.
     * 
     * @param image
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void textureInUse(Image image, float x, float y, float width, float height) {
    	float tilew = image.getWidth();
    	float tileh = image.getHeight();
    	
    	//e.g. 32 / 256
    	for (int r=0; r<width/tilew; r++) {
    		for (int c=0; c<height/tileh; c++) {
    			image.drawEmbedded(x+r*tilew, y+c*tileh, tilew, tileh);
    		}
    	}
    }
    
    public static void texture(Image image, float x, float y, float width, float height) {
    	image.startUse();
    	textureInUse(image, x, y, width, height);
    	image.endUse();
    }
    
    public static void error(String msg) {
    	error(msg, null);
    }
    
    public static void error(String msg, Throwable t) {
    	Sys.alert("Error", msg);
    	if (t!=null) Log.error(msg, t);
    	else Log.error(msg);
    }
    
    public static InputStream getResourceAsStream(String ref) {
        String cpRef = ref.replace('\\', '/');
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(cpRef);
        if (in==null) { // try file system
            try { return new FileInputStream(createFile(ref)); }
            catch (IOException e) {}
        }
        return in;
    }
    
    public static URL getResource(String ref) {
        String cpRef = ref.replace('\\', '/');
        URL url = Utils.class.getClassLoader().getResource(cpRef);
        if (url==null) {
            try { 
                File f = createFile(ref);
                if (f.exists())
                    return f.toURI().toURL();
            } catch (IOException e) {}
        }
        return url;
    }
}
