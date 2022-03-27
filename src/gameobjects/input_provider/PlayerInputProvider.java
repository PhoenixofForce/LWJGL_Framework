package gameobjects.input_provider;

import utils.ControllableAction;

public class PlayerInputProvider implements InputProvider {

	@Override
	public boolean moveLeft() {
		return ControllableAction.MOVE_LEFT.anyPressed();
	}

	@Override
	public boolean moveRight() {
		return ControllableAction.MOVE_RIGHT.anyPressed();
	}

	@Override
	public boolean moveUp() {
		return ControllableAction.MOVE_UP.anyPressed();
	}

	@Override
	public boolean moveDown() {
		return ControllableAction.MOVE_DOWN.anyPressed();
	}

	@Override
	public boolean moveForward() {
		return ControllableAction.MOVE_FORWARD.anyPressed();
	}

	@Override
	public boolean moveBackward() {
		return ControllableAction.MOVE_BACKWARD.anyPressed();
	}

	@Override
	public boolean turnLeft() {
		return ControllableAction.TURN_LEFT.anyPressed();
	}

	@Override
	public boolean turnRight() {
		return ControllableAction.TURN_RIGHT.anyPressed();
	}

	@Override
	public boolean turnUp() {
		return ControllableAction.TURN_UP.anyPressed();
	}

	@Override
	public boolean turnDown() {
		return ControllableAction.TURN_DOWN.anyPressed();
	}
}
