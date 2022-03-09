#version 430


in vec2 fragTexCoord;
in vec3 color;

out vec4 finalColor;

void main() {
    finalColor = vec4(color, 1);
}