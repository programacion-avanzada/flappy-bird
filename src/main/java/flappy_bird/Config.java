package flappy_bird;

public class Config {
	public final static int baseHeight = 800;
	public final static int baseWidth = 500;
	public final static int groundHeight = 80;

	public final static double gravity = 1300;
	public final static double jumpForce = 500;
	public final static double DEFAULT_SPEED = 250;
	public static double currentSpeed = DEFAULT_SPEED;

	public final static double emptySpace = 0.25;

	public final static double pipesPerSecond = 1.3;
	public final static int playerCenter = baseWidth / 3;
	
	public static int maxScore = 0;

	private Config() {
	}
}
