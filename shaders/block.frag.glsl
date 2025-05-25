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
    int emissiveTextureID;
    int textureWidth;
    int textureHeight;
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
uniform int fancyTransparency;
uniform int renderPass;

uniform int hurting;

uniform sampler2D shadowMap;
uniform mat4 lightPerspectiveMatrix;

out vec4 out_Color;

int calculateShadow() {
    vec4 posInLightEyes = lightPerspectiveMatrix * vec4(pass_position, 1.0);
    vec3 projCoords = posInLightEyes.xyz / posInLightEyes.w;
    projCoords = (projCoords + 1.0) / 2.0;
    if(projCoords.y > 1.0) {
        return 1;
    }

    float closestDepth = texture(shadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;
    float bias = 0.005;
    int shadow = 0;
    if (projCoords.z > 1.0) {
        shadow = 1;
    }
    else {
        shadow = currentDepth - bias > closestDepth ? 1 : 0;
    }
    return shadow;
}

void main() {
    Material material = materials[pass_materialIndex];
    vec2 texCoords = vec2(pass_textureCoords.x * material.textureWidth / 4096, pass_textureCoords.y * material.textureHeight / 4096);
    vec4 diffuseTextureColor = texture(textures, vec3(texCoords, material.diffuseTextureID));
    vec4 emissive = (material.emissiveTextureID > -1 ? texture(textures, vec3(texCoords, material.emissiveTextureID)) : material.emissiveColor);

    float solidThreshold = (fancyTransparency < 1) ? 0.3 : (1.0 - 0.001);

    if (renderPass == 0 && diffuseTextureColor.a < solidThreshold) {
        discard;
    } else if (renderPass == 1 && diffuseTextureColor.a >= 1.0 - 0.001) {
        discard;
    }

    vec3 norm = normalize(pass_normals);
    vec3 viewDir = normalize(viewPos - pass_position);


    vec3 diffuseBase = diffuseTextureColor.rgb * material.diffuseColor.rgb;
    vec3 ambientBase = diffuseTextureColor.rgb * material.ambientColor.rgb;

    vec3 diffuse = vec3(0);
    vec3 ambient = ambientBase;

    vec3 specular = vec3(0);
    bool generateSpicules = any(greaterThan(material.specularColor.rgb, vec3(0.0)));

    for (int i = 0; i < MAX_DIRECTIONAL_LIGHTS; i++) {
        DirectionalLight light = directionalLights[i];
        float lightNormal = max(dot(norm, light.direction.xyz), 0.0);
        diffuse += diffuseBase * lightNormal * light.color.rgb;
        if (generateSpicules) {
            vec3 halfwayDir = normalize(light.direction.xyz + viewDir);
            float spec = pow(max(dot(norm, halfwayDir), 0.0), material.shininess / 2);
            specular += light.color.rgb * material.specularColor.rgb * spec;
        }
    }

    int shadowing = calculateShadow();
    float surfaceSunShadowing = clamp(shadowing * length(directionalLights[0].color.rgb), 0.0, 1.0);
    vec3 blinPhong = (1.0 - surfaceSunShadowing) * (diffuse + specular);
    if (shadowing > 0) {
        ambient *= pass_shading;
        //blinPhong = vec3(0);
    }
    vec4 result = vec4(emissive.rgb + ambient + blinPhong, diffuseTextureColor.a);
    if(hurting == 1) {
        result.r = clamp(result.r + 0.5, 0.0, 1.0);
        result.g = clamp(result.g + 0.2, 0.0, 1.0);
        result.b = clamp(result.b + 0.2, 0.0, 1.0);
        result.a = clamp(result.a + 0.3, 0.0, 1.0);
    }
    out_Color = result;

}