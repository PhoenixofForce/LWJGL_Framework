package meshes;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import utils.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TextureHandler {

	private static final Map<String, Integer> textureMap = new HashMap<>();

	public static int loadImagePng(String textureName, String fileName, Optional<String> path) {
		try {
			BufferedImage img = ImageIO.read(new File(path.orElse(Constants.TEXTURE_PATH) + fileName + ".png"));
			int imgID = loadTexture(img);
			textureMap.put(textureName, imgID);
		} catch (IOException e) {
			throw new RuntimeException("Error parsing image " + fileName);
		}

		return 0;
	}

	public static int loadPixelImagePng(String textureName, String fileName, Optional<String> path) {
		try {
			BufferedImage img = ImageIO.read(new File(path.orElse(Constants.TEXTURE_PATH) + fileName + ".png"));
			int imgID = loadPixelTexture(img);
			textureMap.put(textureName, imgID);
		} catch (IOException e) {
			throw new RuntimeException("Error parsing image " + fileName);
		}

		return 0;
	}

	public static void loadSpriteSheetPNG(String spriteSheetName, String fileName, Optional<String> path) {
		try {
			BufferedImage img = ImageIO.read(new File(path.orElse(Constants.TEXTURE_PATH) + fileName + ".png"));
			Scanner s = new Scanner(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path.orElse(Constants.TEXTURE_PATH) + fileName + ".text")), StandardCharsets.UTF_8);

			int amount = Integer.parseInt(s.nextLine());
			for (int i = 0; i < amount; i++) {
				try {
					String[] line = s.nextLine().split(" ");

					String texture = line[0];
					int x = Integer.parseInt(line[1]);
					int y = Integer.parseInt(line[2]);
					int width = Integer.parseInt(line[3]);
					int height = Integer.parseInt(line[4]);

					BufferedImage sprite = img.getSubimage(x, y, width, height);
					textureMap.put(spriteSheetName + "_" + texture, loadTexture(sprite));
				} catch (Exception e) {
					throw new RuntimeException(String.format("Loading spriteSheet: %s in line %d (%s)", spriteSheetName, i + 2, e.toString()));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error parsing spritesheet " + fileName);
		}
	}

	public static void loadPixelSpriteSheetPNG(String spriteSheetName, String fileName, Optional<String> path) {
		try {
			BufferedImage img = ImageIO.read(new File(path.orElse(Constants.TEXTURE_PATH) + fileName + ".png"));
			Scanner s = new Scanner(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path.orElse(Constants.TEXTURE_PATH) + fileName + ".text")), StandardCharsets.UTF_8);

			int amount = Integer.parseInt(s.nextLine());
			for (int i = 0; i < amount; i++) {
				try {
					String[] line = s.nextLine().split(" ");

					String texture = line[0];
					int x = Integer.parseInt(line[1]);
					int y = Integer.parseInt(line[2]);
					int width = Integer.parseInt(line[3]);
					int height = Integer.parseInt(line[4]);

					BufferedImage sprite = img.getSubimage(x, y, width, height);
					textureMap.put(spriteSheetName + "_" + texture, loadPixelTexture(sprite));
				} catch (Exception e) {
					throw new RuntimeException(String.format("Loading spriteSheet: %s in line %d (%s)", spriteSheetName, i + 2, e.toString()));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error parsing spritesheet " + fileName);
		}
	}

	public static int getTexture(String texture) {
		if(!textureMap.containsKey(texture)) {
			System.err.println("Tried to access unloaded texture " + texture);
		}
		return textureMap.getOrDefault(texture, 0);
	}

	private static int loadTexture(BufferedImage img) {
		return loadTexture(img, GL46.GL_LINEAR_MIPMAP_LINEAR, GL46.GL_LINEAR);
	}

	private static int loadPixelTexture(BufferedImage img) {
		return loadTexture(img, GL46.GL_NEAREST_MIPMAP_LINEAR, GL46.GL_NEAREST);
	}

	private static int loadTexture(BufferedImage i, int scalingType1, int scalingType2) {
		return createImage(i, scalingType1, scalingType2);
	}

	private static int createImage(BufferedImage image, int scalingType1, int scalingType2) {
		int texture = GL46.glGenTextures();
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture);

		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, scalingType1);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, scalingType2);

		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_REPEAT);
		GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_REPEAT);

		GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_SRGB_ALPHA, image.getWidth(), image.getHeight(), 0, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, formatImage(image));
		GL46.glGenerateMipmap(GL46.GL_TEXTURE_2D);
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
		return texture;
	}

	private static ByteBuffer formatImage(BufferedImage image) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[(/*image.getHeight()-1 -*/ y) * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));			// Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));			// Green component
				buffer.put((byte) (pixel & 0xFF));					// Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF));			// Alpha component
			}
		}
		buffer.flip();

		return buffer;
	}

	public static void cleanUp() {
		for(int i: textureMap.values()) {
			GL46.glDeleteTextures(i);
		}
	}

	private TextureHandler() {}
}
