#version 430

in vec3 color;
in vec2 fragTexCoord;

out vec4 finalColor;

//import util_pixelation
//import util_shapes

void main() {
    float keep = circle(pixelate(fragTexCoord, 16), 0f, 0.5f);
    if(keep == 0) discard;

    finalColor = vec4(color, 1);
}