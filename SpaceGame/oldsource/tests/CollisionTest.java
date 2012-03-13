package space.tests;

/*
 * Phys2D - a 2D physics engine based on the work of Erin Catto. The
 * original source remains:
 * 
 * Copyright (c) 2006 Erin Catto http://www.gphysics.com
 * 
 * This source is provided under the terms of the BSD License.
 * 
 * Copyright (c) 2006, Phys2D
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 * 
 *  * Redistributions of source code must retain the above 
 *    copyright notice, this list of conditions and the 
 *    following disclaimer.
 *  * Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution.
 *  * Neither the name of the Phys2D/New Dawn Software nor the names of 
 *    its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 */

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Contact;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.collide.BoxBoxCollider;
import net.phys2d.raw.collide.BoxCircleCollider;
import net.phys2d.raw.collide.CircleBoxCollider;
import net.phys2d.raw.collide.CircleCircleCollider;
import net.phys2d.raw.collide.LineBoxCollider;
import net.phys2d.raw.collide.LineCircleCollider;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;

/**
 * A common demo box super class.
 * 
 * @author Kevin Glass
 */
public class CollisionTest {
	/** The frame displaying the demo */
	protected Frame frame;
	/** True if the simulation is running */
	private boolean running = true;
	/** The rendering strategy */
	private BufferStrategy strategy;
	
	/** The size of body A */
	private Body staticBox = new StaticBody(new Box(40,50));
	/** The size of body B */
	private Body dynamicBox = new Body(new Box(50,50), 0);
	/** The size of body B */
	private Body staticCircle = new StaticBody(new Circle(45));
	/** The size of body D */
	private Body dynamicCircle = new Body(new Circle(36), 0);
	/** The size of body E */
	private Body staticLine = new StaticBody(new Line(100,100));
	
	/** The element we're current controlling */
	private int current = 1;
	/** The contacts array used whe nsearching for collisions */
	private Contact[] contacts = new Contact[] {new Contact(), new Contact()};
	
	/**
	 * Create a new test
	 */
	public CollisionTest() {
		staticBox.setPosition(200,300);
		staticBox.setRotation(0.4f);
		dynamicBox.setPosition(350,250);
		//dynamicBox.setRotation(0.4f);
		staticCircle.setPosition(150,400);
		staticCircle.setRotation(0.8f);
		dynamicCircle.setPosition(250,430);
		dynamicCircle.setRotation(0.3f);
		staticLine.setPosition(200,100);
	}
	
	/**
	 * Notification that a key was pressed
	 * 
	 * @param c The character of key hit
	 */
	protected void keyHit(char c) {
		if (c == '1') {
			current = 1;
		}
		if (c == '2') {
			current = 2;
		}
	}
	
