#version 430

layout(location = 40) uniform vec3 color;


in vec2 fragTexCoord;

out vec4 finalColor;

void main() {
    finalColor = vec4(color, 1);
}