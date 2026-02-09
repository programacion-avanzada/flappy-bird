package flappy_bird.objects;

import flappy_bird.Config;
import flappy_bird.interfaces.Renderable;
import flappy_bird.interfaces.Updatable;
import flappy_bird.utils.GameObject;
import flappy_bird.utils.GameObjectBuilder;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Radio extends GameObject implements Updatable, Renderable {
	// Sprite dimensions
	private static final int WIDTH = 50;
	private static final int HEIGHT = 43;

	// Throw animation constants
	private static final Duration THROW_DURATION = Duration.millis(1500);
	private static final double THROW_HEIGHT = 250;
	private static final double THROW_ROTATION = 1130;
	private static final double LANDED_SCALE = 0.75;
	private static final double AIR_SPEED_FACTOR = 0.25;

	// Sound distance constants
	private static final double MAX_SOUND_DISTANCE = 1500;
	private static final double REMOVE_DISTANCE = 5000;

	// Custom interpolators for physics-like animation
	private static final Interpolator EASE_IN_QUAD = new Interpolator() {
		@Override
		protected double curve(double t) {
			return t * t; // Accelerating (like gravity)
		}
	};

	private static final Interpolator EASE_OUT_QUAD = new Interpolator() {
		@Override
		protected double curve(double t) {
			return 1 - (1 - t) * (1 - t); // Decelerating (like throwing up)
		}
	};

	private final FlappyBird player;
	private final double radioBaseY;

	private MediaPlayer mediaPlayer;
	private ImageView render;
	private Image image;
	private double posX;
	private boolean started = false;
	private boolean inAir = true;

	private Animation thrownAnimation;

	public Radio(double posX, double posY, FlappyBird player) {
		this.posX = posX;
		this.player = player;

		image = new Image(ClassLoader.getSystemResourceAsStream("img/portal-radio.png"), WIDTH, HEIGHT, false, false);
		render = new ImageView(image);
		render.setTranslateX(posX - WIDTH / 2);

		// Bind radio Y position to bird's translateY for synchronized bobbing
		radioBaseY = posY - HEIGHT - player.getHeight() / 2 + 2;
		render.translateYProperty().bind(
				player.getRender().translateYProperty().add(radioBaseY));

		Media loop = new Media(ClassLoader.getSystemResource("snd/looping-radio-mix.mp3").toString());
		mediaPlayer = new MediaPlayer(loop);
		mediaPlayer.setVolume(1);
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaPlayer.play();
	}

	public void start() {
		started = true;
		// Unbind from bird before starting thrown animation
		render.translateYProperty().unbind();
		render.setTranslateY(radioBaseY + player.getRender().getTranslateY());

		thrownAnimation = initThrownAnimation();
		thrownAnimation.play();
		thrownAnimation.setOnFinished(e -> inAir = false);
	}

	private Animation initThrownAnimation() {
		// Rising phase (1/3 of duration) - decelerates like throwing upward
		TranslateTransition riseTransition = new TranslateTransition(THROW_DURATION.divide(3));
		riseTransition.setInterpolator(EASE_OUT_QUAD);
		riseTransition.setToY(render.getTranslateY() - THROW_HEIGHT);

		// Falling phase (2/3 of duration) - accelerates like gravity
		TranslateTransition fallTransition = new TranslateTransition(THROW_DURATION.divide(3).multiply(2));
		fallTransition.setInterpolator(EASE_IN_QUAD);
		fallTransition.setToY(Config.baseHeight - Config.groundHeight - HEIGHT * 2 / 3);

		SequentialTransition translateSequence = new SequentialTransition(render, riseTransition, fallTransition);

		RotateTransition rotateTransition = new RotateTransition(THROW_DURATION);
		rotateTransition.setToAngle(THROW_ROTATION);

		ScaleTransition scaleTransition = new ScaleTransition(THROW_DURATION);
		scaleTransition.setInterpolator(Interpolator.EASE_OUT);
		scaleTransition.setToX(LANDED_SCALE);
		scaleTransition.setToY(LANDED_SCALE);

		return new ParallelTransition(render, translateSequence, rotateTransition, scaleTransition);
	}

	@Override
	public ImageView getRender() {
		return render;
	}

	@Override
	public void update(double deltaTime) {
		if (!started) {
			return;
		}

		// Move slower while in air, normal speed on ground
		double speedFactor = inAir ? AIR_SPEED_FACTOR : 1;
		posX -= Config.currentSpeed * deltaTime * speedFactor;
		render.setTranslateX(posX - WIDTH / 2);

		// Calculate distance for spatial audio
		double distance = Math.hypot(player.getX() - posX, player.getY() - render.getTranslateY());

		if (distance > REMOVE_DISTANCE) {
			GameObjectBuilder.getInstance().remove(this);
		} else {
			// Stereo balance based on horizontal distance
			mediaPlayer.setBalance(-distance / MAX_SOUND_DISTANCE);
			// Volume falls off with square of distance
			double normalizedDistance = (MAX_SOUND_DISTANCE - distance) / MAX_SOUND_DISTANCE;
			mediaPlayer.setVolume(normalizedDistance < 0 ? 0 : normalizedDistance * normalizedDistance);
		}
	}

	@Override
	public void destroy() {
		mediaPlayer.stop();
	}

}
