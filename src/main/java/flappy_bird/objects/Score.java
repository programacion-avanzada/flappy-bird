package flappy_bird.objects;

import flappy_bird.Config;
import flappy_bird.interfaces.Renderable;
import flappy_bird.utils.GameObject;
import flappy_bird.utils.Sound;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class Score extends GameObject implements Renderable {
	private final int Y = 15;
	private int score = 0;

	private Text scoreText;
	private Text maxScoreText;
	private VBox render;

	private Animation zoomAnimation;
	private Sound pointAudio;

	public Score() {
		scoreText = new Text("" + score);
		maxScoreText = new Text("TOP: " + Config.maxScore);

		render = new VBox(maxScoreText, scoreText);
		render.setSpacing(5);
		render.setAlignment(Pos.TOP_CENTER);
		render.setTranslateY(Y);
		// Esto debería heredarse?
		render.setPrefWidth(Config.baseWidth);

		// XXX change everything to getResource
		// pointAudio = new AudioClip(getClass().getResource("../../../point.wav").toExternalForm());
		//pointAudio = new AudioClip("file:src/main/resources/sfx/point.wav");
		pointAudio = new Sound("sfx/point.wav");

		Font font = Font.loadFont("file:src/main/resources/font/flappy-bird-numbers.ttf", 50);
		scoreText.setTextAlignment(TextAlignment.CENTER);
		scoreText.setFont(font);
		scoreText.setFill(Color.BLACK);
		
		maxScoreText.setFont(Font.font("MONOSPACE", 15));

		DropShadow ds = new DropShadow();
		ds.setColor(Color.WHITE);
		scoreText.setEffect(ds);
	}

	@Override
	public Node getRender() {
		return render;
	}

	public void increase() {
		score++;
		scoreText.setText("" + score);
		zoomAnimation = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(scoreText.scaleXProperty(), 1),
						new KeyValue(scoreText.scaleYProperty(), 1)),
				new KeyFrame(Duration.seconds(0.15),
						new KeyValue(scoreText.scaleXProperty(), Math.min(1.05 + score / 200.0, 2)),
						new KeyValue(scoreText.scaleYProperty(), Math.min(1.05 + score / 200.0, 2))),
				new KeyFrame(Duration.seconds(0.4), new KeyValue(scoreText.scaleXProperty(), 1),
						new KeyValue(scoreText.scaleYProperty(), 1)));
		zoomAnimation.play();

		pointAudio.play();
	}

	public int getScore() {
		return score;
	}

	@Override
	public void destroy() {	}
	
}
