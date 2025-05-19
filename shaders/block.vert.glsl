#version 400 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 normals;
layout (location = 3) in float shading;
layout (location = 4) in int materialIndex;

out vec2 pass_textureCoords;
out vec3 pass_normals;
out float pass_shading;
flat out int pass_materialIndex;

uniform mat4 transformation;
uniform mat4 projection;
uniform mat4 viewMatrix;

void main() {
  gl_Position = projection * viewMatrix * transformation * vec4(position, 1.0);
  pass_textureCoords = textureCoords;
  pass_shading = shading;
  pass_materialIndex = materialIndex;
}