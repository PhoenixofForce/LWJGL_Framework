package window.inputs;

import utils.MathUtils;
import utils.Constants;
import utils.Options;
import utils.Screenshot;
import window.Window;
import window.views.MainMenuView;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

	private static Stack<FocusHolder> focusStack = new Stack<>();	//maybe change to single Holder to avoid filling up. or clear stack if window gets focus
	private static FocusHolder getCurrentFocusHolder() {
		if(!focusStack.isEmpty()) {
			return focusStack.peek();
		}

		return Window.INSTANCE;
	}

	public static boolean hasFocus(FocusHolder focusHolder) {
		return getCurrentFocusHolder() == focusHolder;
	}

	public static void requestFocus(FocusHolder focus) {
		if(focus instanceof Window) {
			focusStack.clear();
		}

		else if(focusStack.isEmpty() || focusStack.peek() != focus) {
			focusStack.push(focus);
		}
	}

	public static boolean dequestFocus(FocusHolder focus) {
		if(!focusStack.isEmpty() && focusStack.peek() == focus) {
			focusStack.pop();
			return true;
		}

		return false;
	}

	public static float mouseX = 0, mouseY = 0;
	public static float mouseDX, mouseDY;

	public static void callbacks() {
		glfwSetCharCallback(Window.INSTANCE.window, (window, charAsInt) -> {
			getCurrentFocusHolder().charStartRepeat((char) charAsInt);
		});

		glfwSetKeyCallback(Window.INSTANCE.window, (window, key, scancode, action, mods) -> {
			KeyHit click = lastPresses.get(key);

			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				Window.INSTANCE.setView(new MainMenuView(), true);
			if(key == GLFW_KEY_F2 && action == GLFW_RELEASE) {
				Screenshot.screenShot();
			}

			if(action == GLFW_RELEASE && ((key == GLFW_KEY_ENTER && isKeyPressed(GLFW_KEY_LEFT_ALT) > 0) ||
					(key == GLFW_KEY_LEFT_ALT && isKeyPressed(GLFW_KEY_ENTER) > 0))) {
				Window.INSTANCE.setFullscreen(!Window.INSTANCE.isFullscreen());
				Options.fullScreen = Window.INSTANCE.isFullscreen();
			}

			if(action == GLFW_PRESS) {
				click = new KeyHit(key);
				lastPresses.put(key, click);

				getCurrentFocusHolder().handleStart(click);
			} else if(action == GLFW_RELEASE) {
				if(click != null) {
					click.setEnd();
					getCurrentFocusHolder().handleEnd(click);
				}
				else System.err.println("click null");
			} else if(action == GLFW_REPEAT) {
				if(click != null) {
					getCurrentFocusHolder().handleRepeat(click);
				}
			}
			else {
				System.err.println("Unknown action " + action);
			}
		});
	}

	public static void update() {
		getMouseInputs();
		getControllerInputs();
	}

	private static void getMouseInputs() {
		float[] mousePosition = getMousePosition();

		mouseDX = Math.signum(mousePosition[0] - mouseX) * Constants.MOUSE_SENSITIVITY;
		mouseDY = Math.signum(mousePosition[1] - mouseY) * Constants.MOUSE_SENSITIVITY;

		putMouseInputInMap(KeyCodes.MOUSE_MOVE_LEFT, -1, mouseDX, 0);
		putMouseInputInMap(KeyCodes.MOUSE_MOVE_RIGHT, 0, mouseDX, 1);
		putMouseInputInMap(KeyCodes.MOUSE_MOVE_DOWN, -1, mouseDY, 0);
		putMouseInputInMap(KeyCodes.MOUSE_MOVE_UP, 0, mouseDY, 1);

		if(Constants.GRAB_MOUSE) {
			glfwSetCursorPos(Window.INSTANCE.window, Constants.DEFAULT_WIDTH / 2.0f, Constants.DEFAULT_HEIGHT / 2.0f);
			mousePosition = getMousePosition();
		}

		mouseX = mousePosition[0];
		mouseY = mousePosition[1];
	}

	private static void putMouseInputInMap(int code, float min, float mouseDD, float max) {
		float movement = Math.abs(MathUtils.clamp(min, mouseDD, max));
		KeyHit hit = lastPresses.getOrDefault(code, null);
		if(hit != null && movement == 0) hit.setValue(0);
		if(hit == null) hit = new KeyHit(code, 0);
		hit.setValue(movement);
		lastPresses.put(code, hit);
	}

	private static void getControllerInputs() {
		for (int c = 0; c < 16; c++) {
			if (glfwJoystickPresent(GLFW_JOYSTICK_1 + c) && glfwJoystickIsGamepad(GLFW_JOYSTICK_1 + c)) {
				ByteBuffer values = glfwGetJoystickButtons(GLFW_JOYSTICK_1 + c);
				for (int i = 0; i < values.limit(); i++) {
							putControllerInputInMap(100 + i, values.get(i) == 1, 1);
				}

				FloatBuffer values2 = glfwGetJoystickAxes(GLFW_JOYSTICK_1 + c);
				for (int j = 0; j < values2.limit(); j++) {
					if (j == GLFW_GAMEPAD_AXIS_LEFT_TRIGGER || j == GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) {
						float triggerValue = (values2.get(j) + 1f) / 2f;
						putControllerInputInMap(115 + j, triggerValue > 0, triggerValue);

					} else {

						float up_left = -Math.min(0, values2.get(j) + Constants.STICK_DEAD_ZONE) / (1 - Constants.STICK_DEAD_ZONE);
						putControllerInputInMap(115 + j, up_left != 0, up_left);

						float down_right = Math.max(0, values2.get(j) - Constants.STICK_DEAD_ZONE) / (1 - Constants.STICK_DEAD_ZONE);
						putControllerInputInMap(121 + j, down_right != 0, down_right);
					}
				}
			}
		}
	}

	private static void putControllerInputInMap(int code, boolean pressed, float value) {
		KeyHit hit = lastPresses.get(code);

		if(pressed) {
			if(hit == null || !hit.isClickInProgress()) {
				hit = new KeyHit(code, value);
				lastPresses.put(code, hit);
			}

			if(hit.isClickInProgress()) {
				hit.setValue(value);
			}
		} else {
			if(hit != null && hit.isClickInProgress()) {
				hit.setEnd();
			}
		}
	}

	//>--| KEYS |--<\\

	private static final Map<Integer, KeyHit> lastPresses = new HashMap<>();

	public static float isKeyPressed(int keyCode) {
		return isKeyPressed(Window.INSTANCE, keyCode);
	}

	public static float isKeyPressed(FocusHolder focus, int keyCode) {
		if(getCurrentFocusHolder() != focus) return 0;

		int action = glfwGetKey(Window.INSTANCE.window, keyCode);

		if(keyCode == KeyCodes.MOUSE_MOVE_UP) {
			System.out.println(lastPresses.containsKey(keyCode));
			System.out.println(lastPresses.get(keyCode));
		}

		if(lastPresses.containsKey(keyCode) && lastPresses.get(keyCode).isClickInProgress()) {
			return lastPresses.get(keyCode).value;
		}

		return action == GLFW_PRESS? 1: 0;
	}

	public static float isKeyPressed(int keyCode, long timeBuffer) {
		return isKeyPressed(Window.INSTANCE, keyCode, timeBuffer);
	}

	public static float isKeyPressed(FocusHolder focus, int keyCode, long timeBuffer) {
		if(getCurrentFocusHolder() != focus) return 0;

		if(lastPresses.containsKey(keyCode) && lastPresses.get(keyCode).timeSinceEnd() <= timeBuffer) {
			return lastPresses.get(keyCode).value;
		}

		return isKeyPressed(keyCode);
	}

	public static float isKeyPressedConsume(int keyCode, long timeBuffer) {
		return isKeyPressedConsume(Window.INSTANCE, keyCode, timeBuffer);
	}

	public static float isKeyPressedConsume(FocusHolder focus, int keyCode, long timeBuffer) {
		if(getCurrentFocusHolder() != focus) return 0;

		float out = isKeyPressed(keyCode, timeBuffer);
		if(out > 0.0f) consumeClick(focus, keyCode);
		return out;
	}

	public static float isLongClick(int keyCode, long minimumForLong) {
		return isLongClick(Window.INSTANCE, keyCode, minimumForLong);
	}

	public static float isLongClick(FocusHolder focus, int keyCode, long minimumForLong) {
		if(getCurrentFocusHolder() != focus) return 0;

		if(lastPresses.containsKey(keyCode)) {
			KeyHit hit = lastPresses.get(keyCode);
			if(hit == null) return 0.0f;

			return hit.getClickDuration() >= minimumForLong? 1: 0;
		}

		return 0.0f;
	}

	public static float isLongClickConsume(int keyCode, long minimumForLong) {
		return isLongClickConsume(Window.INSTANCE, keyCode, minimumForLong);
	}

	public static float isLongClickConsume(FocusHolder focus, int keyCode, long minimumForLong) {
		if(getCurrentFocusHolder() != focus) return 0;

		float out = isLongClick(keyCode, minimumForLong);
		if(out > 0.0) consumeClick(focus, keyCode);
		return out;
	}

	public static void consumeClick(int keycode) {
		consumeClick(Window.INSTANCE, keycode);
	}

	public static void consumeClick(FocusHolder focus, int keycode) {
		if(getCurrentFocusHolder() != focus) return;

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