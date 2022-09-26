package gameobjects.entities;

import gameobjects.Entity;
import gameobjects.component_system.components.LookingComponent;
import gameobjects.component_system.components.MovementComponent;
import gameobjects.component_system.components.PositionComponent;
import gameobjects.input_provider.InputProvider;
import gameobjects.input_provider.middleware.PlayerControlMiddleware;
import org.joml.Vector3f;

public class Camera extends Entity {

	public Camera() {
		super.init();
	}

	@Override
	protected void addComponents() {
		this.addComponent(new PositionComponent(this, new Vector3f(0, 0, 0)));
		this.addComponent(new LookingComponent(this, new Vector3f(0, 0, 1)));
		this.addComponent(new MovementComponent(this, new InputProvider(new PlayerControlMiddleware())));
	}

	public Vector3f getPosition() {
		return getComponent(PositionComponent.class).get().getPosition();
	}

	public Vector3f getLookingDirection() {
		return getComponent(LookingComponent.class).get().getLookingDirection();
	}

	public Vector3f getUp() {
		return getComponent(LookingComponent.class).get().getUpAxis();
	}
}
