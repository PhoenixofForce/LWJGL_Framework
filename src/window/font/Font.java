package window.font;

import org.joml.Vector4f;

public abstract class Font {

	public abstract int getAtlas();
	public abstract Vector4f getBounds(char c);

	public abstract int getXoffset(char c);
	public abstract int getYoffset(char c);
	public abstract int getAdvance(char c);

	public abstract boolean hasCharacter(char c);

}
