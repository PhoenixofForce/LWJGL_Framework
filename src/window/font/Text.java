package window.font;

import org.joml.Vector3f;
import window.gui.Anchor;

import java.util.List;

public interface Text {

	List<String> getTextFragments();
	List<Vector3f> getColorFragments();
	List<Float> getWobbleFragments();

	Anchor getAlignment();
	char getFirstChar();
	boolean hasChanged();

}
