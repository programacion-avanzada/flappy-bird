package flappy_bird;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FlappyBirdGame extends Application {
	private Stage stage;

	private MenuSceneHandler menuSceneHandler;

	private GameSceneHandler gameSceneHandler;
	@Override
	public void start(Stage stage) {
		this.stage = stage;

		menuSceneHandler = new MenuSceneHandler(this);
		Scene scene = menuSceneHandler.getScene();
		stage.setScene(scene);

		menuSceneHandler.load();
		
		// XXX patron state para controlar paso de escenas?

		// Scale
		// TODO scale and fill to maintain proportion (also center)
		// scale = new Scale();
		// dinamico, cada vez que cambio el tamaÃ±o de ventana
		// scale.setX(scene.getWidth() / WIDTH);
		// scale.setY(scene.getHeight() / HEIGHT);
		// images.getTransforms().add(scale);

		stage.getIcons().add(new Image("file:src/main/resources/ico/logo.png"));
		stage.setTitle("Flappy Bird FXGame | Programación Avanzada");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}

	public void startGame() {
		menuSceneHandler.unload();
		gameSceneHandler = new GameSceneHandler(this);
		Scene scene = gameSceneHandler.getScene();
		stage.setScene(scene);
		gameSceneHandler.load(true);
	}
	
	public void startMenu() {
		gameSceneHandler.unload();
		menuSceneHandler = new MenuSceneHandler(this);
		Scene scene = menuSceneHandler.getScene();
		stage.setScene(scene);
		menuSceneHandler.load();
	}
}
