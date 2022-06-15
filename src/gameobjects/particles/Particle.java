package gameobjects.particles;

import org.joml.Vector3f;

import java.awt.*;
import java.util.Random;

public class Particle {

	private final float startSize;
	private float currentSize;
	private final ParticleChanger<Float> sizeChanger;

	private final float startSpeed;
	private float currentSpeed;
	private final ParticleChanger<Float> speedChanger;

	private Vector3f position;
	private final Vector3f startMovementDirection;
	private Vector3f currentMovementDirection;
	private final ParticleChanger<Vector3f> directionChanger;

	private float lifetime;
	private final float maxLifeTime;
	private final Vector3f color;

	Particle(Vector3f position,
			 Vector3f movementDirection, ParticleChanger<Vector3f> directionChanger,
			 float speed, ParticleChanger<Float> speedChanger,
			 float size, ParticleChanger<Float> sizeChanger,
			 float lifetime) {

		this.position = position;
		this.lifetime = lifetime;
		this.maxLifeTime = lifetime;

		this.startMovementDirection = movementDirection;
		this.currentMovementDirection = movementDirection;
		this.directionChanger = directionChanger;

		this.startSpeed = speed;
		this.currentSpeed = speed;
		this.speedChanger = speedChanger;

		this.startSize = size;
		this.currentSize = size;
		this.sizeChanger = sizeChanger;

		Color c = new Color(new Random().nextInt());
		color = new Vector3f(c.getRed() / 255f, Math.min(c.getGreen() - 20, c.getRed()) / 255f / 2, c.getBlue() / 255f / 4);
	}

	public void update(long dt) {
		float dts = dt / 1000.0f;
		this.currentSize = sizeChanger.calculateAttribute(startSize, currentSize, lifetime, maxLifeTime, dts);
		this.currentSpeed = speedChanger.calculateAttribute(startSpeed, currentSpeed, lifetime, maxLifeTime, dts);
		if(this.currentMovementDirection.length() > 0) this.currentMovementDirection = directionChanger.calculateAttribute(startMovementDirection, currentMovementDirection, lifetime, maxLifeTime, dts);

		lifetime -= dt;
		if(currentSpeed > 0 && currentMovementDirection.length() > 0) position.add(new Vector3f(currentMovementDirection).normalize().mul(this.currentSpeed * dts));
	}

	public boolean isDead() {
		return (lifetime <= 0 || currentSize <= 0);
	}

	public float getSize() {
		return currentSize;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getColor() {
		return color;
	}
}
