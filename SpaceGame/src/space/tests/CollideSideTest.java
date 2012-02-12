package space.tests;


import java.util.ArrayList;
import java.util.List;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

/**
 * A new class
 * @author Matt
 */
public class CollideSideTest extends BasicGame {
    CollideSideTest() { super("asdgs"); }
    public static void main(String[] args) throws SlickException {
        new AppGameContainer(new CollideSideTest(), 800, 600, false).start();
    }
    
    Sprite player;
    List<Sprite> entities; 
    final int TILESIZE = 32;
    
    World world = new World(new Vector2f(0, 5f), 10);
    int worldUpdateInterval = 5;
    int counter = 0;
    
    class Player extends Sprite {
        
        private final float SPEED = .05f;
        
        
        Player(World world, Image img, float x, float y) {
            super(world, img, x, y);
        }
        
        protected Body createBody(float w, float h) {
            return new Body(new Box(w, h), 10f);
        }
        
        
        public void update(GameContainer c, int delta) {
            float vy = body.getVelocity().getY();
            //System.out.println(vy);
            if (vy < 1) {
                if (c.getInput().isKeyDown(Input.KEY_A)) {
                    body.adjustVelocity(new Vector2f(-SPEED*delta, 0f));
                } else if (c.getInput().isKeyDown(Input.KEY_D)) {
                    body.adjustVelocity(new Vector2f(SPEED*delta, 0f));
                    
                } 
                if (c.getInput().isKeyDown(Input.KEY_SPACE)) {
                    //if (vy < 10)
                        body.adjustVelocity(new Vector2f(0f, -SPEED*2*delta));
                }
                
                //System.out.println(body.getVelocity().getY());
            }
            
        }
    }

    class Sprite {
        Image img;
        Body body;
        float w, h;
        
        Sprite(World world, Image img, float x, float y, float w, float h) {
            this.img = img;
            this.w = w;
            this.h = h;
            body = createBody(w, h);
            body.setPosition(x, y);
            body.setRotatable(false);
            world.add(body);
        }
        
        Sprite(World world, Image img, float x, float y) {
            this(world, img, x, y, img.getWidth(), img.getHeight());
        }
        
        protected Body createBody(float w, float h) {
            return new StaticBody(new Box(w, h));
        }
        
        public void draw(Graphics g) {
            ROVector2f v = body.getPosition();
            float vx = v.getX()-w/2f;
            float vy = v.getY()-h/2f;
            
            //quick tiling hack
            img.startUse();
            for (int r=0; r<w; r+=TILESIZE)
                for (int c=0; c<h; c+=TILESIZE)
                    img.drawEmbedded(vx+r, vy+c, img.getWidth(), img.getHeight());
            img.endUse();
            g.setColor(Color.red);
            g.drawRect(vx, vy, w, h);
        }
        
        public void update(GameContainer c, int delta) {
        }
    }
    
    private Image ui;
    
    public void init(GameContainer container) throws SlickException {
        //container.getGraphics().setBackground(Color.lightGray);
        container.setSmoothDeltas(true);
        
        //world.enableRestingBodyDetection(0.01f, 0.000001f, 0.01f);
        
        float x = container.getWidth()/2f;
        float y = container.getHeight()*.65f;
        
        entities = new ArrayList<Sprite>();
        
        
        float px = x+TILESIZE/2f, py=y-TILESIZE*3;
        entities.add(player = new Player(world, new Image("res/mario.png"), px, py));
        
        Image img = new Image("res/box1.png");
        Image img2 = new Image("res/box2.png");
        
        entities.add(new Sprite(world, img, x, y, 5*TILESIZE, TILESIZE));
        
        x -=  5*TILESIZE/2f;
        y -= TILESIZE*2;
        entities.add(new Sprite(world, img2, x+2*TILESIZE, y));
        entities.add(new Sprite(world, img2, x+4*TILESIZE, y));
        
        
        ui = new Image("res/ui.png");
    }

    public void update(GameContainer container, int delta) throws SlickException {
        for (int i=0; i<entities.size(); i++)
            entities.get(i).update(container, delta);
        counter += delta;
        while (counter > worldUpdateInterval) {
            world.step(worldUpdateInterval * 0.01f);
            counter -= worldUpdateInterval;
        }
    }
    private float r = 0;

    public void render(GameContainer container, Graphics g) throws SlickException {
        for (int i=0; i<entities.size(); i++)
            entities.get(i).draw(g);
    }
    
    
    public static void drawCentered(Image img, GameContainer screen) {
        drawCentered(img, screen, 0, 0);
    }
    
    public static void drawCentered(Image img, GameContainer screen, float x, float y) {
        drawCentered(img, screen.getWidth(), screen.getHeight(), x, y);
    }
    
    public static void drawCentered(Image img, float parentWidth, float parentHeight, float x, float y) {
        img.draw(x + parentWidth/2f-img.getWidth()/2f, y + parentHeight/2f-img.getHeight()/2f);
    }
}
