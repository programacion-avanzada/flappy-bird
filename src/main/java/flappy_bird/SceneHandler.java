package flappy_bird;

import java.util.List;

import flappy_bird.interfaces.Updatable;
import flappy_bird.utils.GameObjectBuilder;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public abstract class SceneHandler {
	protected static final long NANOS_IN_SECOND = 1_000_000_000;
	protected static final double NANOS_IN_SECOND_D = 1_000_000_000.0;
	
	
	protected AnimationTimer gameTimer;
	private long previousNanoFrame;
	private boolean firstFrame;
	protected FlappyBirdGame g;

	protected Scene scene;
	
	protected EventHandler<KeyEvent> keyEventHandler;
	protected EventHandler<MouseEvent> mouseEventHandler;

	public SceneHandler(FlappyBirdGame g) {
			this.g = g;
			prepareScene();
			defineEventHandlers();
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void update(double delta) {
		List<Updatable> updatables = GameObjectBuilder.getInstance().getUpdatables();
		for (Updatable updatable : updatables) {
			updatable.update(delta);
		}
	}

	protected void addTimeEventsAnimationTimer() {
		firstFrame = true;
		gameTimer = new AnimationTimer() {
			@Override
			public void handle(long currentNano) {
				if (firstFrame) {
					previousNanoFrame = currentNano;
					firstFrame = false;
					return;
				}
				double deltaTime = (currentNano - previousNanoFrame) / NANOS_IN_SECOND_D;
				previousNanoFrame = currentNano;
				update(deltaTime);
			}
		};

		gameTimer.start();
	}

	protected void addInputEvents() {
		scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
		scene.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
	}

	protected void removeInputEvents() {
		scene.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
		scene.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
	}
	
	protected abstract void prepareScene();
	protected abstract void defineEventHandlers();

	protected void unload() {
		GameObjectBuilder.getInstance().removeAll();
		gameTimer.stop();
		removeInputEvents();
	}
}
