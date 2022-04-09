package window;

import assets.AssetLoader;
import assets.TextureHandler;
import assets.audio.AudioPlayer;
import assets.audio.AudioType;
import assets.models.ScreenRect;
import gameobjects.entities.Camera;
import gameobjects.particles.ParticleSpawner;
import rendering.ShaderHandler;
import utils.Constants;
import utils.Options;
import utils.TimeUtils;
import window.font.Font;
import window.font.GeneralFont;
import window.font.TextureAtlasFont;
import window.gui.*;
import window.inputs.InputHandler;

import java.nio.*;
import java.util.List;
import java.util.Optional;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.openal.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window extends BasicColorGuiElement {

	public static Window INSTANCE;

	// The window handle
	public long window;
	public long audioDevice, audioContext;

	private int width = 960;
	private int height = 600;

	private final String title = "This is an engine!";

	private Camera cam;
	private GuiText text;

	public Window() {
		super(null, 0, 0, 0, 0);
	}

	public void run() {
		INSTANCE = this;

		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();
		cleanUp();
	}

	private void init() {
		initOpenAL();
		initGLFW();
		initCallbacks();
		initOpenGL();

		System.out.printf("OpenGL Version %s%n", GL11.glGetString(GL11.GL_VERSION));
		System.out.printf("OpenAL Version %s%n", AL11.alGetString(AL11.AL_VERSION));
		System.out.println();

		ShaderHandler.initShader();
		AssetLoader.loadAll();
		loadGui();

		cam = new Camera();

		ParticleSpawner.getNewSpawner(new Vector3f(0, 5, 0), ParticleSpawner.DEFAULT);
		AudioPlayer.playMusic(AudioType.MUSIC);

		glfwShowWindow(window);
	}

	private void initOpenAL() {
		audioDevice = ALC11.alcOpenDevice((ByteBuffer) null);
		ALCCapabilities caps = ALC.createCapabilities(audioDevice);

		audioContext = ALC11.alcCreateContext(audioDevice, (IntBuffer) null);
		ALC11.alcMakeContextCurrent(audioContext);

		AL.createCapabilities(caps);

		//Creates an audio listener at (0, 0, 0)
		AL10.alListener3f(AL10.AL_POSITION, 0, 0, 0);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}

	private void initGLFW() {
		GLFWErrorCallback.createPrint(System.err).set();

		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(width, height, title, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		glfwMakeContextCurrent(window);
		glfwSwapInterval(Options.useVsync? 1: 0);

		Optional<TextureHandler.WindowIcon> opIcon = TextureHandler.getWindowIcon();
		if(opIcon.isPresent()) {
			TextureHandler.WindowIcon windowIcon = opIcon.get();

			ByteBuffer icon = windowIcon.buffer();
			GLFWImage.Buffer gb = GLFWImage.create(1);
			GLFWImage iconGI = GLFWImage.create().set(windowIcon.width(), windowIcon.height(), icon);
			gb.put(0, iconGI);

			glfwSetWindowIcon(window, gb);
		}
	}

	private void initOpenGL() {
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDepthFunc(GL_LEQUAL);

		glClearColor(0, 0, 0, 0.0f);
	}


	private GuiElement lastClicked;
	private void initCallbacks() {
		InputHandler.callbacks();

		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			float[] mouse =  InputHandler.getMousePosition();
			if(action == GLFW_PRESS) {
				lastClicked = handleMouseButton(action, button, mouse[0], mouse[1]);
			} else {
				GuiElement toClick = lastClicked != null? lastClicked: this;
				toClick.handleMouseButton(action, button, InputHandler.mouseX, InputHandler.mouseY);
			}
		});

		glfwSetWindowSizeCallback(window, (window, width, height) -> {
			this.width = width;
			this.height = height;
			glViewport(-1, -1, width, height);
		});

		glfwSetErrorCallback((e, f) -> System.err.println(e + " " + f));
	}

	private void loop() {
		long lastUpdate = TimeUtils.getTime();
		long lastUpdateNano = TimeUtils.getNanoTime();
		while ( !glfwWindowShouldClose(window) ) {
			long dt = TimeUtils.getTime() - lastUpdate;
			long dtNano = TimeUtils.getNanoTime() - lastUpdateNano;
			lastUpdate = TimeUtils.getTime();
			lastUpdateNano = TimeUtils.getNanoTime();

			if(dt > 0) glfwSetWindowTitle(window, title + " (" + (int)(Math.ceil(1000000000.0 / dtNano)) + ")");
			
			testOpenGLError();
			glfwPollEvents();

			input();
			update(dt);
			render();
		}
	}

	private void input() {
		InputHandler.update();
		for(int i: List.of(GLFW_MOUSE_BUTTON_LEFT, GLFW_MOUSE_BUTTON_RIGHT, GLFW_MOUSE_BUTTON_RIGHT)) {
			if(InputHandler.isMousePressed(i)) {
				GuiElement toClick = lastClicked != null? lastClicked: this;
				toClick.handleMouseButton(GLFW_REPEAT, i, InputHandler.mouseX, InputHandler.mouseY);
			}
		}
	}

	private void update(long dt) {
		updateGui(dt);
		AudioPlayer.update(dt);
		ParticleSpawner.updateAll(dt);

		cam.update(dt);
		if(Constants.RUNTIME >= 1000) {
			text.clear().addText("TICKS: ").addText(Constants.RUNTIME + "", Constants.RUNTIME > 1000? new Vector3f(1, 0, 0): (Constants.RUNTIME > 750? new Vector3f(1, 1, 0): new Vector3f(0, 0, 1))).build();
		}

		Constants.RUNTIME++;
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Matrix4f projection_matrix = new Matrix4f().perspective((float)Math.PI/3, ((float) width)/height,0.001f, 1250f);
		Matrix4f view_matrix = new Matrix4f().lookAt(cam.getPosition(), new Vector3f(cam.getLookingDirection()).add(cam.getPosition()), cam.getUp());

		ParticleSpawner.renderAll(projection_matrix, view_matrix);
		super.renderGui();

		glfwSwapBuffers(window);
	}

	private void cleanUp() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();

		AudioPlayer.cleanUp(true);
		ShaderHandler.cleanup();
		AssetLoader.cleanUp();
		ParticleSpawner.cleanUpAll(true);
		ScreenRect.getInstance().cleanUp();
	}

	public void testOpenGLError() {
		int errorCode = glGetError();
		if (errorCode != GL_NO_ERROR) {
			if(errorCode == GL_INVALID_ENUM) throw new RuntimeException("Invalid Enumereraion (" + errorCode + ")");
			else if(errorCode == GL_INVALID_VALUE) throw new RuntimeException("Invalid Value (" + errorCode + ")");
			else if(errorCode == GL_INVALID_OPERATION) throw new RuntimeException("Invalid Operation (" + errorCode + ")");
			else if(errorCode == GL_STACK_OVERFLOW) throw new RuntimeException("Stack Overflow (" + errorCode + ")");
			else if(errorCode == GL_STACK_UNDERFLOW) throw new RuntimeException("Stack Underflow (" + errorCode + ")");
			else if(errorCode == GL_OUT_OF_MEMORY) throw new RuntimeException("Out of Memory (" + errorCode + ")");
			else if(errorCode == GL_INVALID_FRAMEBUFFER_OPERATION) throw new RuntimeException("Invalid Framebuffer Operation (" + errorCode + ")");
			else if(errorCode == GL_CONTEXT_LOST) throw new RuntimeException("Context Lost (" + errorCode + ")");
		}
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	public void loadGui() {
		GuiElement healtBar = new BasicColorGuiElement(this, Anchor.BEGIN, Anchor.BEGIN, 20, 20, 200, 20);
		GuiElement staminaBar = new BasicColorGuiElement(this, Anchor.BOTTOM_RIGHT, -20, 20, 200, 20);
		GuiElement manaBar = new BasicColorGuiElement(this, Anchor.BOTTOM_CENTER, 0.5f, 20, 200, 20);
		GuiElement currentMana = new BasicColorGuiElement(manaBar, Anchor.TOP_LEFT, 0, 1, 0.3f, 20);

		GuiElement crosshair = new BasicColorGuiElement(this, 0.5f, 0.5f, 10, 10);

		GuiSlider slider = new GuiSlider(this, Anchor.BOTTOM_LEFT, 50, 200, 200, 20);
		slider.setValue(Options.musicVolume);
		slider.setChangeListener(v -> Options.musicVolume = v);

		GuiButton button = new GuiButton(this, Anchor.CENTERCENTER, 150, 275, 200, 50);
		GuiCheckbox checkbox = new GuiCheckbox(this, Anchor.BOTTOM_LEFT, 50, 320, 20, 20);
		GuiSelector selector = new GuiSelector(this, Anchor.BOTTOM_LEFT, 50, 400, 200, 50);

		Font font1 = new GeneralFont("WhitePeaberryOutline", 2);
		Font font2 = new TextureAtlasFont("Font");

		text = new GuiText(this, Anchor.TOP_LEFT,  20, -20f, 450, font1, 32f, 50)
				.addText("The quick brown fox jumps over the lazy dog", new Vector3f(1, 0, 0))
				.newLine()
				.addText("ThisIsAVeryLongLineAgain")
				.newLine()
				.addText("\\<test\\> = <test>")
				.build();

		this.setMouseClickListener((e, b) -> {
			if(e != 2) AudioType.EFFECT.play();
		});
	}
}