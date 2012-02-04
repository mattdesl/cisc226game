package space.tests;

import java.io.File;
import java.util.Random;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author Matt
 */
public class SimpleTest extends BasicGame {
    
    
    public SimpleTest() {
        super("Space Test");
    }
    
    private Image atmos, ship;
    private float atmosAlpha = 0.3f;
    private float mult = 1, starMult = 1;
//    private float rot;
    private float x=50, y=50;
    private float turnSpeed = 0.02f;
    private float moveSpeed = 0.5f;
    
    private float atmosRot = 0;
    
    private Star[] stars = new Star[4];
    private Image starMap;
    Random rnd = new Random();
    private Color starTint = new Color(1f,1f,1f,1f);
    private float starRot;
    
    private Image starCache;
    private Graphics starGraphics;
    private GameContainer container;
    private float starX = 0f;
    private float starY = 0f;
    
    
    
    private class Star {
        float x, y;
        Image img;
        
        public Star(GameContainer c, Image sheet, int x, int y, int w, int h) {
            img = sheet.getSubImage(x, y, w, h);
            this.x = rnd.nextInt(c.getWidth())-w/2f;
            this.y = rnd.nextInt(c.getHeight())-w/2f;
        }
    }
    
    public void init(GameContainer container) throws SlickException {
        this.container = container;
        container.getGraphics().setBackground(Color.black);
        //container.setSmoothDeltas(true);
        //container.setAlwaysRender(true);
        
        atmos = new Image("res/atmos.png");
        atmos.setAlpha(0.3f);
        ship = new Image("res/ship.png");
        
        starMap = new Image("res/stars.png");
        stars[0] = new Star(container, starMap, 0, 0, 512, 512);
        stars[1] = new Star(container, starMap, 512, 0, 512, 512);
        stars[2] = new Star(container, starMap, 512, 512, 512, 512);
        stars[3] = new Star(container, starMap, 0, 512, 512, 512);
        starRot = rnd.nextFloat() * 360;
        
        starCache = new Image(container.getWidth(), container.getHeight());
        Texture old = starCache.getTexture();
        starGraphics = starCache.getGraphics();
        starGraphics.setBackground(Color.black);
        recreateStars();
        old.release();
    }
    
    public void update(GameContainer container, int delta) throws SlickException {
        this.container = container;
        if (container.getInput().isKeyDown(Input.KEY_LEFT))
            ship.setRotation(ship.getRotation() + turnSpeed * delta);
        else if (container.getInput().isKeyDown(Input.KEY_LEFT))
            ship.setRotation(ship.getRotation() - turnSpeed * delta);
        
        if (container.getInput().isKeyDown(Input.KEY_A))
            x -= moveSpeed * delta;
        else if (container.getInput().isKeyDown(Input.KEY_D))
            x += moveSpeed * delta;
        if (container.getInput().isKeyDown(Input.KEY_S))
            y += moveSpeed * delta;
        else if (container.getInput().isKeyDown(Input.KEY_W))
            y -= moveSpeed * delta;
        
        atmos.setAlpha(atmos.getAlpha()+mult*0.0000025f);
        
        if (starTint.a < 0.2f) {
            starMult *= -1;
        } else if (starTint.a > 1f) {
            starTint.a = 1f;
            starMult *= -1;
        }
        
        if (atmos.getAlpha() > 0.4f)
            mult *= -1;
        else if (atmos.getAlpha() < 0.25f)
            mult *= -1;
        
        atmosRot += delta * 0.0025f;
        
    }
    
