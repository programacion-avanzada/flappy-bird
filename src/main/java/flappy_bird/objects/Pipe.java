package flappy_bird.objects;

import flappy_bird.Config;
import flappy_bird.interfaces.Collideable;
import flappy_bird.interfaces.Renderable;
import flappy_bird.interfaces.Updatable;
import flappy_bird.utils.GameObject;
import flappy_bird.utils.GameObjectBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Pipe extends GameObject implements Updatable, Renderable, Collideable {
	private double posX;

	private final int width = 78;
	@SuppressWarnings("unused")
	private final int height;
	private final int imageHeight = 42;
	private final int offScreenTolerance = 50;

	private VBox render;
	private Rectangle collider;

	public Pipe(int x, int height, boolean fromTop) {
		this.height = height;

		Image pipe;
		Image pipePattern;
		if (fromTop) {
			pipe = new Image("file:src/main/resources/img/pipe1a.png", width, imageHeight, false, false);
			pipePattern = new Image("file:src/main/resources/img/pipe1b.png", width, 3, false, false);
		} else {
			pipe = new Image("file:src/main/resources/img/pipe2a.png", width, imageHeight, false, false);
			pipePattern = new Image("file:src/main/resources/img/pipe2b.png", width, 3, false, false);
		}
		ImageView imageView = new ImageView(pipe);
		ImagePattern imagePattern = new ImagePattern(pipePattern);

		Rectangle rectanglePattern = new Rectangle(width, height - imageHeight);
		rectanglePattern.setFill(imagePattern);
		if (fromTop) {
			render = new VBox(rectanglePattern, imageView);
		} else {
			rectanglePattern.setTranslateY(Config.baseHeight - Config.groundHeight - height);
			imageView.setTranslateY(Config.baseHeight - Config.groundHeight - height);
			render = new VBox(imageView, rectanglePattern);
		}
		render.setViewOrder(1);

		collider = new Rectangle(x, fromTop ? -1e6 : Config.baseHeight - Config.groundHeight - height, width,
				fromTop ? height + 1e6 : height);
		collider.setFill(null);
		collider.setStroke(Color.FUCHSIA);
		collider.setStrokeWidth(2);

		setPosX(x);
	}

	public double getPosX() {
		return posX;
	}

	private void setPosX(double posX) {
		this.posX = posX;
		render.setTranslateX(posX - width / 2);
		collider.setX(posX - width / 2);
	}

	@Override
	public void update(double deltaTime) {
		setPosX(posX + -Config.baseSpeed * deltaTime);

		if (isOffScreen()) {
			GameObjectBuilder.getInstance().remove(this);
		}
	}

	@Override
	public VBox getRender() {
		return render;
	}

	public boolean isOffScreen() {
		return posX + width < -offScreenTolerance;
	}

	@Override
	public Shape getCollider() {
		return collider;
	}
}
