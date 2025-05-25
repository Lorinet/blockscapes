#version 400 core
out vec4 out_Color;

in vec3 pass_texCoords;

uniform vec3 sunlightColor;
uniform samplerCube textureSampler;

void main()
{
    out_Color = (1.0 - clamp(length(sunlightColor), 0.0, 1.0)) * texture(textureSampler, pass_texCoords);
    //out_Color = vec4(0.0, 1.0, 0.0, 1.0)
}