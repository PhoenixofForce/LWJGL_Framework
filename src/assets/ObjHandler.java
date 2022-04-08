package assets;

import assets.models.Model;
import utils.Constants;

import java.io.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

public class ObjHandler {

	private static Map<String, Model> modelMap = new HashMap<>();

	public static Model getModel(String model) {
		if(!modelMap.containsKey(model)) {
			System.err.println("Tried to access unloaded model " + model);
		}
		return modelMap.getOrDefault(model, null);
	}

	//>---|  LOAD OBJ  |--<\\

	public static Model loadObjWithTexture(String modelName, String textureName) {
		if(modelMap.containsKey(modelName)) return modelMap.get(modelName);

		File objFile = new File(Constants.OBJ_PATH + "base/" + modelName + ".obj");
		try {
			RoughModel rm = RoughModel.loadRoughModel(objFile);
			Model model = RoughModel.convertRoughModel(rm, modelName, Optional.of(textureName));

			modelMap.put(modelName, model);
			return model;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Model loadOBJWithoutTexture(String name) {
		if(modelMap.containsKey(name)) return modelMap.get(name);
		File objFile = new File(Constants.OBJ_PATH + "base/" + name + ".obj");

		try {
			RoughModel rm = RoughModel.loadRoughModel(objFile);
			Model model = RoughModel.convertRoughModel(rm, name, Optional.empty());

			modelMap.put(name, model);
			return model;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Model loadOBJ(String name) {
		if(modelMap.containsKey(name)) return modelMap.get(name);
		File objFile = new File(Constants.OBJ_PATH + name + "/" + name + ".obj");

		try {
			TextureHandler.loadImagePng(name, name, Optional.of(Constants.OBJ_PATH + name + "/"));
			RoughModel rm = RoughModel.loadRoughModel(objFile);
			Model model = RoughModel.convertRoughModel(rm, name, Optional.of(name));

			modelMap.put(name, model);
			return model;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void cleanUp() {
		for(Model m: modelMap.values()) {
			m.cleanUp();
		}
 	}
}
