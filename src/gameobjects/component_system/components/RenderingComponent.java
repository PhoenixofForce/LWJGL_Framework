package gameobjects.component_system.components;

import org.joml.Matrix4f;

import gameobjects.Entity;
import gameobjects.component_system.Component;

public abstract class RenderingComponent extends Component {

	protected PositionComponent pc;

	public RenderingComponent(Entity e) {
		super(e, 10);
	}

	@Override
	public boolean init() {
		if(owner.hasComponent(PositionComponent.class)) {
			pc = owner.getComponent(PositionComponent.class).get();
		}

		return true;
	}

	@Override
	public void update(long dt) { }

	//public abstract void render(Matrix4f projectionMatrix, Camera cam);
	public abstract void render(Matrix4f projectionMatrix, Matrix4f viewMatrix);

	
	@Override
	public String toString() {
		return this.getClass().getSuperclass().getSimpleName();
	}
}
