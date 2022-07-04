package window.gui;

public class GuiConfig {

	final Anchor xAnchor;
	final Anchor yAnchor;

	final float xOffset;
	final float yOffset;

	final float width;
	final float height;

	public GuiConfig(Anchor xAnchor, Anchor yAnchor, float xOffset, float yOffset, float width, float height) {
		this.xAnchor = xAnchor;
		this.yAnchor = yAnchor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;
	}

	public GuiConfig(Anchor[] anchors, float xOffset, float yOffset, float width, float height) {
		this(anchors[0], anchors[1], xOffset, yOffset, width, height);
	}

	public GuiConfig(float xOffset, float yOffset, float width, float height) {
		this(Anchor.CENTER, Anchor.CENTER, xOffset, yOffset, width, height);
	}
}
