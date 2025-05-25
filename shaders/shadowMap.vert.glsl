#version 400 core

layout (location = 0) in vec3 vertex;

uniform mat4 transformationMatrix;
uniform mat4 lightPerspectiveTransformationMatrix;

void main() {
    gl_Position = lightPerspectiveTransformationMatrix * transformationMatrix * vec4(vertex, 1.0);
}
