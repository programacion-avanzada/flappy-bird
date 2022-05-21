package flappy_bird;

import java.util.List;

import flappy_bird.interfaces.Collidator;
import flappy_bird.interfaces.Collideable;
import flappy_bird.objects.Background;
import flappy_bird.objects.FlappyBird;
import flappy_bird.objects.FpsInfo;
import flappy_bird.objects.Ground;
import flappy_bird.objects.PipeBuilder;
import flappy_bird.objects.Radio;
import flappy_bird.objects.Score;
import flappy_bird.utils.GameObjectBuilder;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class GameSceneHandler extends SceneHandler {
	
	private FlappyBird player;
	private Background background;
	private Ground ground;
	private PipeBuilder pipeBuilder;
	private Score score;
	private FpsInfo fpsInfo;
	private Radio radio;

	// TODO pause
	// boolean paused = false;
	boolean started = false;
	boolean ended = false;

	public GameSceneHandler(FlappyBirdGame g) {
		super(g);
	}

	protected void prepareScene() {
		Group rootGroup = new Group();
		scene = new Scene(rootGroup, Config.baseWidth, Config.baseHeight, Color.BLACK);
	}

	protected void defineEventHandlers() {
		mouseEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) {
					makeAction();
				}
			}
		};

		keyEventHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				switch (e.getCode()) {

				case W:
				case UP:
				case ENTER:
				case SPACE:
					makeAction();
					break;
				case R:
					restart();
					break;
				case Q:
				case ESCAPE:
					g.startMenu();
					break;
				default:
					break;
				}
			}
		};
	}
	
	public void load(boolean fullStart) {
		Group rootGroup = new Group();
		scene.setRoot(rootGroup);
		
		score = new Score();
		player = new FlappyBird(Config.playerCenter, Config.baseHeight / 2, score);
		background = new Background();
		ground = new Ground();
		pipeBuilder = new PipeBuilder();
		fpsInfo = new FpsInfo(fps);
		radio = new Radio(Config.playerCenter, Config.baseHeight / 2, player);

		// Add to builder
		GameObjectBuilder gameOB = GameObjectBuilder.getInstance();
		gameOB.setRootNode(rootGroup);
		gameOB.add(background, radio, player, ground, score, fpsInfo, pipeBuilder);


		if (fullStart) {
			addTimeEventsAnimationTimer();
			addInputEvents();
		}
	}
	
	public void restart() {
		cleanData();
		load(false);
	}
	
	private void cleanData() {
		GameObjectBuilder.getInstance().removeAll();
		ended = false;
		started = false;
		Config.baseSpeed = 250;
	}

	private void makeAction() {
		if (!started) {
			started = true;
			pipeBuilder.startBuilding(2 * NANOS_IN_SECOND);
			radio.start();
		}
		player.push();
	}

	public void update(double delta) {
		super.update(delta);

		checkColliders();

		if (!ended) {
			if (player.isDead()) {
				ended = true;
				pipeBuilder.stopBuilding();
				Config.baseSpeed = 0;

				// Improve
				TranslateTransition tt = new TranslateTransition(Duration.millis(50), scene.getRoot());
				tt.setFromX(-20f);
				tt.setByX(20f);
				tt.setCycleCount(6);
				tt.setAutoReverse(true);
				tt.playFromStart();
				tt.setOnFinished(event -> {
					scene.getRoot().setTranslateX(0);
				});
			}
		}
	}

	private void checkColliders() {
		List<Collidator> collidators = GameObjectBuilder.getInstance().getCollidators();
		List<Collideable> collideables = GameObjectBuilder.getInstance().getCollideables();

		for (int i = 0; i < collidators.size(); i++) {
			Collidator collidator = collidators.get(i);
			for (int j = i + 1; j < collidators.size(); j++) {
				Collidator otherCollidator = collidators.get(j);
				Shape intersect = Shape.intersect(collidator.getCollider(), otherCollidator.getCollider());
				if (intersect.getBoundsInLocal().getWidth() != -1) {
					collidator.collide(otherCollidator);
					otherCollidator.collide(collidator);
				}
			}

			for (int j = 0; j < collideables.size(); j++) {
				Collideable collideable = collideables.get(j);
				Shape intersect = Shape.intersect(collidator.getCollider(), collideable.getCollider());

				// TODO test times
				// XXX Si el substract es distinto???
				// Check intersects
				if (intersect.getBoundsInLocal().getWidth() != -1) {
					collidator.collide(collideable);
				} else {
					// Check contains
					Bounds collideableBounds = collideable.getCollider().getBoundsInLocal();
					Bounds collidatorBounds = collidator.getCollider().getBoundsInLocal();
					if (collideableBounds.contains(collidatorBounds.getCenterX(), collidatorBounds.getCenterY())) {
						collidator.collide(collideable);
					}
				}
			}
		}
	}
	
	public void unload() {
		cleanData();
		super.unload();
	}

}
