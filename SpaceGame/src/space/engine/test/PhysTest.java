package space.engine.test;

import java.util.Random;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;

import space.entities.Ship;
import space.entities.Constants;

/**
 * A new class
 * @author Matt
 */
public class PhysTest extends BasicGame {

	PhysTest() {
		super("");
	}

	int worldUpdateInterval = 5;
	int counter = 0;
	Ship ship;
	Image shipImg, shipImg2;
	World world = new World(new Vector2f(0, 0), 10);

	private float dirX, dirY, ang;
	private float mouseX, mouseY;
	//private final float MOVE_SPEED = 0.015f;
	private final float TURN_SPEED = 0.25f;

	private Image atmos;
	private float atmosRot;
	private Star[] stars;
	private Image starMap;

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

	public void init(GameContainer container) throws SlickException {
		this.container = container;
		ship = new Ship(new Image("res/ship.png"), 10f);
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

		atmos = new Image("res/atmos.png");
		atmos.setAlpha(0.3f);


		/*		int tw = 38;
		int th = 48;
		Image shipSheet = new Image("res/ship.png");
		shipImg = shipSheet.getSubImage(0, 0, tw, th);
		shipStrafeRight = shipSheet.getSubImage(tw, 0, tw, th);
		shipStrafeLeft = shipSheet.getSubImage(tw*2, 0, tw, th);
		shipImg2 = shipSheet.getSubImage(0, th, tw, th);
		shipStrafeRight2 = shipSheet.getSubImage(tw, th, tw, th);
		shipStrafeLeft2 = shipSheet.getSubImage(tw*2, th, tw, th);*/

		//ship = new Body(new Circle(shipImg.getWidth()/2f), 10f);
		ship.setPosition(400, 300);

/*		ship.setMaxVelocity(25f, 25f);*/
		//world.setGravity(0, 0);
		world.add(ship.getBody());
		updateVector();
	}


	private Image createStarLayer(GameContainer container) throws SlickException {
		int w=container.getWidth(), h=container.getHeight();
		float cx=w/2f, cy=h/2f;

		Image starCache = new Image(container.getWidth(), container.getHeight());
		Texture old = starCache.getTexture();
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
		old.release();
		return starCache;
	}

	private void updateVector() {
		/*double r = Math.toRadians(ang);
		ship.setHeading(r);
*/
	}

	public void keyPressed(int k, char c) {
	}

	public void mouseMoved(int oldx, int oldy, int x, int y) {
		if (!mouseDir)
			return;
		mouseX = x;
		mouseY = y;
		float cx = container.getWidth()/2f;
		float cy = container.getHeight()/2f;

		ang = -(float)Math.toDegrees( Math.atan2( ship.getX()-x, ship.getY()-y ) );
		ship.setHeading(mouseX, mouseY);

		//ang = 360 * (x/(float)container.getWidth()) - 180;
		updateVector();
	}

	private float zoomOut = 0f;


	public void update(GameContainer container, int delta) throws SlickException {
		delta = Math.min(delta, 5);

		if (container.getInput().isKeyDown(Input.KEY_W)) {
			ship.thrustStraight(delta);
			dir = 1;
		} else { 
			ship.idling();
			dir = 0;
		}

		camDampX += -camDampX*.01f;
		camDampY += -camDampY*.01f;
       
		if (container.getInput().isKeyDown(Input.KEY_A)) {
			strafe = -1;
			ship.strafeLeft(delta);
		}
		else if (container.getInput().isKeyDown(Input.KEY_D)) {
			strafe = 1;
			ship.strafeRight(delta);

		} else {
			strafe = 0;
		}

		if (ship.isShipMoving()) {
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
		}


		//System.out.println(ship.getVelocity());

		counter += delta;
		while (counter > worldUpdateInterval) {
			world.step(worldUpdateInterval * 0.01f);
			counter -= worldUpdateInterval;
		}
	}

/*	private float camXOff = 0f, camYOff = 0f;
	private float damp = 0f;*/

	public void render(GameContainer container, Graphics g) throws SlickException {
		//float x=ship.getPosition().getX(), y=ship.getPosition().getY();
		//float cx = x+shipImg.getWidth()/2f, cy = y+shipImg.getHeight()/2f;

		float sx = container.getWidth()-ship.getX()/2, sy = ship.getY();

		star1.draw(-sx*.10f, -sy*.10f, .75f);

		g.setDrawMode(Graphics.MODE_SCREEN);
		star2.draw(-sx*.35f, -sy*.35f, .85f);
		star3.draw(-sx*.85f, -sy*.85f, 1f, new Color(1f,1f,1f,0.25f));
		g.setDrawMode(Graphics.MODE_NORMAL);

		//get the center
		float cx = container.getWidth()/2f, cy = container.getHeight()/2f;

		//get the ship velocity
		float vx = ship.getVelX(), vy = ship.getVelY();

		float shipSpeed = Math.max(Math.abs(vx), Math.abs(vy));

		float scaleAmt = Math.min(CAM_ZOOM, zoomOut);
		float scale = 1f-scaleAmt;
		float xOff = vx/2.5f;
		float yOff = vy/2.5f;

		g.rotate(ship.getX(), ship.getY(), (float)Math.toDegrees(ship.getRotation()));

		g.setAntiAlias(true);
		ship.draw(g, xOff, yOff, scale);
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
		g.drawString("Velocity: "+(int)ship.getVelX()+" "+(int)ship.getVelY(), 10, 25);
		g.drawString("Angle: "+ang, 10, 40);
		g.drawString("Mouse Location: X: "+ mouseX + " Y: "+ mouseY, 10, 60);
		g.drawString("Ship position: X: " + ship.getX() + " Y: "+ ship.getY(), 10, 80);
		g.drawString("Total Textures: "+InternalTextureLoader.getTextureCount(), 10, 100);
		g.drawString("Camera damp: "+(int)camDampX+" "+(int)camDampY, 10, 120);

		//        g.resetTransform();

		g.rotate(cx, cy, atmosRot);
		atmos.draw(-container.getWidth()/2f, -container.getHeight()/2f, container.getWidth()*2, container.getHeight()*2);
		g.resetTransform();

	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer container = new AppGameContainer(new PhysTest());
		container.setDisplayMode(800, 600, false);
		container.start();
	}
}
