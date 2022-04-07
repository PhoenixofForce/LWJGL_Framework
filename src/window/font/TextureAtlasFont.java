package window.font;

import meshes.dim2.TextureAtlas;
import meshes.loader.TextureHandler;
import org.joml.Vector4f;

public class TextureAtlasFont extends Font {

	private TextureAtlas atlas;
	private int advance;

	public TextureAtlasFont(String atlasName) {
		this(TextureHandler.getAtlas(atlasName));
	}

	public TextureAtlasFont(TextureAtlas atlas) {
		this.atlas = atlas;
		this.advance = (int) atlas.getTextureBounds("A").z;
	}

	@Override
	public int getAtlas() {
		return atlas.getTexture();
	}

	@Override
	public Vector4f getBounds(char c) {
		return atlas.getTextureBounds(c + "");
	}

	@Override
	public int getXoffset(char c) {
		return 0;
	}

	@Override
	public int getYoffset(char c) {
		return 0;
	}

	@Override
	public int getAdvance(char c) {
		return advance;
	}

	@Override
	public float getMaximunLength() {
		return atlas.getTextureBounds("A").w;
	}

	@Override
	public boolean hasCharacter(char c) {
		return atlas.hasTexture(c + "");
	}
}
