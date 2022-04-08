package gameobjects.input_provider;

public interface InputProvider {

	float moveLeft();
	float moveRight();
	float moveUp();
	float moveDown();
	float moveForward();
	float moveBackward();

	float turnLeft();
	float turnRight();
	float turnUp();
	float turnDown();

}
