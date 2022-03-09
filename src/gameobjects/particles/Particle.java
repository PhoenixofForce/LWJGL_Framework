package gameobjects.particles;

import org.joml.Vector3f;

import java.awt.*;
import java.util.Random;

class Particle {

	private float size;
	private float sizeChange;

	private float speed;
	private float speedChange;

	private Vector3f position;
	private Vector3f movementDirection;
	private Vector3f directionChange;

	private float  lifetime;

	private Vector3f color;

	Particle(Vector3f position, Vector3f movementDirection, Vector3f directionChange, float speed, float speedChange, float size, float sizeChange, float lifetime) {
		this.position = position;
		this.movementDirection = movementDirection;
		this.directionChange = directionChange;
		this.speed = speed;
		this.speed = speed;
		this.size = size;
		this.sizeChange = sizeChange;
		this.lifetime = lifetime;

		Color c = new Color(new Random().nextInt());
		color = new Vector3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
	}

	public void update(long dt) {
		size += sizeChange;
		lifetime -= dt;
		position.add(movementDirection.normalize().mul(speed));
		speed += speedChange;
		movementDirection.add(directionChange).normalize();
	}

	public boolean isDead() {
		return lifetime <= 0;
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
