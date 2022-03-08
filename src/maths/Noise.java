package maths;

import java.util.Random;

public class Noise {
	public static double noise(int seed, double x, int STEP_SIZE) {
		int x1 = (int) Math.floor(x / STEP_SIZE);
		int x2 = (int) Math.ceil(x / STEP_SIZE);

		double d = (x % STEP_SIZE) / STEP_SIZE;

		double l = new Random(seed * x1).nextInt(100000) / 100000d;
		double r = new Random(seed * x2).nextInt(100000) / 100000d;

		return lerp(l, r, d);
	}

	public static double noise(int seed, double x, double y, int STEP_SIZE) {
		int x1 = (int) Math.floor(x / STEP_SIZE);
		int x2 = (int) Math.ceil(x / STEP_SIZE);
		int y1 = (int) Math.floor(y / STEP_SIZE);
		int y2 = (int) Math.ceil(y / STEP_SIZE);

		double dx = (x % STEP_SIZE) / STEP_SIZE;
		double dy = (y % STEP_SIZE) / STEP_SIZE;

		double l = nextDouble(seed, x1, y2);
		double r = nextDouble(seed, x2, y2);
		double t = nextDouble(seed, x1, y1);
		double b = nextDouble(seed, x2, y1);

		double w0 = lerp(t, b, dx);
		double w1 = lerp(l, r, dx);
		return lerp(w0, w1, dy);
	}

	private static double nextDouble(int seed, int seed2, int seed3) {
		Random r1 = new Random(seed);
		r1.nextLong();

		Random r2 = new Random(seed2);
		r2.nextLong();
		r2.nextLong();

		Random r3 = new Random(seed3);
		r2.nextLong();
		r2.nextLong();
		r2.nextLong();

		Random r = new Random(r1.nextLong() ^ r2.nextLong() ^ r3.nextLong());
		r.nextDouble();

		return r.nextFloat();
	}

	public static double lerp(double l, double r, double d) {
		double m = (1.0-Math.cos(d*Math.PI))/2.0;
		return l*(1-m)+r*m; 						//Cosine
		//return 2*(r-l)*d*d*d + (l-r)*d*d + l;   	//Cubic - Not working correctly
		//return l + d * (r - l);                	//Linear
	}
}
