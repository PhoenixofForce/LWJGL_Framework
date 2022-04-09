package window.font;

import assets.TextureHandler;
import assets.textures.TextureAtlas;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GeneralFont extends Font {

	private TextureAtlas atlas;
	private Map<String, Vector3i> offsetsAndAdvance;

	public GeneralFont(String name, float sizeScale) {
		super(sizeScale);
		String filePath = "res/fonts/" + name + "/";
		this.offsetsAndAdvance = new HashMap<>();

		List<TextureHandler.SingleTexture> textureList = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath + name + ".txt"));

			String line = reader.readLine();

			while(!line.startsWith("char id")) {
				line = reader.readLine();
			}

			while(line.startsWith("char id")) {
				String[] parts = line.split("\s+");


				String charName = parts[11].substring(8);
				charName = charName.substring(0, charName.length() - 1);

				if(charName.equals("space")) charName = " ";

				int x = Integer.parseInt(parts[2].split("=")[1]);
				int y = Integer.parseInt(parts[3].split("=")[1]);
				int width = Integer.parseInt(parts[4].split("=")[1]);
				int height = Integer.parseInt(parts[5].split("=")[1]);
				height = Math.max(1, height);

				int xOffset = Integer.parseInt(parts[6].split("=")[1]);
				int yOffset = Integer.parseInt(parts[7].split("=")[1]);
				int advance = Integer.parseInt(parts[8].split("=")[1]);

				textureList.add(new TextureHandler.SingleTexture(charName, new Vector4f(x, y, width, height)));
				offsetsAndAdvance.put(charName, new Vector3i(xOffset, yOffset, advance));

				line = reader.readLine();
			}

			atlas = TextureHandler.loadPixelTextureAtlasPNG(name, name, Optional.of(filePath), textureList);

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getAtlas() {
		return atlas.getTexture();
	}

	@Override
	public Vector4f getBounds(String c) {
		return atlas.getTextureBounds(c + "");
	}

	@Override
	public int getXoffset(String c) {
		return offsetsAndAdvance.getOrDefault(c + "", new Vector3i()).x;
	}

	@Override
	public int getYoffset(String c) {
		return offsetsAndAdvance.getOrDefault(c + "", new Vector3i()).y;
	}

	@Override
	public int getAdvance(String c) {
		return offsetsAndAdvance.getOrDefault(c + "", new Vector3i()).z;
	}

	@Override
	public boolean hasCharacter(String c) {
		return atlas.hasTexture(c + "");
	}
}
