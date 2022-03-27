package utils;

import window.inputs.InputHandler;

public enum ControllableAction {

	MOVE_UP, MOVE_DOWN,
	MOVE_LEFT, MOVE_RIGHT,
	MOVE_FORWARD, MOVE_BACKWARD,
	TURN_LEFT, TURN_RIGHT,
	TURN_UP, TURN_DOWN,
	;

	public boolean isTaken() {
		return InputHandler.isKeyPressed(Options.getKeyboardKey(this));
	}

}
