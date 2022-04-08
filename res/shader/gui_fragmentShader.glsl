#version 430

layout(location = 200) uniform float borderX;
layout(location = 201) uniform float borderY;
layout(location = 400) uniform vec3 color;

in vec2 fragTexCoord;

out vec4 finalColor;

//import util_shapes

void main() {
    float border = rect(fragTexCoord, vec2(borderX, borderY));

    if(border <= 0.5) discard;

    finalColor = vec4(color, 1);
}