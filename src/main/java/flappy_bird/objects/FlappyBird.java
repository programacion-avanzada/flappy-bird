package flappy_bird.objects;

import flappy_bird.Config;
import flappy_bird.interfaces.Collidator;
import flappy_bird.interfaces.Collideable;
import flappy_bird.interfaces.Renderable;
import flappy_bird.interfaces.Updatable;
import flappy_bird.utils.AudioResources;
import flappy_bird.utils.GameObject;
import flappy_bird.utils.IndividualSpriteAnimation;
import flappy_bird.utils.Utils;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class FlappyBird extends GameObject implements Updatable, Renderable, Collidator {
	// Sprite dimensions
	private static final int WIDTH = 51;
	private static final int HEIGHT = 36;

	// Rotation constants
	private static final double ROTATION_FREE_FALL = 20;
	private static final double ROTATION_THRESHOLD = 0.6;
	private static final double ROTATION_SPEED = 250;
	private static final double ROTATION_ON_PUSH = -20;
	private static final double ROTATION_MIN = -30;
	private static final double ROTATION_MAX = 90;

	// Animation constants
	private static final double IDLE_BOB_AMPLITUDE = 10;
	private static final Duration IDLE_BOB_DURATION = Duration.millis(1000);
	private static final Duration FLAP_CYCLE_DURATION = Duration.millis(500);

	// Collider
	private static final double COLLIDER_TOLERANCE = 0.5;
	private final int colliderWidth = (int) (WIDTH * COLLIDER_TOLERANCE);
	private final int colliderHeight = (int) (HEIGHT * COLLIDER_TOLERANCE);

	private Score score;

	private double posX;
	private double posY;
	private double velY = 0;
	private double rotation = 0;
	private double timeStandby = 0;

	private boolean idle = true;
	private boolean dead = false;
	private boolean grounded = false;

	private Image imageBase;
	private Image imageUp;
	private Image imageDown;
	
	private AudioClip dieAudio;
	private AudioClip hitAudio;
	private AudioClip wingAudio;

	private ImageView render;

	private Rectangle collider;

	private final IndividualSpriteAnimation flappyAnimation;
	private final TranslateTransition idleAnimation;

	public FlappyBird(int x, int y, Score score) {
		posY = y;
		posX = x;
		this.score = score;

		initImages();
		initAudios();
		render = new ImageView(imageBase);
		render.relocate(posX - WIDTH / 2, 0);

		collider = new Rectangle(posX - colliderWidth / 2, posY - colliderHeight / 2, colliderWidth, colliderHeight);
		collider.setFill(null);
		collider.setStroke(Color.FUCHSIA);

		flappyAnimation = initFlappyAnimation();
		idleAnimation = initIdleAnimation();
	}

	private void initImages() {
		imageUp = new Image(ClassLoader.getSystemResourceAsStream("img/flappy-bird-up.png"), WIDTH, HEIGHT, false, false);
		imageBase = new Image(ClassLoader.getSystemResourceAsStream("img/flappy-bird.png"), WIDTH, HEIGHT, false, false);
		imageDown = new Image(ClassLoader.getSystemResourceAsStream("img/flappy-bird-down.png"), WIDTH, HEIGHT, false, false);

		Color[] original = { Color.rgb(247, 182, 67), Color.rgb(215, 229, 204), Color.rgb(208, 48, 21),
				Color.rgb(249, 58, 28), Color.rgb(249, 115, 39) };
		Color[] blue = { Color.rgb(228, 96, 23), Color.rgb(215, 229, 204), Color.rgb(65, 163, 209),
				Color.rgb(74, 193, 248), Color.rgb(84, 208, 255) };
		Color[] yellow = { Color.rgb(252, 56, 0), Color.rgb(252, 216, 132), Color.rgb(224, 128, 44),
				Color.rgb(249, 183, 51), Color.rgb(250, 215, 140) };

		Color[][] posibleColos = { original, blue, yellow };

		int randomIndex = (int) Math.floor(Math.random() * 3);
		Color[] colorRandom = posibleColos[randomIndex];

		imageUp = Utils.reColor(imageUp, original, colorRandom);
		imageBase = Utils.reColor(imageBase, original, colorRandom);
		imageDown = Utils.reColor(imageDown, original, colorRandom);
	}
	
	private void initAudios() {
		dieAudio = AudioResources.getDieAudio();
		hitAudio = AudioResources.getHitAudio();
		wingAudio = AudioResources.getWingAudio();
	}

	private IndividualSpriteAnimation initFlappyAnimation() {
		IndividualSpriteAnimation animation = new IndividualSpriteAnimation(
				new Image[] { imageUp, imageBase, imageDown }, render, FLAP_CYCLE_DURATION);
		animation.setCustomFrames(new int[] { 0, 1, 2, 1 });
		animation.setCycleCount(Animation.INDEFINITE);
		animation.play();
		return animation;
	}

	private TranslateTransition initIdleAnimation() {
		TranslateTransition animation = new TranslateTransition(IDLE_BOB_DURATION, render);
		animation.setCycleCount(Animation.INDEFINITE);
		animation.setFromY(-IDLE_BOB_AMPLITUDE);
		animation.setToY(IDLE_BOB_AMPLITUDE);
		animation.setAutoReverse(true);
		animation.play();
		return animation;
	}

	@Override
	public void update(double deltaTime) {
		timeStandby += deltaTime;
		setY(posY + velY * deltaTime);

		if (!idle) {
			if (!grounded) {
				velY += Config.gravity * deltaTime;
			}

			if (timeStandby > 0) {
				setRotation(Math.min(ROTATION_MIN + timeStandby * ROTATION_SPEED, ROTATION_MAX));
			}

			if (rotation > ROTATION_FREE_FALL && flappyAnimation.getStatus() == Animation.Status.RUNNING) {
				flappyAnimation.stop();
				render.setImage(imageBase);
			}
		}
	}

	public void push() {
		if (!dead) {
			wingAudio.play();
			idle = false;
			idleAnimation.jumpTo(IDLE_BOB_DURATION.divide(2));
			idleAnimation.stop();
			flappyAnimation.play();
			velY = -Config.jumpForce;
			timeStandby = -ROTATION_THRESHOLD;
			setRotation(ROTATION_ON_PUSH);
		}
	}

	public boolean isDead() {
		return dead;
	}

	private void setY(double posY) {
		this.posY = posY;
		render.setY(posY - HEIGHT / 2);
		collider.setY(posY - colliderHeight / 2);
	}

	private void setRotation(double rotation) {
		this.rotation = rotation;
		render.setRotate(rotation);
	}

	@Override
	public ImageView getRender() {
		return render;
	}

	@Override
	public Shape getCollider() {
		return collider;
	}

	@Override
	public void collide(Collideable collideable) {
		if (!grounded) {
			if (collideable.getClass() == ScoreCollider.class) {
				score.increase();
				((ScoreCollider) collideable).remove();
			} else {
				if (!dead) {
					hitAudio.play();
					dieAudio.play();
					dead = true;
				}
				if (collideable.getClass() == Ground.class) {
					setY(((Rectangle) ((Ground) collideable).getCollider()).getY() - HEIGHT / 2);
					velY = 0;
					grounded = true;
				}
			}
		}
	}

	public double getX() {
		return posX;
	}

	public double getY() {
		return posY;
	}

	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public void destroy() {
		flappyAnimation.stop();
		idleAnimation.stop();
	}
}
