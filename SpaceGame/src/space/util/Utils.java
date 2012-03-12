package space.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import net.phys2d.math.ROVector2f;

import org.lwjgl.Sys;
import org.newdawn.slick.util.Log;

public class Utils {
	public static final long DEFAULT_SEED = 1331106565117L;
	private static final Random RND = new Random();
    private static ResourceLoader resourceLocator = new DefaultResourceLoader();
    
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
    
    public static void error(String msg) {
    	error(msg, null);
    }
    
    public static void error(String msg, Throwable t) {
    	Sys.alert("Error", msg);
    	if (t!=null) Log.error(msg, t);
    	else Log.error(msg);
    }
    
    public static URL getResource(String str) {
    	return resourceLocator.getResource(str);
    }
    
    public static InputStream getResourceAsStream(String str) {
    	return resourceLocator.getResourceAsStream(str);
    }
    
    public static void setResourceLocator(ResourceLoader r) {
    	resourceLocator = r;
    }
    
    public static ResourceLoader getResourceLocator() {
    	return resourceLocator;
    }
	
    public static final class DefaultResourceLoader implements ResourceLoader {

    	public static final File ROOT = new File(".");
    	
        private static File createFile(String ref) {
            File file = new File(ROOT, ref);
            if (!file.exists()) {
                file = new File(ref);
            }
            
            return file;
        }
        
	    public InputStream getResourceAsStream(String ref) {
	        String cpRef = ref.replace('\\', '/');
	        InputStream in = Utils.class.getClassLoader().getResourceAsStream(cpRef);
	        if (in==null) { // try file system
	            try { return new FileInputStream(createFile(ref)); }
	            catch (IOException e) {}
	        }
	        if (in==null)
	        	Log.warn("could not find resource "+ref);
	        return in;
	    }
	    
	    public URL getResource(String ref) {
	        String cpRef = ref.replace('\\', '/');
	        URL url = Utils.class.getClassLoader().getResource(cpRef);
	        if (url==null) {
	            try { 
	                File f = createFile(ref);
	                if (f.exists())
	                    return f.toURI().toURL();
	            } catch (IOException e) {}
	        }
	        if (url==null)
	        	Log.warn("could not find resource "+ref);
	        return url;
	    }
    }
    
    public static interface ResourceLoader {
    	public URL getResource(String str);
    	public InputStream getResourceAsStream(String str);
    }
}
