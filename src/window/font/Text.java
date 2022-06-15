package window.font;

import utils.MathUtils;
import org.joml.Vector3f;
import utils.StringUtils;
import window.gui.Anchor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Text {

	private List<String> text;
	private List<Vector3f> colors;
	private List<Float> wobbleStrengths;

	private Anchor alignment;

	public Text() {
		this(Anchor.ALIGN_LEFT);
	}

	public Text(Anchor alignment) {
		this.clear(Optional.of(alignment));
	}

	public Anchor getAlignment() {
		return alignment;
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

	public boolean hasChanged() {
		return false;
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

	public Text clear(Optional<Anchor> alignment) {
		this.alignment = alignment.orElse(this.alignment);
		text = new ArrayList<>();
		colors = new ArrayList<>();
		wobbleStrengths = new ArrayList<>();

		return this;
	}

	public Text clear() {
		return clear(Optional.empty());
	}

	public static Text fromString(String s) {
		Text out = new Text();

		List<String> optionTextFraments = StringUtils.splitTextIntoFraments(s, '[', ']', Optional.of("[def]"));

		for(int i = 0; i < optionTextFraments.size(); i += 2) {
			String options = optionTextFraments.get(i);
			String text = (i+1 < optionTextFraments.size()? optionTextFraments.get(i + 1): "");

			Vector3f color = MathUtils.vecFromColor(Color.decode(StringUtils.getOption(options, "color", ',', '=').orElse("#FFFFFF")));
			float wobble = Float.parseFloat(StringUtils.getOption(options, "wobble", ',', '=').orElse("0"));

			out.addText(text, color, wobble);
		}

		return out;
	}

}
