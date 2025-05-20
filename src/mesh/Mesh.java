package mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
    private final int vao;
    private MaterialArray materialArray;
    private int[] textures;
    private final int vertexCount;
    private final ModelData modelData;

    public Mesh(int vao, Material[] materials, int[] textures, int vertexCount, ModelData modelData) {
        this.vao = vao;
        if(materials != null) {
            this.materialArray = new MaterialArray(materials);
        }
        this.textures = textures;
        this.vertexCount = vertexCount;
        this.modelData = modelData;
    }

    public int getVAO() {
        return vao;
    }

    public MaterialArray getMaterialArray() {
        return materialArray;
    }

    public int[] getTextures() {
        return textures;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public ModelData getModelData() {
        return modelData;
    }

    public void destroy() {
        ModelManager.unloadModel(vao);
    }
}
