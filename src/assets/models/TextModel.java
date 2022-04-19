package assets.models;

import org.joml.Vector3f;
import org.joml.Vector4f;
import rendering.Renderable;
import utils.Constants;
import window.font.Font;
import window.font.Text;
import window.gui.Anchor;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL45.glDisableVertexArrayAttrib;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;

public class TextModel extends Renderable {

	private record TextFragment(String texture, Vector3f color, float wobbleStrength) { }


	/*
		NOTE: In this class are some * 2 and / 2 when calculating widths ant heights
		im not sure why that is needed, but i thinks its because the vertices used are from [-1, 1] which makes the rectangle 2 long

		<Special Character> denotes special textures.
		If you want < to be written you need to escape it with an \ (or \\ when in java string)

	 */

	private float maxWidth, maxHeight;

	private int rectVAO, rectVBO, rectUVVBO;
	private int instanceVBO;

	private int chars = 0;
	private float width;
	private float height;

	private static final int floats = 4 + 4 + 3 + 1 + 1;	//4 for position, 4 for atlas bounds, 3 for color, 1 for the char index, 1 for wobble strength

	public TextModel(float maxWidth, float maxHeight) {
		super();
		this.maxWidth = (maxWidth == 0? Float.MAX_VALUE: maxWidth);
		this.maxHeight = (maxHeight == 0? Float.MAX_VALUE: maxHeight);

		initVao();
		createInstanceVBO();
	}

	private void initVao() {
		rectVAO = glGenVertexArrays();
		glBindVertexArray(rectVAO);

		rectVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, rectVBO);
		glBufferData(GL_ARRAY_BUFFER, Constants.RECT_VERT, GL_STATIC_DRAW);

		glEnableVertexArrayAttrib(rectVAO, 0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

		rectUVVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, rectUVVBO);
		glBufferData(GL_ARRAY_BUFFER, Constants.RECT_UV, GL_STATIC_DRAW);

		glEnableVertexArrayAttrib(rectVAO, 1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		buffers.add(rectVBO);
		buffers.add(rectUVVBO);
	}

