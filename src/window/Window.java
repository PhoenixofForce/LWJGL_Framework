package window;

import assets.AssetLoader;
import assets.TextureHandler;
import assets.audio.AudioPlayer;
import assets.models.ScreenRect;
import gameobjects.particles.ParticleSpawner;
import org.joml.Vector3f;
import rendering.ShaderHandler;
import utils.Constants;
import utils.Options;
import utils.TimeUtils;
import window.gui.*;
import window.inputs.FocusHolder;
import window.inputs.InputHandler;

import java.nio.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import window.inputs.KeyHit;
import window.views.View;

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

public class Window extends BasicColorGuiElement implements FocusHolder {

	public static Window INSTANCE;

	// The window handle
	public long window;
	public long audioDevice, audioContext;

	private int width = Constants.DEFAULT_WIDTH;
	private int height = Constants.DEFAULT_HEIGHT;

	private boolean isFullscreen;

	private final Map<String, Long> runtimeCount;
	private View currentView;
	private View nextView;
	private boolean shouldCleanUp;

	public Window() {
		super(new GuiConfig(0, 0, 0, 0));
		INSTANCE = this;
		this.runtimeCount = new HashMap<>();
		init();
	}

	public void run(View view) {
		this.currentView = view;
		if(view != null) view.init(this);

		glfwShowWindow(window);
		setFullscreen(Options.fullScreen);

		loop();
		cleanUp();
	}

	private void init() {
		initOpenAL();
		initGLFW();
		initCallbacks();
		initOpenGL();

		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		System.out.printf("OpenGL Version %s%n", GL11.glGetString(GL11.GL_VERSION));
		System.out.printf("OpenAL Version %s%n", AL11.alGetString(AL11.AL_VERSION));
		System.out.println();

		ShaderHandler.initShader();
		AssetLoader.loadAll();
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
		window = glfwCreateWindow(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, Constants.TITLE, NULL, NULL);
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

		if(Constants.GRAB_MOUSE) {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		}
	}

	private void initOpenGL() {
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDepthFunc(GL_LEQUAL);

		glClearColor(0, 0, 0, 1);
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
			if(height != this.height || width != this.width) {
				handleResize();
			}

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
			switchViews();

			long dt = TimeUtils.getTime() - lastUpdate;
			long dtNano = TimeUtils.getNanoTime() - lastUpdateNano;
			lastUpdate = TimeUtils.getTime();
			lastUpdateNano = TimeUtils.getNanoTime();

			if(dt > 0) glfwSetWindowTitle(window, Constants.TITLE + " (" + (int)(Math.ceil(1000000000.0 / dtNano)) + ")");
			
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
		if(currentView != null) {
			currentView.update(dt);

			runtimeCount.put(currentViewString(), getRuntime() + 1);
		}
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if(currentView != null) currentView.render();
		super.renderGui();

		glfwSwapBuffers(window);
	}

	private void switchViews() {
		if(nextView != null) {
			currentView.remove();
			cleanUpGui(shouldCleanUp);
			if(shouldCleanUp) {
				if(currentView != null) currentView.cleanUp();
				softCleanUp();
			}

			currentView = nextView;
			nextView = null;
			currentView.init(this);
		}
	}

	private void softCleanUp() {
		runtimeCount.remove(currentViewString());
		AudioPlayer.cleanUp();
	}

	private void cleanUp() {
		Options.save();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();

		cleanUpGui(true);
		if(currentView != null) currentView.cleanUp();
		AudioPlayer.cleanUpAll();
		ShaderHandler.cleanup();
		AssetLoader.cleanUp();
		ParticleSpawner.cleanUpAll(true);
		ScreenRect.getInstance().cleanUp();
	}

	public void setFullscreen(boolean value) {
		GLFWVidMode mode = glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

		if(value) {
			glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, mode.width(), mode.height(), mode.refreshRate());
		} else {
			glfwSetWindowMonitor(window, NULL, (mode.width() - Constants.DEFAULT_WIDTH) / 2, (mode.height() - Constants.DEFAULT_HEIGHT) / 2, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, GLFW_DONT_CARE);
		}
		this.isFullscreen = value;
	}

	public boolean isFullscreen() {
		return isFullscreen;
	}

	public void setBackgroundColor(Vector3f color) {
		glClearColor(color.x, color.y, color.z, 1);
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
		return this.width;
	}

	@Override
	public float getHeight() {
		return this.height;
	}

	public void setView(View view, boolean shouldCleanUp) {
		this.nextView = view;
		this.shouldCleanUp = shouldCleanUp;
	}

	public long getRuntime() {
		return runtimeCount.getOrDefault(currentViewString(), 0L);
	}

	public String currentViewString() {
		return currentView.toString().hashCode() + "";
	}

	public View getCurrentView() {
		return currentView;
	}

	@Override
	public void charStartRepeat(char c) {

	}

	@Override
	public void handleStart(KeyHit hit) { }

	@Override
	public void handleRepeat(KeyHit hit) { }

	@Override
	public void handleEnd(KeyHit hit) {
	}
}