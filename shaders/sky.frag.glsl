#version 400 core
//uniform vec3 celestialColor;
uniform sampler2D sunTexture;

in vec2 pass_textureCoords;
out vec4 out_Color;

void main() {
    out_Color = /*celestialColor*/texture(sunTexture, pass_textureCoords);
}
