package window.gui;

import meshes.TextModel;
import org.joml.Vector3f;
import rendering.Renderer;
import rendering.ShaderHandler;
import rendering.Uniform;
import utils.Constants;
import window.Window;
import window.font.Font;

import java.util.ArrayList;
import java.util.List;

//BasicColorGuiElement for Debug purposes
public class GuiText extends BasicColorGuiElement {

	private Font font;

	private List<String> text;
	private List<Vector3f> colors;

	private float fontSize;

	private TextModel model;
	private boolean wasBuild;

	private long writerDuration = 2000;
	private long displayTime = -500;

	/*
		There are two types of texts
		With the first type you specify the Text

		Lorem Ipsum
		Dolor sit

		And the width and height get set automatically (TODO: currently the sizes match the first character thus anchors are not working as intedet here)

		+-----------+
        |Lorem Ipsum|
        |Dolor sit  |
        +-----------+

        And with the second type you specify the width of the box (5)	(TODO: not implemented)
        +-----+
        +-----+

        And the Text gets matched by that bounds
        +-----+
        |Lorem|
        |Ipsum|
        |Dolor|
        |sit  |
        +-----+
	 */

	public GuiText(GuiElement parent, float xOff, float yOff, Font font, float fontSize) {
		this(parent, Anchor.TOP_CENTER, xOff, yOff, font, fontSize);
	}

	public GuiText(GuiElement parent, Anchor[] anchors, float xOff, float yOff, Font font, float fontSize) {
		super(parent, anchors, xOff, yOff, fontSize, fontSize * Constants.FONT_ASPECT);
		this.font = font;
		this.fontSize = fontSize;
		this.wasBuild = false;

		text = new ArrayList<>();
		colors = new ArrayList<>();
	}

	@Override
	public void updateGui(long dt) {
		super.updateGui(dt);
		displayTime += dt;
	}

	@Override
	public void renderComponent() {
		if(wasBuild) {
			super.renderComponent();
			float translationX = toScreenSpace(getCenterX(), Window.INSTANCE.getWidth());
			float translationY = toScreenSpace(getCenterY(), Window.INSTANCE.getHeight());

			ShaderHandler.ShaderType type = ShaderHandler.ShaderType.TEXT;
			Uniform u = new Uniform();
			u.setTextures(font.getAtlas());
			u.setFloats(translationX, translationY, Window.INSTANCE.getWidth(), Window.INSTANCE.getHeight(), Constants.RUNTIME, model.charCount(), (float) displayTime / writerDuration);

			Renderer.renderArraysInstanced(type, model, u, model.charCount());
		}
	}

	public GuiText addText(String string, Vector3f color) {
		for(String s: string.split(" ")) {
			text.add(s);
			colors.add(color);
		}

		return this;
	}

	public GuiText addText(String string) {
		return this.addText(string, new Vector3f(1));
	}

	public GuiText build() {
		wasBuild = true;
		model = new TextModel(font, fontSize, width, text, colors);
		return this;
	}
}