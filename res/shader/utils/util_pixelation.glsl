vec2 pixelate(vec2 uv, float pixels) {
	return floor(uv * pixels) / pixels + vec2(1 / (2 * pixels));
}

vec2 pixelate(vec2 uv, vec2 pixels) {
	float x = floor(uv.x * pixels.x) / pixels.x + 1 / (2 * pixels.x);
	float y = floor(uv.y * pixels.y) / pixels.y + 1 / (2 * pixels.y);

	return vec2(x, y);
}