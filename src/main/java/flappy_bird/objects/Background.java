package flappy_bird.objects;

import flappy_bird.Config;
import flappy_bird.interfaces.Renderable;
import flappy_bird.interfaces.Updatable;
import flappy_bird.utils.GameObject;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Background extends GameObject implements Updatable, Renderable {
	private Group render;
	private double posX = 0;

	private final int cityWidth = 136;
	private final int cityHeight = 152;
	private final int grassHeight = 100;

	public Background() {
		Image backgroundImage = new Image(ClassLoader.getSystemResourceAsStream("img/background.png"), cityWidth, cityHeight, false, false);

		int totalWidth = Config.baseWidth + cityWidth;
		int skyHeight = Config.baseHeight - cityHeight - grassHeight;

		Canvas canvas = new Canvas(totalWidth, Config.baseHeight);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		gc.setFill(Color.rgb(84, 192, 201));
		gc.fillRect(0, 0, totalWidth, skyHeight);

		for (int x = 0; x < totalWidth; x += cityWidth) {
			gc.drawImage(backgroundImage, x, skyHeight);
		}

		gc.setFill(Color.rgb(100, 224, 117));
		gc.fillRect(0, skyHeight + cityHeight, totalWidth, grassHeight);

		render = new Group(canvas);
		render.setViewOrder(10);
	}

	@Override
	public Node getRender() {
		return render;
	}

	@Override
	public void update(double deltaTime) {
		posX += -Config.currentSpeed * deltaTime * 0.01;
		render.setTranslateX(posX % cityWidth);
	}

	@Override
	public void destroy() { }

}
