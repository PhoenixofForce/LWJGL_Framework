package assets.models;

import gameobjects.particles.Particle;
import gameobjects.particles.ParticleSpawner;
import rendering.Renderable;

import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class ParticleModel extends Renderable {

	private static final float[] vertices = {-0.5f, 0.5f, 0, -0.5f, -0.5f, 0, 0.5f, 0.5f, 0, 0.5f, -0.5f, 0};
	private static final float[] rectangleUV = new float[] {
			1.0f, 0.0f, 0.0f, 0.0f, 1f, 1f, 0.0f, 1.0f
	};

	private static int maxFloats;
	private final float[] data;
	private int instanceVBO = -1;

	public ParticleModel() {
		super();
		maxFloats = (ParticleSpawner.MAX_PARTICLES) * 7;
		data = new float[maxFloats];

		initVAO(vertices, rectangleUV, List.of(new int[]{3,2,1}, new int[]{1,2,0}));
		createInstanceVBO();
	}

	private void createInstanceVBO() {
		if(instanceVBO != -1) glDeleteBuffers(instanceVBO);
		instanceVBO = glGenBuffers();

		glBindVertexArray(getVAO());
		glBindBuffer(GL_ARRAY_BUFFER, instanceVBO);
		glBufferData(GL_ARRAY_BUFFER, 4L * maxFloats, GL_STREAM_DRAW);

		glVertexAttribPointer(3, 3, GL_FLOAT, false, 7 * 4, 0 * 4);
		glVertexAttribPointer(4, 1, GL_FLOAT, false, 7 * 4, 3 * 4);
		glVertexAttribPointer(5, 3, GL_FLOAT, false, 7 * 4, 4 * 4);
		glVertexAttribDivisor(3, 1);
		glVertexAttribDivisor(4, 1);
		glVertexAttribDivisor(5, 1);

		glEnableVertexArrayAttrib(getVAO(), 3);
		glEnableVertexArrayAttrib(getVAO(), 4);
		glEnableVertexArrayAttrib(getVAO(), 5);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	public void updateVBO(List<Particle> particles) {
		int numberOfParticles = Math.min(ParticleSpawner.MAX_PARTICLES, particles.size());

		for(int i = 0; i < numberOfParticles; i++) {
			data[7 * i + 0] = particles.get(i).getPosition().x;
			data[7 * i + 1] = particles.get(i).getPosition().y;
			data[7 * i + 2] = particles.get(i).getPosition().z;

			data[7 * i + 3] = particles.get(i).getSize();

			data[7 * i + 4] = particles.get(i).getColor().x;
			data[7 * i + 5] = particles.get(i).getColor().y;
			data[7 * i + 6] = particles.get(i).getColor().z;
		}

		glBindBuffer(GL_ARRAY_BUFFER, instanceVBO);
		glBufferData(GL_ARRAY_BUFFER, maxFloats * 4L, GL_STREAM_DRAW);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		glDeleteBuffers(instanceVBO);

		glDisableVertexArrayAttrib(getVAO(), 3);
		glDisableVertexArrayAttrib(getVAO(), 4);
		glDisableVertexArrayAttrib(getVAO(), 5);
	}


}
