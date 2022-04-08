package gameobjects.input_provider;

import utils.ControllableAction;

public class PlayerInputProvider implements InputProvider {

	@Override
	public float moveLeft() {
		return ControllableAction.MOVE_LEFT.anyPressed();
	}

	@Override
	public float moveRight() {
		return ControllableAction.MOVE_RIGHT.anyPressed();
	}

	@Override
	public float moveUp() {
		return ControllableAction.MOVE_UP.anyPressed();
	}

	@Override
	public float moveDown() {
		return ControllableAction.MOVE_DOWN.anyPressed();
	}

	@Override
	public float moveForward() {
		return ControllableAction.MOVE_FORWARD.anyPressed();
	}

	@Override
	public float moveBackward() {
		return ControllableAction.MOVE_BACKWARD.anyPressed();
	}

	@Override
	public float turnLeft() {
		return ControllableAction.TURN_LEFT.anyPressed();
	}

	@Override
	public float turnRight() {
		return ControllableAction.TURN_RIGHT.anyPressed();
	}

	@Override
	public float turnUp() {
		return ControllableAction.TURN_UP.anyPressed();
	}

	@Override
	public float turnDown() {
		return ControllableAction.TURN_DOWN.anyPressed();
	}
}
