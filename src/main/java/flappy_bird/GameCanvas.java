package flappy_bird;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

public class GameCanvas {
	private final Scene scene;
	private final Group centerGroup;
	private final Group scaleWrapper;
	private final Scale scaleTransform;

	public GameCanvas() {
		scaleWrapper = new Group();
		centerGroup = new Group(scaleWrapper);

		scaleWrapper.setClip(new Rectangle(Config.baseWidth, Config.baseHeight));

		scaleTransform = new Scale(1, 1, 0, 0);
		scaleWrapper.getTransforms().add(scaleTransform);

		scene = new Scene(centerGroup, Config.baseWidth, Config.baseHeight, Color.BLACK);

		scene.widthProperty().addListener((obs, old, newVal) -> updateScale());
		scene.heightProperty().addListener((obs, old, newVal) -> updateScale());
	}

	private void updateScale() {
		double scaleX = scene.getWidth() / Config.baseWidth;
		double scaleY = scene.getHeight() / Config.baseHeight;
		double scale = Math.min(scaleX, scaleY);

		scaleTransform.setX(scale);
		scaleTransform.setY(scale);

		centerGroup.setTranslateX((scene.getWidth() - Config.baseWidth * scale) / 2);
		centerGroup.setTranslateY((scene.getHeight() - Config.baseHeight * scale) / 2);
	}

	public Scene getScene() {
		return scene;
	}

	public Group getScaleWrapper() {
		return scaleWrapper;
	}
}
