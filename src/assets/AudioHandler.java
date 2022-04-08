package assets;

import assets.audio.AudioType;
import org.lwjgl.openal.AL11;
import utils.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AudioHandler {

	private static Map<String, Integer> audioBuffers = new HashMap<>();

	public static void loadAudio(AudioType type) {
		String file = Constants.AUDIO_PATH + type.getFileName() + ".wav";
		Optional<WaveAudio> wv = WaveAudio.create(file);

		if(wv.isPresent()) {
			WaveAudio waveAudio = wv.get();
			audioBuffers.put(type.toString().toLowerCase(), waveAudio.getBuffer());
		} else {
			System.err.println("Error loading audio file (" + type + ")");
			System.err.println(file);
		}
	}

	public static int getBuffer(AudioType type) {
		return audioBuffers.getOrDefault(type.toString().toLowerCase(), 0);
	}

	public static void unloadAudio(AudioType type) {
		int buffer = audioBuffers.remove(type.toString().toLowerCase());
		AL11.alDeleteBuffers(buffer);
	}

	public static void cleanUp() {
		for(int i: audioBuffers.values()) {
			AL11.alDeleteBuffers(i);
		}

		audioBuffers = new HashMap<>();
	}

}
