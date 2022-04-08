package assets.audio;

import org.joml.Vector3f;

public enum AudioType {

	MUSIC("Chillstep_1"),
	EFFECT("powerup"),
	;

	private final String fileName;
	AudioType(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void play() {
		AudioPlayer.playSoundEffect(this);
	}

	public void play(Vector3f pos) {
		AudioPlayer.playSoundEffect(this, pos);
	}
}
