package flappy_bird.objects;

import flappy_bird.Config;
import flappy_bird.interfaces.Collideable;
import flappy_bird.interfaces.Updatable;
import flappy_bird.utils.GameObject;
import flappy_bird.utils.GameObjectBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ScoreCollider extends GameObject implements Updatable, Collideable {
	private Rectangle collider;
	
	private double posX;

	public ScoreCollider(int x) {
		posX = x;
		collider = new Rectangle(x, 0, Config.baseWidth / 10, Config.baseHeight - Config.groundHeight);
		collider.setFill(null);
		collider.setStroke(Color.WHITE);
		collider.setStrokeWidth(2);
	}

	@Override
	public void update(double deltaTime) {
		posX -= Config.baseSpeed * deltaTime;
		collider.setX(posX);
	}

	@Override
	public Shape getCollider() {
		return collider;
	}

	public void remove() {
		GameObjectBuilder.getInstance().remove(this);
	}

}
