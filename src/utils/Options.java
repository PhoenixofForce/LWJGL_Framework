package utils;

import window.inputs.KeyCodes;

import java.io.*;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Options {

	private static final String optionsFile = "options";

	public static boolean useVsync = true;
	public static int fps = 60; //0 for unlimited

	public static float totalVolume = 0.5f;
	public static float musicVolume = 0.05f;
	public static float effectVolume = 1f;

	public static boolean fullScreen = false;

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
			ControllableAction.MOVE_LEFT, KeyCodes.GAMEPAD_1_LEFT_AXIS_LEFT
			, ControllableAction.MOVE_RIGHT, KeyCodes.GAMEPAD_1_LEFT_AXIS_RIGHT
			, ControllableAction.MOVE_UP, KeyCodes.GAMEPAD_1_RIGHT_TRIGGER
			, ControllableAction.MOVE_DOWN, KeyCodes.GAMEPAD_1_LEFT_TRIGGER
			, ControllableAction.MOVE_FORWARD, KeyCodes.GAMEPAD_1_LEFT_AXIS_UP
			, ControllableAction.MOVE_BACKWARD, KeyCodes.GAMEPAD_1_LEFT_AXIS_DOWN
			, ControllableAction.TURN_LEFT, KeyCodes.GAMEPAD_1_RIGHT_AXIS_LEFT
			, ControllableAction.TURN_RIGHT, KeyCodes.GAMEPAD_1_RIGHT_AXIS_RIGHT
			, ControllableAction.TURN_UP, KeyCodes.GAMEPAD_1_RIGHT_AXIS_UP
			, ControllableAction.TURN_DOWN, KeyCodes.GAMEPAD_1_RIGHT_AXIS_DOWN
	);

	public static int getGamepadAction(ControllableAction action) {
		return gamepadMapping.get(action);
	}


	public static void save() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(optionsFile));

			writer.write(useVsync? "1": "0");
			writer.newLine();
			writer.write(fps + "");
			writer.newLine();
			writer.write(totalVolume + "");
			writer.newLine();
			writer.write(musicVolume + "");
			writer.newLine();
			writer.write(effectVolume + "");
			writer.newLine();
			writer.write(fullScreen? "1": "0");
			writer.newLine();

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(optionsFile));

			useVsync = reader.readLine().equals("1");
			fps = Integer.parseInt(reader.readLine());
			totalVolume = Float.parseFloat(reader.readLine());
			musicVolume = Float.parseFloat(reader.readLine());
			effectVolume = Float.parseFloat(reader.readLine());
			fullScreen = reader.readLine().equals("1");

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
