#version 430

in vec3 color;
in vec2 fragTexCoord;
in float charID;
in vec4 boxSize;

out vec4 finalColor;

layout(location = 100) uniform sampler2D atlas;
layout(location = 207) uniform float maxChars;
layout(location = 208) uniform float writerProgess;

void main() {

    //if(gl_FragCoord.x < boxSize.x || gl_FragCoord.x >= boxSize.x + boxSize.z ||
    //   gl_FragCoord.y < boxSize.y - boxSize.w / 2 || gl_FragCoord.y >= boxSize.y + boxSize.w) discard;    //TODO: this is the basic concept of cutting text into the right size
    vec4 texColor = texture(atlas, fragTexCoord);
    if(texColor.a < 1.0 || writerProgess * maxChars < charID) discard;

    finalColor = vec4(texColor.rgb * color, 1);
}