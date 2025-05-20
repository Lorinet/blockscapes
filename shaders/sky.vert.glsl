#version 400 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform mat4 transformation;
uniform mat4 viewMatrix;
uniform mat4 projection;

void main() {
    pass_textureCoords = textureCoords;
    gl_Position = projection * viewMatrix * transformation * vec4(position, 1.0);
}

