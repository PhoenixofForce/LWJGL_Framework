package window.gui;

import meshes.ScreenRect;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import rendering.Renderer;
import rendering.ShaderHandler;
import rendering.Uniform;
import window.Window;

import java.awt.*;
import java.util.Random;

public class BasicColorGuiElement extends GuiElement {

	private Vector3f color;
	private Vector3f color2;
	public BasicColorGuiElement(GuiElement parent, Anchor xAnchor, Anchor yAnchor, float xOffset, float yOffset, float width, float height) {
		super(parent, xAnchor, yAnchor, xOffset, yOffset, width, height);

		Color c = new Color(new Random().nextInt());
		color = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
		c = new Color(new Random().nextInt());
		color2 = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
	}

	public BasicColorGuiElement(GuiElement parent, Anchor[] anchors, float xOffset, float yOffset, float width, float height) {
		super(parent, anchors, xOffset, yOffset, width, height);

		Color c = new Color(new Random().nextInt());
		color = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
		c = new Color(new Random().nextInt());
		color2 = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
	}

	public BasicColorGuiElement(GuiElement parent, float xOffset, float yOffset, float width, float height) {
		super(parent, xOffset, yOffset, width, height);

		Color c = new Color(new Random().nextInt());
		color = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
		c = new Color(new Random().nextInt());
		color2 = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
	}

	public BasicColorGuiElement(float xOffset, float yOffset, float width, float height) {
		super(xOffset, yOffset, width, height);

		Color c = new Color(new Random().nextInt());
		color = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
		c = new Color(new Random().nextInt());
		color2 = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
	}

	@Override
	public void renderComponent() {
		float translationX = toScreenSpace(getCenterX(), Window.INSTANCE.getWidth());
		float translationY = toScreenSpace(getCenterY(), Window.INSTANCE.getHeight());

		float glWidth = getWidth() / Window.INSTANCE.getWidth();
		float glHeight = getHeight() / Window.INSTANCE.getHeight();

		Vector3f renderColor = color;
		if(isMouseEntered()) {
			renderColor = color2;

			float ratio = glWidth / glHeight;
			glHeight *= 1.2;
			glWidth = ratio * glHeight;
		}

		Matrix4f transformationMatrix = new Matrix4f();
		transformationMatrix.translate(translationX, translationY, 0);
		transformationMatrix.scale(glWidth, glHeight, 1);

		Uniform uniform = new Uniform();
		uniform.setMatrices(new Matrix4f(), new Matrix4f(), transformationMatrix);
		uniform.setVector3fs(renderColor);
		Renderer.renderArrays(ShaderHandler.ShaderType.DEFAULT, ScreenRect.getInstance(), uniform);
	}
}
