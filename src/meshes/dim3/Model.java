package meshes.dim3;

import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.Renderable;

import java.util.List;

public class Model extends Renderable {

	private String name;

	protected Model() {}

	public Model(String name, List<Vector3f> vertices, List<Vector2f> textureCoordinates, List<Vector3f> vertexNormals, List<int[]> faces) {
		this.name = name;
		initVAO(vertices, textureCoordinates, vertexNormals, faces);
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Model m) {
			return m.name.equals(name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
	}
}