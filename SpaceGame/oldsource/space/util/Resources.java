package space.util;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import space.game.GameContext;

public class Resources {
	
	
	static final int FILMGRAIN_WIDTH = 320;
	static final int FILMGRAIN_HEIGHT = 238;
	static final int FILMGRAIN_SPACING = 1;
	private static Image sheet1, atmosphere;
	
	private static HashMap<String, Image> images = new HashMap<String, Image>();
	
	private static Image fontSheet;
	private static Font font, bold, italic, header;
	public static final int PLAIN = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;
	public static final int HEADER = 3;
	
	public static void create(GameContext context) throws SlickException {
		int f = context.getDetailLevel()==GameContext.DETAIL_LOWEST ? Image.FILTER_NEAREST : Image.FILTER_LINEAR; 
		images.put("sheet", sheet1=new Image("res/tex/sprites.png", false, f));
		images.put("atmosphere", atmosphere=new Image("res/tex/atmos.png", false, f));
		images.put("fonts", fontSheet=new Image("res/fonts/allersheet.png", false, f));
	}
	
	public static Image getImage(String key) {
		return images.get(key);
	}
	
	public static Font getFont(int style) {
		if (style==BOLD) return bold;
		else if (style==ITALIC) return italic;
		else if (style==HEADER) return header;
		else return font;
	}
	
	public static Font getFont() {
		return font;
	}
	

    private static void loadFonts() throws SlickException {
        HashMap<String, Image> sheet = new HashMap<String, Image>();
        try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(ResourceLoader.getResourceAsStream("res/fonts/allersheet.xml"));
			
			NodeList list = doc.getElementsByTagName("sprite");
			for (int i=0;i<list.getLength();i++) {
				Element element = (Element) list.item(i);
				
				String name = element.getAttribute("name");
				int x = Integer.parseInt(element.getAttribute("x"));
				int y = Integer.parseInt(element.getAttribute("y"));
				int width = Integer.parseInt(element.getAttribute("width"));
				int height = Integer.parseInt(element.getAttribute("height"));
				
				sheet.put(name, fontSheet.getSubImage(x,y,width,height));
			}
		} catch (Exception e) {
			throw new SlickException("Failed to parse sprite sheet XML", e);
		}
        
        font = new AngelCodeFont("res/fonts/normal.fnt", sheet.get("normal"));
        bold = new AngelCodeFont("res/fonts/bold.fnt", sheet.get("bold"));
        italic = new AngelCodeFont("res/fonts/italic.fnt", sheet.get("italic"));
        header = new AngelCodeFont("res/fonts/header.fnt", sheet.get("header"));
    }
	
	/**
	 * Once the sheets are loaded, call this to initialize the individual sprites.
	 */
	public static void initSprites(GameContext context) throws SlickException {
		images.put("menu.paper", sheet1.getSubImage(0, 0, 686, 954));
		images.put("menu.bg", sheet1.getSubImage(690, 0, 150, 150));
		images.put("menu.alphaMap", sheet1.getSubImage(690, 153, 256, 256));
		loadFonts();
	}
}
