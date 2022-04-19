package window.gui;

import window.font.TextureAtlasFont;

import java.util.List;

public class GuiSelector extends GuiElement {

	private static List<String> options = List.of(
			"Option 1",
			"Option 2",
			"Option 3",
			"Option 4"
	);

	private int selectedOption;
	private int optionCount;

	private GuiButton left;
	private GuiButton right;
	private GuiText selectionDisplay;

	public GuiSelector(Anchor xAnchor, Anchor yAnchor, float xOffset, float yOffset, float width, float height) {
		super(xAnchor, yAnchor, xOffset, yOffset, width, height);
	}

	public GuiSelector(Anchor[] anchors, float xOffset, float yOffset, float width, float height) {
		super(anchors, xOffset, yOffset, width, height);
	}

	public GuiSelector(float xOffset, float yOffset, float width, float height) {
		super(xOffset, yOffset, width, height);
	}

	@Override
	protected void initComponent() {
		left = new GuiButton(Anchor.BOTTOM_LEFT, 0, 0, 0.1f, 1f);
		right = new GuiButton(Anchor.BOTTOM_RIGHT, 1f, 0, 0.1f, 1f);
		selectionDisplay = new GuiText(Anchor.CENTERCENTER, 0.5f, 0.5f, new TextureAtlasFont("Font"), 16f);

		left.setClickListener(() -> setOption(selectedOption - 1));
		right.setClickListener(() -> setOption(selectedOption + 1));

		this.addElement(left);
		this.addElement(right);
		this.addElement(selectionDisplay);

		optionCount = options.size();
		setOption(0);
	}

	@Override
	public void renderComponent() {}

	@Override
	public void cleanUpComponent() { }

	private void setOption(int option) {
		this.selectedOption = option % optionCount;
		while(selectedOption < 0) selectedOption += optionCount;
		selectionDisplay.setText(options.get(selectedOption));
	}
}
