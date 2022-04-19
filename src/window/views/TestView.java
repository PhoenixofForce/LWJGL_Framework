package window.views;

import assets.audio.AudioPlayer;
import assets.audio.AudioType;
import gameobjects.entities.Camera;
import gameobjects.particles.ParticleSpawner;
import maths.MathUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import utils.Options;
import window.Window;
import window.font.Font;
import window.font.GeneralFont;
import window.font.TextureAtlasFont;
import window.gui.*;

public class TestView implements View {

	private Camera cam;
	private GuiText text;
	private GuiElement healthBar, staminaBar, manaBar, currentMana, crosshair, button, checkbox, selector;
	private GuiSlider slider;

	private int particle;

	public TestView() {
		cam = new Camera();
		this.particle = ParticleSpawner.getNewSpawner(new Vector3f(0, 5, 0), new ParticleSpawner.ParticleType().setDirection(MathUtils.randomVectorAround(new Vector3f(1, 0, 0), 360), 0, new Vector3f(0)));

		healthBar = new BasicColorGuiElement(Anchor.BEGIN, Anchor.BEGIN, 20, 20, 200, 20);
		staminaBar = new BasicColorGuiElement(Anchor.BOTTOM_RIGHT, -20, 20, 200, 20);
		manaBar = new BasicColorGuiElement(Anchor.BOTTOM_CENTER, 0.5f, 20, 200, 20);
		currentMana = new BasicColorGuiElement(Anchor.TOP_LEFT, 0, 1, 0.3f, 20);

		manaBar.addElement(currentMana);

		crosshair = new BasicColorGuiElement(0.5f, 0.5f, 10, 10);

		slider = new GuiSlider(Anchor.BOTTOM_LEFT, 50, 200, 200, 20);
		slider.setValue(Options.musicVolume);
		slider.setChangeListener(v -> Options.musicVolume = v);

		button = new GuiButton(Anchor.CENTERCENTER, 150, 275, 200, 50);
		checkbox = new GuiCheckbox(Anchor.BOTTOM_LEFT, 50, 320, 20, 20);
		selector = new GuiSelector(Anchor.BOTTOM_LEFT, 50, 400, 200, 50);

		Font font1 = new GeneralFont("WhitePeaberryOutline", 2);
		Font font2 = new TextureAtlasFont("Font");

		text = new GuiText(Anchor.TOP_LEFT,  20, -20f, 500, font1, 24f, 50)
				.addText("The quick brown fox jumps over the lazy dog", new Vector3f(1, 0, 0))
				.newLine()
				.addText("\\<test\\> becomes <test>", new Vector3f(0, 1, 0), 0.02f)
				.build();

		Window.INSTANCE.setMouseClickListener((e, b) -> {
			if(e != 2) AudioType.EFFECT.play();
		});
	}

	@Override
	public void init(Window window) {
		window.addElement(healthBar);
		window.addElement(staminaBar);
		window.addElement(manaBar);
		window.addElement(crosshair);
		window.addElement(slider);
		window.addElement(button);
		window.addElement(checkbox);
		window.addElement(selector);
		window.addElement(text);

		ParticleSpawner.unFreeze(particle);
		ParticleSpawner.startSpawning(particle);
		AudioPlayer.playMusic(AudioType.MUSIC);
	}

	@Override
	public void update(long dt) {
		cam.update(dt);
		long runTime = Window.INSTANCE.getRuntime();

		if(runTime >= 1000) {
			text.clear().addText("TICKS: ").addText(runTime + "", runTime > 5000? new Vector3f(1, 0, 0): (runTime > 2500? new Vector3f(1, 1, 0): new Vector3f(0, 0, 1))).build();
		}
	}

	@Override
	public void render() {
		Matrix4f projection_matrix = new Matrix4f().perspective((float)Math.PI/3, Window.INSTANCE.getWidth() / Window.INSTANCE.getHeight(),0.001f, 1250f);
		Matrix4f view_matrix = new Matrix4f().lookAt(cam.getPosition(), new Vector3f(cam.getLookingDirection()).add(cam.getPosition()), cam.getUp());

		ParticleSpawner.renderAll(projection_matrix, view_matrix);
	}

	@Override
	public void remove() {
		ParticleSpawner.freeze(particle);
	}

	@Override
	public void cleanUp() {
		ParticleSpawner.cleanUp(particle);
	}
}
