package window.gui;

import window.inputs.InputHandler;
import window.Window;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/*
 * Offsets
 *  - an offset larger than 1 is an absolute offset
 *  - an offset smaller than 1 is an relative offset
 *  - a negative offset means that point is specified from the oposite border of the parent
 *  - position are always from the bottom left of the parent
 *
 *	     +---------+
 *           | v       |
 *           |>*       |
 *           |         |
 *           +---------+
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

	public void updateGui(long dt) {
		updateComponent(dt);
		for(GuiElement child: children) {
			child.updateGui(dt);
		}
	}

	public abstract void renderComponent();
	public void updateComponent(long dt) { }

	public float getCenterX() {
		float out = 0;

		if(parent != null) {
			float parentWidth = parent.getWidth();

			out = parent.getCenterX();
			if(!(parent instanceof Window)) out -= parentWidth / 2;

			if(Math.abs(xOffset) < 1) out += xOffset * parentWidth;
			else out += xOffset;

			if(Math.signum(xOffset) == -1) {
				out += parentWidth;
			}
		}

		return out + xAnchor.calculateOffset(getWidth());
	}

	public float getCenterY() {
		float out = 0;

		if(parent != null) {
			float parentHeight = parent.getHeight();

			out = parent.getCenterY();
			if(!(parent instanceof Window)) out -= parentHeight / 2;

			if(Math.abs(yOffset) < 1) out += yOffset * parentHeight;
			else out += yOffset;

			if(Math.signum(yOffset) == -1) {
				out += parentHeight;
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

	protected void handleMouseButton(int event, int button, float x, float y) {
		boolean isClickOnChild = false;

		for(GuiElement child: children) {
			if(child.containsPoint(x, y)) {
				isClickOnChild = true;
				child.handleMouseButton(event, button, x, y);
				break;
			}
		}

		if(!isClickOnChild) {
			if(event == GLFW_RELEASE) onClick(button);
		}
	}

	public void onClick(int button) { }

	protected boolean isMouseEntered() {
		float x = InputHandler.mouseX;
		float y = InputHandler.mouseY;

		if(containsPoint(x, y)) {
			for(GuiElement child: children) {
				if(child.containsPoint(x, y)) return false;
			}

			return true;
		}

		return false;
	}

	protected boolean containsPoint(float x, float y) {
		float width = getWidth();
		float height = getHeight();

		float lowerX = getCenterX() - width / 2f;
		float lowerY = getCenterY() - height / 2f;

		boolean out = x >= lowerX && x < lowerX + width &&
				y >= lowerY && y < lowerY + height;

		/*
		System.out.println(lowerX + " " + x + " " + (lowerX + width));
		System.out.println(lowerY + " " + y + " " + (lowerY + height));
		System.out.println(out);
		 */

		return out;
	}
}
