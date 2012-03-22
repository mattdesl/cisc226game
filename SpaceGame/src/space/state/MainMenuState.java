package space.state;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import space.GameContext;
import space.engine.SpriteBatch;
import space.engine.StyledText;
import space.util.Resources;

public class MainMenuState extends AbstractState {

	private Image background;
	private Image title;
	private Image newGame;
	private Image exit;
	
	public MainMenuState(GameContext context){
		super(context, 0);
	}
	@Override
	public void init(GameContext context) throws SlickException {
		background = Resources.getSprite("background");
		title = Resources.getSprite("title");
		newGame = Resources.getSprite("newGame");
		exit = Resources.getSprite("exit");
	}

	@Override
	public void render(GameContext context, SpriteBatch batch, Graphics g)
			throws SlickException {
		// TODO Auto-generated method stub
		int cx = context.getContainer().getWidth();
		int cy = context.getContainer().getHeight();
		int titley = cy / 2 - title.getHeight()/2;
		batch.drawImage(background, 0,0, cx, cy);
		batch.drawText(Resources.getSquareFont(), "Blastronomy", cx / 2 - title.getWidth()/2, titley);
		batch.drawText(Resources.getNiceFont(), "New Game", cx / 2 - newGame.getWidth()/2 , titley + 20);
		batch.drawText(Resources.getNiceFont(), "Exit", cx / 2 - exit.getWidth()/2, titley + 40);
	}

	@Override
	public void update(GameContext context, int delta) throws SlickException {
		// TODO Auto-generated method stub
		Input input = context.getInput();
		
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();
		int cx = context.getContainer().getWidth();
		int cy = context.getContainer().getHeight();
		int newGameX = cx / 2 - newGame.getWidth()/2;
		int newGameY = cy / 2 - title.getHeight()/2+20;
		int exitX = cx / 2 - exit.getWidth()/2;
		int exitY = cy / 2 - exit.getHeight()/2+40;
		
		
		boolean inNewGame = false;
		boolean inExit = false;
		
		// dirty
		if ( (mouseX >= newGameX && mouseX <= newGameX + newGame.getWidth()) 
				&&
		     (mouseY >= newGameY && mouseY <= newGameY + newGame.getHeight())){
			inNewGame = true;
		} else if ((mouseX >= exitX && mouseX <= exitX + exit.getWidth())
					&& 
				   (mouseY >= exitY && mouseY <= exitY + exit.getHeight())){
			inExit = true;
		}
		
		if (inNewGame){
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)){
				context.enterGame();
			}
		}
		
		if (inExit){
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)){
				System.exit(0);
			}
		}
	}
}