    private void recreateStars() {
        int w=container.getWidth(), h=container.getHeight();
        float cx=w/2f, cy=h/2f;
        
        starGraphics.clear();
        //starGraphics.setBackground(Color.black);
        //starGraphics.fillRect(0, 0, starCache.getWidth(), starCache.getHeight());
        starGraphics.rotate(cx, cy, rnd.nextFloat()*360);
        starGraphics.setDrawMode(Graphics.MODE_ADD);
        starMap.startUse();
        
        
        for (int i=0; i<4; i++) {
            float x = rnd.nextInt(w)-256;
            float y = rnd.nextInt(h)-256;
            
            GL11.glColor4f(1f, 1f, 1f, starTint.a);
            stars[i].img.drawEmbedded(x, y, 512, 512);
        }
        
        starMap.endUse();
        starGraphics.setDrawMode(Graphics.MODE_NORMAL);
        starGraphics.flush();
    }
    
    public void render(GameContainer container, Graphics g) throws SlickException {
        float cx=container.getWidth()/2f, cy=container.getHeight()/2f;
        
        //GL11.glDisable(GL11.GL_BLEND);
        System.out.println(starX);
        
        
        starCache.startUse();
        starCache.drawEmbedded(starX, starY, starCache.getWidth(), starCache.getHeight());
        starCache.endUse();
        //GL11.glEnable(GL11.GL_BLEND);
        
        
        
        g.rotate(cx, cy, atmosRot);
        atmos.draw(-container.getWidth()/2f, -container.getHeight()/2f, container.getWidth()*2, container.getHeight()*2);
        g.resetTransform();
        ship.draw(cx, cy);
        
        g.drawString("Total textures: "+InternalTextureLoader.totalTextures, x, y);
        
        //drawImage3D(container, hud, 0f, 0f, -4f, 1f, 1f, 1f, 0, rot, 0, null);
        
    }
    
    public void drawImage3D(GameContainer container, Image image,
                float worldX, float worldY, float worldZ, 
                float scaleX, float scaleY, float scaleZ,
                float xTilt, float yTilt, float zTilt, Color filter) {
        SlickCallable.enterSafeBlock();
        
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        
        GLU.gluPerspective(45.0f,container.getWidth()/(float)container.getHeight(),0.1f,100);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClearDepth(1.0f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        
        GL11.glLoadIdentity();
        
        float x = 0;
        float y = 0;
        float width = 1f;
        float height = -image.getHeight()/(float)image.getWidth();
        GL11.glTranslatef(worldX, worldY, worldZ);
        GL11.glScalef(scaleX, scaleY, scaleZ);
        GL11.glRotatef(xTilt, 1f, 0, 0);
        GL11.glRotatef(yTilt, 0f, 1f, 0);
        GL11.glRotatef(zTilt, 0f, 0f, 1f);
        
        image.bind();
        
        float textureOffsetX = image.getTextureOffsetX();
        float textureOffsetY = image.getTextureOffsetY();
        float textureWidth = image.getTextureWidth();
        float textureHeight = image.getTextureHeight();
        
        if (filter!=null)
            filter.bind();
        else
            Color.white.bind();
        GL11.glBegin(GL11.GL_QUADS);
        //TOP LEFT
        GL11.glTexCoord2f(textureOffsetX, textureOffsetY);
        GL11.glVertex3f(x, y, 0);
        //BOTTOM LEFT
        GL11.glTexCoord2f(textureOffsetX, textureOffsetY + textureHeight);
        GL11.glVertex3f(x, y + height, 0);
        //BOTTOM RIGHT
        GL11.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY
                        + textureHeight);
        GL11.glVertex3f(x + width, y + height, 0);
        //TOP RIGHT
        GL11.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY);
        GL11.glVertex3f(x + width, y, 0); 
        GL11.glEnd();
        
        SlickCallable.leaveSafeBlock();
    }
    
    
    public void keyPressed(int k, char c) {
        if (c=='1')
            recreateStars();
    }

    public static void main(String[] argv) {
        System.out.println(System.getProperty("java.library.path"));
        System.out.println(new File(System.getProperty("java.library.path")).getAbsoluteFile());
        System.out.println(ResourceLoader.getResource(System.getProperty("java.library.path")+"/openal.dylib"));
        
        try {
            AppGameContainer container = new AppGameContainer(new SimpleTest());
            container.setDisplayMode(800, 600, false);
            container.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
