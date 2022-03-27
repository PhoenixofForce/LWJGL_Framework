package gameobjects.component_system.components;

import gameobjects.Entity;
import gameobjects.component_system.Component;
import org.joml.Vector3f;

public class LookingComponent extends Component {

	private Vector3f originalLookingDirection;
	private Vector3f lookingDirection;
	private Vector3f upNormal, upUsed;

	public LookingComponent(Entity e) {
		this(e, new Vector3f(0, 0, 1));
	}

	public LookingComponent(Entity e, Vector3f lookingDirection) {
		super(e);
		this.lookingDirection = lookingDirection.normalize();
		this.originalLookingDirection = new Vector3f(this.lookingDirection);

		upNormal = new Vector3f(0.0f, 1.0f, 0.0f);
		upUsed = new Vector3f(upNormal);
	}

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public void update(long dt) { }

	public Vector3f getLookingDirection() {
		return new Vector3f(lookingDirection);
	}

	public Vector3f getForwardAxis() {
		return new Vector3f(lookingDirection.x, 0, lookingDirection.z).normalize();
	}

	public Vector3f getRightAxis() {
		return new Vector3f(-lookingDirection.z, 0, lookingDirection.x).normalize();
	}

	public Vector3f getUpAxis() {
		return upUsed;
	}

	public Vector3f getNormalUp() {
		return upNormal;
	}

	public void set(Vector3f lookingDirection, Vector3f up) {
		if(up != null) this.upUsed = up;
		if(lookingDirection != null) this.lookingDirection = lookingDirection.normalize();
	}

	public Vector3f getOriginalLookingDirection() {
		return new Vector3f(originalLookingDirection);
	}

}
