package flappy_bird.objects;

import flappy_bird.interfaces.Renderable;
import flappy_bird.interfaces.Updatable;
import flappy_bird.utils.GameObject;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FpsInfo extends GameObject implements Renderable, Updatable {
	private static final int LEFT_MARGIN = 5;
	private static final int TOP_MARGIN = 5;
	private static final double ONE_SECOND = 1.0;

	private Text text;
	private VBox render;

	private int frameCount = 0;
	private double elapsedTime = 0;

	public FpsInfo() {
		text = new Text("FPS: --");

		render = new VBox(text);
		render.setTranslateX(LEFT_MARGIN);
		render.setTranslateY(TOP_MARGIN);

		text.setFont(Font.font("MONOSPACED"));
		text.setFill(Color.BLACK);
	}

	@Override
	public void update(double deltaTime) {
		frameCount++;
		elapsedTime += deltaTime;

		if (elapsedTime >= ONE_SECOND) {
			text.setText("FPS: " + frameCount);
			frameCount = 0;
			elapsedTime -= ONE_SECOND;
		}
	}

	@Override
	public Node getRender() {
		return render;
	}

	@Override
	public void destroy() {	}

}
