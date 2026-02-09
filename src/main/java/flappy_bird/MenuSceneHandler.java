package flappy_bird;

import flappy_bird.objects.Background;
import flappy_bird.objects.FlappyBird;
import flappy_bird.objects.FpsInfo;
import flappy_bird.objects.Ground;
import flappy_bird.objects.menu.TextoComenzar;
import flappy_bird.objects.menu.Title;
import flappy_bird.utils.GameObjectBuilder;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MenuSceneHandler extends SceneHandler {

	private FlappyBird player;
	private Background background;
	private Ground ground;
	private Title title;
	private TextoComenzar textoComenzar;
	private FpsInfo fpsInfo;

	private Group rootGroup;

	public MenuSceneHandler(FlappyBirdGame g) {
		super(g);	
	}

	protected void prepareScene() {
		rootGroup = new Group();
		scene = new Scene(rootGroup, Config.baseWidth, Config.baseHeight, Color.BLACK);
	}

	protected void defineEventHandlers() {
		mouseEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) {
					g.startGame();
				}
			}
		};

		keyEventHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				switch (e.getCode()) {
				case UP:
				case W:
				case SPACE:
				case ENTER:
					g.startGame();
					break;
				case Q:
				case ESCAPE:
					System.exit(0);
					break;
				default:
					break;
				}
			}
		};
	}

	public void load() {
		boolean fullStart = true;
		Group baseGroup = new Group();
		rootGroup.getChildren().add(baseGroup);
		
		player = new FlappyBird(Config.baseWidth - 75, Config.baseHeight / 3, null);
		
		background = new Background();
		ground = new Ground();
		fpsInfo = new FpsInfo();

		title = new Title();
		textoComenzar = new TextoComenzar();

		GameObjectBuilder gameOB = GameObjectBuilder.getInstance();
		gameOB.setRootNode(baseGroup);
		gameOB.add(background, player, ground, title, textoComenzar, fpsInfo);

		if (fullStart) {
			addTimeEventsAnimationTimer();
			addInputEvents();
		}
	}

	public void unload() {
		rootGroup.getChildren().remove(0);
		super.unload();
	}
}
