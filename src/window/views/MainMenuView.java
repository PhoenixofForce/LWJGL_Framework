package window.views;

import utils.MathUtils;
import utils.Constants;
import window.Window;
import window.font.Font;
import window.font.GeneralFont;
import window.gui.Anchor;
import window.gui.GuiButton;
import window.gui.GuiText;

public class MainMenuView implements View {

	private final GuiText title;
	private final GuiButton play;

	public MainMenuView() {
		Font font = new GeneralFont("WhitePeaberryOutline", 2);
		this.title = new GuiText(Anchor.CENTERCENTER, 0.5f, -0.1f, font, 48);
		this.title.setText(Constants.TITLE);

		this.play = new GuiButton(Anchor.CENTERCENTER, 0.5f, 0.4f, 200, 50);

		play.setClickListener(() -> Window.INSTANCE.setView(new TestView(), true));
	}

	@Override
	public void init(Window window) {
		window.setBackgroundColor(MathUtils.vecFromColor(50, 51, 83));
		window.addElement(title);
		window.addElement(play);
	}

	@Override
	public void update(long dt) {}

	@Override
	public void render() {}

	@Override
	public void remove() {
		Window.INSTANCE.setBackgroundColor(MathUtils.vecFromColor(0, 0, 0));
	}

	@Override
	public void cleanUp() {}
}
