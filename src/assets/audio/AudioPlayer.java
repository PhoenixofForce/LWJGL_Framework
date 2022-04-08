package assets.audio;

import org.joml.Vector3f;
import utils.Options;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayer {

	private static final List<AudioSource> audioSources = new ArrayList<>();
	private static final AudioSource musicSource = new AudioSource(false)
			.setVolume(Options.musicVolume * Options.totalVolume)
			.setLooping(true);

	public static void playSoundEffect(AudioType type) {
		playSoundEffect(type, new Vector3f());
	}

	public static void playSoundEffect(AudioType type, Vector3f position) {
		AudioSource afxSource = new AudioSource(position.x, position.y, position.z, true)
				.setVolume(Options.effectVolume * Options.totalVolume)
				.play(type);

		audioSources.add(afxSource);
	}

	public static void playMusic(AudioType music) {
		musicSource.stop()
				.play(music);
	}

	public static void update(long dt) {
		musicSource.setVolume(Options.musicVolume * Options.totalVolume);

		for(int i = 0; i < audioSources.size(); i++) {
			AudioSource source = audioSources.get(i);
			source.setVolume(Options.effectVolume * Options.totalVolume);

			if(!source.isPlaying()) {
				source.stop();
				audioSources.remove(source);

				if(source.shouldDeleteOnFinish()) source.cleanUp();
				i--;
			}
		}
	}

	public static void cleanUp(boolean deleteMusic) {
		for(int i = audioSources.size() - 1; i >= 0; i--) {
			AudioSource src = audioSources.get(0);
			src.stop().cleanUp();
		}

		if(deleteMusic) musicSource.stop().cleanUp();
	}
}
