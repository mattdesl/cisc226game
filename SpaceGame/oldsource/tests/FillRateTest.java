package space.tests;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import net.phys2d.math.Vector2f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

import space.util.Utils;

public class FillRateTest extends BasicGame {
    
	FillRateTest() { super("Particles!"); }
    public static void main(String[] args) throws SlickException {
        new AppGameContainer(new FillRateTest(), 1920, 1080, false).start();
    }
    
    Image tile;
    final int TILESIZE =16;
    float PSIZE;
    boolean points = true;
    
    final int COUNT = 17000;
    private ArrayList<Vector2f> particles = new ArrayList<Vector2f>(COUNT);
    Image clouds, alpha;
    Texture texture;
    
	public void init(GameContainer container) throws SlickException {
		tile = new Image("res/tex/tile.png");
		clouds = new Image("res/menu.png");
		alpha = new Image("res/alpha.png");
		FloatBuffer b = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL12.GL_SMOOTH_POINT_SIZE_RANGE, b);
		PSIZE = b.get(1);
		//System.out.println("max size "+PSIZE);
		
		for (int i=0; i<COUNT; i++) {
			particles.add(new Vector2f(Utils.rnd(0f, -container.getWidth()-TILESIZE/2f), Utils.rnd(0f, -container.getHeight()-TILESIZE/2f)));
		}
		
		texture = clouds.getTexture();
	}
	
	
	
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		
		//image size, e.g. 550x200
		float width = texture.getImageWidth(); 
		float height = texture.getImageHeight();
		//the physical width of the texture which will be used in glTexCoord (generally a float between 0 and 1)
		float textureWidth = texture.getWidth();
		float textureHeight = texture.getHeight();
		//texture offsets, for texture atlas purposes. leave at 0 for full image
		float textureOffsetX = 0;
		float textureOffsetY = 0;
		//where on screen to draw the image
		float x = 0;
		float y = 0;
		
		Color.white.bind();
		clouds.bind();
//		GL11.glBegin(GL11.GL_QUADS);
//			GL11.glTexCoord2f(textureOffsetX, textureOffsetY);
//			GL11.glVertex3f(x, y, 0);
//			GL11.glTexCoord2f(textureOffsetX, textureOffsetY + textureHeight);
//			GL11.glVertex3f(x, y + height, 0);
//			GL11.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY
//					+ textureHeight);
//			GL11.glVertex3f(x + width, y + height, 0);
//			GL11.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY);
//			GL11.glVertex3f(x + width, y, 0);
//		GL11.glEnd();
		
        GL11.glBegin(GL11.GL_QUADS);
	        GL11.glTexCoord2f(0,0);GL11.glVertex2f(100,100);
	        GL11.glTexCoord2f(1,0);GL11.glVertex2f(100+texture.getTextureWidth(),100);
	        GL11.glTexCoord2f(1,1);GL11.glVertex2f(100+texture.getTextureWidth(),100+texture.getTextureHeight());
	        GL11.glTexCoord2f(0,1);GL11.glVertex2f(100,100+texture.getTextureHeight());
	     GL11.glEnd();
		
		/*SGL GL = Renderer.get();
		int size = Math.min((int)PSIZE, TILESIZE);
		float hs = size/2f;
		//GLContext.getCapabilities().OpenGL20;
		//GLContext.getCapabilities().GL_ARB_point_sprite
		
		int count = 100;
		if (points) {
			tile.bind();
			GL.glEnable(GL20.GL_POINT_SPRITE);
			GL11.glPointSize(size);
			//GL.glEnable(GL.GL_POINT_SMOOTH);
			GL.glTexEnvi(GL20.GL_POINT_SPRITE, GL20.GL_COORD_REPLACE, GL11.GL_TRUE);
			//GL.glTexEnvi(GL20.GL_POINT_SPRITE, GL20.GL_CO, value)
			GL.glBegin(GL.GL_POINTS);
			for (Vector2f v : particles) {
				GL.glVertex2f(v.getX()+hs, v.getY()+hs);
			}
			GL.glEnd();
			GL.glDisable(ARBPointSprite.GL_POINT_SPRITE_ARB);
		} else {
			tile.startUse();
			for (Vector2f v : particles) {
				tile.drawEmbedded(v.getX(), v.getY(), TILESIZE, TILESIZE);
			}
			tile.endUse();
		}
		g.drawString(COUNT+" sprites with point mode? "+points, 10, 25);
		
		g.setColor(Color.white);
		g.fillRect(50, 50, 250, 250);
		
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		alpha.draw(50, 50, 500, 500, new Color(0f,1f,0f,1f));
		
		g.setDrawMode(Graphics.MODE_ALPHA_BLEND);
		clouds.draw(50, 50, 500, 500, new Color(0f,1f,0f,.25f));
		
		g.setDrawMode(Graphics.MODE_NORMAL);*/
	}


	public void update(GameContainer container, int delta)
			throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_SPACE))
			points = !points;
		
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
