package flappy_bird;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FlappyBirdGame extends Application {
	private Stage stage;
	private GameCanvas canvas;

	private MenuSceneHandler menuSceneHandler;
	private GameSceneHandler gameSceneHandler;

	@Override
	public void start(Stage stage) {
		this.stage = stage;

		canvas = new GameCanvas();
		stage.setScene(canvas.getScene());

		menuSceneHandler = new MenuSceneHandler(this);
		menuSceneHandler.load();

		// XXX patron state para controlar paso de escenas?

		stage.setMinWidth(Config.baseWidth / 2);
		stage.setMinHeight(Config.baseHeight / 2);
		stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("ico/logo.png")));
		stage.setTitle("Flappy Bird FXGame | Programación Avanzada");
		stage.show();
	}

	public GameCanvas getCanvas() {
		return canvas;
	}

	public static void main(String[] args) {
		launch();
	}

	public void startGame() {
		menuSceneHandler.unload();
		gameSceneHandler = new GameSceneHandler(this);
		gameSceneHandler.load(true);
	}

	public void startMenu() {
		gameSceneHandler.unload();
		menuSceneHandler = new MenuSceneHandler(this);
		menuSceneHandler.load();
	}
}
