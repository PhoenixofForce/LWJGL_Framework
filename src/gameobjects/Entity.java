package gameobjects;

import gameobjects.component_system.Component;
import gameobjects.component_system.components.RenderingComponent;
//import gameobjects.entities.Camera;
import org.joml.Matrix4f;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Entity {

	private Map<String, List<Component>> components;

	//>--| ENTITY LIFECYCLE  |--<\\

	protected void init() {
		this.components = new HashMap<>();
		addComponents();
		initComponents();
	}

	public void update() {
		updateComponents();
	}
	
	//public void render(Matrix4f projection_matrix, Camera cam) {
	public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
		for(RenderingComponent rc: getComponents(RenderingComponent.class)) {
			//rc.render(projection_matrix, cam);
			rc.render(projectionMatrix, viewMatrix);
		}
	}
	public void cleanUp() { 
		cleanupComponents();
	}

	//>--| COMPONENT LIFECYCLE  |--<\\

	private void initComponents() {
		components.forEach((k, v) -> {
			v.sort(Comparator.comparingDouble(Component::getPriority));
		});

		components.forEach((k, v) -> {
			for(int i = v.size() - 1; i >= 0; i--) {
				Component c = v.get(i);
				boolean initSuccess = c.init();
				if(!initSuccess) {
					System.err.println("Error initialising " + c.toString() + " of " + this.toString());
					v.remove(c);
				}
			}
		});
	}

	private void updateComponents() {
		components.forEach((k, v) -> {
			v.forEach(Component::update);
		});
	}

	private void cleanupComponents() {
		components.forEach((k, v) -> {
			v.forEach(Component::cleanup);
		});
	}

	//>--| COMPONENT HANDLING  |--<\\

	protected abstract void addComponents();
	public Component addComponent(Component c) {
		if(!components.containsKey(c.toString())) components.put(c.toString(), new LinkedList<>());
		components.get(c.toString()).add(c);
		return c;
	}

	public boolean hasComponent(Class<? extends Component> t) {
		return components.containsKey(t.toString()) && components.get(t.toString()).size() > 0;
	}

	public <T extends Component> Optional<T> getComponent(Class<T> t) {
		if(hasComponent(t)) {
			return (Optional<T>) Optional.of(components.get(t).get(0));
		}
		return Optional.empty();
	}

	private <T extends Component> List<T> getComponents(Class<T> t) {
		return (List<T>) components.getOrDefault(t, new LinkedList<>());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}