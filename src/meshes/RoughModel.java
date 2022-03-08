package meshes;

import org.joml.Vector2f;
import org.joml.Vector3f;
import utils.VecUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

record RoughModel(List<Vector3f> vertices, List<Vector2f> textureCoordinates, List<Vector3f> normals, List<int[][]> faces) {

	static RoughModel loadRoughModel(File objFile) throws IOException {
		List<Vector3f> vertices = new ArrayList<>();
		List<Vector2f> textureCoordinates = new ArrayList<>();
		List<Vector3f> vertexNormnals = new ArrayList<>();
		List<int[][]> faces = new ArrayList<>();

		BufferedReader r = new BufferedReader(new FileReader(objFile));
		String line = r.readLine();

		while(line != null) {
			line = line.replace("  ", " ");
			String[] parts = line.split(" ");

			if("v".equals(parts[0])) {				//v x y z
				Vector3f v = parseVector3f(parts);
				vertices.add(v);
			} else if("vt".equals(parts[0])) {		//vt x y
				Vector2f vt = parseVector2f(parts);
				vt.y = 1 - vt.y;
				textureCoordinates.add(vt);
			} else if("vn".equals(parts[0])) {		//vn x y z
				Vector3f vn = parseVector3f(parts);
				vertexNormnals.add(vn);
			} else if("f".equals(parts[0])) {		//f	v1/t1/n1 ... vn/tn/nn	#vertices, texture coords, normals for polygon with n vertices
				int[] vertexIndices = parseFace(parts, 0);
				int[] textureCoordinatesIndicies = parseFace(parts, 1);
				int[] normalIndicies = parseFace(parts, 2);

				for(int i = 1; i < vertexIndices.length - 1; i++) {
					int[] indices = new int[]{0, i, i + 1};
					faces.add(new int[][] {subArray(vertexIndices, indices), subArray(textureCoordinatesIndicies, indices), subArray(normalIndicies, indices)});
				}
			}

			line = r.readLine();
		}
		r.close();

		return new RoughModel(vertices, textureCoordinates, vertexNormnals, faces);
	}

	static Model convertRoughModel(RoughModel rm, String name, Optional<String> textureName) {
		List<Vector3f> vertices = new ArrayList<>();
		List<Vector2f> textureCoordinates = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();

		List<int[]> faces = new ArrayList<>();

		Map<String, Integer> vertex_names = new HashMap<>();
		int j = 0;

		for(int[][] f: rm.faces) {
			int[] faceArray = new int[f[0].length];
			for(int i = 0; i < f[0].length; i++) {
				String tuple = "";
				for (int k = 0; k < f.length; k++) {
					tuple += f[k][i] + ",";
				}
				if (vertex_names.containsKey(tuple)) {
					faceArray[i] = vertex_names.get(tuple);

					continue;
				}

				Vector3f v = rm.vertices.get(f[0][i] - 1);
				vertices.add(VecUtils.cloneVec3f(v));

				Vector2f vt = rm.textureCoordinates.get(f[1][i] - 1);
				textureCoordinates.add(VecUtils.cloneVec2f(vt));

				if (f.length >= 3) {
					Vector3f n = rm.normals.get(f[2][i] - 1);
					normals.add(VecUtils.cloneVec3f(n));
				}

				faceArray[i] = j;
				vertex_names.put(tuple, j);
				j++;
			}

			faces.add(faceArray);
		}

		if(textureName.isPresent()) return new TexturedModel(name, textureName.get(), vertices, textureCoordinates, normals, faces);
		else return new Model(name, vertices, textureCoordinates, normals, faces);
	}

	private static int[] subArray(int[] array, int... indices) {
		int[] out = new int[indices.length];
		for(int i = 0; i < indices.length; i++) {
			out[i] = array[indices[i]];
		}
		return out;
	}

	private static int[] parseFace(String[] parts, int index) {
		int[] out = new int[parts.length - 1];
		for(int i = 1; i < parts.length; i++) {
			out[i - 1] = Integer.parseInt(parts[i].split("/")[index]);
		}
		return out;
	}

	private static Vector2f parseVector2f(String[] args) {
		return new Vector2f(Float.parseFloat(args[1]), Float.parseFloat(args[2]));
	}

	private static Vector3f parseVector3f(String[] args) {
		return new Vector3f(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
	}
}
