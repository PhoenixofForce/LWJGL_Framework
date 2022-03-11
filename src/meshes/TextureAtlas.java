package meshes;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector4f;

public class TextureAtlas {

	private final String atlasTextureName;
	private final Map<String, Vector4f> textureBounds;

	public TextureAtlas(String atlasTextureName) {
		this.atlasTextureName = atlasTextureName;
		this.textureBounds = new HashMap<>();
	}

	public void addTexture(String textureName, Vector4f textureBounds) {
		this.textureBounds.put(textureName, textureBounds);
	}

	public int getTexture() {
		return TextureHandler.getTexture(atlasTextureName);
	}

	public Vector4f getTextureBounds(String textureName) {
		return textureBounds.getOrDefault(textureName, new Vector4f());
	}
}
