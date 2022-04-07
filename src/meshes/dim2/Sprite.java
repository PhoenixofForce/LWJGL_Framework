package meshes.dim2;

import maths.Easing;
import meshes.loader.TextureHandler;
import utils.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sprite {
	private final List<String> textures;
	private final int time;								//Time (in ms) before changing sprite
	private final Easing easing;

	public Sprite(int time, String... textureNames) {
		this(Easing::linear, time, textureNames);
	}

	public Sprite(Easing easing, int time, String... textureNames) {
		this.easing = easing;
		this.textures = new ArrayList<>();
		this.time = time;

		//Adding texture coordinates to list
		textures.addAll(Arrays.asList(textureNames));
	}

	public Sprite(String texture) {
		this.easing = Easing::linear;
		this.textures = new ArrayList<>();
		this.time = 1;

		//Adding texture coordinates to list
		textures.add(texture);
	}

	public Sprite(int time, String texture, int c) {
		this(Easing::linear, time, texture, c);
	}

	public Sprite(Easing easing, int time, String texture, int c) {
		this.easing = easing;
		this.textures = new ArrayList<>();
		this.time = time;

		//Adding texture coordinates to list
		for(int i = 0; i < c; i++) {
			textures.add(texture + "_" + i);
		}
	}

	/**
	 * Gets current sprite of the animation
	 *
	 * @param startTime   time when the animation started (in ms)
	 * @param currentTime the real time when the the texture is needed (in ms)
	 * @return coordinates from current sprite
	 */
	public String getTexture(long startTime, long currentTime) {
		long timeRuning = currentTime - startTime;
		long totalAnimationTime = (long) time * textures.size();

		double timePercentile = (1.0 * timeRuning) / (1.0 * totalAnimationTime) % 1.0;
		double indexPercentile = easing.ease(timePercentile) % 1.0;

		int index = (int) Math.floor(indexPercentile * textures.size());

		return textures.get(index);
	}

	public String getTexture(long startTime) {
		return getTexture(startTime, TimeUtils.getTime());
	}

	public String getTexture() {
		return getTexture(0);
	}

	@Override
	public boolean equals(Object b) {
		if (b instanceof Sprite s) {
			if (s.time != time) return false;
			if (textures.size() != s.textures.size()) return false;

			for (String r : textures) {
				boolean has = false;
				for (String r2 : s.textures) {
					if (r2.equals(r)) {
						has = true;
						break;
					}
				}
				if (!has) return false;
			}
			return true;
		}
		return false;
	}

	public int getTotalTime() {
		return time * textures.size();
	}
}
