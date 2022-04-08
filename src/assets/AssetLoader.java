package assets;

import assets.audio.AudioType;

import java.util.Optional;

public class AssetLoader {

	public static void loadAll() {
		loadTextures();
		loadModels();
		loadAudio();
	}

	public static void loadTextures() {
		//TODO:
		TextureHandler.loadImagePng("kenney_grass", "kenney_grass", Optional.empty());
		TextureHandler.loadPixelTextureAtlasPNG("Font", "Font", Optional.empty());
	}

	public static void loadModels() {
		//TODO:
		ObjHandler.loadOBJ("pawn");
		ObjHandler.loadOBJWithoutTexture("cube");
	}

	public static void loadAudio() {
		for(AudioType type: AudioType.values()) {
			AudioHandler.loadAudio(type);
		}
	}

	public static void cleanUp() {
		TextureHandler.cleanUp();
		ObjHandler.cleanUp();
		AudioHandler.cleanUp();
	}
}
