package window.font;

import org.joml.Vector4f;

public abstract class Font {

	private float scale;
	public Font() {
		this(1);
	}

	public Font(float scale) {
		this.scale = scale;
	}

	public abstract int getAtlas();
	public abstract Vector4f getBounds(String c);

	public abstract int getXoffset(String c);
	public abstract int getYoffset(String c);
	public abstract int getAdvance(String c);

	public abstract boolean hasCharacter(String c);

	public float getHeight(String c, float fontSize) {
		float heightMeasure = getBounds("M").w;
		float out = getBounds(c).w / heightMeasure * fontSize;
		return out * scale;
	}

	public float getWidth(String c, float fontSize) {
		Vector4f bounds = getBounds(c);
		float height = getHeight(c, fontSize);
		float aspect = bounds.z / bounds.w;

		return aspect * height;
	}

	public float getXoffset(String c, float fontSize) {
		Vector4f bounds = getBounds(c);
		float height = getHeight(c, fontSize);
		float aspect = getXoffset(c) / bounds.w;

		return aspect * height;
	}

	public float getYoffset(String c, float fontSize) {
		Vector4f bounds = getBounds(c);
		float height = getHeight(c, fontSize);
		float aspect = getYoffset(c) / bounds.w;

		return aspect * height;
	}

	public float getAdvance(String c, float fontSize) {
		Vector4f bounds = getBounds(c);
		float height = getHeight(c, fontSize);
		float aspect = getAdvance(c) / bounds.w;

		return aspect * height;
	}

}
