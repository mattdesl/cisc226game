package space.tests;

import org.newdawn.slick.Image;

/**
 * Used for randomization of starfields. A starfield texture is made up
 * of two textures. 
 */
public class StarField {
    
    private Image sheet;
    private Image[] subImages;
    
    /** 
     * Produces a star field from the given sprite sheet; typically four
     * seamless 512x512 sprites are packed into a 1024x1024 sheet.
     */
    public StarField(Image sheet, int tileSize) {
        this.sheet = sheet;
        int r = sheet.getWidth()/tileSize, c = sheet.getHeight()/tileSize;
        subImages = new Image[r + c];
        for (int x=0, idx=0; x<r; x++) {
            for (int y=0; y<c; y++, idx++) {
                subImages[idx] = sheet.getSubImage(x*tileSize, y*tileSize, tileSize, tileSize);
            }
        }
        
        
    }
    
    
}
