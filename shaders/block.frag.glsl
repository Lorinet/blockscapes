#version 400 core

in vec2 pass_textureCoords;
in vec3 pass_normals;
in float pass_shading;
flat in int pass_materialIndex;

struct Materiali {
    int diffuseTextureID;
};

struct Materialf {
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
    float shininess;
};

struct Material {
    Materiali mati;
    Materialf matf;
};

uniform Materiali materialis[16];
uniform Materialf materialfs[16];
uniform sampler2D textures[16];

out vec4 out_Color;

Material selectMaterial() {
    Materiali mati;
    Materialf matf;
    switch (pass_materialIndex) {
        case 0:
        mati = materialis[0];
        matf = materialfs[0];
        break;
        case 1:
        mati = materialis[1];
        matf = materialfs[1];
        break;
        case 2:
        mati = materialis[2];
        matf = materialfs[2];
        break;
        case 3:
        mati = materialis[3];
        matf = materialfs[3];
        break;
        case 4:
        mati = materialis[4];
        matf = materialfs[4];
        break;
        case 5:
        mati = materialis[5];
        matf = materialfs[5];
        break;
        case 6:
        mati = materialis[6];
        matf = materialfs[6];
        break;
        case 7:
        mati = materialis[7];
        matf = materialfs[7];
        break;
        case 8:
        mati = materialis[8];
        matf = materialfs[8];
        break;
        case 9:
        mati = materialis[9];
        matf = materialfs[9];
        break;
        case 10:
        mati = materialis[10];
        matf = materialfs[10];
        break;
        case 11:
        mati = materialis[11];
        matf = materialfs[11];
        break;
        case 12:
        mati = materialis[12];
        matf = materialfs[12];
        break;
        case 13:
        mati = materialis[13];
        matf = materialfs[13];
        break;
        case 14:
        mati = materialis[14];
        matf = materialfs[14];
        break;
        case 15:
        mati = materialis[15];
        matf = materialfs[15];
        break;
    }
    Material mat;
    mat.mati = mati;
    mat.matf = matf;
    return mat;
}

void main() {
    //Material mat = selectMaterial();
    vec4 diffuseColor = vec4(1.0, 0.0, 0.0, 1.0);
    //if (mat.mati.diffuseTextureID == 0) {
        diffuseColor = texture(textures[0], pass_textureCoords);
    //}
    out_Color = diffuseColor * pass_shading;
    if (diffuseColor.a == 0) {
        discard;
    }
}