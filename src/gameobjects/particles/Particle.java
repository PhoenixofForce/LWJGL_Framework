package gameobjects.particles;

import org.joml.Vector3f;

import java.awt.*;
import java.util.Random;

class Particle {

	private float size;
	private final float sizeChange;

	private float speed;
	private final float speedChange;

	private Vector3f position;
	private final Vector3f movementDirection;
	private final Vector3f directionChange;

	private float  lifetime;
	private final Vector3f color;

	Particle(Vector3f position, Vector3f movementDirection, Vector3f directionChange, float speed, float speedChange, float size, float sizeChange, float lifetime) {
		this.position = position;
		this.movementDirection = movementDirection;
		this.directionChange = directionChange;
		this.speed = speed;
		this.speedChange = speedChange;
		this.size = size;
		this.sizeChange = sizeChange;
		this.lifetime = lifetime;

		Color c = new Color(new Random().nextInt());
		color = new Vector3f(c.getRed() / 255f, Math.min(c.getGreen(), c.getRed()) / 255f / 4, c.getBlue() / 255f / 8);
	}

	public void update(long dt) {
		size += sizeChange;
		lifetime -= dt;
		if(speed > 0 && movementDirection.length() > 0) position.add(new Vector3f(movementDirection).normalize(speed));
		speed += speedChange;
		if(directionChange.length() > 0) movementDirection.add(directionChange);
	}

	public boolean isDead() {
		return (lifetime <= 0 || size <= 0);
	}

	public float getSize() {
		return size;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getColor() {
		return color;
	}
}
