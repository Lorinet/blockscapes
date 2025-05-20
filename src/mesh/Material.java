package mesh;

import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Objects;

public class Material {
    private String name;
    private Vector3f ambientColor = new Vector3f(0.3f, 0.3f, 0.3f);
    private Vector3f diffuseColor = new Vector3f(0.7f, 0.7f, 0.7f);
    private Vector3f specularColor = new Vector3f(0.0f, 0.0f, 0.0f);
    private Vector3f emissiveColor = new Vector3f(0.0f, 0.0f, 0.0f);
    private float shininess = 0.0f;
    private float dissolve = 1.0f;
    private float opticalDensity = 1.0f;
    private int illuminationModel = 2;

    private String ambientTexturePath;
    private String diffuseTexturePath;
    private String specularTexturePath;
    private String emissiveTexturePath;
    private String normalMapPath;
    private String dissolveTexturePath;

    private int diffuseTextureIndex = -1;
    private Vector2i diffuseTextureSize = new Vector2i(0, 0);

    public Material(String name) {
        this.name = name;
    }

    public Material(String name, String diffuseTexturePath) {
        this.name = name;
        this.diffuseTexturePath = diffuseTexturePath;
    }

    public Material(Material other) {
        this.name = other.name;
        this.ambientColor = new Vector3f(other.ambientColor);
        this.diffuseColor = new Vector3f(other.diffuseColor);
        this.specularColor = new Vector3f(other.specularColor);
        this.emissiveColor = new Vector3f(other.emissiveColor);
        this.shininess = other.shininess;
        this.dissolve = other.dissolve;
        this.opticalDensity = other.opticalDensity;
        this.illuminationModel = other.illuminationModel;
        this.ambientTexturePath = other.ambientTexturePath;
        this.diffuseTexturePath = other.diffuseTexturePath;
        this.specularTexturePath = other.specularTexturePath;
        this.emissiveTexturePath = other.emissiveTexturePath;
        this.normalMapPath = other.normalMapPath;
        this.dissolveTexturePath = other.dissolveTexturePath;
        this.diffuseTextureIndex = other.diffuseTextureIndex;
        this.diffuseTextureSize = new Vector2i(other.diffuseTextureSize);
    }

    public String getName() {
        return name;
    }

    public Vector3f getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Vector3f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public Vector3f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector3f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vector3f getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(Vector3f specularColor) {
        this.specularColor = specularColor;
    }

    public Vector3f getEmissiveColor() {
        return emissiveColor;
    }

    public void setEmissiveColor(Vector3f emissiveColor) {
        this.emissiveColor = emissiveColor;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public float getDissolve() {
        return dissolve;
    }

    public void setDissolve(float dissolve) {
        this.dissolve = dissolve;
    }

    public float getOpticalDensity() {
        return opticalDensity;
    }

    public void setOpticalDensity(float opticalDensity) {
        this.opticalDensity = opticalDensity;
    }

    public int getIlluminationModel() {
        return illuminationModel;
    }

    public void setIlluminationModel(int illuminationModel) {
        this.illuminationModel = illuminationModel;
    }

    public String getAmbientTexturePath() {
        return ambientTexturePath;
    }

    public void setAmbientTexturePath(String ambientTexturePath) {
        this.ambientTexturePath = ambientTexturePath;
    }

    public String getDiffuseTexturePath() {
        return diffuseTexturePath;
    }

    public void setDiffuseTexturePath(String diffuseTexturePath) {
        this.diffuseTexturePath = diffuseTexturePath;
    }

    public String getSpecularTexturePath() {
        return specularTexturePath;
    }

    public void setSpecularTexturePath(String specularTexturePath) {
        this.specularTexturePath = specularTexturePath;
    }

    public String getEmissiveTexturePath() {
        return emissiveTexturePath;
    }

    public void setEmissiveTexturePath(String emissiveTexturePath) {
        this.emissiveTexturePath = emissiveTexturePath;
    }

    public String getNormalMapPath() {
        return normalMapPath;
    }

    public void setNormalMapPath(String normalMapPath) {
        this.normalMapPath = normalMapPath;
    }

    public String getDissolveTexturePath() {
        return dissolveTexturePath;
    }

    public void setDissolveTexturePath(String dissolveTexturePath) {
        this.dissolveTexturePath = dissolveTexturePath;
    }

    public void setDiffuseTextureIndex(int diffuseTextureIndex) {
        this.diffuseTextureIndex = diffuseTextureIndex;
    }

    public int getDiffuseTextureIndex() {
        return diffuseTextureIndex;
    }

    public void setDiffuseTextureSize(Vector2i diffuseTextureSize) {
        this.diffuseTextureSize = new Vector2i(diffuseTextureSize);
    }

    public Vector2i getDiffuseTextureSize() {
        return diffuseTextureSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ambientColor, diffuseColor, specularColor, emissiveColor, shininess, dissolve, opticalDensity, illuminationModel, ambientTexturePath, diffuseTexturePath, specularTexturePath, emissiveTexturePath, normalMapPath, dissolveTexturePath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return Float.compare(material.shininess, shininess) == 0 && Float.compare(material.dissolve, dissolve) == 0 && Float.compare(material.opticalDensity, opticalDensity) == 0 && illuminationModel == material.illuminationModel && Objects.equals(name, material.name) && Objects.equals(ambientColor, material.ambientColor) && Objects.equals(diffuseColor, material.diffuseColor) && Objects.equals(specularColor, material.specularColor) && Objects.equals(emissiveColor, material.emissiveColor) && Objects.equals(ambientTexturePath, material.ambientTexturePath) && Objects.equals(diffuseTexturePath, material.diffuseTexturePath) && Objects.equals(specularTexturePath, material.specularTexturePath) && Objects.equals(emissiveTexturePath, material.emissiveTexturePath) && Objects.equals(normalMapPath, material.normalMapPath) && Objects.equals(dissolveTexturePath, material.dissolveTexturePath);
    }
}
