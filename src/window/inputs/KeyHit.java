package window.inputs;

import utils.TimeUtils;

public class KeyHit {

	private int keyCode;

	private long clickStart;
	private long clickEnd;

	private boolean finishedClick;

	public KeyHit(int keyCode) {
		this.keyCode = keyCode;
		this.clickStart = TimeUtils.getTime();
		setEnd(false);
	}

	public void reset() {
		this.clickStart = TimeUtils.getTime();
		this.clickEnd = finishedClick? -1: TimeUtils.getTime();
	}

	public void setEnd(boolean finished) {
		this.clickEnd = TimeUtils.getTime();
		this.finishedClick = finished;
	}

	public long getClickDuration() {
		return (finishedClick? clickEnd: TimeUtils.getTime()) - clickStart;
	}

	public boolean isClickInProgress() {
		return !finishedClick;
	}

	public long timeSinceEnd() {
		return TimeUtils.getTime() - clickEnd;
	}

	@Override
	public String toString() {
		return "Click " + ((char) keyCode) + "(" + keyCode + ") (" + clickStart + " - " + clickEnd + ") " + (finishedClick? "finished": "in progress");
	}

}
