package flappy_bird.objects.menu;

import flappy_bird.Config;
import flappy_bird.interfaces.Renderable;
import flappy_bird.utils.GameObject;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TextoComenzar extends GameObject implements Renderable {
	private final int Y = Config.baseHeight * 3 / 5;

	private Text text;
	private VBox render;

	public TextoComenzar() {
		text = new Text("Interactue para\ncomenzar");

		render = new VBox(text);
		render.setAlignment(Pos.TOP_CENTER);
		render.setTranslateY(Y);
		// Esto debería heredarse?
		render.setPrefWidth(Config.baseWidth);

		Font font = Font.font("Verdana", FontWeight.NORMAL, 40);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setFont(font);
		text.setFill(Color.BLACK);
	}

	@Override
	public Node getRender() {
		return render;
	}


}
