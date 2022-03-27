package utils;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Options {


	public static boolean useVsync = true;
	public static int fps = 60; //0 for unlimited


	//>--| Controlls |--<\\
	public static Map<ControllableAction, Integer> keyboardMapping = Map.of(
			ControllableAction.MOVE_LEFT, GLFW_KEY_A
			, ControllableAction.MOVE_RIGHT, GLFW_KEY_D
			, ControllableAction.MOVE_UP, GLFW_KEY_SPACE
			, ControllableAction.MOVE_DOWN, GLFW_KEY_LEFT_SHIFT
			, ControllableAction.MOVE_FORWARD, GLFW_KEY_W
			, ControllableAction.MOVE_BACKWARD, GLFW_KEY_S
			, ControllableAction.TURN_LEFT, GLFW_KEY_LEFT
			, ControllableAction.TURN_RIGHT, GLFW_KEY_RIGHT
			, ControllableAction.TURN_UP, GLFW_KEY_UP
			, ControllableAction.TURN_DOWN, GLFW_KEY_DOWN
	);

	public static int getKeyboardKey(ControllableAction action) {
		return keyboardMapping.get(action);
	}

	public static Map<ControllableAction, Integer> gamepadMapping = Map.of(
			ControllableAction.MOVE_LEFT, GLFW_JOYSTICK_1
			, ControllableAction.MOVE_RIGHT, GLFW_JOYSTICK_1
			, ControllableAction.MOVE_UP, GLFW_JOYSTICK_1
			, ControllableAction.MOVE_DOWN, GLFW_JOYSTICK_1
			, ControllableAction.TURN_LEFT, GLFW_JOYSTICK_2
			, ControllableAction.TURN_RIGHT, GLFW_JOYSTICK_2
			, ControllableAction.TURN_UP, GLFW_JOYSTICK_2
			, ControllableAction.TURN_DOWN, GLFW_JOYSTICK_2
	);

	public static int getGamepadAction(ControllableAction action) {
		return gamepadMapping.get(action);
	}
}
