package flappy_bird;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import flappy_bird.interfaces.Updatable;
import flappy_bird.objects.Background;
import flappy_bird.objects.FlappyBird;
import flappy_bird.objects.FpsInfo;
import flappy_bird.objects.Ground;
import flappy_bird.objects.menu.TextoComenzar;
import flappy_bird.objects.menu.Title;
import flappy_bird.utils.GameObjectBuilder;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class MenuSceneHandler {
	private final long NANOS_IN_SECOND = 1_000_000_000;
	private final double NANOS_IN_SECOND_D = 1_000_000_000.0;

	private int frames = 0;
	private int last_fps_frame = 0;
	private AtomicInteger fps = new AtomicInteger(0);

	private FlappyBird player;
	private Background background;
	private Ground ground;
	private FpsInfo fpsInfo;
	private Title title;
	private TextoComenzar textoComenzar;

	AnimationTimer gameTimer;

	private long previousNanoFrame;
	private long previousNanoSecond;

	EventHandler<KeyEvent> keyEventHandler;

	private Scene scene;
	private Group rootGroup;

	public MenuSceneHandler(FlappyBirdGame g) {
		rootGroup = new Group();
		scene = new Scene(rootGroup, Config.baseWidth, Config.baseHeight, Color.BLACK);

		keyEventHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				switch (e.getCode()) {

				case UP:
				case W:
				case SPACE:
				case ENTER:
					g.startGame();
				case ESCAPE:
					// TODO quit
					break;
				default:
					break;
				}
			}
		};
	}

	public void load() {
		Group baseGroup = new Group();
		rootGroup.getChildren().add(baseGroup);
		player = new FlappyBird(Config.baseWidth - 75, Config.baseHeight / 3, null);
		background = new Background();
		ground = new Ground();
		fpsInfo = new FpsInfo(fps);
		title = new Title();
		textoComenzar = new TextoComenzar();

		GameObjectBuilder gameOB = GameObjectBuilder.getInstance();
		gameOB.setRootNode(baseGroup);
		gameOB.add(background, player, ground, title, textoComenzar, fpsInfo);

		addTimeEventsAnimationTimer();
		addInputEvents();
	}

	public void unload() {
		rootGroup.getChildren().remove(0);
		GameObjectBuilder.getInstance().removeAll();
		player = null;
		background = null;
		ground = null;
		fpsInfo = null;
		title = null;
		textoComenzar = null;
		gameTimer.stop();
		gameTimer = null;
		removeInputEvents();
	}

	private void addInputEvents() {
		scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
	}

	private void removeInputEvents() {
		scene.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
	}

	private void addTimeEventsAnimationTimer() {
		gameTimer = new AnimationTimer() {
			@Override
			public void handle(long currentNano) {
				// Update tick
				update((currentNano - previousNanoFrame) / NANOS_IN_SECOND_D);
				previousNanoFrame = currentNano;

				// Update second
				if (currentNano - previousNanoSecond > NANOS_IN_SECOND) {
					oneSecondUpdate((currentNano - previousNanoSecond) / NANOS_IN_SECOND_D);
					previousNanoSecond = currentNano;
				}

			}
		};

		previousNanoSecond = System.nanoTime();
		previousNanoFrame = System.nanoTime();
		gameTimer.start();
	}

	public void update(double delta) {
		frames++;

		List<Updatable> updatables = GameObjectBuilder.getInstance().getUpdatables();
		for (Updatable updatable : updatables) {
			updatable.update(delta);
		}
	}

	public void oneSecondUpdate(double delta) {
		fps.set(frames - last_fps_frame);
		last_fps_frame = frames;
	}

	public Scene getScene() {
		return scene;
	}

	public void onStart(Object object) { }
}
