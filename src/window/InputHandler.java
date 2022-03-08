package window;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

	public boolean isKeyPressed(int keyCode) {
		return glfwGetKey(Window.INSTANCE.window, keyCode) == GLFW_PRESS;
	}

}
