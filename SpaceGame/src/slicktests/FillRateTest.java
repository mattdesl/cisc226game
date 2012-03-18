package slicktests;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import net.phys2d.math.Vector2f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBPointSprite;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

import space.engine.SpriteBatch;
import space.engine.test.SpriteTest.Ball;
import space.util.Utils;

public class FillRateTest extends BasicGame {
    
	FillRateTest() { super("Particles!"); }
    public static void main(String[] args) throws SlickException {
        new AppGameContainer(new FillRateTest(), 800, 600, false).start();
    }
    
    Image image;
    final int TILESIZE =32;
    float PSIZE;
    boolean points = false;

	int numberOfBalls = 18000;
	Ball[] balls = new Ball[100000];
    
	public void init(GameContainer container) throws SlickException {
		image = new Image("res/small.png");
		FloatBuffer b = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL12.GL_SMOOTH_POINT_SIZE_RANGE, b);
		PSIZE = b.get(1);

		container.setShowFPS(false);
		container.getGraphics().setBackground(Color.white);
		container.setClearEachFrame(false);

		
		// initilise balls
		for (int i = 0; i < balls.length; i++) {
			balls[i] = new Ball(Utils.rnd(0, container.getWidth()), Utils.rnd(0, container.getHeight()));
		}
		
		image.bind();
	}
	
	
	
	public void render(GameContainer container, Graphics g)
			throws SlickException { 
		g.clear();
		
		SGL GL = Renderer.get();
		int size = Math.min((int)PSIZE, TILESIZE);
		float hs = size/2f;
		//GLContext.getCapabilities().OpenGL20;
		//GLContext.getCapabilities().GL_ARB_point_sprite
		
		if (points) {
			GL.glEnable(GL20.GL_POINT_SPRITE);
			GL11.glPointSize(size);
			//GL.glEnable(GL.GL_POINT_SMOOTH);
			GL.glTexEnvi(GL20.GL_POINT_SPRITE, GL20.GL_COORD_REPLACE, GL11.GL_TRUE);
			//GL.glTexEnvi(GL20.GL_POINT_SPRITE, GL20.GL_CO, value)
			GL.glBegin(GL.GL_POINTS);
		} else
			image.startUse();
		
		for (int i = 0; i < numberOfBalls; i++) {
			if (points) {
				GL.glVertex2f(balls[i].x+TILESIZE/2f, balls[i].y+TILESIZE/2f);
			} else {
				image.drawEmbedded(balls[i].x, balls[i].y, TILESIZE, TILESIZE);
			}
		}
		
		if (points) {
			GL.glEnd();
			GL.glDisable(ARBPointSprite.GL_POINT_SPRITE_ARB);
		} else {
			image.endUse();
		}
	}


	public void update(GameContainer container, int delta)
			throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_SPACE))
			points = !points;
		// update ball movement
		for (int i = 0; i < numberOfBalls; i++) {
			balls[i].update(delta);
		}
		
		Display.setTitle("FPS: "+container.getFPS()+" Count: "+numberOfBalls
				+" Mode: "+(points?"points":"drawEmbedded")
		);
	}

}

/*float tx = tile.getTextureOffsetX();
float ty = tile.getTextureOffsetY();
float tw = tile.getTextureWidth();
float th = tile.getTextureHeight();

float repeatX = 35, repeatY = 25;
float x = 0, y = 0;
float width = tile.getWidth()*repeatX, height = tile.getHeight()*repeatY;

int w = 600, h = 600;

SGL GL = Renderer.get(); 

tile.bind();
boolean r = true;
int cl = r ? GL11.GL_REPEAT : EXTTextureMirrorClamp.GL_MIRROR_CLAMP_TO_EDGE_EXT;

Color.white.bind();
GL.glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
GL.glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

//option A, clamp the texture
if (op==1) {
	GL.glBegin(GL.GL_QUADS);
	GL.glTexCoord2f(tx, ty);
	GL.glVertex3f(x, y, 0);
	GL.glTexCoord2f(tx, ty + th*repeatY);
	GL.glVertex3f(x, y + height, 0);
	GL.glTexCoord2f(tx + tw*repeatX, ty + th*repeatY);
	
	GL.glVertex3f(x + width, y + height, 0);
	GL.glTexCoord2f(tx + tw*repeatX, ty);
	GL.glVertex3f(x + width, y, 0);
	GL.glEnd();
} else {
	Utils.texture(tile, 0, 0, width, height);
	
}

g.setColor(Color.red);
g.drawRect(0,  0, w, h);
g.drawString("GL_REPEAT? "+(op==1), x, y);

//tile.endUse();*/
