package gameobjects.component_system.components;

import gameobjects.Entity;
import gameobjects.component_system.Component;
import gameobjects.input_provider.InputProvider;
import org.joml.Vector3f;

public class MovementComponent extends Component {

	private float movementSpeed = 3f;
	private float rotationSpeed = 2f;
	private Vector3f rotation;

	private PositionComponent pc;
	private LookingComponent lc;

	private InputProvider inputProvider;

	public MovementComponent(Entity e, InputProvider provider) {
		super(e);
		rotation = new Vector3f();
		this.inputProvider = provider;
	}

	@Override
	public boolean init() {
		if(owner.hasComponent(PositionComponent.class)) {
			pc = owner.getComponent(PositionComponent.class).get();
		} else return false;

		if(owner.hasComponent(LookingComponent.class)) {
			lc = owner.getComponent(LookingComponent.class).get();
		} else return false;

		return true;
	}

	@Override
	public void update(long dt) {
		float dts = dt / 1000.0f;

		float dx = 0, dy = 0, dz = 0;
		dz -= movementSpeed * inputProvider.moveBackward();
		dz += movementSpeed * inputProvider.moveForward();

		dx += movementSpeed * inputProvider.moveRight();
		dx -= movementSpeed * inputProvider.moveLeft();

		dy += movementSpeed * inputProvider.moveUp();
		dy -= movementSpeed * inputProvider.moveDown();

		pc.add(lc.getForwardAxis().mul(dz * dts));
		pc.add(lc.getRightAxis().mul(dx * dts));
		pc.add(new Vector3f(0, dy * dts, 0));


		float rotateY = 0, rotateX = 0;
		rotateY += rotationSpeed * dts * inputProvider.turnLeft();
		rotateY -= rotationSpeed * dts * inputProvider.turnRight();

		rotateX += rotationSpeed * dts * inputProvider.turnUp();
		rotateX -= rotationSpeed * dts * inputProvider.turnDown();

		rotation.add(rotateX, rotateY, 0);
		rotation.x = Math.min((float) Math.PI/2-0.05f, Math.max((float) -Math.PI/2 + 0.05f, rotation.x));

		Vector3f newLookingDirection = lc.getOriginalLookingDirection();
		newLookingDirection.rotateY(rotation.y);
		newLookingDirection.rotateAxis(rotation.x, -newLookingDirection.z, 0, newLookingDirection.x);

		Vector3f newUp = new Vector3f(lc.getNormalUp()).rotateAxis(rotation.z, newLookingDirection.x, 0, newLookingDirection.z);
		lc.set(newLookingDirection, newUp);
	}
}