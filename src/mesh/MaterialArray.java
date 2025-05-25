package mesh;

public class MaterialArray extends UBO {
    public MaterialArray(Material[] materials) {
        super(96, materials.length);

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
            putInt(material.getEmissiveTextureIndex());
            putInt(material.getTextureSize().x);
            putInt(material.getTextureSize().y);
            putDummy();
            putDummy();
            putDummy();
        }

        bufferData();
    }


}