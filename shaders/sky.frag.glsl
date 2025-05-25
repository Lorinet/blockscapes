#version 400 core
uniform sampler2D sunTexture;

in vec2 pass_textureCoords;
out vec4 out_Color;

void main() {
    out_Color = texture(sunTexture, pass_textureCoords);
}
