package window;

import meshes.AssetLoader;
import meshes.ObjHandler;
import meshes.ScreenRect;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import rendering.Renderer;
import rendering.ShaderHandler;
import rendering.Uniform;
import utils.TimeUtils;
import window.gui.Anchor;
import window.gui.BasicColorGuiElement;
import window.gui.GuiElement;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window extends BasicColorGuiElement {

	public static Window INSTANCE;

	// The window handle
	public long window;
	private int width = 960;
	private int height = 600;

	private String title = "This is an engine!";

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

		initCallbacks();

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
		glfwSwapInterval(1);			//vsync on

		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDepthFunc(GL_ALWAYS);

		ShaderHandler.initShader();
		AssetLoader.loadAll();
		loadGui();

		glfwShowWindow(window);
		glClearColor(0, 0, 1.0f, 0.0f);
	}

	private void initCallbacks() {
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
				System.out.println("Left mouse button pressed");
			}
		});

		glfwSetWindowSizeCallback(window, (window, width, height) -> {
			this.width = width;
			this.height = height;
			glViewport(-1, -1, width, height);
		});

		glfwSetErrorCallback((e, f) -> {
			System.err.println(e + " " + f);
		});
	}

	private void loop() {
		position = new Vector3f(0, 0, -3);
		lookingDirection = new Vector3f(0, 0, 1);
		rotation = new Vector3f(0, 0, 0);
		upNormal = new Vector3f(0.0f, 1.0f, 0.0f);

		long lastUpdate = TimeUtils.getTime();
		while ( !glfwWindowShouldClose(window) ) {
			double dt = (TimeUtils.getTime() - lastUpdate) / 1000.0;
			lastUpdate = TimeUtils.getTime();

			testOpenGLError();
			glfwPollEvents();

			input();
			//update
			render();
		}
	}

	private void input() {
		float dx = 0, dy = 0, dz = 0;
		if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) dz += 0.1f;
		if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) dz -= 0.1f;

		if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) dx += 0.1f;
		if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) dx -= 0.1f;

		if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) dy += 0.1f;
		if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) dy -= 0.1f;

		float rotateY = 0, rotateX = 0;
		if(glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) rotateY += 0.04f;
		if(glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) rotateY -= 0.04f;

		if(glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) rotateX += 0.04f;
		if(glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) rotateX -= 0.04f;

		float tilt = 0;
		if(glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) tilt -= 0.5f;
		if(glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) tilt += 0.5f;

		rotation.add(rotateX, rotateY, 0);
		rotation.z = tilt;
		rotation.x = Math.min((float) Math.PI/2-0.05f, Math.max((float) -Math.PI/2 + 0.05f, rotation.x));

		lookingDirection.set(0, 0, 1);
		lookingDirection.rotateY(rotation.y);
		lookingDirection.rotateAxis(rotation.x, -lookingDirection.z, 0, lookingDirection.x);
		lookingDirection.normalize();

		position.sub(new Vector3f(lookingDirection.x, 0, lookingDirection.z).normalize().mul(dz));
		position.add(new Vector3f(-lookingDirection.z, 0, lookingDirection.x).normalize().mul(dx));
		position.add(0, dy, 0);

		glClearColor(lookingDirection.x / 2f + 0.5f, lookingDirection.y / 2f + 0.5f, lookingDirection.z / 2f + 0.5f, 0.0f);
	}

	private Vector3f position, rotation, lookingDirection, upNormal;
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Matrix4f projection_matrix = new Matrix4f().perspective((float)Math.PI/3, ((float) width)/height,0.0000001f, 1250f);
		Matrix4f view_matrix = new Matrix4f().lookAt(position, new Vector3f(lookingDirection).add(position), upNormal);
		Matrix4f transformationMatrix = new Matrix4f(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);


		Uniform uniform = new Uniform();
		uniform.setMatrices(projection_matrix, view_matrix, transformationMatrix);
		uniform.setVector3fs(new Vector3f(1, 0, 1));

		Renderer.render(ShaderHandler.ShaderType.DEFAULT, ObjHandler.loadOBJ("pawn"), uniform);
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

		ShaderHandler.cleanup();
		AssetLoader.unloadAll();
		ScreenRect.getInstance().cleanUp();
	}

	private void testOpenGLError() {
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
		GuiElement currentMana = new BasicColorGuiElement(manaBar, Anchor.BOTTOM_LEFT, 0, 0, 0.3f, 20);

		GuiElement crosshair = new BasicColorGuiElement(this, 0.5f, 0.5f, 10, 10);
	}
}