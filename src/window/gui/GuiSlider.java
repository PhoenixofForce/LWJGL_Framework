package window.gui;

import utils.MathUtils;
import window.inputs.InputHandler;
import window.listener.SliderChangeListener;

import java.awt.*;

public class GuiSlider extends GuiElement {

	private float value;
	private BasicColorGuiElement bar;
	private BasicColorGuiElement slider;

	private SliderChangeListener changeListener;

	public GuiSlider(GuiConfig config) {
		this(config, 0.5f);
	}

	public GuiSlider(GuiConfig config, float value) {
		super(config, false);
		this.value = value;
		initComponent();
	}

	protected void initComponent() {
		bar = new BasicColorGuiElement(new GuiConfig(Anchor.CENTERCENTER, 0.5f, 0.5f, 1f, 0.1f));
		bar.setMouseClickListener(this::onClick);
		bar.setColors(new Color(200, 200, 200));

		slider = new BasicColorGuiElement(new GuiConfig(Anchor.CENTERCENTER, value, 0.5f, 10, 1f));
		slider.setMouseClickListener(this::onClick);
		slider.setColors(new Color(85, 112, 138), new Color(47, 98, 161));

		this.addElement(bar);
		this.addElement(slider);
	}

	@Override
	public void renderComponent() { }

	@Override
	public void updateComponent(long dt) {
		if(slider.isMouseEntered()) {
			slider.setScale(1.2f, 1.2f);
		} else {
			slider.setScale(1, 1);
		}
	}

	@Override
	public void cleanUpComponent() { }

	@Override
	public void onClick(int event, int button) {
		super.onClick(event, button);

		float componentCenterX = getCenterX();
		float componentWidth = getWidth();

		float mouseX = InputHandler.mouseX;

		float newValue = MathUtils.map(mouseX, componentCenterX - componentWidth / 2, componentCenterX + componentWidth / 2, 0, 1);
		newValue = MathUtils.clamp(0, newValue, 1);

		setValue(newValue);
	}

	public void setValue(float value) {
		if(this.value != value && changeListener != null) {
			changeListener.onChange(value);
		}

		this.value = value;
		slider.setX(value);
	}

	public float getValue() {
		return value;
	}

	public void setChangeListener(SliderChangeListener changeListener) {
		this.changeListener = changeListener;
	}
}
