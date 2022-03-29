package utils;

public class Constants {

	public static int RUNTIME = 0;

	public static final String OBJ_PATH = "res/obj/";
	public static final String SHADER_PATH = "res/shader/";
	public static final String SKYBOX_PATH = "res/skybox/";
	public static final String TEXTURE_PATH = "res/textures/";

	public static final float STICK_DEAD_ZONE = 0.2f;

	public static final float FONT_ASPECT = 7.0f / 5.0f; 		//font height : font width


	public static final float[] RECT_VERT = new float[]{
			-1.0f, -1.0f,
			1.0f, -1.0f,
			1.0f,  1.0f,

			1.0f,  1.0f,
			-1.0f,  1.0f,
			-1.0f, -1.0f,
	};

	public static final float[] RECT_UV = new float[] {
			0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,1.0f, 1.0f,0.0f, 1.0f, 0.0f, 0.0f
	};

}
