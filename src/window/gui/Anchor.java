package window.gui;

public enum Anchor {

	/*
	    E +---------+
	      |         |
	    C |         |
	      |         |
	    B +---------+
	      B    C    E
	 */
	BEGIN(1), CENTER(0), END(-1);

	final float multiplier;
	Anchor(float multiplier) {
		this.multiplier = multiplier;
	}

	public float calculateOffset(float length) {
		return multiplier * length / 2f;
	}

	public static final Anchor[]	BOTTOM_LEFT = {BEGIN, BEGIN}, BOTTOM_CENTER = {CENTER, BEGIN}, BOTTOM_RIGHT = {END, BEGIN},
									CENTER_LEFT = {BEGIN, CENTER}, CENTERCENTER = {CENTER, CENTER}, CENTER_RIGHT = {END, CENTER},
									TOP_LEFT = {BEGIN, END}, TOP_CENTER = {CENTER, END}, TOP_RIGHT = {END, END};

}
