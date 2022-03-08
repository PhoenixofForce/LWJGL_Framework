package maths;

import utils.TimeUtils;

public class SmoothFloat {

	private static final long BASE_DURATION = 1000;

	private float beginValue;
	private float currentValue;
	private float targetValue;
	private long beginTime, endTime;

	private boolean inTransition;

	private final Easing easing;

	public SmoothFloat(Easing easing, float value) {
		this.easing = easing;

		this.beginValue = value;
		this.currentValue = value;
		this.targetValue = value;

		this.beginTime = 0;
		this.endTime = 0;
		this.inTransition = false;
	}

	public SmoothFloat(float value) {
		this(Easing::linear, value);
	}

	public void set(float value) {
		this.beginValue = value;
		this.currentValue = value;
		this.targetValue = value;

		this.inTransition = false;
	}

	public void setSmooth(float value, long duration) {
		if(inTransition) {
			currentValue = targetValue;
		}

		beginValue = currentValue;
		targetValue = value;
		beginTime = TimeUtils.getTime();
		endTime = beginTime + duration;

		inTransition = true;
	}

	public void setSmooth(float value) {
		this.setSmooth(value, BASE_DURATION);
	}

	public void update() {
		if(inTransition) {
			if(TimeUtils.getTime() > endTime) {
				currentValue = targetValue;
				inTransition = false;
			} else {
				double transitionDuration = endTime - beginTime;
				double timeSinceStart = TimeUtils.getTime() - beginTime;

				float easeFactor = (float) easing.ease(timeSinceStart / transitionDuration);
				float valueDifference = targetValue - beginValue;
				currentValue = beginValue + easeFactor * valueDifference;
			}
		}
	}

	public float getValue() {
		return currentValue;
	}

	public float getActualValue() {
		return this.targetValue;
	}

	//>--| ADD |--<\\

	public void add(float val) {
		this.set(this.targetValue + val);
	}

	public void addSmooth(float val, long duration) {
		this.setSmooth(this.targetValue + val, duration);
	}

	public void addSmooth(float val) {
		this.addSmooth(val, BASE_DURATION);
	}

	//>--| SUB |--<\\

	public void sub(float val) {
		this.set(this.targetValue - val);
	}

	public void subSmooth(float val, long duration) {
		this.setSmooth(this.targetValue - val, duration);
	}

	public void subSmooth(float val) {
		this.subSmooth(val, BASE_DURATION);
	}

	//>--| MUL |--<\\

	public void mul(float val) {
		this.set(this.targetValue * val);
	}

	public void mulSmooth(float val, long duration) {
		this.setSmooth(this.targetValue * val, duration);
	}

	public void mulSmooth(float val) {
		this.mulSmooth(val, BASE_DURATION);
	}

	//>--| DIV |--<\\

	public void div(float val) {
		this.set(this.targetValue / val);
	}

	public void divSmooth(float val, long duration) {
		this.setSmooth(this.targetValue / val, duration);
	}

	public void divSmooth(float val) {
		this.mulSmooth(val, BASE_DURATION);
	}
}
