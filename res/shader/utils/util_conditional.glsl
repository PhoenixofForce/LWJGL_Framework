float when_le(float x, float y) {
    return 1.0 - max(sign(x - y), 0.0);
}

float when_gt(float x, float y) {
    return max(sign(x - y), 0.0);
}

float when_eq(float x, float y) {
    return 1.0 - abs(sign(x - y));
}