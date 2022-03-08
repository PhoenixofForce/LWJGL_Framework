package meshes;

import rendering.Renderable;

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
	private final float[] rectangleVerts = new float[]{
			-1.0f, -1.0f,
			1.0f, -1.0f,
			1.0f,  1.0f,

			1.0f,  1.0f,
			-1.0f,  1.0f,
			-1.0f, -1.0f,
	};
	private final float[] rectangleUV = new float[] {
			0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,1.0f, 1.0f,0.0f, 1.0f, 0.0f, 0.0f
	};

	private ScreenRect() {
		INSTANCE = this;

		rectVAO = glGenVertexArrays();
		glBindVertexArray(rectVAO);

		rectVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, rectVBO);
		glBufferData(GL_ARRAY_BUFFER, rectangleVerts, GL_STATIC_DRAW);

		glEnableVertexArrayAttrib(rectVAO, 0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

		rectUVVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, rectUVVBO);
		glBufferData(GL_ARRAY_BUFFER, rectangleUV, GL_STATIC_DRAW);

		glEnableVertexArrayAttrib(rectVAO, 1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
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