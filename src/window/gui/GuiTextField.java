package window.gui;

import org.lwjgl.glfw.GLFW;
import utils.StringUtils;
import window.font.DynamicText;
import window.font.GeneralFont;
import window.inputs.FocusHolder;
import window.inputs.InputHandler;
import window.inputs.KeyHit;

import java.util.Optional;

public class GuiTextField extends BasicColorGuiElement implements FocusHolder {

	//TODO: proper background
	//TODO: blinking cursor
	//TODO: paste
	
	private String currentString;
	private GuiText inputDisplay;

	private String tooltip;
	private TextConfirmer textFilter;

	private int cursorPosition;

	public GuiTextField(GuiConfig config, String tooltip, Optional<TextConfirmer> confirmer) {
		super(config);

		this.cursorPosition = 0;
		this.tooltip = tooltip;
		this.textFilter = confirmer.orElse(new TextConfirmer.AllConfirmer());
	}

	@Override
	protected void initComponent() {
		super.initComponent();
		GuiTextField thisElement = this;

		currentString = "";
		inputDisplay = new GuiText(new GuiConfig(Anchor.BOTTOM_LEFT, 0, 0, 1f, 1f), new GeneralFont("WhitePeaberryOutline", 2), 16)
			.setText(new DynamicText() {
				@Override
				public String getText() {
					boolean hasFocus = InputHandler.hasFocus(thisElement);
					String focusPart = (hasFocus? "|": "");

					if(currentString.length() == 0) {
						return focusPart + tooltip;
					} else {
						return StringUtils.insert(currentString, cursorPosition, focusPart);
					}
				}
			});

		inputDisplay.setClickable(false);
		this.addElement(inputDisplay);
	}

	@Override
	public void renderComponent() {
		super.renderComponent();
	}

	@Override
	public void cleanUpComponent() { }

	@Override
	public void charStartRepeat(char c) {
		String newString = StringUtils.insert(currentString, cursorPosition, c + "");

		if(!textFilter.confirm(currentString, newString)) return;
		currentString = newString;
		cursorPosition++;
	}

	@Override
	public void handleStart(KeyHit hit) { }

	@Override
	public void handleRepeat(KeyHit hit) {
		if(hit.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE) {
			if(currentString.length() > 0 && cursorPosition > 0) {
				currentString = currentString.substring(0, cursorPosition - 1) + currentString.substring(cursorPosition);
				cursorPosition--;
			}
		}

		if(hit.getKeyCode() == GLFW.GLFW_KEY_DELETE) {
			if(currentString.length() > 0 && cursorPosition < currentString.length()) {
				currentString = currentString.substring(0, cursorPosition) + currentString.substring(cursorPosition + 1);
			}
		}

		if(hit.getKeyCode() == GLFW.GLFW_KEY_LEFT) {
			cursorPosition = Math.max(0, cursorPosition - 1);
		}

		if(hit.getKeyCode() == GLFW.GLFW_KEY_RIGHT) {
			cursorPosition = Math.min(currentString.length(), cursorPosition + 1);
		}
	}

	@Override
	public void handleEnd(KeyHit hit) {
		handleRepeat(hit);

		if(hit.getKeyCode() == GLFW.GLFW_KEY_ENTER) InputHandler.dequestFocus(this);
	}

	public String getString() {
		return currentString;
	}
}
