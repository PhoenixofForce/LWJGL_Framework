package window.gui;

import org.lwjgl.glfw.GLFW;
import window.font.DynamicText;
import window.font.Font;
import window.font.GeneralFont;
import window.font.TextureAtlasFont;
import window.inputs.FocusHolder;
import window.inputs.InputHandler;
import window.inputs.KeyHit;

import java.util.Optional;

public class GuiTextField extends BasicColorGuiElement implements FocusHolder {

	//TODO: proper background
	//TODO: tooltip and regex filter in constructor
	//TODO: move cursor
	//TODO: blinking cursor

	private String stringInput;
	private GuiText text;

	private String tooltip;
	private Optional<String> regexFilter;

	public GuiTextField(Anchor xAnchor, Anchor yAnchor, float xOffset, float yOffset, float width, float height) {
		super(xAnchor, yAnchor, xOffset, yOffset, width, height);
	}

	public GuiTextField(Anchor[] anchors, float xOffset, float yOffset, float width, float height) {
		super(anchors, xOffset, yOffset, width, height);
	}

	public GuiTextField(float xOffset, float yOffset, float width, float height) {
		super(xOffset, yOffset, width, height);
	}

	@Override
	protected void initComponent() {
		super.initComponent();
		tooltip = "Enter Name...";
		regexFilter = Optional.of("[A-z]{0,10}");	//TODO:
		GuiTextField thisElement = this;

		stringInput = "";
		text = new GuiText(Anchor.BOTTOM_LEFT, 0, 0, 1f, 1f, new GeneralFont("WhitePeaberryOutline", 2), 16, 0)
			.setText(new DynamicText() {
				@Override
				public String getText() {
					boolean hasFocus = InputHandler.hasFocus(thisElement);
					String focusPart = (hasFocus? "|": "");

					if(stringInput.length() == 0) {
						return focusPart + tooltip;
					} else {
						return stringInput + focusPart;
					}
				}
			});

		text.setClickable(false);
		this.addElement(text);
	}

	@Override
	public void renderComponent() {
		super.renderComponent();
	}

	@Override
	public void cleanUpComponent() { }

	@Override
	public void charStartRepeat(char c) {
		if(regexFilter.isPresent() && !(stringInput + c).matches(regexFilter.get())) return;
		stringInput += c;
	}

	@Override
	public void handleStart(KeyHit hit) { }

	@Override
	public void handleRepeat(KeyHit hit) {
		if(hit.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE) {
			if(stringInput.length() > 0) stringInput = stringInput.substring(0, stringInput.length() - 1);
		}
	}

	@Override
	public void handleEnd(KeyHit hit) {
		if(hit.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE) {
			if(stringInput.length() > 0) stringInput = stringInput.substring(0, stringInput.length() - 1);
		}
	}
}
