package flappy_bird.objects;

import flappy_bird.Config;
import flappy_bird.interfaces.Renderable;
import flappy_bird.utils.AudioResources;
import flappy_bird.utils.GameObject;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class Score extends GameObject implements Renderable {
	// Layout constants
	private static final int TOP_MARGIN = 15;
	private static final int SPACING = 5;

	// Font sizes
	private static final int SCORE_FONT_SIZE = 50;
	private static final int HIGH_SCORE_FONT_SIZE = 15;

	// Animation constants
	private static final double PULSE_PEAK_SCALE = 1.15;
	private static final Duration PULSE_PEAK_TIME = Duration.millis(150);
	private static final Duration PULSE_TOTAL_TIME = Duration.millis(400);

	private int score = 0;

	private Text scoreText;
	private Text maxScoreText;
	private VBox render;

	private Timeline pulseAnimation;
	private AudioClip pointAudio;

	public Score() {
		scoreText = new Text("" + score);
		maxScoreText = new Text("TOP: " + Config.maxScore);

		render = new VBox(maxScoreText, scoreText);
		render.setSpacing(SPACING);
		render.setAlignment(Pos.TOP_CENTER);
		render.setTranslateY(TOP_MARGIN);
		render.setPrefWidth(Config.baseWidth);

		pointAudio = AudioResources.getPointAudio();

		Font font = Font.loadFont(ClassLoader.getSystemResource("font/flappy-bird-numbers.ttf").toString(), SCORE_FONT_SIZE);
		scoreText.setTextAlignment(TextAlignment.CENTER);
		scoreText.setFont(font);
		scoreText.setFill(Color.BLACK);

		maxScoreText.setFont(Font.font("MONOSPACE", HIGH_SCORE_FONT_SIZE));

		DropShadow ds = new DropShadow();
		ds.setColor(Color.WHITE);
		scoreText.setEffect(ds);

		pulseAnimation = initPulseAnimation();
	}

	private Timeline initPulseAnimation() {
		return new Timeline(
				new KeyFrame(Duration.ZERO,
						new KeyValue(scoreText.scaleXProperty(), 1),
						new KeyValue(scoreText.scaleYProperty(), 1)),
				new KeyFrame(PULSE_PEAK_TIME,
						new KeyValue(scoreText.scaleXProperty(), PULSE_PEAK_SCALE),
						new KeyValue(scoreText.scaleYProperty(), PULSE_PEAK_SCALE)),
				new KeyFrame(PULSE_TOTAL_TIME,
						new KeyValue(scoreText.scaleXProperty(), 1),
						new KeyValue(scoreText.scaleYProperty(), 1)));
	}

	@Override
	public Node getRender() {
		return render;
	}

	public void increase() {
		score++;
		scoreText.setText("" + score);
		this.updateHighScore();

		pulseAnimation.playFromStart();
		pointAudio.play();
	}

	public int getScore() {
		return score;
	}
	
	public void updateHighScore() {
		if (this.score > Config.maxScore) {
			Config.maxScore = this.score;
			maxScoreText.setText("TOP: " + Config.maxScore);
		}
	}

	@Override
	public void destroy() {
		pulseAnimation.stop();
	}
	
}
