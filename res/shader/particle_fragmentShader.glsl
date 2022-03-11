#version 430

in vec3 color;
in vec2 fragTexCoord;

out vec4 finalColor;

void main() {

    float dis = abs(fragTexCoord.x - 0.5) + abs(fragTexCoord.y - 0.5);

    finalColor = vec4(color, 1);
}