package assets.audio;

import org.joml.Vector3f;
import utils.Options;
import window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioPlayer {

	private static final Map<String, AudioPlayer> players = new HashMap<>();

	private static AudioPlayer getPlayer() {
		String key = Window.INSTANCE.currentViewString();
		if(!players.containsKey(key)) players.put(key, new AudioPlayer());
		return players.get(key);
	}

	public static void playSoundEffect(AudioType type) {
		getPlayer().AP_playSoundEffect(type, new Vector3f());
	}

	public static void playSoundEffect(AudioType type, Vector3f position) {
		getPlayer().AP_playSoundEffect(type, position);
	}

	public static void playMusic(AudioType music) {
		getPlayer().AP_playMusic(music);
	}

	public static void pauseAll() {
		getPlayer().AP_pauseAll();
	}

	public static void resumeAll() {
		getPlayer().AP_resumeAll();
	}

	public static void update(long dt) {
		getPlayer().AP_update(dt);
	}

	public static void cleanUp() {
		getPlayer().AP_cleanUp();
	}

	public static void cleanUpAll() {
		List<AudioPlayer> allPlayers = players.values().stream().toList();	//needed so we do not run into a ConcurrentModificationException
		allPlayers.forEach(AudioPlayer::AP_cleanUp);
	}


	private final List<AudioSource> audioSources = new ArrayList<>();
	private final AudioSource musicSource = new AudioSource(false)
			.setVolume(Options.musicVolume * Options.totalVolume)
			.setLooping(true);

	private AudioPlayer() {}
	private void AP_playSoundEffect(AudioType type) {
		playSoundEffect(type, new Vector3f());
	}

	private void AP_playSoundEffect(AudioType type, Vector3f position) {
		AudioSource afxSource = new AudioSource(position.x, position.y, position.z, true)
				.setVolume(Options.effectVolume * Options.totalVolume)
				.play(type);

		audioSources.add(afxSource);
	}

	private void AP_pauseAll() {
		for(int i = audioSources.size() - 1; i >= 0; i--) {
			AudioSource src = audioSources.remove(0);
			src.pause();
		}
		musicSource.pause();
	}

	private void AP_resumeAll() {
		for(int i = audioSources.size() - 1; i >= 0; i--) {
			AudioSource src = audioSources.remove(0);
			src.resume();
		}
		musicSource.resume();
	}

	private void AP_playMusic(AudioType music) {
		musicSource.stop()
				.play(music);
	}

	private void AP_update(long dt) {
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

	private void AP_cleanUp() {
		for(int i = audioSources.size() - 1; i >= 0; i--) {
			AudioSource src = audioSources.remove(0);
			src.stop();
			src.cleanUp();
		}

		musicSource.stop();
		musicSource.cleanUp();

		players.remove(Window.INSTANCE.currentViewString());
	}
}
