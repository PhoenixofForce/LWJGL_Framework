package window.font;

import org.joml.Vector3f;
import window.gui.Anchor;

import java.util.List;

public abstract class DynamicText implements Text {

	private StaticText text;
	private String lastText = "";
	private boolean hasChanged = false;
	private Anchor alignment;

	public DynamicText() {
		this(Anchor.ALIGN_LEFT);
	}

	public DynamicText(Anchor alignment) {
		this.alignment = alignment;
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
			text = StaticText.fromString(newText);
			lastText = newText;
			hasChanged = true;
		} else {
			hasChanged = false;
		}
	}

	@Override
	public Anchor getAlignment() {
		return alignment;
	}
}
