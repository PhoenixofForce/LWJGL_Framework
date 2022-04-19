package window.font;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Text {

	private List<String> text;
	private List<Vector3f> colors;
	private List<Float> wobbleStrengths;

	public Text() {
		this.clear();
	}

	public List<String> getTextFragments() {
		return text;
	}

	public List<Vector3f> getColorFragments() {
		return colors;
	}

	public List<Float> getWobbleFragments() {
		return wobbleStrengths;
	}

	public char getFirstChar() {
		if(text.size() == 0) return ' ';
		String s = text.get(0);
		if(s.length() == 0) return ' ';
		return s.charAt(0);
	}


	//>--| BUILDER |--<\\

	public Text addText(String string, Vector3f color, float wobbleStrength) {
		text.add(string.replaceAll("(\r\n|\r)", "\n"));	//we want \n be the only newline character
		colors.add(color);
		wobbleStrengths.add(wobbleStrength);

		return this;
	}

	public Text addText(String string, Vector3f color) {
		return this.addText(string, color, 0);
	}

	public Text addText(String string) {
		return this.addText(string, new Vector3f(1));
	}

	public Text newLine() {
		return this.addText("\n");
	}

	public Text clear() {
		text = new ArrayList<>();
		colors = new ArrayList<>();
		wobbleStrengths = new ArrayList<>();

		return this;
	}

}
