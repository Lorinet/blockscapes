package mesh;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.*;


public class ModelData {
    private List<Material> materials;
    private FloatArrayList vertexes;
    private FloatArrayList textureCoords;
    private FloatArrayList normals;
    private FloatArrayList shading;
    private IntArrayList materialIndices;
    private IntArrayList indices;
    private boolean hasTransparency;

    public ModelData(FloatArrayList vertexes, FloatArrayList textureCoords, FloatArrayList normals, IntArrayList materialIndices, IntArrayList indices, List<Material> materials, boolean hasTransparency) {
        this.vertexes = vertexes;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.materialIndices = materialIndices;
        this.indices = indices;
        this.materials = materials;
        this.shading = new FloatArrayList(vertexes.size() / 3);
        for(int i = 0; i < vertexes.size() / 3; i++) {
            this.shading.add(1.0f);
        }
        this.hasTransparency = hasTransparency;
    }

    public ModelData(FloatArrayList vertexes, FloatArrayList textureCoords, FloatArrayList normals, FloatArrayList shading, IntArrayList materialIndices, IntArrayList indices, List<Material> materials, boolean hasTransparency) {
        this.vertexes = vertexes;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.materialIndices = materialIndices;
        this.indices = indices;
        this.materials = materials;
        this.shading = shading;
        this.hasTransparency = hasTransparency;
    }

    public ModelData(ModelData other) {
        this.vertexes = new FloatArrayList(other.vertexes);
        this.textureCoords = new FloatArrayList(other.textureCoords);
        this.normals = new FloatArrayList(other.normals);
        this.materialIndices = new IntArrayList(other.materialIndices);
        this.indices = new IntArrayList(other.indices);
        this.materials = new ArrayList<>(other.materials);
        this.shading = new FloatArrayList(other.shading);
        this.hasTransparency = other.hasTransparency;
    }

    public FloatArrayList getVertexes() {
        return vertexes;
    }

    public FloatArrayList getTextureCoords() {
        return textureCoords;
    }

    public FloatArrayList getNormals() {
        return normals;
    }

    public FloatArrayList getShading() {
        return shading;
    }

    public IntArrayList getIndices() {
        return indices;
    }

    public IntArrayList getMaterialIndices() {
        return materialIndices;
    }

    public void setMaterialIndices(IntArrayList materialIndices) {
        this.materialIndices = materialIndices;
    }

    public boolean getHasTransparency() {
        return hasTransparency;
    }

    public List<Material> getMaterials() {
        return materials;
    }
}
