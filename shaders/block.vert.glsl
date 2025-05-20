#version 400 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 normals;
layout (location = 3) in float shading;
layout (location = 4) in int materialIndex;

out vec2 pass_textureCoords;
out vec3 pass_normals;
out vec3 pass_position;
out float pass_shading;
flat out int pass_materialIndex;

uniform mat4 transformation;
uniform mat4 projection;
uniform mat4 viewMatrix;

void main() {
  pass_textureCoords = textureCoords;
  pass_shading = shading;
  pass_materialIndex = materialIndex;
  pass_position = vec3(transformation * vec4(position, 1.0));
  pass_normals = mat3(transpose(inverse(transformation))) * normals;
  gl_Position = projection * viewMatrix * vec4(pass_position, 1.0);
}