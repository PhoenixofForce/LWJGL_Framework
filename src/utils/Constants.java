package utils;

public class Constants {

	//>--| WINDOW |--<\\
	public static final String TITLE = "This is a engine";
	public static final int DEFAULT_WIDTH = 960;
	public static final int DEFAULT_HEIGHT = 600;


	public static int RUNTIME = 0;

	public static final String OBJ_PATH = "res/obj/";
	public static final String SHADER_PATH = "res/shader/";
	public static final String SKYBOX_PATH = "res/skybox/";
	public static final String TEXTURE_PATH = "res/textures/";
	public static final String AUDIO_PATH = "res/audio/";

	public static final boolean GRAB_MOUSE = false;
	public static final float MOUSE_SENSITIVITY = 1.0f;
	public static final float STICK_DEAD_ZONE = 0.2f;

	public static final float FONT_SPACING = 0.25f;

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
