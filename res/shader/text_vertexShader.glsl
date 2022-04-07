#version 430

//layout(location = 0) uniform mat4 trans;

layout(location = 0) in vec4 cPosition;
layout(location = 1) in vec2 cTexCoord;
layout(location = 2) in vec4 posOffset;
layout(location = 3) in vec4 spriteSheetBounds;
layout(location = 4) in vec3 inColor;
layout(location = 5) in float charIndex;
layout(location = 6) in float wobbleStrength;

layout(location = 100) uniform sampler2D atlas;
layout(location = 200) uniform float translationX;
layout(location = 201) uniform float translationY;
layout(location = 202) uniform float windowWidth;
layout(location = 203) uniform float windowHeight;
layout(location = 204) uniform float ticks;
layout(location = 205) uniform float maxChars;
layout(location = 206) uniform float writerProgess;

out vec3 color;
out vec2 fragTexCoord;
out float charID;

vec2 random2( vec2 p ) {
    return fract(sin(vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))))*43758.5453);
}

void main()
{

    vec2 shakey = random2((spriteSheetBounds.xy + vec2(1)) * ticks * charIndex) * wobbleStrength;
    shakey = vec2(shakey.x, shakey.y * 1.4);

    float wobble = sin((ticks + charIndex * 2) * 0.1f) * wobbleStrength;

    vec2 offset = vec2(posOffset.x / windowWidth, posOffset.y / windowHeight);

    mat4 transformation = mat4(
        posOffset.z / windowWidth, 0, 0, 0,
        0, posOffset.w / windowHeight, 0, 0,
        0, 0, 1, 0,
        offset.x + translationX, offset.y + translationY, 0, 1
    );


    gl_Position = transformation * cPosition + vec4(0, 0 + wobble, 0, 0);

    vec2 atlasSize = textureSize(atlas, 0);
    vec2 range = spriteSheetBounds.zw;
    vec2 texPos = (vec2(cTexCoord.x, 1 - cTexCoord.y) * range + spriteSheetBounds.xy);
    texPos = vec2(texPos.x / atlasSize.x, texPos.y / atlasSize.y);


    fragTexCoord = texPos;

    charID = charIndex;
    color = inColor;
}