package maths;

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
		return lower + range * upper;
	}

}
