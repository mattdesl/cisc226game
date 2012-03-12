package space.game.copy;

import java.util.Random;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.StateBasedGame;

import space.util.NullTexture;

public class GameExample extends SpaceGameState {

    public GameExample(GameContext context) {
		super(context, 1);
	}

	int worldUpdateInterval = 5;
    int counter = 0;
    Body ship;
    Image shipImg, shipImg2;
    World world = new World(new Vector2f(0, 0), 10);
    
    private float dirX, dirY, ang;
    //private final float MOVE_SPEED = 0.015f;
    private final float MOVE_SPEED = 3f;
    private final float TURN_SPEED = 0.25f;
    private final float STRAFE_SPEED = 4f;
    private final float MAX_SPEED = Float.MAX_VALUE;
    
    private Image atmos;
    private float atmosRot = 0;
    private Star[] stars;
    private Image starMap;
    private Color atmosFilter = new Color(1f, 1f, 1f, 0.9f);
    
    private Image shipStrafeLeft, shipStrafeRight;
    private Image shipStrafeLeft2, shipStrafeRight2;
    
    private Image star1, star2, star3;
    Random rnd = new Random();
    private GameContainer container;
    
    private boolean mouseDir = true;
    private float mx, my;
    private float camDampX, camDampY;
    
    private final float CAM_ZOOM = 0.08f;
    
    private float zoom = 0f;
    private boolean camMoving = false;
    private int strafe = 0;
    private int dir = 0;
    
    private class Star {
        float x, y;
        Image img;
        
        public Star(GameContainer c, Image sheet, int x, int y, int w, int h) {
            img = sheet.getSubImage(x, y, w, h);
            this.x = rnd.nextInt(c.getWidth())-w/2f;
            this.y = rnd.nextInt(c.getHeight())-w/2f;
        }
    }
    
    //SHIELDS like in Halo
    //your shield is down == super vulnerable - crashing into an object with >X velocity will destroy you
    //getting hit with shields down == huge damage
    
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.container = container;
        //container.setSmoothDeltas(true);
        //container.setTargetFrameRate(60);
        //container.setVSync(true);
        
        starMap = new Image("res/stars.png");
        stars = new Star[4];
        stars[0] = new Star(container, starMap, 0, 0, 512, 512);
        stars[1] = new Star(container, starMap, 512, 0, 512, 512);
        stars[2] = new Star(container, starMap, 512, 512, 512, 512);
        stars[3] = new Star(container, starMap, 0, 512, 512, 512);
        
        star1 = createStarLayer(container);
        star2 = createStarLayer(container);
        star3 = createStarLayer(container);
        starMap.destroy();
        
        atmos = new Image("res/tex/atmos.png");
        atmos.setAlpha(0.3f);
        
        int tw = 38;
        int th = 48;
        Image shipSheet = new Image("res/ship.png");
        shipImg = shipSheet.getSubImage(0, 0, tw, th);
        shipStrafeLeft = shipSheet.getSubImage(tw, 0, tw, th);
        shipStrafeRight = shipSheet.getSubImage(tw*2, 0, tw, th);
        shipImg2 = shipSheet.getSubImage(0, th, tw, th);
        shipStrafeLeft2 = shipSheet.getSubImage(tw, th, tw, th);
        shipStrafeRight2 = shipSheet.getSubImage(tw*2, th, tw, th);
        
        ship = new Body(new Circle(shipImg.getWidth()/2f), 10f);
        ship.setPosition(50, 50);
        
        ship.setMaxVelocity(25f, 25f);
        
