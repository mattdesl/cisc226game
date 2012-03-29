package space.util;

import space.GameContext;
import space.entities.Enemy;
import space.entities.Kamikaze;
import space.entities.Wingbat;
import space.state.InGameState;

public class SpawnController {
	private int wave;
	
	// new spawner starting at wave 1
	public SpawnController(){
		wave = 1; 
	}
	
	public int getWave() {
		return wave;
	}
	
	public void spawnWave(GameContext context){
		InGameState igs = context.getInGameState();
		// decide how many ships to spawn here
		int numKami = Utils.rnd(wave-1, wave+1);
		int numWingbat = Utils.rnd(wave, wave+1);
		System.out.println("Wave :" + wave + "| Kamikazes = " + numKami + " | Wingbats = " + numWingbat);
		// add kamikazes
//		for (int i = 0; i < numKami; i++){
//			Enemy newKami = new Kamikaze(wave);
//			randomizePos(newKami, context);
//			igs.addEntity(newKami);
//		}
		
		for (int i = 0; i < numWingbat; i++){	
			Enemy newWingbat = new Wingbat(wave);
			randomizePos(newWingbat, context);
			igs.addEntity(newWingbat);
		}
		wave++;
	}
	
	
	// split the playable area into 4 "zones", one for each corner. Once we find which zone we're in, 
	// we randomize the position within that zone
	private void randomizePos(Enemy enemy, GameContext context){
		int spawnZone = Utils.rnd(0, 4);
		int xPos = 0;
		int yPos = 0;
		switch (spawnZone){
		case 0: // 0 <= xPos <= 50, 0 <= yPos <= 50. Top left
			xPos = Utils.rnd(0, 51);
			yPos = Utils.rnd(0, 51);
			break;
		case 1: // container.getWidth - 50 <= xPos <= container.getWidth, 0 <= yPos <= 50. Top right
			xPos = Utils.rnd(context.getContainer().getWidth()-50, context.getContainer().getWidth()+1);
			yPos = Utils.rnd(0, 51);
			break;
		case 2: // 0 <= xPos <= 50, container.getHeight - 50 <= yPos <= container.getHeight. bottom left
			xPos = Utils.rnd(0,51);
			yPos = Utils.rnd(context.getContainer().getHeight()-50, context.getContainer().getHeight()+1);
			break;
		case 3: // container.getWidth - 50 <= xPos <= container.getWidth, container.getHeight - 50 <= yPos <= container.getHeight. bottom right
			xPos = Utils.rnd(context.getContainer().getWidth()-50, context.getContainer().getWidth()+1);
			yPos = Utils.rnd(context.getContainer().getHeight()-50, context.getContainer().getHeight()+1);
			break;
		}
		enemy.setPosition(xPos, yPos);				
	}
	


}
