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

public class ProjectileTest extends BasicGame {
	// copied these classes from your test, but renamed vars for myself

	class Player extends Sprite {
		private final float SPEED = .05f;
		private int damage = 5;
		private int fireRate = 500; // twice per second, ideally
		private int cdCount = 0;
		private int cdRemain = fireRate;
		private int step = cdRemain /3;


		Player (World world, Image img, float x, float y){
			super(world,img,x,y);
		}

		public Body createBody(float width, float height){
			Body retVal =  new Body(new Box(width,height),10f);
			retVal.setMaxVelocity(20,35);
			return retVal;
		}

		public void update(GameContainer container, int delta){
			float vectorY = body.getVelocity().getY();

			// gotta add a listener to spawn a shot

			if (container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)){
				cdCount += delta;
				while (cdCount > step){
					cdCount -= step;
					cdRemain -= step;
				}	
				if (cdRemain <= 0){
					// we can fire
					// lol at the hax 
					Bullet bullet = new Bullet(world, body.getPosition().getX()+body.getShape().getBounds().getWidth(),body.getPosition().getY());
					entities.add(bullet);
					cdRemain = fireRate;
				}
			}

			if (vectorY < 1) { 
				if (container.getInput().isKeyDown(Input.KEY_A)){
					body.adjustVelocity(new Vector2f(-SPEED*delta,0f));
				}
				else if (container.getInput().isKeyDown(Input.KEY_D)){
					body.adjustVelocity(new Vector2f(SPEED*delta, 0f));
				}
				if (container.getInput().isKeyDown(Input.KEY_SPACE) && vectorY == 0){
					body.adjustVelocity(new Vector2f(0f, -35f));
				}
			}
		}
	}


	private class Bullet extends Sprite {
		Body body; 
		int width = 5 , height = 5;

		public Bullet (World world, float x, float y){
			body = new Body (new Box(width, height),.5f);
			body.setPosition(x, y);
			body.setMaxVelocity(70,70);
			body.adjustVelocity(new Vector2f(70f,0));
			body.setGravityEffected(false);
			world.add(body);
		}

		public void draw(Graphics g){
			ROVector2f vector = body.getPosition();
			float vectorX = vector.getX()-width/2f;
			float vectorY = vector.getY()-height/2f;

			g.setColor(Color.red);
			g.drawRect(vectorX, vectorY, width, height);
		}

		public void update(GameContainer gameContainer, int delta){
		}

	}


	class Enemy extends Sprite{
		private final float SPEED = .05f;
		private int hitpoints = 25;

		Enemy (World world, Image img, float x, float y){
			super(world,img,x,y);
		}

		public Body createBody(float width, float height){
			return new Body(new Box(width, height),10f);
		}

		public int getHp(){
			return hitpoints;
		}

		public void update(GameContainer container, int delta){
			ROVector2f enemyVel = body.getVelocity();
			boolean isHit = false; //place holder
			// if the enemy is hit by a bullet, slow him down based on hp damage dealt
			// needs some complex stuff. need to know which side of him the bullet hit, to reverse the 
			// velocity for a suitable time when he's hit

		}
	}

	class Sprite {
		Image img;
		Body body;
		float width, height;

		Sprite (World world, Image img, float x, float y, float width, float height){
			this.img = img;
			this.width = width;
			this.height = height;
			body = createBody(width,height);
			body.setPosition(x, y);
			body.setRotatable(false);
			world.add(body);
		}

		Sprite(){
			//default for hax
		}
		Sprite(World world, Image img, float x, float y){
			this(world, img, x, y, img.getWidth(), img.getHeight());
		}

		protected Body createBody(float width, float height){
			return new StaticBody(new Box(width, height));
		}

		public void draw(Graphics g){
			ROVector2f vector = body.getPosition();
			float vectorX = vector.getX()-width/2f;
			float vectorY = vector.getY()-height/2f;

			img.startUse();
			for (int i = 0; i < width; i+=TILESIZE){
				for (int j = 0; j < height; j+=TILESIZE){
					img.drawEmbedded(vectorX+i, vectorY+j, img.getWidth(), img.getHeight());
				}
			}
			img.endUse();
			g.setColor(Color.red);
			g.drawRect(vectorX, vectorY, width, height);
		}

		public void update(GameContainer container, int delta){}
	}


	// ProjectileTest members

	Sprite player;
	List<Sprite> entities;
	final int TILESIZE=32;

	World world = new World(new Vector2f(0,8f),10);
	int worldUpdateInterval = 5;
	int counter = 0;

	private Image ui;

	public ProjectileTest() {super("Projectile Test");}

	public static void main (String[]args) throws SlickException {
		new AppGameContainer(new ProjectileTest(), 800, 600, false).start();
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		for (int i =0; i<entities.size(); i++){
			entities.get(i).draw(g);
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		float x = container.getWidth()/2f;
		float y = container.getHeight()/2f;

		entities = new ArrayList<Sprite>();

		float playerX = x+TILESIZE/2f, playerY = y-TILESIZE*3;
		entities.add(new Player(world, new Image("res/mario.png"), playerX, playerY));
		float enemyX = x+TILESIZE*3f, enemyY = y-TILESIZE/2f-8;
		entities.add(new Enemy(world, new Image("res/mario.png"), enemyX, enemyY));

		Image box1 = new Image("res/box1.png");

		entities.add(new Sprite(world, box1, x, y, 20*TILESIZE, TILESIZE));
		ui = new Image("res/ui.png"); 

	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		for (int i = 0; i < entities.size(); i++){
			entities.get(i).update(container,  delta);
		}
		counter += delta; 
		while(counter > worldUpdateInterval){
			world.step(worldUpdateInterval * 0.01f);
			counter -= worldUpdateInterval;
		} 
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