        world.add(ship);
        updateVector();
    }
    
    
    private Image createStarLayer(GameContainer container) throws SlickException {
        int w=container.getWidth(), h=container.getHeight();
        float cx=w/2f, cy=h/2f;
        
        Image starCache = new Image(new NullTexture(container.getWidth(), container.getHeight()));
        Graphics starGraphics = starCache.getGraphics();
        starGraphics.setBackground(Color.black);
        starGraphics.clear();
        //starGraphics.setBackground(Color.black);
        //starGraphics.fillRect(0, 0, starCache.getWidth(), starCache.getHeight());
        starGraphics.rotate(cx, cy, rnd.nextFloat()*360);
        starGraphics.setDrawMode(Graphics.MODE_ADD);
        starMap.startUse();
        
        
        for (int i=0; i<4; i++) {
            float x = rnd.nextInt(w)-256;
            float y = rnd.nextInt(h)-256;
            
            //GL11.glColor4f(1f, 1f, 1f, 1f);
            stars[i].img.drawEmbedded(x, y, 512, 512);
        }
        
        starMap.endUse();
        starGraphics.setDrawMode(Graphics.MODE_NORMAL);
        starGraphics.flush();
        return starCache;
    }
    
    private void updateVector() {
        double r = Math.toRadians(ang);
        dirX = (float) Math.sin(r);
        dirY = (float) -Math.cos(r);
        ship.setRotation((float)r);
    }
    
    public void keyPressed(int k, char c) {
        
        
    }
    
    private float zoomOut = 0f;
    

    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        delta = Math.min(delta, 5);
        final float DECAY_SPEED = 0.010f;
        
        float dirXAmt = 0f;
        float dirYAmt = 0f;
        boolean shipMoving = false;
        if (container.getInput().isKeyDown(Input.KEY_W)) {
            dirXAmt = dirX * delta * MOVE_SPEED;
            dirYAmt = dirY * delta * MOVE_SPEED;
            //ship.addForce(new Vector2f(0f, 5f));
            //ROVector2f v = ship.getVelocity();
            //ship.adjustVelocity(new Vector2f(dirX * delta * MOVE_SPEED, dirY * delta * MOVE_SPEED));
            //upDown = true;
            shipMoving = true;
            ship.addForce(new Vector2f(dirXAmt, dirYAmt));
            float vx = ship.getVelocity().getX();
            float vy = ship.getVelocity().getY();
            
//            if (camMoving) {
//                camMoving = true;
//                camDampX += dirXAmt / 50;
//                camDampY += dirYAmt / 50;
//                if (Math.abs(camDampX)>10||Math.abs(camDampY)>10)
//                    camMoving = false;
//            }
            dir = 1;
        } else if (container.getInput().isKeyDown(Input.KEY_S)) {
//            dirXAmt = -dirX * delta * MOVE_SPEED;
//            dirYAmt = -dirY * delta * MOVE_SPEED;
//            ship.addForce(new Vector2f(dirXAmt, dirYAmt));
//            float vx = ship.getVelocity().getX();
//            float vy = ship.getVelocity().getY();
////            if (camMoving) {
////                camMoving = true;
////                camDampX += dirXAmt / 50;
////                camDampY += dirYAmt / 50;
////                if (Math.abs(camDampX)>10||Math.abs(camDampY)>10)
////                    camMoving = false;
////            }
//            dir = -1;
        } else { //thrustors off, give some decay...
            
            float vx = ship.getVelocity().getX();
            float vy = ship.getVelocity().getY();
            dirXAmt = -vx * 0.05f;
            dirYAmt = -vy * 0.05f;
            if (dirXAmt!=0 || dirYAmt!=0)
                ship.addForce(new Vector2f(dirXAmt, dirYAmt));
            dir = 0;
        }
        
        camDampX += -camDampX*.01f;
        camDampY += -camDampY*.01f;
        
        
        
//        if (container.getInput().isKeyDown(Input.KEY_E)) {
//            //strafe right
//            //ship.adjustVelocity(new Vector2f(dirX * delta * STRAFE_SPEED, dirY * delta * STRAFE_SPEED));
//            
//            float cx = container.getWidth()/2f;
//            float cy = container.getHeight()/2f;
//            //float x = this.dirX*150f, y = this.dirY*150f;
//            
//            //ang = -(float)Math.toDegrees( Math.atan2( cx-x, cy-y ) );
//            /*float s = 1f * .360f * .05f;
//            float ts = 0.1f;
//            
//            ang -= ts * delta;
//            double r = Math.toRadians(ang + 90);
//            float dirX = (float)Math.sin(r);
//            float dirY = (float)-Math.cos(r);
//            updateVector();
//            ship.adjustVelocity(new Vector2f(dirX * delta * s, dirY * delta * s));*/
//            
        if (container.getInput().isKeyDown(Input.KEY_A)) {
            //strafe right
            strafe = -1;
            double r = Math.toRadians(ang - 45);
            float dirX = (float)Math.sin(r);
            float dirY = (float)-Math.cos(r);
            shipMoving = true;
            ship.addForce(new Vector2f(dirX * delta * STRAFE_SPEED, dirY * delta * STRAFE_SPEED));
        }
        else if (container.getInput().isKeyDown(Input.KEY_D)) {
            //strafe right
            strafe = 1;
            double r = Math.toRadians(ang + 45);
            float dirX = (float)Math.sin(r);
            float dirY = (float)-Math.cos(r);
            shipMoving = true;
            ship.addForce(new Vector2f(dirX * delta * STRAFE_SPEED, dirY * delta * STRAFE_SPEED));
        } else {
            strafe = 0;
        }
        
        if (shipMoving) {
            zoomOut = Math.min(CAM_ZOOM, zoomOut+0.0001f);
        } else {
            zoomOut = Math.max(0f, zoomOut-0.0003f);
        }
        
        if (!mouseDir) {
            if (container.getInput().isKeyDown(Input.KEY_D)) {
                ang += TURN_SPEED * delta;
                updateVector();

            } else if (container.getInput().isKeyDown(Input.KEY_A)) {
                ang -= TURN_SPEED * delta;
                updateVector();
            }
        } else {
        	float cx = container.getWidth()/2f;
            float cy = container.getHeight()/2f;
            float x = container.getInput().getMouseX(), y = container.getInput().getMouseY();
            ang = -(float)Math.toDegrees( Math.atan2( cx-x, cy-y ) );
            
            //ang = 360 * (x/(float)container.getWidth()) - 180;
            updateVector();
        }
        
        
        //System.out.println(ship.getVelocity());
        
        counter += delta;
        while (counter > worldUpdateInterval) {
            world.step(worldUpdateInterval * 0.01f);
            counter -= worldUpdateInterval;
        }
    }
    
    private float camXOff = 0f, camYOff = 0f;
    private float damp = 0f;
    
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        //float x=ship.getPosition().getX(), y=ship.getPosition().getY();
        //float cx = x+shipImg.getWidth()/2f, cy = y+shipImg.getHeight()/2f;
        
        
        
        float sx = ship.getPosition().getX(), sy = ship.getPosition().getY();
        
        star1.draw(-sx*.10f, -sy*.10f, .75f);
        
        g.setDrawMode(Graphics.MODE_SCREEN);
        star2.draw(-sx*.35f, -sy*.35f, .85f);
        star3.draw(-sx*.85f, -sy*.85f, 1f, new Color(1f,1f,1f,0.25f));
        g.setDrawMode(Graphics.MODE_NORMAL);
        
        //get the center
        float cx = container.getWidth()/2f, cy = container.getHeight()/2f;
        
        //get the ship velocity
        float vx = ship.getVelocity().getX(), vy = ship.getVelocity().getY();
        
        float shipSpeed = Math.max(Math.abs(vx), Math.abs(vy));
        
        //camXOff += vx;
