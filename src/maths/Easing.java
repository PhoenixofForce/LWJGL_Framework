package maths;

public interface Easing {

	/**
	 * f: [0, 1] -> [0, 1]
	 *
	 * @param x
	 * @return
	 */
	double ease(double x);

	static double ease(double derivateAt0, double derivateAt1, double x) {
		double arg3 = derivateAt1 + derivateAt0 -2;
		double arg2 = -derivateAt1 - 2 * derivateAt0 + 3;
		double arg1 = derivateAt0;

		return arg1 * x + arg2 * x * x + arg3 * x * x * x;
	}

	/*      /
	 *     /
	 *    /
	 *   /
	 */
	static double linear(double x) {
		return ease(1, 1, x);
	}

	static double invLinear(double x) {
		return 1.0 - linear(x);
	}

	/*         /
	 *        /
	 *      _/
	 *   __/
	 */
	static double easeIn(double x) {
		return ease(0, 2, x);
	}

	static double invEaseIn(double x) {
		return 1.0 - easeIn(x);
	}

	/*       __
	 *     _/
	 *    /
	 *   /
	 */
	static double easeOut(double x) {
		return ease(2, 0, x);
	}

	static double invEaseOut(double x) {
		return 1.0 - easeOut(x);
	}

	/*          __
	 *        _/
	 *      _/
	 *   __/
	 */
	static double easeInEaseOut(double x) {
		return ease(0, 0, x);
	}

	static double invEaseInEaseOut(double x) {
		return 1.0 - easeInEaseOut(x);
	}

	/*      /\  /
	 *     /  \/
	 *    /
	 *   /
	 */
	static double easeOutBounce(double x) {
		double n1 = 7.5625;
		double d1 = 2.75;

		if (x < 1.0 / d1) {
			return n1 * x * x;
		} else if (x < 2.0 / d1) {
			return n1 * (x -= 1.5 / d1) * x + 0.75;
		} else if (x < 2.5 / d1) {
			return n1 * (x -= 2.25 / d1) * x + 0.9375;
		} else {
			return n1 * (x -= 2.625 / d1) * x + 0.984375;
		}
	}

	/*          /
	 *         /
	 *    /\  /
	 *   /  \/
	 */
	static double easeInBounce(double x) {
		return 1 - easeOutBounce(1 - x);
	}

	/*          /\  /
	 *         /  \/
	 *    /\  /
	 *   /  \/
	 */
	static double easeInEaseOutBounce(double x) {
		return x < 0.5
				? (1.0 - easeOutBounce(1.0 - 2.0 * x)) / 2.0
				: (1.0 + easeOutBounce(2.0 * x - 1.0)) / 2.0;
	}
}