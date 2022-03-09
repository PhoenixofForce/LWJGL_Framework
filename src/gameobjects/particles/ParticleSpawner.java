package gameobjects.particles;

import maths.MathUtils;
import meshes.ObjHandler;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import rendering.Renderer;
import rendering.ShaderHandler;
import rendering.Uniform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleSpawner {

	private static final int MAX_PARTICLES = 10;
	private static final Map<ParticleType, ParticleSpawner> spawner = new HashMap<>();

	public static final ParticleType DEFAULT = new ParticleType();

	public static ParticleSpawner getSpawner(Vector3f pos, ParticleType type) {
		if(spawner.containsKey(type)) return spawner.get(type);
		else {
			ParticleSpawner out = new ParticleSpawner(type, pos);
			spawner.put(type, out);

			return out;
		}
	}

	public static void updateAll(long dt) {
		for(ParticleSpawner p: spawner.values()) p.update(dt);
	}

	public static void renderAll(Matrix4f proj, Matrix4f view) {
		spawner.values().forEach(p -> p.render(proj, view));
	}


	//---------------------------------------

	private Uniform uniform;
	private ParticleType type;

	private boolean isSpawning = true;
	private int particlesPerUpdate = 1;

	private List<Particle> particles;
	private Vector3f position;

	private ParticleSpawner(ParticleType type, Vector3f position) {
		this.type = type;
		this.particles = new ArrayList<>();
		this.position = position;

		uniform = new Uniform();
	}

	private void update(long dt) {
		particles.removeAll(particles.stream().filter(Particle::isDead).toList());
		particles.forEach(p -> p.update(dt));

		if(isSpawning) {
			burst(position, particlesPerUpdate);
		}
	}

	private void render(Matrix4f proj, Matrix4f view) {
		int particleCount = Math.min(MAX_PARTICLES, particles.size());
		Vector3f[] colorsAndPositions = new Vector3f[2 * MAX_PARTICLES];
		float[] sizes = new float[particleCount];

		for(int i = 0; i < particleCount; i++) {
			sizes[i] = particles.get(i).getSize();
			colorsAndPositions[i] = particles.get(i).getColor();
			colorsAndPositions[i + MAX_PARTICLES] = particles.get(i).getPosition();
		}

		uniform.setMatrices(proj, view);
		uniform.setVector3fs(colorsAndPositions);
		uniform.setFloats(sizes);

		Renderer.renderInstanced(ShaderHandler.ShaderType.PARTICLE, ObjHandler.getModel("cube"), uniform, particleCount);
	}

	private void burst(Vector3f position, int amount) {
		if(particles.size() >= MAX_PARTICLES) return;

		for(int i = 0; i < amount; i++) {
			particles.add(type.generate(position));
		}
	}

	public void startSpawning() {
		isSpawning = true;
	}

	public void stopSpawning() {
		isSpawning = false;
	}

	public static class ParticleType {
		//TODO: make changes a function
		//TODO: color

		private float minSize = 2.1f, maxSize = 10f;
		private float sizeChange = 1;

		private float minSpeed = 0, maxSpeed = 0;
		private float speedChange = 0;

		private Vector3f movementDirection = new Vector3f(0, 1, 0);
		private float angleOffset = (float) (Math.PI / 4f);
		private Vector3f directionChange = new Vector3f();

		private Vector3f positionOffset = new Vector3f(0);
		private float minLifetime = 300, maxLifetime = 300;

		Particle generate(Vector3f position) {
			float size = MathUtils.random(minSize, maxSize);
			float speed = MathUtils.random(minSpeed, maxSpeed);
			Vector3f thisMovementDirection = MathUtils.randomVectorAround(movementDirection, angleOffset);
			float lifetime = MathUtils.random(minLifetime, maxLifetime);

			Vector3f thisPosition = new Vector3f(position).add(
					MathUtils.random(-positionOffset.x, positionOffset.x),
					MathUtils.random(-positionOffset.y, positionOffset.y),
					MathUtils.random(-positionOffset.z, positionOffset.z)
			);

			return new Particle(thisPosition, thisMovementDirection, directionChange, speed, speedChange, size, sizeChange, lifetime);
		}

		public ParticleType setSize(float min, float max, float change) {
			this.minSize = min;
			this.maxSize = max;
			this.sizeChange = change;
			return this;
		}

		public ParticleType setSpeed(float min, float max, float change) {
			this.minSpeed = min;
			this.maxSpeed = max;
			this.speedChange = change;
			return this;
		}

		public ParticleType setDirection(Vector3f dir, float angle, Vector3f change) {
			this.movementDirection = dir;
			this.angleOffset = angle;
			this.directionChange = change;
			return this;
		}

		public ParticleType setLifetime(float min, float max) {
			this.minLifetime = min;
			this.maxLifetime = max;
			return this;
		}

		public ParticleType setPositionOffset(Vector3f off) {
			this.positionOffset = off;
			return this;
		}
	}
}