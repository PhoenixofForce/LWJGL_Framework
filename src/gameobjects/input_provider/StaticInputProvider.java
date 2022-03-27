package gameobjects.input_provider;

public class StaticInputProvider implements InputProvider {

	@Override
	public boolean moveLeft() {
		return false;
	}

	@Override
	public boolean moveRight() {
		return false;
	}

	@Override
	public boolean moveUp() {
		return false;
	}

	@Override
	public boolean moveDown() {
		return false;
	}

	@Override
	public boolean moveForward() {
		return false;
	}

	@Override
	public boolean moveBackward() {
		return false;
	}

	@Override
	public boolean turnLeft() {
		return false;
	}

	@Override
	public boolean turnRight() {
		return false;
	}

	@Override
	public boolean turnUp() {
		return false;
	}

	@Override
	public boolean turnDown() {
		return false;
	}
}