	private void createInstanceVBO() {
		if(instanceVBO != -1) glDeleteBuffers(instanceVBO);
		instanceVBO = glGenBuffers();

		glBindVertexArray(getVAO());
		glBindBuffer(GL_ARRAY_BUFFER, instanceVBO);
		glBufferData(GL_ARRAY_BUFFER, 4L * floats, GL_STREAM_DRAW);

		glVertexAttribPointer(2, 4, GL_FLOAT, false, floats * 4, 0 * 4);
		glVertexAttribPointer(3, 4, GL_FLOAT, false, floats * 4, 4 * 4);
		glVertexAttribPointer(4, 3, GL_FLOAT, false, floats * 4, 8 * 4);
		glVertexAttribPointer(5, 1, GL_FLOAT, false, floats * 4, 11 * 4);
		glVertexAttribPointer(6, 1, GL_FLOAT, false, floats * 4, 12 * 4);

		glVertexAttribDivisor(2, 1);
		glVertexAttribDivisor(3, 1);
		glVertexAttribDivisor(4, 1);
		glVertexAttribDivisor(5, 1);
		glVertexAttribDivisor(6, 1);

		glEnableVertexArrayAttrib(getVAO(), 2);
		glEnableVertexArrayAttrib(getVAO(), 3);
		glEnableVertexArrayAttrib(getVAO(), 4);
		glEnableVertexArrayAttrib(getVAO(), 5);
		glEnableVertexArrayAttrib(getVAO(), 6);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	public void updateInstance(Font font, float fontSize, float width, Text text) {
		List<List<TextFragment>> lines = preprocess(font, fontSize, text.getTextFragments(), text.getColorFragments(), text.getWobbleFragments());
		this.width = lines.stream().map(e -> calculateFragmentWidth(font, fontSize, e)).max(Float::compare).orElse(0f) * 2;
		this.width = Math.max(this.width, width);
		buildModel(font, fontSize, lines, text.getAlignment());
	}

	/*
		Preprocesses the text so that each line is filled as much as possible

	 */
	private List<List<TextFragment>> preprocess(Font font, float fontSize, List<String> textFragments, List<Vector3f> colorFragments, List<Float> wobbleStrengthFragments) {
		List<List<TextFragment>> lines = new ArrayList<>();

		int currentLineLength = 0;
		List<TextFragment> currentLine = new ArrayList<>();

		for(int i = 0; i < textFragments.size(); i++) {
			String text = textFragments.get(i);
			Vector3f color = colorFragments.get(i);
			float wobble = wobbleStrengthFragments.get(i);

			List<String> parsedText = parseText(font, text);
			List<List<String>> words = toWords(parsedText);

			for(int wordIndex = 0; wordIndex < words.size(); wordIndex++) {
				List<String> word = words.get(wordIndex);

				if(word.size() == 1 && word.get(0).equals("\n")) {
					lines.add(currentLine);
					currentLine = new ArrayList<>();
					currentLineLength = 0;
					continue;
				}

				float wordLength = calculateWidth(font, fontSize, word);
				if(currentLineLength + wordLength > maxWidth) {
					if(currentLineLength > 0) {
						lines.add(currentLine);
						currentLine = new ArrayList<>();
						currentLineLength = 0;
					}

					if(wordLength > maxWidth) {
						List<List<String>> cutWord = cutWord(font, fontSize, word);
						words.set(wordIndex, cutWord.get(0));

						for(int j = cutWord.size() - 1; j >= 1; j--) {
							words.add(wordIndex+1, cutWord.get(j));
						}

						word = words.get(wordIndex);
					}
				}

				for(String s: word) {
					if(s.equals(" ") && currentLineLength == 0) continue;

					currentLineLength += font.getAdvance(s, fontSize);
					currentLine.add(new TextFragment(s, color, wobble));
				}
			}
		}

		lines.add(currentLine);
		this.width = Math.max(currentLineLength * 2, this.width);
		this.height = (2 + Constants.FONT_SPACING) * lines.size() * fontSize - Constants.FONT_SPACING * fontSize;

		return lines;
	}

	private void buildModel(Font font, float fontSize, List<List<TextFragment>> lines, Anchor alignment) {
		float effectiveWidth = maxWidth == Float.MAX_VALUE? width: maxWidth;

		List<Float> floatsData = new ArrayList<>();
		float x = 0;
		float y = 0;

		chars = 0;
		int indexes = 0;

		float align = (-alignment.getMultiplier() + 1);

		for(List<TextFragment> line: lines) {
			x = effectiveWidth * align - calculateFragmentWidth(font, fontSize, line) * align;	//calculates alignment position here
					// left = 0
					// center = this.width - lineWidth
				    // right = this.width * 2 - lineWidth * 2
					// Left(0), center(1), Right(2)
					// x = this.width * align.float - lineWidth * align.float

			for(TextFragment character: line) {
				String texture = character.texture;

				float fontWidth = font.getWidth(texture, fontSize);
				float fontHeight = font.getHeight(texture, fontSize);

				addData(floatsData, x + font.getXoffset(texture, fontSize), y - font.getYoffset(texture, fontSize) , fontWidth, fontHeight);
				addData(floatsData, font.getBounds(texture));
				addData(floatsData, character.color);
				addData(floatsData, indexes, character.wobbleStrength);

				x += font.getAdvance(texture, fontSize) * 2;
				chars++;
				indexes++;
				if(List.of(".", "?", "!").contains(texture)) indexes += 9;		//longer pauses at sentence ends
				else if(List.of(",", ":", ";", "-").contains(texture)) indexes += 4;	//medium pauses at these
			}

			y -= fontSize * (Constants.FONT_SPACING + 2);
		}

		float[] data = new float[floatsData.size()];
		for(int i = 0; i < floatsData.size(); i++) {
			data[i] = floatsData.get(i);
		}

		glBindBuffer(GL_ARRAY_BUFFER, instanceVBO);
		glBufferData(GL_ARRAY_BUFFER, chars * floats * 4L, GL_STREAM_DRAW);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		this.chars = indexes;
	}

	/*
		Parses the text into characters that the font has
	 */
	private List<String> parseText(Font font, String text) {
		List<String> out = new ArrayList<>();

		String specialText = "";
		boolean escaped = false;
		boolean inSpecialText = false;
		for(char c: text.toCharArray()) {
			String character = c + "";

			if(c == '<' && !escaped) {
				inSpecialText = true;
				continue;
			} else if(c == '>' && !escaped) {
				inSpecialText = false;

				if(font.hasCharacter(specialText)) {
					out.add(specialText);
				}

				continue;
			} else if(c == '\\' && !escaped) {
				escaped = true;
				continue;
			}

			if(inSpecialText) {
				specialText += c;
				continue;
			}

			if(!font.hasCharacter(character) && c != '\n') {
				if(font.hasCharacter(character.toUpperCase())) character = character.toUpperCase();
				else if(font.hasCharacter(character.toLowerCase())) character = character.toLowerCase();
				else continue;
			}

			if(font.hasCharacter(character) || c == '\n')
				out.add(character);

			escaped = false;
		}

		return out;
	}

	/*
		Splits the parsed text into words. A word is
		ajkshd
		<smiley>
		space
		\n
	 */
	private List<List<String>> toWords(List<String> symbols) {
		List<List<String>> out = new ArrayList<>();
		List<String> currentWord = new ArrayList<>();

		for(String symbol: symbols) {
			if(symbol.equals("\n")|| symbol.equals(" ") || symbol.length() > 1) {
				if(currentWord.size() > 0) out.add(currentWord);
				out.add(new ArrayList<>(List.of(symbol)));
				currentWord = new ArrayList<>();
			} else {
				currentWord.add(symbol);
			}
		}
		if(currentWord.size() > 0) out.add(currentWord);

		return out;
	}

	private float calculateWidth(Font font, float fontSize, List<String> parsed) {
		float out = 0.0f;
		for(String s: parsed) out += font.getAdvance(s, fontSize);
		return out;
	}

	private float calculateFragmentWidth(Font font, float fontSize, List<TextFragment> parsed) {
		float out = 0.0f;
		for(TextFragment s: parsed) out += font.getAdvance(s.texture(), fontSize);
		return out;
	}

	/*
		Cuts a word
		that is too long for the current line

		thisistheexampleline

		into smaller lines

		thisis-
		theexa-
		mpleli-
		ne
	 */
	private List<List<String>> cutWord(Font font, float fontSize, List<String> word) {
		float maxLength = maxWidth;
		if(font.hasCharacter("-")) maxLength -= font.getWidth("-", fontSize);

		List<List<String>> out = new ArrayList<>();
		List<String> currentLine = new ArrayList<>();
		float currentLength = 0;

		for(String character: word) {
			if(currentLength + font.getAdvance(character, fontSize) >= maxLength) {
				currentLine.add("-");
				out.add(currentLine);
				out.add(new ArrayList<>(List.of("\n")));
				currentLength = 0;
				currentLine = new ArrayList<>();
			}

			currentLine.add(character);
			currentLength += font.getAdvance(character, fontSize);
		}
		if(currentLine.size() > 0) out.add(currentLine);

		return out;
	}


	@Override
	public void cleanUp() {
		glDeleteBuffers(instanceVBO);

		glDisableVertexArrayAttrib(getVAO(), 3);
		glDisableVertexArrayAttrib(getVAO(), 4);
		glDisableVertexArrayAttrib(getVAO(), 5);
		glDisableVertexArrayAttrib(getVAO(), 6);

		super.cleanUp();
	}


	public int charCount() {
		return chars;
	}

	@Override
	public int getVAO() {
		return rectVAO;
	}

	@Override
	public int getFaceCount() {
		return 2;
	}

	public float getWidth() {
		return width / 2;
	}

	public float getHeight() {
		return height / 2;
	}


	private void addData(List<Float> floats, float... toAdd) {
		for(float f: toAdd) floats.add(f);
	}

	private void addData(List<Float> floats, Vector3f... toAdd) {
		for(Vector3f f: toAdd) {
			floats.add(f.x);
			floats.add(f.y);
			floats.add(f.z);
		}
	}

	private void addData(List<Float> floats, Vector4f... toAdd) {
		for(Vector4f f: toAdd) {
			floats.add(f.x);
			floats.add(f.y);
			floats.add(f.z);
			floats.add(f.w);
		}
	}
}
