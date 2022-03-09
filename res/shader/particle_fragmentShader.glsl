#version 430


in vec2 fragTexCoord;
layout(location = 400) uniform vec3 color;


out vec4 finalColor;

void main() {
    finalColor = vec4(color, 1);
}