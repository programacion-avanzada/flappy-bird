package flappy_bird.objects;

import java.io.File;

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
	private MediaPlayer mediaPlayer;
	private ImageView render;

	private Image image;
	private double posX;
	private boolean started = false;
	private boolean irAir = true;

	private final int width = 50;
	private final int height = 43;
	private final FlappyBird player;

	private Animation thrownAnimation;
	private final Animation idleAnimation;

	private final Duration translateDuration = Duration.millis(1000);
	private final Duration throwDuration = Duration.millis(1500);
	private final double maxDistanceSound = 1500;
	private final double distanceRemoveSound = 5000;

	public Radio(double posX, double posY, FlappyBird player) {
		this.posX = posX;
		this.player = player;

		image = new Image("file:src/main/resources/img/portal-radio.png", width, height, false, false);
		render = new ImageView(image);
		render.setTranslateX(posX - width / 2);
		render.setTranslateY(posY - height - player.getHeight() / 2 + 2); // XXX magic number player.height

		Media loop = new Media(new File("src/main/resources/snd/looping-radio-mix.mp3").toURI().toString());
		mediaPlayer = new MediaPlayer(loop);
		mediaPlayer.setVolume(1);
		// mediaPlayer.seek(Duration.ZERO);
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaPlayer.play();

		idleAnimation = initIdleAnimation();
		idleAnimation.play();
		
	}

	public void start() {
		started = true;
		idleAnimation.stop();
		thrownAnimation = initThrownAnimation();
		thrownAnimation.play();
		thrownAnimation.setOnFinished(e -> irAir = false);
	}

	private Animation initIdleAnimation() {
		TranslateTransition translateTransition = new TranslateTransition(translateDuration, render);
		translateTransition.setCycleCount(Animation.INDEFINITE);
		translateTransition.setFromY(render.getTranslateY() - 10);
		translateTransition.setToY(render.getTranslateY() + 10);
		translateTransition.setAutoReverse(true);
		translateTransition.jumpTo(Duration.millis(120));

		return translateTransition;
	}

	private Animation initThrownAnimation() {
		Interpolator gravityInterpolator = new Interpolator() {
			@Override
			protected double curve(double t) {
				return t * t;
			}
		};
		
		Interpolator inverseGravityInterpolator = new Interpolator() {
			@Override
			protected double curve(double t) {
				return 1 - (1 - t) * (1 - t);
			}
		};

		TranslateTransition translateTransition = new TranslateTransition(throwDuration.divide(3));
		translateTransition.setInterpolator(inverseGravityInterpolator);
		translateTransition.setToY(render.getTranslateY() - 250);

		TranslateTransition translateTransition2 = new TranslateTransition(throwDuration.divide(3).multiply(2));
		translateTransition2.setInterpolator(gravityInterpolator);
		translateTransition2.setToY(Config.baseHeight - Config.groundHeight - height * 2 / 3);
		SequentialTransition translateSequence = new SequentialTransition(render, translateTransition, translateTransition2);

		RotateTransition rotateTransition = new RotateTransition(throwDuration);
		rotateTransition.setToAngle(1130);

		ScaleTransition scaleTransition = new ScaleTransition(throwDuration);
		scaleTransition.setInterpolator(Interpolator.EASE_OUT);
		scaleTransition.setToX(0.75);
		scaleTransition.setToY(0.75);
		
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
		posX -= Config.baseSpeed * deltaTime * (irAir ? 0.25 : 1);
		render.setTranslateX(posX - width / 2);
		double distance = Math.hypot(player.getX() - posX, player.getY() - render.getTranslateY());
		if (distance > distanceRemoveSound) {
			GameObjectBuilder.getInstance().remove(this);
		} else {
			mediaPlayer.setBalance(-distance / maxDistanceSound);
			double linearVolume = (maxDistanceSound - distance) / maxDistanceSound;
			mediaPlayer.setVolume(linearVolume < 0 ? 0 : Math.pow((maxDistanceSound - distance) / maxDistanceSound, 2));
		}
	}

	@Override
	public void destroy() {
		mediaPlayer.stop();
	}

}
