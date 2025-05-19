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

    public ModelData(FloatArrayList vertexes, FloatArrayList textureCoords, FloatArrayList normals, IntArrayList materialIndices, IntArrayList indices, List<Material> materials) {
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
    }

    public ModelData(FloatArrayList vertexes, FloatArrayList textureCoords, FloatArrayList normals, FloatArrayList shading, IntArrayList materialIndices, IntArrayList indices, List<Material> materials) {
        this.vertexes = vertexes;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.materialIndices = materialIndices;
        this.indices = indices;
        this.materials = materials;
        this.shading = shading;
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

    public Collection<Material> getMaterials() {
        return materials;
    }
}
