package mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
    private final int vao;
    //private final int texture;
    private FloatBuffer materialsFloatBuffer;
    private IntBuffer materialsIntBuffer;
    private int[] textures;
    private final int vertexCount;

    public Mesh(int vao, Material[] materials, int[] textures, int vertexCount) {
        this.vao = vao;
        if(materials != null) {
            this.materialsFloatBuffer = ModelManager.createMaterialfUniformBuffer(materials);
            this.materialsIntBuffer = ModelManager.createMaterialiUniformBuffer(materials);
        }
        this.textures = textures;
        this.vertexCount = vertexCount;
    }

    public int getVAO() {
        return vao;
    }

    public IntBuffer getMaterialsIntBuffer() {
        return materialsIntBuffer;
    }

    public FloatBuffer getMaterialsFloatBuffer() {
        return materialsFloatBuffer;
    }

    public int[] getTextures() {
        return textures;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void destroy() {
        ModelManager.unloadModel(vao);
    }
}
