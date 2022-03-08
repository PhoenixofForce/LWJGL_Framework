package gameobjects.hitboxes;

import gameobjects.hitboxes.HitboxData.*;

public class AABBHitbox3D {

	public float x, y, z, width, height, depth;
	public HitboxType type;

	public AABBHitbox3D(float x, float y, float z, float width, float height, float depth) {
		this(x, y, z, width, height, depth, HitboxType.BLOCKING);
	}

	public AABBHitbox3D(float x, float y, float z, float width, float height, float  depth, HitboxType type) {
		this.x = x;
		this.y = y;
		this.z = z;

		this.width = width;
		this.height = height;
		this.depth = depth;

		this.type = type;
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
	}

	/*
	 * 	Return true if two rectangles(2d) or  cuboids(3d) overlap
	 *    +--------+
	 *    |        |
	 *    |     +--------+
	 *    |     |  |     |
	 *    +-----|--+     |
	 *          +--------+
	 */
	public boolean collides(AABBHitbox3D box) {
		return ((x + width) > box.x && (box.x + box.width) > x &&
				(y + height) > box.y && (box.y + box.height) > y &&
				(z + depth) > box.z && (box.z + box.depth) > z);

	}

	/*
	 * 	Return the direction of a gives hitbox in relation to this box
	 *	LUR
	 *  L#R    - # this box
	 *  LDR
	 */
	public HitboxDirection direction(AABBHitbox3D box) {
		if (collides(box)) return HitboxDirection.COLLIDE;

		if ((x + width) <= box.x) return HitboxDirection.RIGHT;
		if ((box.x + box.width) <= x) return HitboxDirection.LEFT;
		if ((y + height) <= box.y) return HitboxDirection.UP;
		if ((box.y + box.height) <= y) return HitboxDirection.DOWN;
		if ((z + depth) <= box.z) return HitboxDirection.FRONT;
		return HitboxDirection.BACK;

	}

	/*
	 * 	the amount of the given direction the second object has to be moved to avoid collision
	 *    +--------+		         +--------+
	 *    |        |                 |        |
	 *    |     +--------+           |        |+--------+
	 *    |     |  |     |           |        ||        |
	 *    +-----|--+     |           +--------+|        |
	 *          +--------+                     +--------+
	 *          >>>>(4)       =>
	 */
	public float collisionDepth(AABBHitbox3D box, float ax, float ay, float az) {
		if (!collides(box)) return 0;

		float distance = Float.MAX_VALUE;

		if (ax != 0) {
			if (ax < 0) {
				distance = ((x + width) - box.x) / (-ax);
			} else {
				distance = ((box.x + box.width) - x) / ax;
			}
		}

		if (ay != 0) {
			if (ay < 0) {
				distance = Math.min(((y + height) - box.y) / (-ay), distance);
			} else {
				distance = Math.min(((box.y + box.height) - y) / ay, distance);
			}
		}

		if (az != 0) {
			if (az < 0) {
				distance = Math.min(((z + depth) - box.z) / (-az), distance);
			} else {
				distance = Math.min(((box.z + box.depth) - z) / az, distance);
			}
		}

		return distance;
	}
}
