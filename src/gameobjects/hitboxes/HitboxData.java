package gameobjects.hitboxes;

public class HitboxData {

	enum HitboxDirection {
		LEFT(-1, 0, 0), UP(0, 1, 0),
		RIGHT(1, 0, 0), DOWN(0, -1, 0),
		FRONT(0, 0, 1), BACK(0, 0, -1),
		COLLIDE(0, 0, 0);

		private final float ax, ay, az;
		HitboxDirection(float ax, float ay, float az) {
			this.ax = ax;
			this.ay = ay;
			this.az = az;
		}

		public HitboxDirection invert() {
			for (HitboxDirection direction: HitboxDirection.values()) {
				if (direction.ax == -ax && direction.ay == -ay && direction.az == -az) return direction;
			}
			return COLLIDE;
		}

		public float getXDirection() {
			if (ax == 0 && ay == 0 && az == 0) return ((float) Math.random()) * 2 - 1;
			return ax;
		}

		public float getYDirection() {
			if (ax == 0 && ay == 0 && az == 0) return ((float) Math.random()) * 2 - 1;
			return ay;
		}

		public float getZDirection() {
			if (ax == 0 && ay == 0 && az == 0) return ((float) Math.random()) * 2 - 1;
			return az;
		}
	}

	enum HitboxType {
		BLOCKING, HALF_BLOCKING, NOT_BLOCKING
	}
}