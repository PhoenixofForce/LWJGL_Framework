package window.gui;

import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class GuiCheckbox extends BasicColorGuiElement {

	private boolean checked;

	public GuiCheckbox(Anchor xAnchor, Anchor yAnchor, float xOffset, float yOffset, float width, float height) {
		super(xAnchor, yAnchor, xOffset, yOffset, width, height);
	}

	public GuiCheckbox(Anchor[] anchors, float xOffset, float yOffset, float width, float height) {
		super(anchors, xOffset, yOffset, width, height);
	}

	public GuiCheckbox(float xOffset, float yOffset, float width, float height) {
		super(xOffset, yOffset, width, height);
	}

	@Override
	public void initComponent() {
		super.initComponent();
		checked = false;
		border = checked? Float.MAX_VALUE: 3;
		setColors(new Color(85, 112, 138), new Color(47, 98, 161));
	}

	@Override
	public void onClick(int event, int button) {
		if(event == GLFW.GLFW_RELEASE) {
			checked = !checked;
			border = checked? Float.MAX_VALUE: 3;
		}
	}
}
