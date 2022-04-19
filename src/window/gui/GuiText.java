package window.gui;

import assets.audio.AudioSource;
import assets.audio.AudioType;
import assets.models.TextModel;
import org.joml.Vector3f;
import rendering.Renderer;
import rendering.ShaderHandler;
import rendering.uniform.MassUniform;
import utils.Constants;
import utils.Options;
import window.Window;
import window.font.Font;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;

public class GuiText extends GuiElement {

	//TODO: Alignments

	private Font font;

	private List<String> text;
	private List<Vector3f> colors;
	private List<Float> wobbleStrengths;

	private float fontSize;

	private TextModel model;

	private long writerDuration = 0;
	private long displayTime = 0;

	private long clearAfterMS;

	private boolean fixedWidth;
	private boolean fixedHeight;

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

        And with the second type you specify the width of the box (5)
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

	public GuiText(float xOff, float yOff, Font font, float fontSize) {
		this(Anchor.TOP_LEFT, xOff, yOff, font, fontSize);
	}

	public GuiText(Anchor[] anchors, float xOff, float yOff, Font font, float fontSize) {
		this(anchors, xOff, yOff, font, fontSize, 0);
	}

	public GuiText(Anchor[] anchors, float xOff, float yOff, Font font, float fontSize, long writerDuration) {
		this(anchors, xOff, yOff, 0, font, fontSize, writerDuration);
	}

	public GuiText(Anchor[] anchors, float xOff, float yOff, float width, Font font, float fontSize, long writerDuration) {
		this(anchors, xOff, yOff, width, 0, font, fontSize, writerDuration);
	}

	public GuiText(Anchor[] anchors, float xOff, float yOff, float width, float height, Font font, float fontSize, long writerDuration) {
		super(anchors, xOff, yOff, width, height);
		this.font = font;
		this.fontSize = fontSize;

		fixedWidth =  width != 0;
		fixedHeight = height != 0;
		clear(writerDuration);
	}

	@Override
	protected void initComponent() {
		//super.initComponent();
	}

	@Override
	public void updateGui(long dt) {
		super.updateGui(dt);
		displayTime += dt;

		if(clearAfterMS >= 0 && displayTime >= clearAfterMS + writerDuration) {
			clear().addText("").build();
		}
	}

	@Override
	public void renderComponent() {
		if(model != null) {
			//super.renderComponent();

			//(translationX, translationY) needs to be the center of the first char
			//getCenterX points to center of the whole text
			//-getWidth/2 to the left side of the first letter
			//+fontSize (aka width)/2 to the middle of the first letter
			float width = font.getWidth(text.get(0).charAt(0) + "", fontSize);
			float translationX = toScreenSpace(getCenterX() - getWidth() / 2 + (fontSize / width) / 2, Window.INSTANCE.getWidth());
			float translationY = toScreenSpace(getCenterY() + getHeight() / 2 - (fontSize / 2), Window.INSTANCE.getHeight());

			ShaderHandler.ShaderType type = ShaderHandler.ShaderType.TEXT;
			MassUniform u = new MassUniform();
			u.setTextures(font.getAtlas());
			u.setFloats(translationX, translationY, Window.INSTANCE.getWidth(), Window.INSTANCE.getHeight(), Window.INSTANCE.getRuntime(), model.charCount(), (float) displayTime / writerDuration);

			Renderer.renderArraysInstanced(type, model, u, model.charCount());
		}
	}

	@Override
	public boolean resizeComponent() {
		build();
		return super.resizeComponent();
	}

	@Override
	public void cleanUpComponent() {
		if(model != null) {
			model.cleanUp();
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

	public GuiText clear() {
		return clear(0);
	}

	public GuiText clear(long writerDurationPerChar) {
		return clear(writerDurationPerChar, -1L);
	}

	public GuiText clear(long writerDurationPerChar, long clearAfterMS) {
		this.writerDuration = writerDurationPerChar;
		this.clearAfterMS = clearAfterMS;

		text = new ArrayList<>();
		colors = new ArrayList<>();
		wobbleStrengths = new ArrayList<>();

		return this;
	}

	public GuiText build() {
		if(model == null) {
			model = new TextModel(getWidth(), getHeight());
		}
		model.updateInstance(font, fontSize, width, text, colors, wobbleStrengths);

		//writer duration is set per char, so we have to multiply it with the amount of characters
		displayTime = -writerDuration;
		writerDuration *= model.charCount();

		if(!fixedWidth) this.setRawWidth(model.getWidth());
		if(!fixedHeight) this.setRawHeight(model.getHeight());

		return this;
	}
}