package flappy_bird.objects;

import flappy_bird.Config;
import flappy_bird.interfaces.Collidator;
import flappy_bird.interfaces.Collideable;
import flappy_bird.interfaces.Renderable;
import flappy_bird.interfaces.Updatable;
import flappy_bird.utils.GameObject;
import flappy_bird.utils.IndividualSpriteAnimation;
import flappy_bird.utils.Sound;
import flappy_bird.utils.Utils;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class FlappyBird extends GameObject implements Updatable, Renderable, Collidator {
	private final double ROTATION_FREE_FALL = 20;
	private final double ROTATION_THRESHOLD = 0.6;
	private final double ROTATION_SPEED = 250;

	private Score score;

	private final int width = 51;
	private final int height = 36;

	private double posX;
	private double posY;
	private double velY = 0;
	private double rotation = 0;
	private double timeStandby = 0;

	private boolean idle = true;
	private boolean dead = false;
	private boolean grounded = false;
	private boolean freeFall = false;

	private Image imageBase;
	private Image imageUp;
	private Image imageDown;
	
	private Sound dieAudio;
	private Sound hitAudio;
	private Sound wingAudio;

	private ImageView render;

	private Rectangle collider;
	private final double colliderTolerance = 0.75;
	private final int colliderWidth = (int) (width * colliderTolerance);
	private final int colliderHeight = (int) (height * colliderTolerance);

	private final IndividualSpriteAnimation flappyAnimation;
	private final TranslateTransition idleAnimation;
	private final Duration translateDuration = Duration.millis(1000);

	public FlappyBird(int x, int y, Score score) {
		posY = y;
		posX = x;
		this.score = score;

		initImages();
		initAudios();
		render = new ImageView(imageBase);
		render.relocate(posX - width / 2, 0);

		collider = new Rectangle(posX - colliderWidth / 2, posY - colliderHeight / 2, colliderWidth, colliderHeight);
		collider.setFill(null);
		collider.setStroke(Color.FUCHSIA);

		flappyAnimation = initFlappyAnimation();
		idleAnimation = initIdleAnimation();
	}

	private void initImages() {
		imageUp = new Image("file:src/main/resources/img/flappy-bird-up.png", width, height, false, false);
		imageBase = new Image("file:src/main/resources/img/flappy-bird.png", width, height, false, false);
		imageDown = new Image("file:src/main/resources/img/flappy-bird-down.png", width, height, false, false);

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
		dieAudio = Sound.getDieAudio();
		hitAudio = Sound.getHitAudio();
		wingAudio = Sound.getWingAudio();
	}

	private IndividualSpriteAnimation initFlappyAnimation() {
		IndividualSpriteAnimation individualSpriteAnimation = new IndividualSpriteAnimation(
				new Image[] { imageUp, imageBase, imageDown }, render, Duration.millis(500));
		individualSpriteAnimation.setCustomFrames(new int[] { 0, 1, 2, 1 });
		individualSpriteAnimation.setCycleCount(Animation.INDEFINITE);
		individualSpriteAnimation.play();
		return individualSpriteAnimation;
	}

	private TranslateTransition initIdleAnimation() {
		TranslateTransition translateTransition = new TranslateTransition(translateDuration, render);
		translateTransition.setCycleCount(Animation.INDEFINITE);
		translateTransition.setFromY(-10);
		translateTransition.setToY(10);
		translateTransition.setAutoReverse(true);
		translateTransition.play();
		return translateTransition;
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
				setRotation(Math.min(-30 + timeStandby * ROTATION_SPEED, 90));
			}

			if (rotation > ROTATION_FREE_FALL && !freeFall) {
				freeFall();
			}
		}
	}

	private void freeFall() {
		freeFall = true;
		flappyAnimation.stop();
		render.setImage(imageBase);
	}

	public void push() {
		if (!dead) {
			wingAudio.play();
			idle = false;
			freeFall = false;
			idleAnimation.jumpTo(translateDuration.divide(2));
			idleAnimation.stop();
			flappyAnimation.play();
			velY = -Config.jumpForce;
			timeStandby = -ROTATION_THRESHOLD;
			setRotation(-20);
		}
	}

	public boolean isDead() {
		return dead;
	}

	private void setY(double posY) {
		this.posY = posY;
		render.setY(posY - height / 2);
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
					setY(((Rectangle) ((Ground) collideable).getCollider()).getY() - height / 2);
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

	@Override
	public void destroy() { }
}
