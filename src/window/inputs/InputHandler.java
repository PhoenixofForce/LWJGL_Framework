package window.inputs;

import utils.Constants;
import window.Window;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;


public class InputHandler {

	//TODO: Gamepad Support

	public static float mouseX = 0, mouseY = 0;

	public static void getInputs() {
		float[] mousePosition = getMousePosition();
		mouseX = mousePosition[0];
		mouseY = mousePosition[1];
	}

	public static void callbacks() {
		glfwSetKeyCallback(Window.INSTANCE.window, (window, key, scancode, action, mods) -> {
			KeyHit click = lastPresses.get(key);

			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

			if(action == GLFW_PRESS) {
				click = new KeyHit(key);
				lastPresses.put(key, click);
			} else if(action == GLFW_RELEASE) {
				if(click != null) click.setEnd();
				else System.err.println("click null");
			} else if(action == GLFW_REPEAT) { }
			else {
				System.err.println("Unknown action " + action);
			}
		});
	}

	public static void update() {
		getInputs();

		for (int c = 0; c < 16; c++) {
			if (glfwJoystickPresent(GLFW_JOYSTICK_1 + c) && glfwJoystickIsGamepad(GLFW_JOYSTICK_1 + c)) {
				ByteBuffer values = glfwGetJoystickButtons(GLFW_JOYSTICK_1 + c);
				for (int i = 0; i < values.limit(); i++) {
					boolean pressed = values.get(i) == 1;
					int code = 100 + i;

					KeyHit hit = lastPresses.get(code);

					if(pressed) {
						if(hit == null || !hit.isClickInProgress()) {
							hit = new KeyHit(code);
							lastPresses.put(code, hit);
						}
					} else {
						if(hit != null && hit.isClickInProgress()) {
							hit.setEnd();
						}
					}
				}

				FloatBuffer values2 = glfwGetJoystickAxes(GLFW_JOYSTICK_1 + c);
				for (int j = 0; j < values2.limit(); j++) {
					if (j == GLFW_GAMEPAD_AXIS_LEFT_TRIGGER || j == GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) {
						float triggerValue = (values2.get(j) + 1f) / 2f;
						int code = 115 + j;

						KeyHit hit = lastPresses.get(code);
						boolean pressed = triggerValue > 0;
						if(pressed) {
							if(hit == null || !hit.isClickInProgress()) {
								hit = new KeyHit(code, triggerValue);
								lastPresses.put(code, hit);
							}

							if(hit.isClickInProgress()) {
								hit.setValue(triggerValue);
							}
						} else {
							if(hit != null && hit.isClickInProgress()) {
								hit.setEnd();
							}
						}
					} else {

						float up_left = -Math.min(0, values2.get(j) + Constants.STICK_DEAD_ZONE) / (1 - Constants.STICK_DEAD_ZONE);
						boolean pressed = up_left != 0;

						int code = 115 + j;
						KeyHit hit = lastPresses.get(code);

						if(pressed) {
							if(hit == null || !hit.isClickInProgress()) {
								hit = new KeyHit(code, up_left);
								lastPresses.put(code, hit);
							}

							if(hit.isClickInProgress()) {
								hit.setValue(up_left);
							}
						} else {
							if(hit != null && hit.isClickInProgress()) {
								hit.setEnd();
							}
						}

						float down_right = Math.max(0, values2.get(j) - Constants.STICK_DEAD_ZONE) / (1 - Constants.STICK_DEAD_ZONE);
						if(down_right != 0) System.out.println(down_right);
						pressed = down_right != 0;

						code = 121 + j;
						hit = lastPresses.get(code);

						if(pressed) {
							if(hit == null || !hit.isClickInProgress()) {
								hit = new KeyHit(code, down_right);
								lastPresses.put(code, hit);
							}

							if(hit.isClickInProgress()) {
								hit.setValue(down_right);
							}
						} else {
							if(hit != null && hit.isClickInProgress()) {
								hit.setEnd();
							}
						}
					}
				}
			}
		}
	}

	//>--| KEYS |--<\\

	private static final Map<Integer, KeyHit> lastPresses = new HashMap<>();

	public static boolean isKeyPressed(int keyCode) {
		int action = glfwGetKey(Window.INSTANCE.window, keyCode);
		return (action == GLFW_PRESS || (lastPresses.containsKey(keyCode) && lastPresses.get(keyCode).isClickInProgress()));
	}

