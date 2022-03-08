#version 430

layout(location = 0) uniform mat4 cProjectionMatrix;
layout(location = 1) uniform mat4 cModelViewMatrix;
layout(location = 2) uniform mat4 tranformation;

layout(location = 0) in vec4 cPosition;
layout(location = 1) in vec2 cTexCoord;
layout(location = 2) in vec3 vNormal;

out vec2 fragTexCoord;


void main()
{
    gl_Position = cProjectionMatrix * cModelViewMatrix * tranformation * cPosition;
    fragTexCoord = cTexCoord;
}