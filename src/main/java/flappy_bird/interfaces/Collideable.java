package flappy_bird.interfaces;

import javafx.scene.shape.Shape;

// TODO separar que pueda colisionar y ser colisionado o solo ser colisionado
public interface Collideable {
	public Shape getCollider();
}
