package gameobjects.particles;

import assets.models.ParticleModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import rendering.Renderer;
import rendering.ShaderHandler;
import rendering.uniform.MassUniform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleSpawner {

	public static final int MAX_PARTICLES = 1000;
	private static final Map<Integer, ParticleSpawner> spawner = new HashMap<>();
	private static int nextID = 0;

	public static final ParticleType DEFAULT = new ParticleType();

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

	public static void freeze(int id) {
		ParticleSpawner ps = spawner.get(id);

		if(ps != null) {
			ps.frozen = true;
		}
	}

	public static void unFreeze(int id) {
		ParticleSpawner ps = spawner.get(id);

		if(ps != null) {
			ps.frozen = false;
		}
	}
	
	public static void cleanUp(int id) {
		spawner.remove(id);
	}
	
	public static void cleanUpAll(boolean deleteModel) {
		spawner.clear();
		nextID = 0;
		if(deleteModel) model.cleanUp();
	}
	
	//---------------------------------------

	private static final ParticleModel model = new ParticleModel();

	private MassUniform uniform;
	private ParticleType type;

	private boolean isSpawning = false;
	private int particlesPerUpdate = 10;

	private List<Particle> particles;
	private Vector3f position;

	private boolean frozen = false;

	private ParticleSpawner(ParticleType type, Vector3f position) {
		this.type = type;
		this.particles = new ArrayList<>();
		this.position = position;

		uniform = new MassUniform();
	}

	private void update(long dt) {
		if(frozen) return;

		particles.removeAll(particles.stream().filter(Particle::isDead).toList());
		particles.forEach(p -> p.update(dt));

		if(isSpawning) {
			burst(position, particlesPerUpdate);
		}
	}

	private void render(Matrix4f proj, Matrix4f view) {
		if(frozen) return;

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
}