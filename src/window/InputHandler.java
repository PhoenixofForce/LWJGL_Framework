package window;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

	public static float mouseX = 0, mouseY = 0;

	public static void getInputs() {
		float[] mousePosition = getMousePosition();
		mouseX = mousePosition[0];
		mouseY = mousePosition[1];
	}

	//>--| KEYS |--<\\

	public static boolean isKeyPressed(int keyCode) {
		return glfwGetKey(Window.INSTANCE.window, keyCode) == GLFW_PRESS;
	}

	//>--| MOUSE |--<\\

	public enum MouseButton {
		LEFT(GLFW_MOUSE_BUTTON_LEFT), MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE), RIGHT(GLFW_MOUSE_BUTTON_RIGHT);

		private int buttonValue;
		MouseButton(int buttonValue) {
			this.buttonValue = buttonValue;
		}

		private int getButtonValue() {
			return buttonValue;
		}
	}

	public static float[] getMousePosition() {
		double[] x = new double[1];
		double[] y = new double[1];
		glfwGetCursorPos(Window.INSTANCE.window, x, y);

		return new float[]{(float) x[0], Window.INSTANCE.getHeight() - (float) y[0]};
	}

	public static boolean isMousePressed(int button) {
		return glfwGetMouseButton(Window.INSTANCE.window, button) == GLFW_PRESS;
	}

	public static boolean isMousePressed(MouseButton button) {
		return isMousePressed(button.getButtonValue());
	}


}
