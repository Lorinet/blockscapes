package mesh;

public class Mesh {
    private final int vao;
    private final int texture;
    private final int vertexCount;

    public Mesh(int vao, int texture, int vertexCount) {
        this.vao = vao;
        this.texture = texture;
        this.vertexCount = vertexCount;
    }

    public int getVAO() {
        return vao;
    }

    public int getTexture() {
        return texture;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void destroy() {
        GeometryManager.unloadModel(vao);
    }
}
