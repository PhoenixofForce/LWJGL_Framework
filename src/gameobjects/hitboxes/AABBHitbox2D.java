package gameobjects.hitboxes;

public class AABBHitbox2D extends AABBHitbox3D {
	public AABBHitbox2D(float x, float y, float width, float height) {
		super(x, y, 0, width, height, 0.01f);
	}

	public void set(float x, float y) {
		super.set(x, y, 0);
	}

	public float collisionDepth(AABBHitbox3D box, float ax, float ay) {
		return super.collisionDepth(box, ax, ay, 0);
	}
}