//        cx += camDampX;
//        cy += camDampY;
        
            
        //damp += 0.5f;
        
//        if (camMoving)
//            camXOff -= 0.03f*vx;
////        else
////            camXOff += vx;
//        
//        cx += camXOff;
        
        //if (Math.max(Math.abs(vx), Math.abs(vy)))
        
        
        
        //float x = container.getWidth()/2f-shipImg.getWidth()/2f;
        //float y = container.getHeight()/2f-shipImg.getHeight()/2f;
        Image shipImg = this.shipImg;
        if (strafe<0)
            shipImg = dir==1 ? shipStrafeLeft2 : shipStrafeLeft;
        else if (strafe>0)
            shipImg = dir==1 ? shipStrafeRight2 : shipStrafeRight;
        else if (dir==1) {
            shipImg = shipImg2;
        }
        
        
        
        float scaleAmt = Math.min(CAM_ZOOM, zoomOut);
        float scale = 1f-scaleAmt;
        float rx = cx-shipImg.getWidth()*scale/2f;
        float ry = cy-shipImg.getHeight()*scale/2f;
        //g.scale(scale, scale);
        
        g.translate(vx*.8f, vy*.8f);
        
        g.rotate(cx, cy, (float)Math.toDegrees(ship.getRotation()));
        
        g.setAntiAlias(true);
        shipImg.draw(rx, ry, scale);
        g.setAntiAlias(false);
//        g.rotate(cx, cy, -(float)Math.toDegrees(ship.getRotation()));
//        
//        g.setColor(Color.red);
//        mx = cx-25/2f + dirX*200f;
//        my = cy-25/2f + dirY*200f;
//        
//        //g.drawOval(mx, my, 25, 25);
//        
//        g.rotate(cx, cy, ang);
//        
//        //g.drawRect(cx-2.5f, cy, 5, -150);
        g.resetTransform();
        
        //g.setColor(new Color(1f,1f,1f,0.25f));
        //g.fillOval(rx+dirX*shipImg.getWidth()*2, ry+dirY*shipImg.getHeight()*2, shipImg.getWidth(), shipImg.getHeight());
        
        g.setColor(Color.white);
        ROVector2f v = ship.getVelocity();
        g.drawString("Velocity: "+(int)v.getX()+" "+(int)v.getY(), 10, 55);
        //g.drawString("Total Textures: "+InternalTextureLoader.totalTextures, 10, 40);
        //g.drawString("Camera damp: "+(int)camDampX+" "+(int)camDampY, 10, 70);
        
//        g.resetTransform();
        
        g.rotate(cx, cy, atmosRot);
        atmos.draw(-container.getWidth()/2f, -container.getHeight()/2f, container.getWidth()*2, container.getHeight()*2, atmosFilter);
        g.resetTransform();
        
    }
}
