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
	private List<Float> wobbleStrengths;

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

		And the width and height get set automatically

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
		this(parent, Anchor.TOP_LEFT, xOff, yOff, font, fontSize);
	}

	public GuiText(GuiElement parent, Anchor[] anchors, float xOff, float yOff, Font font, float fontSize) {
		super(parent, anchors, xOff, yOff, 0, 0);
		this.font = font;
		this.fontSize = fontSize;
		this.wasBuild = false;

		text = new ArrayList<>();
		colors = new ArrayList<>();
		wobbleStrengths = new ArrayList<>();
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
			//(translationX, translationY) needs to be the center of the first char
			//getCenterX points to center of the whole text
			//-getWidth/2 to the left side of the first letter
			//+fontSize (aka width)/2 to the middle of the first letter
			float translationX = toScreenSpace(getCenterX() - getWidth() / 2 + fontSize / 2, Window.INSTANCE.getWidth());
			float translationY = toScreenSpace(getCenterY() + getHeight() / 2 - (fontSize * Constants.FONT_ASPECT / 2), Window.INSTANCE.getHeight());

			ShaderHandler.ShaderType type = ShaderHandler.ShaderType.TEXT;
			Uniform u = new Uniform();
			u.setTextures(font.getAtlas());
			u.setFloats(translationX, translationY, Window.INSTANCE.getWidth(), Window.INSTANCE.getHeight(), Constants.RUNTIME, model.charCount(), (float) displayTime / writerDuration);

			Renderer.renderArraysInstanced(type, model, u, model.charCount());
		}
	}

	//>--| BUILDER |--<\\

	public GuiText addText(String string, Vector3f color, float wobbleStrength) {
		text.add(string.replaceAll("(\r\n|\r)", "\n"));	//we want \n be the only newline character
		colors.add(color);
		wobbleStrengths.add(wobbleStrength);

		return this;
	}

	public GuiText addText(String string, Vector3f color) {
		return this.addText(string, color, 0);
	}

	public GuiText addText(String string) {
		return this.addText(string, new Vector3f(1));
	}

	public GuiText newLine() {
		return this.addText("\n");
	}

	public GuiText build() {
		wasBuild = true;
		model = new TextModel(font, fontSize, width, text, colors, wobbleStrengths);
		displayTime = -writerDuration / model.charCount() * 8;

		if(width == 0) this.setRawWidth(model.getWidth());
		if(height == 0) this.setRawHeight(model.getHeight());

		return this;
	}
}