	public static boolean isKeyPressed(int keyCode, long timeBuffer) {
		return isKeyPressed(keyCode) || (lastPresses.containsKey(keyCode) && lastPresses.get(keyCode).timeSinceEnd() <= timeBuffer);
	}

	public static boolean isKeyPressedConsume(int keyCode, long timeBuffer) {
		boolean out = isKeyPressed(keyCode, timeBuffer);
		if(out) consumeClick(keyCode);
		return out;	}

	public static boolean isLongClick(int keyCode, long minimumForLong) {
		if(lastPresses.containsKey(keyCode)) {
			KeyHit hit = lastPresses.get(keyCode);
			if(hit == null) return false;

			return hit.getClickDuration() >= minimumForLong;
		}

		return false;
	}

	public static boolean isLongClickConsume(int keyCode, long minimumForLong) {
		boolean out = isLongClick(keyCode, minimumForLong);
		if(out) consumeClick(keyCode);
		return out;
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

	public static final int
			GAMEPAD_1_BUTTON_A = 100  + GLFW_GAMEPAD_BUTTON_A,
			GAMEPAD_1_BUTTON_B = 100  + GLFW_GAMEPAD_BUTTON_B,
			GAMEPAD_1_BUTTON_X = 100  + GLFW_GAMEPAD_BUTTON_X,
			GAMEPAD_1_BUTTON_Y = 100  + GLFW_GAMEPAD_BUTTON_Y,
			GAMEPAD_1_BUTTON_LEFT_BUMPER = 100  + GLFW_GAMEPAD_BUTTON_LEFT_BUMPER,
			GAMEPAD_1_BUTTON_RIGHT_BUMPER = 100  + GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER,
			GAMEPAD_1_BUTTON_BACK = 100  + GLFW_GAMEPAD_BUTTON_BACK,
			GAMEPAD_1_BUTTON_START = 100  + GLFW_GAMEPAD_BUTTON_START,
			GAMEPAD_1_BUTTON_GUIDE = 100  + GLFW_GAMEPAD_BUTTON_GUIDE,
			GAMEPAD_1_BUTTON_LEFT_THUMB = 100  + GLFW_GAMEPAD_BUTTON_LEFT_THUMB,
			GAMEPAD_1_BUTTON_RIGHT_THUMB = 100  + GLFW_GAMEPAD_BUTTON_RIGHT_THUMB,
			GAMEPAD_1_BUTTON_DPAD_UP = 100  + GLFW_GAMEPAD_BUTTON_DPAD_UP,
			GAMEPAD_1_BUTTON_DPAD_RIGHT = 100  + GLFW_GAMEPAD_BUTTON_DPAD_RIGHT,
			GAMEPAD_1_BUTTON_DPAD_DOWN = 100  + GLFW_GAMEPAD_BUTTON_DPAD_DOWN,
			GAMEPAD_1_BUTTON_DPAD_LEFT = 100  + GLFW_GAMEPAD_BUTTON_DPAD_LEFT;

	public static final int
			GAMEPAD_1_LEFT_AXIS_LEFT = 115 + GLFW_GAMEPAD_AXIS_LEFT_X,
			GAMEPAD_1_LEFT_AXIS_UP = 115 + GLFW_GAMEPAD_AXIS_LEFT_Y,
			GAMEPAD_1_RIGHT_AXIS_LEFT = 115 + GLFW_GAMEPAD_AXIS_RIGHT_X,
			GAMEPAD_1_RIGHT_AXIS_UP = 115 + GLFW_GAMEPAD_AXIS_RIGHT_Y,
			GAMEPAD_1_LEFT_TRIGGER = 115 + GLFW_GAMEPAD_AXIS_LEFT_TRIGGER,
			GAMEPAD_1_RIGHT_TRIGGER = 115 + GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER,
			GAMEPAD_1_LEFT_AXIS_RIGHT = 121 + GLFW_GAMEPAD_AXIS_LEFT_X,
			GAMEPAD_1_LEFT_AXIS_DOWN = 121 + GLFW_GAMEPAD_AXIS_LEFT_Y,
			GAMEPAD_1_RIGHT_AXIS_RIGHT = 121 + GLFW_GAMEPAD_AXIS_RIGHT_X,
			GAMEPAD_1_RIGHT_AXIS_DOWN = 121 + GLFW_GAMEPAD_AXIS_RIGHT_Y;
}