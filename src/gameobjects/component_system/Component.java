package gameobjects.component_system;

import gameobjects.Entity;

public abstract class Component {

	protected Entity owner;
	private float priority;

	public Component(Entity e) {
		this(e, 0);
	}

	public Component(Entity e, float prio) {
		this.owner = e;
		this.priority = prio;
	}

	public abstract boolean init();
	public abstract void update(long dt);
	public void cleanup() { }

	public float getPriority() {
		return priority;
	}

	@Override
	public String toString() {
		Class<? extends Component> c = this.getClass();
		while(c.getSuperclass().getSimpleName() != Component.class.getSimpleName()) {
			c = (Class<? extends Component>) c.getSuperclass();
		}

		return c.getSimpleName();
	}
}