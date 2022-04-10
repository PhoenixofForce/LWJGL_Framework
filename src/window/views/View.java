package window.views;

public interface View {

	void init();
	void update(long dt);
	void render();
	void cleanUp();

}
