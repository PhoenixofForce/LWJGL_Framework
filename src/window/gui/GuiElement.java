package window.gui;

import window.Window;

import java.util.ArrayList;
import java.util.List;

/*
 * Offsets
 *  - an offset larger than 1 is an absolute offset
 *  - an offset smaller than 1 is an relative offset
 *  - a negative offset means that point is specified from the oposite border of the parent
 *  - position are always from the bottom left of the parent
 *
 *	     +---------+
 *		 | v       |
 *		 |>*       |
 *		 |         |
 *		 +---------+
 *
 * For the given example let * denote a point, > the xOffset and v the yOffset
 * When x and y are absolutes offsets (20, 30) then > is equal to 20 pixels and v to 30 pixels
 * In the case of relative offsets (0.2, 0.3) > would be equal to 20% of the parents width(the box), and v to 30% of the height
 *
 * Width and Height
 *  - can be initialized the same way
 *
 * Anchor
 * The two anchors xAnchor and yAnchor specify which corner should be in the given point.
 *
 */
public abstract class GuiElement {

	protected GuiElement parent;
	protected float xOffset, yOffset;
	protected float width, height;

	protected List<GuiElement> children;
	protected Anchor xAnchor, yAnchor;

	public GuiElement(GuiElement parent, Anchor xAnchor, Anchor yAnchor, float xOffset, float yOffset, float width, float height) {
		this.parent = parent;
		this.xAnchor = xAnchor;
		this.yAnchor = yAnchor;

		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;

		children = new ArrayList<>();
		if(parent != null) parent.addChild(this);
	}

	public GuiElement(GuiElement parent, Anchor[] anchors, float xOffset, float yOffset, float width, float height) {
		this.parent = parent;
		this.xAnchor = anchors[0];
		this.yAnchor = anchors[1];

		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;

		children = new ArrayList<>();
		if(parent != null) parent.addChild(this);
	}

	public GuiElement(GuiElement parent, float xOffset, float yOffset, float width, float height) {
		this(parent, Anchor.CENTER, Anchor.CENTER, xOffset, yOffset, width, height);
	}

	public GuiElement(float xOffset, float yOffset, float width, float height) {
		this(Window.INSTANCE, xOffset, yOffset, width, height);
	}

	public void renderGui() {
		if(parent != null) {
			renderComponent();
		}

		for(GuiElement child: children) {
			child.renderGui();
		}
	}

	public abstract void renderComponent();

	public float getCenterX() {
		float out = 0;

		if(parent != null) {
			out = parent.getCenterX();
			if(!(parent instanceof Window)) out -= parent.getWidth() / 2;

			if(Math.abs(xOffset) < 1) out += xOffset * parent.getWidth();
			else out += xOffset;

			if(Math.signum(xOffset) == -1) {
				out += parent.getWidth();
			}
		}

		return out + xAnchor.calculateOffset(getWidth());
	}

	public float getCenterY() {
		float out = 0;

		if(parent != null) {
			out = parent.getCenterY();
			if(!(parent instanceof Window)) out -= parent.getHeight() / 2;

			if(Math.abs(yOffset) < 1) out += yOffset * parent.getHeight();
			else out += yOffset;

			if(Math.signum(yOffset) == -1) {
				out += parent.getHeight();
			}
		}

		return out + yAnchor.calculateOffset(getHeight());
	}

	public float getWidth() {
		float out = 0;

		if(parent != null && Math.abs(width) < 1) {
			out += width * parent.getWidth();
		} else {
			out = width;
		}

		return out;
	}

	public float getHeight() {
		float out = 0;

		if(parent != null && Math.abs(height) < 1) {
			out += height * parent.getHeight();
		} else {
			out = height;
		}

		return out;
	}

	protected float toScreenSpace(float value, float length) {
		float out = value / length;
		return out * 2 - 1;
	}

	private void addChild(GuiElement element) {
		children.add(element);
	}
}