	/**
	 * Initialise the GUI 
	 */
	private void initGUI() {
		frame = new Frame("Collision Test");
		frame.setResizable(false);
		frame.setSize(500,500);
		
		frame.addMouseListener(new MouseHandler());
		frame.addMouseMotionListener(new MouseHandler());
		
		int x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 500) / 2;
		int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 500) / 2;
		
		frame.setLocation(x,y);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				running = false;
				System.exit(0);
			}
		});
		frame.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				keyHit(e.getKeyChar());
			}
			
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 27) {
					System.exit(0);
				}
			}
			
		});
		
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		
		strategy = frame.getBufferStrategy();
	}
	
	/**
	 * Start the simulation running
	 */
	public void start() {
		initGUI();
		
		float target = 1000 / 60.0f;
		float frameAverage = target;
		long lastFrame = System.currentTimeMillis();
		float yield = 10000f;
		float damping = 0.1f;
		
		while (running) {
			// adaptive timing loop from Master Onyx
			long timeNow = System.currentTimeMillis();
			frameAverage = (frameAverage * 10 + (timeNow - lastFrame)) / 11;
			lastFrame = timeNow;
			
			yield+=yield*((target/frameAverage)-1)*damping+0.05f;

			for(int i=0;i<yield;i++) {
				Thread.yield();
			}
			
			// render
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,500,500);
			
			draw(g);
			g.dispose();
			strategy.show();
			
			update();
		}
	}
	
	/**
	 * Update the demo - just in case we want to add anything over
	 * the top
	 */
	protected void update() {
	}
	
	/**
	 * Draw the elements being checked 
	 * 
	 * @param g The graphics context on which to draw
	 */
	public void draw(Graphics2D g) {
		g.setColor(Color.black);
		g.drawString("1) Block",10,470);
		g.drawString("2) Circle",10,490);
		
		if (current == 1) {
			g.drawString("Block Selected",10,50);
		}
		if (current == 2) {
			g.drawString("Circle Selected",10,50);
		}
		
		drawBoxBody(g, staticBox, false);
		drawCircleBody(g, staticCircle, false);
		drawLineBody(g, staticLine);
		
		BoxBoxCollider collider = new BoxBoxCollider();
		int count = collider.collide(contacts, staticBox, dynamicBox);
		boolean hit1 = count > 0;
		drawContacts(g, contacts, count);
		CircleBoxCollider collider3 = CircleBoxCollider.createCircleBoxCollider();
		count = collider3.collide(contacts, staticCircle, dynamicBox);
		boolean hit2 = count > 0;
		drawContacts(g, contacts, count);
		LineBoxCollider collider5 = LineBoxCollider.create();
		count = collider5.collide(contacts, staticLine, dynamicBox);
		boolean hit3 = count > 0;
		drawContacts(g, contacts, count);

		g.setColor(new Color(0,1,0,0.5f));
		drawBoxBody(g, dynamicBox, hit1 || hit2 || hit3);
		
		BoxCircleCollider collider2 = new BoxCircleCollider();
		count = collider2.collide(contacts, staticBox, dynamicCircle);
		hit1 = count > 0;
		drawContacts(g, contacts, count);
		CircleCircleCollider collider4 = new CircleCircleCollider();
		count = collider4.collide(contacts, staticCircle, dynamicCircle);
		hit2 = count > 0;
		drawContacts(g, contacts, count);
		LineCircleCollider collider6 = new LineCircleCollider();
		count = collider6.collide(contacts, staticLine, dynamicCircle);
		hit3 = count > 0;
		drawContacts(g, contacts, count);

		g.setColor(new Color(0,0,1,0.5f));
		drawCircleBody(g, dynamicCircle, hit1 || hit2 || hit3);
	}

	/**
	 * Draw a representation of a line
	 * 
	 * @param g The graphics context on which to draw
	 * @param body The body to be rendered
	 */
	protected void drawLineBody(Graphics2D g, Body body) {
		Line line = (Line) body.getShape();

		g.setColor(Color.gray);
		drawAABody(g,body,body.getShape().getBounds());
		
		g.setColor(Color.black);
		float xp = body.getPosition().getX();
		float yp = body.getPosition().getY();
		
		g.drawLine((int) (xp + line.getX1()),(int) (yp + line.getY1()),
				   (int) (xp + line.getX2()),(int) (yp + line.getY2()));

	}
	
	/**
	 * Draw the contact points onto the display
	 * 
	 * @param g The graphics context on which to draw
	 * @param contacts The contacts to be drawn
	 * @param count The number of contacts to be drawn
	 */
	protected void drawContacts(Graphics2D g, Contact[] contacts, int count) {
		for (int i=0;i<count;i++) {
			g.setColor(Color.red);
			int x = (int) contacts[i].getPosition().getX();
			int y = (int) contacts[i].getPosition().getY();
			g.fillOval(x-3,y-3,6,6);
			
			float dx = contacts[i].getNormal().getX();
			float dy = contacts[i].getNormal().getY();
			g.setColor(Color.black);
			g.drawLine(x,y,(int) (x+(dx*20)),(int) (y+(dy*20)));
			
			float sep = contacts[i].getSeparation();
			g.setColor(Color.yellow.darker());
			g.drawLine(x,y,(int) (x+(dx*sep)),(int) (y+(dy*sep)));
		}
	}
	
	/**
	 * Draw the bounds of a body
	 * 
	 * @param g The graphics context on which to draw the bounds
	 * @param body The body being drawn
	 * @param box The bounds object being draw 
	 */
	protected void drawAABody(Graphics2D g, Body body, AABox box) {
		float width2 = box.getWidth() / 2;
		float height2 = box.getHeight() / 2;
		
		g.setColor(Color.lightGray);
		g.drawRect((int) (body.getPosition().getX() - width2 + box.getOffsetX()), 
				   (int) (body.getPosition().getY() - height2 + box.getOffsetY()), 
				   (int) box.getWidth(), (int) box.getHeight());
	}
	
	/**
	 * Draw a circle body 
	 * 
	 * @param g The graphics context on which to draw
	 * @param body The body to be drawn
	 * @param fill True if we should draw it filled (indicates a collision)
	 */
	protected void drawCircleBody(Graphics2D g, Body body, boolean fill) {
		Circle circle = (Circle) body.getShape();
		drawCircleBody(g,body,circle,fill);
		drawAABody(g,body,body.getShape().getBounds());
	}

	/**
	 * Draw a circle body 
	 * 
	 * @param g The graphics context on which to draw
	 * @param body The body to be drawn
	 * @param circle The shape to be drawn
	 * @param fill True if we should draw it filled (indicates a collision)
	 */
	protected void drawCircleBody(Graphics2D g, Body body, Circle circle, boolean fill) {
		float x = body.getPosition().getX();
		float y = body.getPosition().getY();
		float r = circle.getRadius();
		float rot = body.getRotation();
		float xo = (float) (Math.cos(rot) * r);
		float yo = (float) (Math.sin(rot) * r);
		
		
		if (fill) {
			g.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
		} else {
			g.drawOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
		}
		g.drawLine((int) x,(int) y,(int) (x+xo),(int) (y+yo));
	}
	
	/**
	 * Draw a box onto the display
	 * 
	 * @param g The graphics context on which to draw
	 * @param body The body to draw
	 * @param fill True if we should draw the body filled, indicates a 
	 * collision.
	 */
	protected void drawBoxBody(Graphics2D g, Body body, boolean fill) {
		Box box = (Box) body.getShape();
		Vector2f[] pts = box.getPoints(body.getPosition(),body.getRotation());
		
		Vector2f v1 = pts[0];
		Vector2f v2 = pts[1];
		Vector2f v3 = pts[2];
		Vector2f v4 = pts[3];
		
		Polygon pol = new Polygon();
		pol.addPoint((int) v1.x,(int) v1.y);
		pol.addPoint((int) v2.x,(int) v2.y);
		pol.addPoint((int) v3.x,(int) v3.y);
		pol.addPoint((int) v4.x,(int) v4.y);
		
		if (fill) {
			g.fill(pol);
		} else {
			g.draw(pol);
		}
		g.setColor(Color.gray);
		drawAABody(g,body,body.getShape().getBounds());
	}
	
	/**
	 * A mouse handler to handle dragging the collision shapes around
	 * 
	 * @author Kevin Glass
	 */
	private class MouseHandler extends MouseAdapter implements MouseMotionListener {

		/**
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		public void mouseDragged(MouseEvent e) {
			if (current == 1) {
				dynamicBox.setPosition(e.getX(),e.getY());
			}
			if (current == 2) {
				dynamicCircle.setPosition(e.getX(),e.getY());
			}
		}

		/**
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		public void mouseMoved(MouseEvent e) {
		}
		
	}
	
	/**
	 * Entry point to out application
	 * 
	 * @param argv The arguments passed to our application
	 */
	public static void main(String[] argv) {
		new CollisionTest().start();
	}
}
