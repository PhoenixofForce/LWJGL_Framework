package gameobjects.component_system.components;

import gameobjects.Entity;
import gameobjects.component_system.Component;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PositionComponent extends Component {

	private Vector3f position;
	private Vector3f rotation;
	private Vector3f scale;

	public PositionComponent(Entity e) {
		this(e, new Vector3f(0, 0, 0));
	}

	public PositionComponent(Entity e, float scale) {
		this(e, new Vector3f(0, 0, 0), scale);
	}

	public PositionComponent(Entity e, Vector3f position) {
		this(e, position, 1);
	}

	public PositionComponent(Entity e, Vector3f position, float scale) {
		this(e, position, new Vector3f(), new Vector3f(scale));
	}

	public PositionComponent(Entity e, Vector3f position, Vector3f rotation) {
		this(e, position, rotation, 1f);
	}

	public PositionComponent(Entity e, Vector3f position, Vector3f rotation, float scale) {
		this(e, position, rotation, new Vector3f(scale));
	}

	public PositionComponent(Entity e, Vector3f position, Vector3f rotation, Vector3f scale) {
		super(e);
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public void update() { }

	public Vector3f getPosition() {
		return new Vector3f(position);
	}

	public void add(Vector3f toAdd) {
		position.add(toAdd);
	}

	public void setRotation(Vector3f newRotation) {
		this.rotation = newRotation;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public Matrix4f transformationMatrix() {
		Matrix4f out = new Matrix4f();
		out.rotateXYZ(rotation.x, rotation.y, rotation.z);
		out.translate(position);
		out.scale(scale);
		return out;
	}
}