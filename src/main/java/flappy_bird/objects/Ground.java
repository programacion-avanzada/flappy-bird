package flappy_bird.objects;

import flappy_bird.Config;
import flappy_bird.interfaces.Collideable;
import flappy_bird.interfaces.Renderable;
import flappy_bird.interfaces.Updatable;
import flappy_bird.utils.GameObject;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Ground extends GameObject implements Updatable, Renderable, Collideable {
	private double posX = 0;

	private VBox render;
	
	private Rectangle collider;

	public Ground() {
		Image backgroundImage = new Image("file:src/main/resources/img/ground.png", 24, 88, false, false);

		ImagePattern image_pattern = new ImagePattern(backgroundImage, 24, 88, 24, 88, false);

		Rectangle ground = new Rectangle(Config.baseWidth + 24, 88);
		ground.setFill(image_pattern);

		render = new VBox(ground);
		render.setTranslateY(Config.baseHeight - Config.groundHeight);

		collider = new Rectangle(0, Config.baseHeight - Config.groundHeight, Config.baseWidth, Config.groundHeight);
		collider.setFill(null);
		collider.setStroke(Color.FUCHSIA);
		collider.setStrokeWidth(2);
	}

	public double getPosX() {
		return posX;
	}

	@Override
	public Node getRender() {
		return render;
	}

	@Override
	public void update(double deltaTime) {
		posX += -Config.baseSpeed * deltaTime;

		render.setTranslateX(posX % 24);
	}
	
	@Override
	public Shape getCollider() {
		return collider;
	}
}
