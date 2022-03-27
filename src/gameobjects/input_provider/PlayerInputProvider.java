package gameobjects.input_provider;

import utils.ControllableAction;

public class PlayerInputProvider implements InputProvider {

	@Override
	public boolean moveLeft() {
		return ControllableAction.MOVE_LEFT.isTaken();
	}

	@Override
	public boolean moveRight() {
		return ControllableAction.MOVE_RIGHT.isTaken();
	}

	@Override
	public boolean moveUp() {
		return ControllableAction.MOVE_UP.isTaken();
	}

	@Override
	public boolean moveDown() {
		return ControllableAction.MOVE_DOWN.isTaken();
	}

	@Override
	public boolean moveForward() {
		return ControllableAction.MOVE_FORWARD.isTaken();
	}

	@Override
	public boolean moveBackward() {
		return ControllableAction.MOVE_BACKWARD.isTaken();
	}

	@Override
	public boolean turnLeft() {
		return ControllableAction.TURN_LEFT.isTaken();
	}

	@Override
	public boolean turnRight() {
		return ControllableAction.TURN_RIGHT.isTaken();
	}

	@Override
	public boolean turnUp() {
		return ControllableAction.TURN_UP.isTaken();
	}

	@Override
	public boolean turnDown() {
		return ControllableAction.TURN_DOWN.isTaken();
	}
}
