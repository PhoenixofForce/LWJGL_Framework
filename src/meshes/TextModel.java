package meshes;

import org.joml.Vector3f;
import org.joml.Vector4f;
import rendering.Renderable;
import utils.Constants;
import window.font.Font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL45.glDisableVertexArrayAttrib;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;

public class TextModel extends Renderable {

	/*
		NOTE: In this class are some * 2 and / 2 when calculating widths ant heights
		im not sure why that is needed, but i thinks its because the vertices used are from [-1, 1] which makes the rectangle 2 long
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
		List<Float> floatsData = new ArrayList<>();

		float fontWidth = fontSize;
		float fontHeight = Constants.FONT_ASPECT * fontWidth;

		float x = 0;
		float y = 0;

		chars = 0;
		int indexes = 0;

		mainLoop:
		for(int fragmentIndex = 0; fragmentIndex < textFragments.size(); fragmentIndex++) {
			if(-y + fontHeight * 2 > maxHeight * 2) break mainLoop; //height restrain

			Vector3f color = colorFragments.get(fragmentIndex);
			float wobbleStrength = wobbleStrengthFragments.get(fragmentIndex);

			String[] words = (textFragments.get(fragmentIndex) + "a").split(" ");	//add a an character at the end so we keep spaces at the end

			for(int wordIndex = 0; wordIndex < words.length; wordIndex++) {
				String word = words[wordIndex];
				if(wordIndex == words.length - 1) word = word.substring(0, word.length() - 1);	//cut of the a again

				float stringLength = calculateWidth(word, fontSize);


				if(x + stringLength > maxWidth * 2 && !word.startsWith("\n")) {	//if word is too long for current line
					//start a new line
					if(x > 0) {
						width = Math.max(0, x - Constants.FONT_SPACING * fontWidth);
						x = 0;
						y -= fontHeight * (2 + Constants.FONT_SPACING);
					}

					if(-y + fontHeight * 2 > maxHeight * 2) break mainLoop;

					if(stringLength > maxWidth * 2) {		//if word is longer than a line
						word = cutLine(word, fontSize);		//cut it so that it fits
					}
				}

				char[] charArray = word.toUpperCase().toCharArray();	//TODO: my font has only uppercase character
				for (char c : charArray) {
					if (font.hasCharacter(c)) {
						addData(floatsData, x, y, fontWidth, fontHeight);
						addData(floatsData, font.getBounds(c));
						addData(floatsData, color);
						addData(floatsData, indexes, wobbleStrength);

						indexes++;
						if(c == '.') indexes += 9; //longer pauses after sentence
						else if(c == ',') indexes += 4; //short pauses after commata
					}

					if (c == '\n') {
						width = Math.max(0, x - Constants.FONT_SPACING * fontWidth);
						x = 0;
						y -= fontHeight * (2 + Constants.FONT_SPACING);

						if(-y + fontHeight * 2 > maxHeight * 2) break mainLoop;
					} else {
						x += fontWidth * (2 + Constants.FONT_SPACING);		//times 2 to start directly after the char, + spacing for spacing
					}

					chars++;
				}

				//We need no space after the last character
				if(wordIndex < words.length - 1) x += fontWidth * (2 + Constants.FONT_SPACING);
			}
		}

		//remove spacing
		this.width = Math.max(width, x - Constants.FONT_SPACING * fontWidth);
		this.height = -y + fontHeight * 2;

		//parse data to opengl
		float[] data = new float[floatsData.size()];
		for(int i = 0; i < floatsData.size(); i++) {
			data[i] = floatsData.get(i);
		}

		glBindBuffer(GL_ARRAY_BUFFER, instanceVBO);
		glBufferData(GL_ARRAY_BUFFER, chars * floats * 4L, GL_STREAM_DRAW);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	/*
		Cuts a string so that it spans multiple lines
		12345 <- maxLineLength
		ThisIsALongLineForDemo

		=>

		12345 <- maxLineLength
		This-
		IsAL-
		ongL-
		ineF-
		orDe-
		mo
	 */
	private String cutLine(String in, float fontSize) {
		//https://stackoverflow.com/questions/3760152/split-string-to-equal-length-substrings-in-java

		int lineLength = charsInLine(maxWidth, fontSize) - 1;
		String[] slices = in.split("(?<=\\G.{" + lineLength + "})");

		return String.join("-\n", slices);
	}


	private float calculateWidth(String text, float size) {
		return text.length() * size * (2 + Constants.FONT_SPACING) - Constants.FONT_SPACING * size;
	}

	private int charsInLine(float width, float size) {
		float out = width + Constants.FONT_SPACING * size;
		out /= (size * (2 + Constants.FONT_SPACING));
		return (int) Math.floor(out * 2);
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
