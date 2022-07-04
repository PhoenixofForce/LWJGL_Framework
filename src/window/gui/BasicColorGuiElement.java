package window.gui;

import assets.models.ScreenRect;
import utils.MathUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import rendering.Renderer;
import rendering.ShaderHandler;
import rendering.uniform.MassUniform;
import window.Window;

import java.awt.*;
import java.util.Random;

public class BasicColorGuiElement extends GuiElement {

	private Vector3f color;
	private Vector3f color2;

	protected float border;

	public BasicColorGuiElement(GuiConfig config) {
		super(config);
	}

	@Override
	protected void initComponent() {
		border = Float.MAX_VALUE;

		Color c = new Color(new Random().nextInt());
		color = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
		c = new Color(new Random().nextInt());
		color2 = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
	}

	@Override
	public void renderComponent() {
		float translationX = toScreenSpace(getCenterX(), Window.INSTANCE.getWidth());
		float translationY = toScreenSpace(getCenterY(), Window.INSTANCE.getHeight());

		float componentWidth = getWidth();
		float componentHeight = getHeight();

		float glWidth = componentWidth / Window.INSTANCE.getWidth();
		float glHeight = componentHeight / Window.INSTANCE.getHeight();

		Vector3f renderColor = color;
		if(isMouseEntered()) {
			renderColor = color2;
		}

		Matrix4f transformationMatrix = new Matrix4f();
		transformationMatrix.translate(translationX, translationY, 0);
		transformationMatrix.scale(glWidth, glHeight, 1);

		MassUniform uniform = new MassUniform();
		uniform.setMatrices(new Matrix4f(), new Matrix4f(), transformationMatrix);
		uniform.setVector3fs(renderColor);
		uniform.setFloats(
				Math.min(componentWidth, border) / componentWidth,
				Math.min(componentHeight, border) / componentHeight
		);

		Renderer.renderArrays(ShaderHandler.ShaderType.GUI, ScreenRect.getInstance(), uniform);
	}

	@Override
	public void cleanUpComponent() { }

	public BasicColorGuiElement setColors(Color mainColor, Color hoverColor) {
		this.color = MathUtils.vecFromColor(mainColor);
		this.color2 = MathUtils.vecFromColor(hoverColor);

		return this;
	}

	public BasicColorGuiElement setColors(Color mainColor) {
		this.color = MathUtils.vecFromColor(mainColor);
		this.color2 = color;

		return this;
	}
}
