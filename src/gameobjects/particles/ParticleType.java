package gameobjects.particles;

import utils.MathUtils;
import org.joml.Vector3f;

public class ParticleType {
	//TODO: color
	//TODO: death emitter

	private float minSize = 0.1f, maxSize = 0.4f;
	private ParticleChanger<Float> sizeChanger = (start, current, timeLeft, lifetime, dts) -> current - 0.03f * dts;

	private float minSpeed = 3f, maxSpeed = 8f;
	private ParticleChanger<Float> speedChanger = (start, current, timeLeft, lifetime, dts) -> current + 0.01f * dts;

	private Vector3f movementDirection = new Vector3f(0, 1, 0);
	private float angleOffset = 20f * (float) (Math.PI * 2) / 360f;
	private ParticleChanger<Vector3f> directionChanger = (start, current, timeLeft, lifetime, dts) -> start;

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

		return new Particle(thisPosition, thisMovementDirection, directionChanger, speed, speedChanger, size, sizeChanger, lifetime);
	}

	public ParticleType setSize(float min, float max) {
		return this.setSize(min, max, (start, current, timeLeft, lifetime, dts) -> start);
	}

	public ParticleType setSize(float min, float max, ParticleChanger<Float> changer) {
		this.minSize = min;
		this.maxSize = max;
		this.sizeChanger = changer;
		return this;
	}

	public ParticleType setSpeed(float min, float max) {
		return this.setSpeed(min, max, (start, current, timeLeft, lifetime, dts) -> start);
	}

	public ParticleType setSpeed(float min, float max, ParticleChanger<Float> changer) {
		this.minSpeed = min;
		this.maxSpeed = max;
		this.speedChanger = changer;
		return this;
	}

	public ParticleType setDirection(Vector3f dir, float angle) {
		return this.setDirection(dir, angle, (start, current, timeLeft, lifetime, dts) -> start);
	}

	public ParticleType setDirection(Vector3f dir, float angle, ParticleChanger<Vector3f> changer) {
		this.movementDirection = dir;
		this.angleOffset = angle;
		this.directionChanger = changer;
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