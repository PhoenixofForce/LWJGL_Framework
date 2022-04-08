package assets.models;

import assets.TextureHandler;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

public class TexturedModel extends Model {

	private final Model model;
	private final String textureName;

	public TexturedModel(String name, String textureName, List<Vector3f> vertices, List<Vector2f> textureCoordinates, List<Vector3f> vertexNormals, List<int[]> faces) {
		this.model = new Model(name, vertices, textureCoordinates, vertexNormals, faces);
		this.textureName = textureName;
	}

	public Model getModel() {
		return model;
	}

	public int getTexture() {
		return TextureHandler.getTexture(textureName);
	}

	@Override
	public int getVAO() {
		return model.getVAO();
	}

	@Override
	public int getFaceCount() {
		return model.getFaceCount();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof TexturedModel m) {
			return model.equals(m) && textureName.equals(m.textureName);
		}
		return false;
	}

	@Override
	public void cleanUp() {
		model.cleanUp();
	}

}
