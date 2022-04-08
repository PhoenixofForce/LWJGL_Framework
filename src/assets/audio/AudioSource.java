package assets.audio;

import assets.AudioHandler;
import org.lwjgl.openal.AL11;

public class AudioSource {

	private boolean deleteOnFinish;
	private final int sourceID;
	
	public AudioSource(boolean deleteOnFinish) {
		this(0, 0, deleteOnFinish);
	}
	
	public AudioSource(float x, float y, boolean deleteOnFinish) {
		this(x, y, .25f, deleteOnFinish);
	}
	
	public AudioSource(float x, float y, float v, boolean deleteOnFinish) {
		this(x, y, 0, v, deleteOnFinish);
	}
	
	public AudioSource(float x, float y, float z, float v, boolean deleteOnFinish) {
		sourceID = AL11.alGenSources();
		setVolume(v);
		AL11.alSourcef(sourceID, AL11.AL_PITCH, 1);
		setPosition(x, y, z);

		this.deleteOnFinish = deleteOnFinish;
	}
	
	public AudioSource play(int buffer) {
		AL11.alSourcei(sourceID, AL11.AL_BUFFER, buffer);
		AL11.alSourcePlay(sourceID);

		return this;
	}
	
	public AudioSource play(AudioType bufferName) {
		return play(AudioHandler.getBuffer(bufferName));
	}

	
	public AudioSource pause() {
		AL11.alSourcePause(sourceID);

		return this;
	}
	
	public AudioSource resume() {
		AL11.alSourcePlay(sourceID);
		return this;
	}
	
	public AudioSource stop() {
		AL11.alSourceStop(sourceID);
		return this;
	}
	
	public AudioSource setVolume(float volume) {
		AL11.alSourcef(sourceID, AL11.AL_GAIN, volume);
		return this;
	}
	
	public AudioSource setPosition(float x, float y, float z) {
		AL11.alSource3f(sourceID, AL11.AL_POSITION, x, y, z);
		return this;
	}
	
	public AudioSource setLooping(boolean loop) {
		AL11.alSourcei(sourceID, AL11.AL_LOOPING, loop ? AL11.AL_TRUE : AL11.AL_FALSE);
		return this;
	}
	
	public boolean isPlaying() {
		return AL11.alGetSourcei(sourceID, AL11.AL_SOURCE_STATE) == AL11.AL_PLAYING;
	}

	public boolean shouldDeleteOnFinish() {
		return deleteOnFinish;
	}
	
	public void cleanUp() {
		AL11.alDeleteSources(sourceID);
	}
}