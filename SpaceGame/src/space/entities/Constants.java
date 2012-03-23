package space.entities;

public class Constants {
	public static final float PLAYER_MOVE_SPEED = 2000f;
	public static final float PLAYER_TURN_SPEED = 0.25f;
	public static final float PLAYER_STRAFE_SPEED = 1200f;
	public static final float PLAYER_MAX_SPEED = 50f;
	public static final float PLAYER_BLASTER_SPEED = 2000f;
	public static final float PLAYER_BOOST_SPEED = 750000f;
	public static final int PLAYER_BOOST_DURATION = 400; // duration of afterimages
	public static final int PLAYER_SHIELD_REGEN_SPEED = 5000; //ms for shields to go from 0 -> max
	public static final int PLAYER_SHIELD_REGEN_COOLDOWN = 2000;
	public static final int PLAYER_SHOOTING_COOLDOWN = 200;
	public static final int PLAYER_BOOST_COOLDOWN = 2000; 
	public static final float CAM_ZOOM = .08f;
	public static final float DECAY_SPEED = .010f;


	public static final int BIT_PLAYER = 2;
	public static final int BIT_ENEMY = 4;
	public static final int BIT_BULLET = 8;
	
	public static final int BITMASK_WALL = BIT_ENEMY | BIT_BULLET;
	
	// enemy constants
	public static final float ENEMY_KAMIKAZE_SPEED = 30f; 
	public static final float ENEMY_WINGBAT_SPEED = 10f; // can catch the player np
	public static final float ENEMY_WINGBAT_REV_SPEED = 10f; // reverse speed 
	public static final int ENEMY_WINGBAT_SHOOTING_COOLDOWN = 1550; // initial wingbat shooting speed
	public static final float ENEMY_BLASTER_SPEED = 1200f;
	
	public static final int WAVE_REST_TIME = 2500; // the amount of time in ms before the next wave spawns
}