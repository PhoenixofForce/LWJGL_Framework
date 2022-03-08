package rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import utils.VecUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

public class UniformLoader {

	private static FloatBuffer fb = BufferUtils.createFloatBuffer(16);

	private static final int matrixStart = 0;
	private static final int maxMatrices = 10;
	public static void loadMatrices(Matrix4f... matrices) {
		for(int i = 0; i < Math.min(matrices.length, maxMatrices); i++) {
			glUniformMatrix4fv(matrixStart + i, false, matrices[i].get(fb));
		}
	}

	private static final int textureStart = matrixStart + maxMatrices;
	private static final int maxTextures = 10;
	public static void loadTextures(int... textures) {
		for(int i = 0; i < Math.min(textures.length, maxTextures); i++) {
			glActiveTexture(GL_TEXTURE0 + i);
			glBindTexture(GL_TEXTURE_2D, textures[i]);

			glUniform1i(textureStart + i, i);
		}
	}

	private static final int floatStart = textureStart + maxTextures;
	private static final int maxFloats = 20;
	public static void loadFloats(float... floats) {
		for(int i = 0; i < Math.min(floats.length, maxFloats); i++) {
			glUniform1f(floatStart + i, floats[i]);
		}
	}

	private static final int vec3Start = floatStart + maxFloats;
	private static final int maxVec3s = 20;
	public static void loadVec3s(Vector3f... vecs) {
		for(int i = 0; i < Math.min(vecs.length, maxVec3s); i++) {
			glUniform3f(vec3Start + i, vecs[i].x, vecs[i].y, vecs[i].z);
		}
	}

	static {
		System.out.println("Matrices start with: " + matrixStart);
		System.out.println("Textures start with: " + textureStart);
		System.out.println("Floats start with: " + floatStart);
		System.out.println("Vec3f start with: " + vec3Start);
	}
}
