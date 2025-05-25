#version 400 core

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D textureSampler;
void main() {
    //  out_Color = vec4(1, 1, 1, 1);
    float depthValue = texture(textureSampler, pass_textureCoords).r;
    float color = clamp(depthValue, 0.0, 1.0);
    out_Color = vec4(color, color, color, 1.0);
}