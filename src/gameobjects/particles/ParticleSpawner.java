package gameobjects.particles;

import maths.MathUtils;
import meshes.ParticleModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import rendering.Renderer;
import rendering.ShaderHandler;
import rendering.Uniform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleSpawner {

	public static final int MAX_PARTICLES = 1000;
	private static final Map<Integer, ParticleSpawner> spawner = new HashMap<>();

	public static final ParticleType DEFAULT = new ParticleType();

	private static int nextID = 0;
	
	public static int getNewSpawner(Vector3f pos, ParticleType type) {
		int id = nextID;
		nextID++;
		
		ParticleSpawner out = new ParticleSpawner(type, pos);
		spawner.put(id, out);
		
		return id;
	}

	public static void updateAll(long dt) {
		for(ParticleSpawner p: spawner.values()) p.update(dt);
	}

	public static void renderAll(Matrix4f proj, Matrix4f view) {
		spawner.values().forEach(p -> p.render(proj, view));
	}

	public static void startSpawning(int id) {
		ParticleSpawner ps = spawner.get(id);
		
		if(ps != null) {
			ps.startSpawning();
		}
	}
	
	public static void stopSpawning(int id) {
		ParticleSpawner ps = spawner.get(id);
		
		if(ps != null) {
			ps.stopSpawning();
		}
	}
	
	public static void burst(int id, Vector3f pos, int amount) {
		ParticleSpawner ps = spawner.get(id);
		
		if(ps != null) {
			ps.burst(pos, amount);
		}
	}
	
	public static void cleanUp(int id) {
		spawner.put(id, null);
	}
	
	public static void cleanUpAll() {
		spawner.clear();
	}
	
	//---------------------------------------

	private static final ParticleModel model = new ParticleModel();

	private Uniform uniform;
	private ParticleType type;

	private boolean isSpawning = true;
	private int particlesPerUpdate = 10;

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
		GL46.glDepthMask(false);
		GL46.glEnable(GL11.GL_BLEND);
		GL46.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

		model.updateVBO(particles);
		uniform.setMatrices(proj, view);
		Renderer.renderInstanced(ShaderHandler.ShaderType.PARTICLE, model, uniform, Math.min(particles.size(), MAX_PARTICLES));

		GL46.glDepthMask(true);
		GL46.glDisable(GL11.GL_BLEND);
	}

	private void burst(Vector3f position, int amount) {
		if(particles.size() >= MAX_PARTICLES) return;

		for(int i = 0; i < amount; i++) {
			particles.add(type.generate(position));
		}
	}

	public void startSpawning() {
		this.isSpawning = true;
	}

	public void startSpawning(Vector3f pos) {
		isSpawning = true;
		this.position = pos;
	}

	public void stopSpawning() {
		isSpawning = false;
	}

	public static class ParticleType {
		//TODO: make changes a function
		//TODO: color
		//TODO: death emitter

		private float minSize = 0.1f, maxSize = 0.4f;
		private float sizeChange = -0.03f;

		private float minSpeed = 3f, maxSpeed = 8f;
		private float speedChange = 0.01f;

		private Vector3f movementDirection = new Vector3f(0, 1, 0);
		private float angleOffset = 20f * (float) (Math.PI * 2) / 360f;
		private Vector3f directionChange = new Vector3f(0, 0, 0f);

		private Vector3f positionOffset = new Vector3f(1, 0.3f, 1);
		private float minLifetime = 200, maxLifetime = 500;

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