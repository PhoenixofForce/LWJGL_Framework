package window.views;

import assets.audio.AudioPlayer;
import assets.audio.AudioType;
import gameobjects.entities.Camera;
import gameobjects.particles.ParticleSpawner;
import maths.MathUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import utils.Constants;
import utils.Options;
import window.Window;
import window.font.Font;
import window.font.GeneralFont;
import window.font.TextureAtlasFont;
import window.gui.*;

public class TestView implements View {

	private Camera cam;
	private GuiText text;

	@Override
	public void init() {
		cam = new Camera();
		ParticleSpawner.getNewSpawner(new Vector3f(0, 5, 0), new ParticleSpawner.ParticleType().setDirection(MathUtils.randomVectorAround(new Vector3f(1, 0, 0), 360), 0, new Vector3f(0)));
		AudioPlayer.playMusic(AudioType.MUSIC);

		GuiElement healtBar = new BasicColorGuiElement(Window.INSTANCE, Anchor.BEGIN, Anchor.BEGIN, 20, 20, 200, 20);
		GuiElement staminaBar = new BasicColorGuiElement(Window.INSTANCE, Anchor.BOTTOM_RIGHT, -20, 20, 200, 20);
		GuiElement manaBar = new BasicColorGuiElement(Window.INSTANCE, Anchor.BOTTOM_CENTER, 0.5f, 20, 200, 20);
		GuiElement currentMana = new BasicColorGuiElement(manaBar, Anchor.TOP_LEFT, 0, 1, 0.3f, 20);

		GuiElement crosshair = new BasicColorGuiElement(Window.INSTANCE, 0.5f, 0.5f, 10, 10);

		GuiSlider slider = new GuiSlider(Window.INSTANCE, Anchor.BOTTOM_LEFT, 50, 200, 200, 20);
		slider.setValue(Options.musicVolume);
		slider.setChangeListener(v -> Options.musicVolume = v);

		GuiButton button = new GuiButton(Window.INSTANCE, Anchor.CENTERCENTER, 150, 275, 200, 50);
		GuiCheckbox checkbox = new GuiCheckbox(Window.INSTANCE, Anchor.BOTTOM_LEFT, 50, 320, 20, 20);
		GuiSelector selector = new GuiSelector(Window.INSTANCE, Anchor.BOTTOM_LEFT, 50, 400, 200, 50);

		Font font1 = new GeneralFont("WhitePeaberryOutline", 2);
		Font font2 = new TextureAtlasFont("Font");

		text = new GuiText(Window.INSTANCE, Anchor.TOP_LEFT,  20, -20f, 500, font1, 24f, 50)
				.addText("The quick brown fox jumps over the lazy dog", new Vector3f(1, 0, 0))
				.newLine()
				.addText("\\<test\\> becomes <test>", new Vector3f(0, 1, 0), 0.02f)
				.build();

		Window.INSTANCE.setMouseClickListener((e, b) -> {
			if(e != 2) AudioType.EFFECT.play();
		});
	}

	@Override
	public void update(long dt) {
		cam.update(dt);
		if(Constants.RUNTIME >= 1000) {
			text.clear().addText("TICKS: ").addText(Constants.RUNTIME + "", Constants.RUNTIME > 1000? new Vector3f(1, 0, 0): (Constants.RUNTIME > 750? new Vector3f(1, 1, 0): new Vector3f(0, 0, 1))).build();
		}
	}

	@Override
	public void render() {
		Matrix4f projection_matrix = new Matrix4f().perspective((float)Math.PI/3, Window.INSTANCE.getWidth() / Window.INSTANCE.getHeight(),0.001f, 1250f);
		Matrix4f view_matrix = new Matrix4f().lookAt(cam.getPosition(), new Vector3f(cam.getLookingDirection()).add(cam.getPosition()), cam.getUp());

		ParticleSpawner.renderAll(projection_matrix, view_matrix);
	}

	@Override
	public void cleanUp() {

	}
}
