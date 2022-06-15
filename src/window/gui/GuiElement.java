package window.gui;

import window.inputs.InputHandler;
import window.Window;
import window.listener.MouseClickListener;

import java.util.ArrayList;
import java.util.List;

/*
 * Offsets
 *  - an offset larger than 1 is an absolute offset
 *  - an offset smaller than 1 is a relative offset
 *  - a negative offset means that point is specified from the oposite border of the parent
 *  - position are always from the bottom left of the parent
 *
 *	         +---------+
 *           | v       |
 *           |>*       |
 *           |         |
 *           +---------+
 *
 * For the given example let * denote a point, > the xOffset and v the yOffset
 * When x and y are absolutes offsets (20, 30) then > is equal to 20 pixels and v to 30 pixels
 * In the case of relative offsets (0.2, 0.3) > would be equal to 20% of the parents' width(the box), and v to 30% of the height
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
	protected float scaleW, scaleH;
	protected Anchor xAnchor, yAnchor;

	protected List<GuiElement> children;
	private MouseClickListener listener;

	protected boolean isClickable = true;
	private boolean isHidden = false;

	public GuiElement(Anchor xAnchor, Anchor yAnchor, float xOffset, float yOffset, float width, float height) {
		this.xAnchor = xAnchor;
		this.yAnchor = yAnchor;

		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;
		this.scaleW = 1;
		this.scaleH = 1;

		children = new ArrayList<>();

		initComponent();
	}

	public GuiElement(Anchor[] anchors, float xOffset, float yOffset, float width, float height) {
		this(anchors[0], anchors[1], xOffset, yOffset, width, height);
	}

	public GuiElement(float xOffset, float yOffset, float width, float height) {
		this(Anchor.CENTER, Anchor.CENTER, xOffset, yOffset, width, height);
	}

	protected abstract void initComponent();

	public void renderGui() {
		if(isHidden) return;

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

	public void handleResize() {
		if(resizeComponent()) {
			children.forEach(GuiElement::handleResize);
		}
	}

	public void cleanUpGui(boolean cleanUpChildren) {
		for(GuiElement child: children) {
			child.parent = null;
			if(cleanUpChildren) child.cleanUpGui(true);
		}

		children = new ArrayList<>();
		cleanUpComponent();
	}

	public boolean resizeComponent() {
		return Math.abs(width) <= 1 || Math.abs(height) <= 1 || this instanceof Window;
	}
	public abstract void renderComponent();
	public void updateComponent(long dt) { }
	public abstract void cleanUpComponent();

	public void addElement(GuiElement element) {
		children.add(element);
		element.parent = this;
	}
	public void setMouseClickListener(MouseClickListener listener) {
		this.listener = listener;
	}

	public void hide() {
		isHidden = true;
	}

	public void unhide() {
		isHidden = false;
	}

	//>--| Input |--<\\

	public GuiElement handleMouseButton(int event, int button, float x, float y) {
		GuiElement out = null;
		boolean isClickOnChild = false;

		for(GuiElement child: children) {
			if(child.containsPoint(x, y) && child.isClickable) {
				isClickOnChild = true;
				out = child.handleMouseButton(event, button, x, y);
				break;
			}
		}

		if(!isClickOnChild && this.isClickable) {
			onClick(event, button);
		}

		return out == null?
				this:
				out;
	}

	public void onClick(int event, int button) {
		if(listener != null) listener.onClick(event, button);
	}

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

		return out;
	}

	protected void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}

	//>--| Sizes and positions |--<\\

	public void setX(float x) {
		this.xOffset = x;
	}

	public void setY(float y) {
		this.yOffset = y;
	}

	public void setScale(float scaleW, float scaleH) {
		this.scaleW = scaleW;
		this.scaleH = scaleH;
	}

	public float getCenterX() {
		float out = 0;

		if(parent != null) {
			float parentWidth = parent.getWidth();

			out = parent.getCenterX();
			if(!(parent instanceof Window)) out -= parentWidth / 2;

			if(Math.abs(xOffset) <= 1) out += xOffset * parentWidth;
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

			if(Math.abs(yOffset) <= 1) out += yOffset * parentHeight;
			else out += yOffset;

			if(Math.signum(yOffset) == -1) {
				out += parentHeight;
			}
		}

		return out + yAnchor.calculateOffset(getHeight());
	}

	public float getRawWidth() {
		return width;
	}

	public void setRawWidth(float width) {
		this.width = width;
	}

	public float getWidth() {
		float out = 0;
		GuiElement upper = (parent == null? Window.INSTANCE: parent);

		if(Math.abs(width) <= 1) {
			out += width * upper.getWidth();
		} else {
			out = width;
			if(width < 0) out += upper.getWidth();
		}

		return out * scaleW;
	}

	public float getRawHeight() {
		return height;
	}

	public void setRawHeight(float height) {
		this.height = height;
	}

	public float getHeight() {
		float out = 0;

		if(parent != null && Math.abs(height) <= 1) {
			out += height * parent.getHeight();
		} else {
			out = height;
			if(height < 0 && parent != null) out += parent.getHeight();
		}

		return out * scaleH;
	}

	protected float toScreenSpace(float value, float length) {
		float out = value / length;
		return out * 2 - 1;
	}
}
