package meshes.loader;

import java.util.Optional;

public class AssetLoader {

	public static void loadAll() {
		loadTextures();
		loadModels();
	}

	public static void loadTextures() {
		//TODO:
		TextureHandler.loadImagePng("kenney_grass", "kenney_grass", Optional.empty());
	}

	public static void loadModels() {
		//TODO:
		ObjHandler.loadOBJ("pawn");
		ObjHandler.loadOBJWithoutTexture("cube");
	}

	public static void unloadAll() {
		TextureHandler.cleanUp();
		ObjHandler.cleanUp();
	}
}
