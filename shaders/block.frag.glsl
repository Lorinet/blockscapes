#version 400 core
#define MAX_MATERIALS 16
#define MAX_DIRECTIONAL_LIGHTS 2

in vec2 pass_textureCoords;
in vec3 pass_normals;
in vec3 pass_position;
in float pass_shading;
flat in int pass_materialIndex;

struct Material {
    vec4 ambientColor;
    vec4 diffuseColor;
    vec4 specularColor;
    vec4 emissiveColor;
    float shininess;
    int diffuseTextureID;
    int diffuseTextureWidth;
    int diffuseTextureHeight;
};

struct DirectionalLight {
    vec4 color;
    vec4 direction;
};

layout(std140) uniform MaterialBlock {
    Material materials[16];
};

layout(std140) uniform DirectionalLightBlock {
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
};

uniform sampler2DArray textures;
uniform vec3 viewPos;

out vec4 out_Color;


void main() {
    Material material = materials[pass_materialIndex];
    float texCoord_x = pass_textureCoords.x * material.diffuseTextureWidth / 4096;
    float texCoord_y = pass_textureCoords.y * material.diffuseTextureHeight / 4096;

    vec3 norm = normalize(pass_normals);
    vec3 viewDir = normalize(viewPos - pass_position);

    vec3 emissive = material.emissiveColor.rgb;
    vec4 textureColor = texture(textures, vec3(vec2(texCoord_x, texCoord_y), material.diffuseTextureID));


    vec3 diffuseBase = textureColor.rgb * material.diffuseColor.rgb;
    vec3 ambientBase = textureColor.rgb * material.ambientColor.rgb;

    vec3 diffuse = vec3(0);
    vec3 ambient = ambientBase;

    vec3 specular = vec3(0);
    bool generateSpicules = any(greaterThan(material.specularColor.rgb, vec3(0.0)));

    DirectionalLight dirLight;
    dirLight.direction = vec4(0.0, 1.0, 0.0, 1.0);
    dirLight.color = vec4(1.0, 1.0, 1.0, 1.0);

    for (int i = 0; i < MAX_DIRECTIONAL_LIGHTS; i++) {
        DirectionalLight light = directionalLights[i];
        //DirectionalLight light = dirLight;
        float lightNormal = max(dot(norm, light.direction.xyz), 0.0);
        diffuse += diffuseBase * lightNormal * light.color.rgb;
        //ambient = ambient * light.color.rgb;

        if (generateSpicules) {
            vec3 halfwayDir = normalize(light.direction.xyz + viewDir);
            float spec = pow(max(dot(norm, halfwayDir), 0.0), material.shininess / 2);
            specular += light.color.rgb * material.specularColor.rgb * spec;
        }
    }


    vec3 result = ambient + diffuse + specular + emissive;

    out_Color = vec4(result/* * pass_shading*/, 1.0);
    //out_Color = vec4(norm, 1.0);
    if (textureColor.a < 0.05) {
        discard;
    }
}