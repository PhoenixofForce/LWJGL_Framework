package gameobjects.component_system.components;

import gameobjects.Entity;
import gameobjects.component_system.Component;
import gameobjects.input_provider.InputProvider;
import gameobjects.input_provider.InputState;
import org.joml.Vector3f;
import utils.ControllableAction;

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
		InputState state = inputProvider.getInputState();

		float dx = 0, dy = 0, dz = 0;
		dz -= movementSpeed * state.getValue(ControllableAction.MOVE_BACKWARD);
		dz += movementSpeed * state.getValue(ControllableAction.MOVE_FORWARD);

		dx += movementSpeed * state.getValue(ControllableAction.MOVE_RIGHT);
		dx -= movementSpeed * state.getValue(ControllableAction.MOVE_LEFT);

		dy += movementSpeed * state.getValue(ControllableAction.MOVE_UP);
		dy -= movementSpeed * state.getValue(ControllableAction.MOVE_DOWN);

		pc.add(lc.getForwardAxis().mul(dz * dts));
		pc.add(lc.getRightAxis().mul(dx * dts));
		pc.add(new Vector3f(0, dy * dts, 0));


		float rotateY = 0, rotateX = 0;
		rotateY += rotationSpeed * dts * state.getValue(ControllableAction.TURN_LEFT);
		rotateY -= rotationSpeed * dts * state.getValue(ControllableAction.TURN_RIGHT);

		rotateX += rotationSpeed * dts * state.getValue(ControllableAction.TURN_UP);
		rotateX -= rotationSpeed * dts * state.getValue(ControllableAction.TURN_DOWN);

		rotation.add(rotateX, rotateY, 0);
		rotation.x = Math.min((float) Math.PI/2-0.05f, Math.max((float) -Math.PI/2 + 0.05f, rotation.x));

		Vector3f newLookingDirection = lc.getOriginalLookingDirection();
		newLookingDirection.rotateY(rotation.y);
		newLookingDirection.rotateAxis(rotation.x, -newLookingDirection.z, 0, newLookingDirection.x);

		Vector3f newUp = new Vector3f(lc.getNormalUp()).rotateAxis(rotation.z, newLookingDirection.x, 0, newLookingDirection.z);
		lc.set(newLookingDirection, newUp);
	}
}