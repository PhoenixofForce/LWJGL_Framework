#version 430

in vec3 color;
in vec2 fragTexCoord;
in float charID;
in vec4 textBoxSize;
in vec4 characterPosition;

out vec4 finalColor;

layout(location = 100) uniform sampler2D atlas;
layout(location = 209) uniform float maxChars;
layout(location = 210) uniform float writerProgess;

void main() {

    //if(gl_FragCoord.x < textBoxSize.x || gl_FragCoord.x >= textBoxSize.x + textBoxSize.z ||                           //pixel perfect
    //   gl_FragCoord.y < textBoxSize.y || gl_FragCoord.y >= textBoxSize.y + textBoxSize.w) discard;                    //TODO: this is the basic concept of cutting text into the right size

    //if(characterPosition.x + characterPosition.z < textBoxSize.x || characterPosition.x >= textBoxSize.x + textBoxSize.z ||     //render whole char if character is in the box
    //   characterPosition.y < textBoxSize.y - textBoxSize.w / 2 || characterPosition.y >= textBoxSize.y + textBoxSize.w) discard;    //could be improved

    vec4 texColor = texture(atlas, fragTexCoord);
    if(texColor.a < 1.0 || writerProgess * maxChars < charID) discard;

    finalColor = vec4(texColor.rgb * color, 1);
}