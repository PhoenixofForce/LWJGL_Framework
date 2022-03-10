package rendering;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import utils.VecUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;

public abstract class Renderable {

	private int faceCount;
	private int vao;
	protected List<Integer> buffers;

	public Renderable() {
		this.buffers = new ArrayList<>();
	}

	public int getVAO() {
		return vao;
	}

	public int getFaceCount() {
		return faceCount;
	}

	public void initVAO(List<Vector3f> vertices, List<Vector2f> textureCoordinates, List<int[]> faces) {
		initVAO(VecUtils.vec3Array(vertices), VecUtils.vec2Array(textureCoordinates), faces);
	}

	public void initVAO(List<Vector3f> vertices, List<Vector2f> textureCoordinates, List<Vector3f> vertexNormals, List<int[]> faces) {
		initVAO(VecUtils.vec3Array(vertices), VecUtils.vec2Array(textureCoordinates), VecUtils.vec3Array(vertexNormals), faces);
	}

	public void initVAO(float[] vertex_data, float[] texture_data, List<int[]> faces) {
		this.faceCount = faces.size();

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		createVBO(vao, "cPosition", vertex_data, 3);

		createVBO(vao, "cTexCoord", texture_data, 2);

		int[] face_indices = new int[faces.size() * 3];
		for(int i = 0; i < faces.size(); i++) {
			face_indices[3 * i + 0] = faces.get(i)[0];
			face_indices[3 * i + 1] = faces.get(i)[1];
			face_indices[3 * i + 2] = faces.get(i)[2];
		}

		int idxVboId = createIndexVBO(face_indices);
		buffers.add(idxVboId);

		glBindVertexArray(0);
	}

	public void initVAO(float[] vertex_data, float[] texture_data, float[] normal_data, List<int[]> faces) {
		this.buffers = new ArrayList<>();
		this.faceCount = faces.size();

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		createVBO(vao, "cPosition", vertex_data, 3);

		createVBO(vao, "cTexCoord", texture_data, 2);

		createVBO(vao, "vNormal", normal_data, 3);

		int[] face_indices = new int[faces.size() * 3];
		for(int i = 0; i < faces.size(); i++) {
			face_indices[3 * i + 0] = faces.get(i)[0];
			face_indices[3 * i + 1] = faces.get(i)[1];
			face_indices[3 * i + 2] = faces.get(i)[2];
		}

		int idxVboId = createIndexVBO(face_indices);
		buffers.add(idxVboId);

		glBindVertexArray(0);
	}

	protected void createVBO(int vao, String name, float[] data, int size) {
		int location = buffers.size();
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

		glEnableVertexArrayAttrib(vao, location);
		glVertexAttribPointer(location, size, GL_FLOAT, false, 0, 0);

		buffers.add(vbo);
	}

	private int createIndexVBO(int[] data) {
		int vbo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		buffers.add(vbo);

		return vbo;
	}

	public void cleanUp() {
		for(int i = 0; i < buffers.size(); i++) { GL46.glDisableVertexAttribArray(i); }
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		buffers.forEach(GL46::glDeleteBuffers);

		glBindVertexArray(0);
		glDeleteVertexArrays(vao);
	}
}
