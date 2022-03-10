#version 430

in vec3 color;
in vec2 fragTexCoord;

out vec4 finalColor;

void main() {
    finalColor = vec4(color, 1);
}