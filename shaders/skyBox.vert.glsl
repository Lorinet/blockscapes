#version 400 core
layout (location = 0) in vec3 vertex;

out vec3 pass_texCoords;

uniform mat4 viewMatrix;
uniform mat4 projection;
uniform mat4 rotation;

void main()
{
    mat4 viewWithoutTranslation = mat4(mat3(viewMatrix));
    pass_texCoords = vertex;
    gl_Position = projection * viewWithoutTranslation * rotation * vec4(vertex, 1.0);
}