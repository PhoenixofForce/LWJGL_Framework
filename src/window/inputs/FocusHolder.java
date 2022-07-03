package window.inputs;

public interface FocusHolder {

	void charStartRepeat(char c);
	void handleStart(KeyHit hit);	//Press
	void handleRepeat(KeyHit hit);	//Repeat
	void handleEnd(KeyHit hit);		//Release

}
