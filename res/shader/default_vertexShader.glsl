#version 430

layout(location = 0) uniform mat4 projection;
layout(location = 1) uniform mat4 view;
layout(location = 2) uniform mat4 transformation;

layout(location = 0) in vec4 cPosition;
layout(location = 1) in vec2 cTexCoord;
layout(location = 2) in vec3 vNormal;

out vec2 fragTexCoord;


void main()
{
    gl_Position = projection * view * transformation * cPosition;
    fragTexCoord = cTexCoord;
}