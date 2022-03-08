package utils;

import org.joml.*;

import java.lang.Math;
import java.util.List;

public class VecUtils {

	public static Vector3f cloneVec3f(Vector3f in) {
		return new Vector3f(in.x, in.y, in.z);
	}

	public static Vector2f cloneVec2f(Vector2f in) {
		return new Vector2f(in.x, in.y);
	}

	public static float[] vec4Array(Vector4f[] in) {
		return vec4Array(List.of(in));
	}

	public static float[] vec4Array(List<Vector4f> vList) {
		float[] out = new float[4 * vList.size()];
		for (int i = 0; i < vList.size(); i++) {
			out[4 * i + 0] = vList.get(i).x;
			out[4 * i + 1] = vList.get(i).y;
			out[4 * i + 2] = vList.get(i).z;
			out[4 * i + 3] = vList.get(i).w;
		}

		return out;
	}

	public static float[] vec3Array(Vector3f[] in) {
		return vec3Array(List.of(in));
	}

	public static float[] vec3Array(List<Vector3f> vList) {
		float[] out = new float[3 * vList.size()];
		for (int i = 0; i < vList.size(); i++) {
			out[3 * i + 0] = vList.get(i).x;
			out[3 * i + 1] = vList.get(i).y;
			out[3 * i + 2] = vList.get(i).z;
		}

		return out;
	}

	public static float[] vec2Array(Vector2f[] in) {
		return vec2Array(List.of(in));
	}

	public static float[] vec2Array(List<Vector2f> vList) {
		float[] out = new float[2 * vList.size()];
		for (int i = 0; i < vList.size(); i++) {
			out[2 * i + 0] = vList.get(i).x;
			out[2 * i + 1] = vList.get(i).y;
		}

		return out;
	}

	public static float[] vec1Array(float... in) {
		return in;
	}

	public static float[] vec1Array(List<Float> vList) {
		float[] out = new float[vList.size()];
		for (int i = 0; i < vList.size(); i++) {
			out[i] = vList.get(i);
		}

		return out;
	}

	public static float[] createFloatArray(Matrix3f r, Vector3f t) {
		float[] out = new float[] {
				r.m00, r.m01, r.m02, 0f,
				r.m10, r.m11, r.m12, 0f,
				r.m20, r.m21, r.m22, 0f,
				t.x, t.y, t.z, 1f
		};

		return out;
	}

	public static Matrix4f createMatrice(Matrix3f r, Vector3f t) {
		Matrix4f out = new Matrix4f();
		out.set(r);
		out.setTranslation(t);

		return out;
	}

	public static Vector3f vecFromAngles(float a1, float a2) {
		return new Vector3f(
				(float) (Math.cos(a1) * Math.cos(a2)),
				(float) (Math.sin(a1) * Math.cos(a2)),
				(float) (Math.sin(a2))
		);
	}

	public static Matrix4f transformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		Matrix3f scaleMatrix = new Matrix3f(scale.x, 0, 0, 0, scale.y, 0, 0, 0, scale.z);
		Matrix3f rotationScaleMatrix = new Matrix3f().rotationXYZ(rotation.x, rotation.y, rotation.z).mul(scaleMatrix);
		return createMatrice(rotationScaleMatrix, translation);
	}
}
