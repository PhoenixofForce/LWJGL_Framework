package meshes;

import rendering.Renderable;
import utils.Constants;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;

public class ScreenRect extends Renderable {

	private static ScreenRect INSTANCE;
	public static ScreenRect getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ScreenRect();
		}

		return INSTANCE;
	}

	int rectVAO, rectVBO, rectUVVBO;

	private ScreenRect() {
		super();
		INSTANCE = this;

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

	@Override
	public int getVAO() {
		return rectVAO;
	}

	@Override
	public int getFaceCount() {
		return 2;
	}

}