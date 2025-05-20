package mesh;

public class MaterialArray extends UBO {
    public MaterialArray(Material[] materials) {
        super(80, materials.length);

        if (materials.length > 16) {
            throw new RuntimeException("Too many materials!");
        }
        bind();

        for (Material material : materials) {
            putVec4(material.getAmbientColor());
            putVec4(material.getDiffuseColor());
            putVec4(material.getSpecularColor());
            putVec4(material.getEmissiveColor());
            putFloat(material.getShininess());
            putInt(material.getDiffuseTextureIndex());
            putInt(material.getDiffuseTextureSize().x);
            putInt(material.getDiffuseTextureSize().y);
        }

        bufferData();
    }


}