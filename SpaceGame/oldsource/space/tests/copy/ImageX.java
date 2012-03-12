package space.tests.copy;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

/**
 * A new class
 * @author Matt
 */

public class ImageX extends org.newdawn.slick.Image {
   protected float cX, cY;
   
   public ImageX(String path) throws SlickException {
      super(path);
   }
   public ImageX(int width, int height) throws SlickException {
      super(width,height);
   }
   
   public void draw(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color filter) {
      super.draw(x-cX,y-cY,x2-cX,y2-cY,srcx,srcy,srcx2,srcy2,filter);
   }
   public void draw(float x, float y, float width, float height, Color col) {
      super.draw(x-cX,y-cY,width,height,col);
   }
   public void drawFlash(float x, float y, float width, float height, Color col) {
      super.drawFlash(x-cX,y-cY,width,height,col);
   }
   public void drawSheared(float x, float y, float hshear, float vshear) {
      super.drawSheared(x-cX,y-cY,hshear,vshear);
   }
   
   public void setRotation(float angle) {
      super.setRotation(-angle);
   }
   
   public void setCenterOfRotation(float x, float y) {
      super.setCenterOfRotation(x,y);
      cX = x; cY = y;
   }
   public void center() {
      setCenterOfRotation(getWidth()/2f,getHeight()/2f);
   }
}