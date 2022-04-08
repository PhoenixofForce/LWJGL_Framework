package rendering;

import rendering.uniform.Uniform;

import static org.lwjgl.opengl.GL46.*;

public class Renderer {

	public static void render(ShaderHandler.ShaderType shaderType, Renderable renderable, Uniform uniform) {
		if(renderable == null) return;

		glBindVertexArray(renderable.getVAO());
		glUseProgram(shaderType.get());

		uniform.load();
		uniform.clear();

		glDrawElements(GL_TRIANGLES, 3 * renderable.getFaceCount(), GL_UNSIGNED_INT, 0);

		glBindVertexArray(0);
		glUseProgram(0);
	}

	public static void renderInstanced(ShaderHandler.ShaderType shaderType, Renderable renderable, Uniform uniform, int count) {
		if(renderable == null) return;

		glBindVertexArray(renderable.getVAO());
		glUseProgram(shaderType.get());

		uniform.load();
		uniform.clear();

		glDrawElementsInstanced(GL_TRIANGLES, 3 * renderable.getFaceCount(), GL_UNSIGNED_INT, 0, count);

		glBindVertexArray(0);
		glUseProgram(0);
	}

	public static void renderArrays(ShaderHandler.ShaderType shaderType, Renderable renderable, Uniform uniform) {
		if(renderable == null) return;

		glBindVertexArray(renderable.getVAO());
		glUseProgram(shaderType.get());

		uniform.load();
		uniform.clear();

		glDrawArrays(GL_TRIANGLES, 0, 3 * renderable.getFaceCount());

		glBindVertexArray(0);
		glUseProgram(0);
	}

	public static void renderArraysInstanced(ShaderHandler.ShaderType shaderType, Renderable renderable, Uniform uniform, int count) {
		if(renderable == null) return;

		glBindVertexArray(renderable.getVAO());
		glUseProgram(shaderType.get());

		uniform.load();
		uniform.clear();

		glDrawArraysInstanced(GL_TRIANGLES, 0, 3 * renderable.getFaceCount(), count);

		glBindVertexArray(0);
		glUseProgram(0);
	}


}
