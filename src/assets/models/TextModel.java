package assets.models;

import org.joml.Vector3f;
import org.joml.Vector4f;
import rendering.Renderable;
import utils.Constants;
import window.font.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	public void updateInstance(Font font, float fontSize, float width, List<String> textFragments, List<Vector3f> colorFragments, List<Float> wobbleStrengthFragments) {
		List<List<TextFragment>> lines = preprocess(font, fontSize, textFragments, colorFragments, wobbleStrengthFragments);
		this.width = Math.max(this.width, width);
		buildModel(font, fontSize, lines);
	}

	private List<List<TextFragment>> preprocess(Font font, float fontSize, List<String> textFragments, List<Vector3f> colorFragments, List<Float> wobbleStrengthFragments) {
		List<List<TextFragment>> lines = new ArrayList<>();

		int currentLineLength = 0;
		List<TextFragment> currentLine = new ArrayList<>();

		String specialText = "";
		boolean escaped = false;
		boolean inSpecialText = false;

		for(int i = 0; i < textFragments.size(); i++) {
			String text = textFragments.get(i);
			Vector3f color = colorFragments.get(i);
			float wobble = wobbleStrengthFragments.get(i);

			String[] words = (text + "a").split(" "); 	//additional a to keep spaces at the end
			for(int wordIndex = 0; wordIndex < words.length; wordIndex++) {
				String word = words[wordIndex];
				if (wordIndex == words.length - 1) word = word.substring(0, word.length() - 1);    //cut of the a again

				float wordLength = calculateWidth(font, fontSize, word);
				System.out.println(word + " " + currentLineLength + " " + wordLength + " " + (currentLineLength + wordLength) + " " + maxWidth);

				if(currentLineLength + wordLength > maxWidth && !word.startsWith("\n")) {	//to long for current line
					if(currentLineLength > 0) {
						this.width = Math.max(currentLineLength, this.width);
						lines.add(currentLine);
						currentLine = new ArrayList<>();
						currentLineLength = 0;
					}

					if(wordLength > maxWidth) {	//cut line
						word = cutWord(font , fontSize, word);
					}
				}

				for(char c: word.toCharArray()) {
					String character = c + "";

					if(c == '<' && !escaped) {
						inSpecialText = true;
						specialText = "";
						continue;
					} else if(c == '>' && !escaped) {
						inSpecialText = false;
						if(!font.hasCharacter(specialText)) continue;

						currentLineLength += font.getAdvance(specialText, fontSize) * 2;
						currentLine.add(new TextFragment(specialText, color, wobble));

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

					if(c == '\n') {
						this.width = Math.max(currentLineLength, this.width);
						lines.add(currentLine);
						currentLine = new ArrayList<>();
						currentLineLength = 0;
					}

					currentLineLength += font.getAdvance(character, fontSize);
					currentLine.add(new TextFragment(character, color, wobble));

					escaped = false;
				}

				currentLine.add(new TextFragment(" ", new Vector3f(1), 0));
				currentLineLength += font.getAdvance(" ", fontSize);
			}
		}

		lines.add(currentLine);
		this.width = Math.max(currentLineLength, this.width);
		this.height = (2 + Constants.FONT_SPACING) * lines.size() * fontSize;

		return lines;
	}

	private void buildModel(Font font, float fontSize, List<List<TextFragment>> lines) {
		List<Float> floatsData = new ArrayList<>();
		float x = 0;
		float y = 0;

		chars = 0;
		int indexes = 0;

		for(List<TextFragment> line: lines) {
			x = 0;	//TODO: calculate alignment position here
					// left = 0
					// center = this.width / 2 - lineWidth / 2
				    // right = this.width - lineWidth
					// Left(0), center(1/2), Right(1)
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
	}

	private String cutWord(Font font, float fontSize, String word) {
		return word;
	}

	/*
			We want the pure width. With no regards to new lines
	 */
	private float calculateWidth(Font font, float fontSize, String text) {
		float out = 0;

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

				if(font.hasCharacter(specialText))
					out += font.getAdvance(specialText, fontSize);

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

			if(font.hasCharacter(character))
				out += font.getAdvance(character, fontSize);

			escaped = false;
		}

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
