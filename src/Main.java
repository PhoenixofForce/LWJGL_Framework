import utils.Options;
import window.Window;
import window.views.MainMenuView;

public class Main {

	public static void main(String[] args) {
		Options.load();
		new Window().run(new MainMenuView());
	}

}
