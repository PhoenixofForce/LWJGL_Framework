package gameobjects.input_provider;

public interface InputProvider {

	boolean moveLeft();
	boolean moveRight();
	boolean moveUp();
	boolean moveDown();
	boolean moveForward();
	boolean moveBackward();

	boolean turnLeft();
	boolean turnRight();
	boolean turnUp();
	boolean turnDown();

}
