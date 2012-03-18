package space.entities;

public class Constants {
	public static final float PLAYER_MOVE_SPEED = 1500f;
	public static final float PLAYER_TURN_SPEED = 0.25f;
	public static final float PLAYER_STRAFE_SPEED = 1200f;
	public static final float PLAYER_MAX_SPEED = 40f;
	public static final float PLAYER_BLASTER_SPEED = 2000f;
	public static final float CAM_ZOOM = .08f;
	public static final float DECAY_SPEED = .010f;


	public static final int BIT_PLAYER = 2;
	public static final int BIT_ENEMY = 4;
	public static final int BIT_BULLET = 8;
	
	public static final int BITMASK_WALL = BIT_ENEMY | BIT_BULLET;
	
	// enemy constants
	public static final float ENEMY_KAMIKAZE_SPEED = 30f; 
}