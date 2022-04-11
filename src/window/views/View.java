package window.views;

import window.Window;

public interface View {

	void init(Window window);
	void update(long dt);
	void render();
	void remove();
	void cleanUp();

}
