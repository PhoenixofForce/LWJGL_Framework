vec2 pixelate(vec2 uv, float pixels) {
	return floor(uv * pixels) / pixels;
}