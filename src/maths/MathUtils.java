package maths;

import org.joml.Vector3f;

public class MathUtils {

	public static double map(double a, double f1, double t1, double f2, double t2) {
		return ((a-f1)/(t1-f1))*(t2-f2)+f2;
	}

	public static float map(float a, float f1, float t1, float f2, float t2) {
		return ((a-f1)/(t1-f1))*(t2-f2)+f2;
	}

	public static double clamp(double l, double val, double r) {
		if(val < l) return l;
		if(val > r) return r;
		return val;
	}

	/*
		Random value in [lower, upper)
	 */
	public static float random(float lower, float upper) {
		float range = Math.abs(upper - lower);
		return (float) (lower + range * Math.random());
	}

	public static Vector3f randomVectorAround(Vector3f dir, float angle) {
		Vector3f direction = new Vector3f(dir);
		direction.normalize();

		float z = random((float) Math.cos(angle / 2), 1);
		float phi = random(0, (float) (2 * Math.PI));

		Vector3f firstVector = new Vector3f((float) (Math.sqrt(1 - z * z) * Math.cos(phi)), (float) (Math.sqrt(1 - z * z) * Math.sin(phi)), z);
		if(direction.equals(new Vector3f(0, 0, 1))) return firstVector;
		if(direction.equals(new Vector3f(new Vector3f(0, 0, 1)))) return firstVector.mul(-1);

		Vector3f ax1 = new Vector3f(0, 0, 1).cross(direction);
		Vector3f ax2 = new Vector3f(ax1).cross(direction);

		Vector3f res = ax1.mul(firstVector.x).add(ax2.mul(firstVector.y)).add(new Vector3f(direction).mul(firstVector.z));
		return res;
	}

	public static Vector3f vecFromColor(int r, int g, int b) {
		return new Vector3f(r / 255.0f, g / 255.0f, b / 255.0f);
	}
}
