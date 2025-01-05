package mesh;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class FaceVertex {

    public static int TEXTURE_SIZE = 8;
    public static int TEXTURES_SIZE = 4096;
    public static int TEXTURES_PER_AXIS = TEXTURES_SIZE / TEXTURE_SIZE;
    public static float TEXTURE_UV_UNIT = 1.0f / (float) TEXTURES_PER_AXIS;

    public Vector3f[] vertexes;
    public Vector2f[] texCoords;
    public float[] shading;
    public int[] indices;

    public FaceVertex(Vector3f[] vertexes, Vector2f[] texCoords, int[] indices, float[] shading) {
        this.vertexes = vertexes;
        this.texCoords = texCoords;
        this.indices = indices;
        this.shading = shading;
    }

    public FaceVertex(FaceVertex other) {
        this.vertexes = new Vector3f[other.vertexes.length];
        for (int i = 0; i < this.vertexes.length; i++) {
            this.vertexes[i] = new Vector3f(other.vertexes[i]);
        }
        this.texCoords = other.texCoords.clone();
        this.indices = other.indices.clone();
        this.shading = other.shading.clone();
    }

    private static Vector2f[] getTextureCoords(int index) {
        int y = index / TEXTURES_PER_AXIS;
        int x = index % TEXTURES_PER_AXIS;
        Vector2f uv = new Vector2f((float) x * TEXTURE_UV_UNIT, (float) y * TEXTURE_UV_UNIT);
        return new Vector2f[]{new Vector2f(uv.x, uv.y + TEXTURE_UV_UNIT), new Vector2f(uv.x + TEXTURE_UV_UNIT, uv.y + TEXTURE_UV_UNIT), new Vector2f(uv.x + TEXTURE_UV_UNIT, uv.y), new Vector2f(uv.x, uv.y),};
    }

    public static FaceVertex down(int index) {
        return new FaceVertex(new Vector3f[]{new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(-0.5f, -0.5f, 0.5f), new Vector3f(0.5f, -0.5f, 0.5f), new Vector3f(0.5f, -0.5f, -0.5f),}, getTextureCoords(index), new int[]{3, 2, 0, 2, 1, 0}, new float[]{0.4f, 0.4f, 0.4f, 0.4f});
    }

    public static FaceVertex right(int index) {
        return new FaceVertex(new Vector3f[]{new Vector3f(0.5f, -0.5f, -0.5f), new Vector3f(0.5f, -0.5f, 0.5f), new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.5f, 0.5f, -0.5f),}, getTextureCoords(index), new int[]{0, 2, 1, 0, 3, 2,}, new float[]{0.6f, 0.6f, 0.6f, 0.6f});
    }

    public static FaceVertex left(int index) {
        return new FaceVertex(new Vector3f[]{new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(-0.5f, -0.5f, 0.5f), new Vector3f(-0.5f, 0.5f, 0.5f), new Vector3f(-0.5f, 0.5f, -0.5f),}, getTextureCoords(index), new int[]{0, 1, 2, 0, 2, 3,}, new float[]{0.6f, 0.6f, 0.6f, 0.6f});
    }

    public static FaceVertex up(int index) {
        return new FaceVertex(new Vector3f[]{new Vector3f(0.5f, 0.5f, -0.5f), new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(-0.5f, 0.5f, 0.5f), new Vector3f(-0.5f, 0.5f, -0.5f),}, getTextureCoords(index), new int[]{3, 2, 0, 2, 1, 0}, new float[]{1, 1, 1, 1});
    }

    public static FaceVertex forward(int index) {
        return new FaceVertex(new Vector3f[]{new Vector3f(0.5f, -0.5f, 0.5f), new Vector3f(-0.5f, -0.5f, 0.5f), new Vector3f(-0.5f, 0.5f, 0.5f), new Vector3f(0.5f, 0.5f, 0.5f),}, getTextureCoords(index), new int[]{0, 2, 1, 0, 3, 2,}, new float[]{0.8f, 0.8f, 0.8f, 0.8f});
    }

    public static FaceVertex back(int index) {
        return new FaceVertex(new Vector3f[]{new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0.5f, -0.5f, -0.5f), new Vector3f(0.5f, 0.5f, -0.5f), new Vector3f(-0.5f, 0.5f, -0.5f),}, getTextureCoords(index), new int[]{3, 2, 0, 2, 1, 0}, new float[]{0.8f, 0.8f, 0.8f, 0.8f});
    }

    public Vector3f[] getVertexes() {
        return vertexes;
    }

    public float[] getShading() {
        return shading;
    }

    public int[] getIndices() {
        return indices;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    public void offsetPos(Vector3i pos) {
        for (Vector3f vec : vertexes) {
            vec.x += pos.x;
            vec.y += pos.y;
            vec.z += pos.z;
        }
    }

    public void offsetIndices(int by) {
        for (int i = 0; i < 6; i++) {
            indices[i] += by;
        }
    }
}