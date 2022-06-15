package window.font;

import org.joml.Vector3f;
import window.gui.Anchor;

import java.util.List;

public abstract class AutoUpdateText extends Text {

	private Text text;
	private String lastText = "";
	private boolean hasChanged = false;

	public AutoUpdateText() {
		this(Anchor.ALIGN_LEFT);
	}

	public AutoUpdateText(Anchor alignment) {
		super(alignment);
		updateText();
	}

	public List<String> getTextFragments() {
		return text.getTextFragments();
	}

	public List<Vector3f> getColorFragments() {
		return text.getColorFragments();
	}

	public List<Float> getWobbleFragments() {
		return text.getWobbleFragments();
	}

	public char getFirstChar() {
		return text.getFirstChar();
	}

	public abstract String getText();

	@Override
	public boolean hasChanged() {
		updateText();
		return hasChanged;
	}

	private void updateText() {
		String newText = getText();
		if(!newText.equals(lastText) && !hasChanged) {
			text = Text.fromString(newText);
			lastText = newText;
			hasChanged = true;
		} else {
			hasChanged = false;
		}
	}
}
