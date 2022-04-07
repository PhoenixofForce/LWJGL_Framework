package rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Uniform {

	private Matrix4f[] matrices;
	private int[] textures;
	private float[] floats;
	private Vector3f[] vector3fs;
	private Vector4f[] vector4fs;

	public Uniform() {}

	public void setMatrices(Matrix4f... matrices) {
		this.matrices = matrices;
	}

	public void setTextures(int... textures) {
		this.textures = textures;
	}

	public void setFloats(float... floats) {
		this.floats = floats;
	}

	public void setVector3fs(Vector3f... vecs) {
		this.vector3fs = vecs;
	}

	public void setVector4fs(Vector4f... vecs) {
		this.vector4fs = vecs;
	}

	public void load() {
		if(matrices != null) UniformLoader.loadMatrices(matrices);
		if(textures != null) UniformLoader.loadTextures(textures);
		if(floats != null) UniformLoader.loadFloats(floats);
		if(vector3fs != null) UniformLoader.loadVec3s(vector3fs);
		if(vector4fs != null) UniformLoader.loadVec4s(vector4fs);
		clear();
	}

	private void clear() {
		matrices = null;
		textures = null;
		floats = null;
		vector3fs = null;
		vector4fs = null;
	}

}
