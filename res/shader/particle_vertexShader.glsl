#version 430

layout(location = 0) uniform mat4 cProjectionMatrix;
layout(location = 1) uniform mat4 cModelViewMatrix;

layout(location = 200) uniform float sizes[10];
layout(location = 400) uniform vec3 colorsAndPositions[20];

layout(location = 0) in vec4 cPosition;
layout(location = 1) in vec2 cTexCoord;
layout(location = 2) in vec3 vNormal;

out vec2 fragTexCoord;
out vec3 color;

void main()
{
    float size = sizes[gl_InstanceID];
    mat4 transformation = mat4(size, 0, 0, 0, 0, size, 0, 0, 0, 0, size, 0, 0, 0, 0, 1);
    gl_Position = cProjectionMatrix * cModelViewMatrix * (cPosition + vec4(colorsAndPositions[10+gl_InstanceID], 0));
    fragTexCoord = cTexCoord;

    color = colorsAndPositions[gl_InstanceID];
}