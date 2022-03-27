package window.inputs;

import utils.TimeUtils;

public class KeyHit {

	private int keyCode;

	private long clickStart;
	private long clickEnd;

	private boolean finishedClick;

	public float value;	//for trigger and joystick

	public KeyHit(int keyCode) {
		this(keyCode, 1);
	}

	public KeyHit(int keyCode, float val) {
		this.keyCode = keyCode;
		this.clickStart = TimeUtils.getTime();
		this.clickEnd = clickStart;
		this.finishedClick = false;
		this.value = val;
	}

	public void reset() {
		this.clickStart = TimeUtils.getTime();
		this.clickEnd = finishedClick? -1: TimeUtils.getTime();
	}

	public void setEnd() {
		this.clickEnd = TimeUtils.getTime();
		this.finishedClick = true;
		this.value = 0;
	}

	public long getClickDuration() {
		return getEnd() - clickStart;
	}

	public boolean isClickInProgress() {
		return !finishedClick;
	}

	public long timeSinceEnd() {
		return TimeUtils.getTime() - getEnd();
	}

	private long getEnd() {
		return (finishedClick? clickEnd: TimeUtils.getTime());
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Click " + ((char) keyCode) + "(" + keyCode + ") (" + clickStart + " - " + clickEnd + ") " + (finishedClick? "finished": "in progress");
	}

}
