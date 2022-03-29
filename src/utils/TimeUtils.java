package utils;

public class TimeUtils {

	public static long getTime() {
		return System.currentTimeMillis();
	}

	public static long getNanoTime() {
		return System.nanoTime();
	}

	public static void sleep(int time) {
		if(time <= 0) return;

		try {
			Thread.sleep(time);
		} catch (Exception ignored) {
		}
	}
}


