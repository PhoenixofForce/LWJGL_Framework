package meshes;

import org.joml.Vector3f;
import org.joml.Vector4f;
import rendering.Renderable;
import utils.Constants;
import window.Window;
import window.font.Font;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL45.glDisableVertexArrayAttrib;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;

public class TextModel extends Renderable {

	private int rectVAO, rectVBO, rectUVVBO;
	private int instanceVBO;

	private int chars = 0;
	private float width;
	private float height;

	private static final int floats = 4 + 4 + 3 + 1 + 1;	//4 for position, 4 for atlas bounds, 3 for color, 1 for the char index, 1 for wobble strength

	public TextModel() {
		super();
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

	public void updateInstance(Font font, float fontSize, float width, List<String> text, List<Vector3f> colors, List<Float> wobbleStrengths) {
		List<Float> floatsData = new ArrayList<>();

		float fontWidth = fontSize;
		float fontHeight = Constants.FONT_ASPECT * fontWidth;

		float x = 0;
		float y = 0;

		chars = 0;
		int indexes = 0;
		for(int i = 0; i < text.size(); i++) {
			String s = text.get(i);
			Vector3f color = colors.get(i);
			float wobbleStrength = wobbleStrengths.get(i);

			for(char c: s.toUpperCase().toCharArray()) {
				//TODO: use offsets

				if(font.hasCharacter(c)) {
					floatsData.add(x);
					floatsData.add(y);
					floatsData.add(fontWidth);
					floatsData.add(fontHeight);

					Vector4f uvBounds = font.getBounds(c);
					floatsData.add(uvBounds.x);
					floatsData.add(uvBounds.y);
					floatsData.add(uvBounds.z);
					floatsData.add(uvBounds.w);

					floatsData.add(color.x);
					floatsData.add(color.y);
					floatsData.add(color.z);

					floatsData.add((float) indexes);
					floatsData.add(wobbleStrength);

					indexes++;
				}

				if(c == '\n') {
					width = Math.max(0, x - Constants.FONT_SPACING * fontWidth);
					x = 0;
					y -= fontHeight * (2 + Constants.FONT_SPACING);
				} else {
					x += fontWidth * (2 + Constants.FONT_SPACING);
				}

				chars++;
			}
		}
		this.width = Math.max(width, x - Constants.FONT_SPACING * fontWidth);
		this.height = -y + fontHeight * 2;

		float[] data = new float[floatsData.size()];
		for(int i = 0; i < floatsData.size(); i++) {
			data[i] = floatsData.get(i);
		}


		glBindBuffer(GL_ARRAY_BUFFER, instanceVBO);
		glBufferData(GL_ARRAY_BUFFER, chars * floats * 4L, GL_STREAM_DRAW);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
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
}
