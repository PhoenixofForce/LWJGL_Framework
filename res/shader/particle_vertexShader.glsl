#version 430

layout(location = 0) uniform mat4 projection;
layout(location = 1) uniform mat4 view;

layout(location = 0) in vec4 cPosition;
layout(location = 1) in vec2 cTexCoord;
layout(location = 2) in vec3 vNormal;
layout(location = 3) in vec3 pos;
layout(location = 4) in float size;
layout(location = 5) in vec3 inColor;

out vec3 color;
out vec2 fragTexCoord;

void main()
{
    mat4 transformation = mat4(size, 0, 0, 0,
                                0, size, 0, 0,
                                0, 0, size, 0,
                                pos.x, pos.y, pos.z, 1);

    mat4 modelView = view * transformation;
    //resetting rotation to make it face the camera
    modelView[0][0] = size;
    modelView[0][1] = 0;
    modelView[0][2] = 0;

    modelView[1][0] = 0;
    modelView[1][1] = size;
    modelView[1][2] = 0;

    modelView[2][0] = 0;
    modelView[2][1] = 0;
    modelView[2][2] = size;

    gl_Position = projection * modelView * cPosition;
    fragTexCoord = cTexCoord;
    color = inColor;
}