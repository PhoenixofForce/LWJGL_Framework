package utils;

public class TimeUtils {

	public static long getTime() {
		return System.currentTimeMillis();
	}

	public static void sleep(int time) {
		try {
			Thread.sleep(Math.max(0, time));
		} catch (Exception e) {
		}
	}

	public static double sToTick(double seconds) {
		return seconds * Constants.TPS;
	}
}


