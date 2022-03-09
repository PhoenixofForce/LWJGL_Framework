#version 430

layout(location = 0) uniform mat4 cProjectionMatrix;
layout(location = 1) uniform mat4 cModelViewMatrix;

layout(location = 200) uniform float sizes;
layout(location = 401) uniform vec3 pos;

layout(location = 0) in vec4 cPosition;
layout(location = 1) in vec2 cTexCoord;
layout(location = 2) in vec3 vNormal;

out vec2 fragTexCoord;

void main()
{
    float size = sizes;
    mat4 transformation = mat4(size, 0, 0, 0,
                                0, size, 0, 0,
                                0, 0, size, 0,
                                pos.x, pos.y, pos.z, 1);
    gl_Position = cProjectionMatrix * cModelViewMatrix * transformation * cPosition;
    fragTexCoord = cTexCoord;
}