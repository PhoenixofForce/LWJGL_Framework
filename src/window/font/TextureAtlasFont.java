package window.font;

import assets.textures.TextureAtlas;
import assets.TextureHandler;
import org.joml.Vector4f;
import utils.Constants;

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
	public Vector4f getBounds(String c) {
		return atlas.getTextureBounds(c + "");
	}

	@Override
	public int getXoffset(String c) {
		return 0;
	}

	@Override
	public int getYoffset(String c) {
		return 0;
	}

	@Override
	public int getAdvance(String c) {
		return advance;
	}


	@Override
	public boolean hasCharacter(String c) {
		return atlas.hasTexture(c + "") || " ".equals(c);
	}

	@Override
	public float getAdvance(String c, float fontSize) {
		return super.getAdvance(c, fontSize) * (1 + Constants.FONT_SPACING);
	}
}
