package window.inputs;

import window.Window;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

	public static float mouseX = 0, mouseY = 0;

	public static void getInputs() {
		float[] mousePosition = getMousePosition();
		mouseX = mousePosition[0];
		mouseY = mousePosition[1];
	}

	//>--| KEYS |--<\\

	private static Map<Integer, KeyHit> lastPresses = new HashMap<>();

	public static boolean isKeyPressed(int keyCode) {
		int action = glfwGetKey(Window.INSTANCE.window, keyCode);
		return action == GLFW_PRESS;
	}

	public static boolean isKeyPressed(int keyCode, long timeBuffer) {
		return isKeyPressed(keyCode) || (lastPresses.containsKey(keyCode) && lastPresses.get(keyCode).timeSinceEnd() <= timeBuffer);
	}

	public static boolean isLongClick(int keyCode, long minimumForLong) {
		if(lastPresses.containsKey(keyCode)) {
			KeyHit hit = lastPresses.get(keyCode);
			if(hit == null) return false;

			return hit.getClickDuration() >= minimumForLong;
		}

		return false;
	}

	public static void callbacks() {
		glfwSetKeyCallback(Window.INSTANCE.window, (window, key, scancode, action, mods) -> {
			KeyHit click = lastPresses.get(key);

			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

			if(action == GLFW_PRESS) {
				click = new KeyHit(key);
				lastPresses.put(key, click);
			} else if(action == GLFW_REPEAT) {
				if(click != null) click.setEnd(false);
				else System.err.println("click null");
			} else if(action == GLFW_RELEASE) {
				if(click != null) click.setEnd(true);
				else System.err.println("click null");
			} else {
				System.err.println("Unknown action " + action);
			}
		});
	}

	public static void consumeClick(int keycode) {
		KeyHit click = lastPresses.get(keycode);
		if(click != null) click.reset();
	}

	//>--| MOUSE |--<\\

	public enum MouseButton {
		LEFT(GLFW_MOUSE_BUTTON_LEFT), MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE), RIGHT(GLFW_MOUSE_BUTTON_RIGHT);

		private final int buttonValue;
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