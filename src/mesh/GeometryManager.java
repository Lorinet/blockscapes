package mesh;

import game.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GeometryManager {
    private static final Map<Integer, ArrayList<Integer>> vertexArrayObjects = new HashMap<>();
    private static final Map<String, Texture> textures = new HashMap<>();

    public static Mesh createModel(String texture, Collection<Float> vertexes, Collection<Float> textureCoords, Collection<Float> shading, Collection<Integer> indexes) {
        loadTexture(texture);
        return createModelUsingTexture(texture, vertexes, textureCoords, shading, indexes);
    }

    public static Mesh createModelUsingTexture(String textureName, Collection<Float> vertexes, Collection<Float> textureCoords, Collection<Float> shading, Collection<Integer> indexes) {
        int vao = createVAO();
        loadFloatDataIntoVBO(vao, 0, 3, vertexes);
        loadFloatDataIntoVBO(vao, 1, 2, textureCoords);
        loadFloatDataIntoVBO(vao, 2, 1, shading);
        loadIndexesVBO(vao, indexes);
        GL46.glBindVertexArray(0);
        return new Mesh(vao, getTexture(textureName).getTextureID(), indexes.size());
    }

    private static void loadTexture(String name) {
        if (!textures.containsKey(name)) {
            textures.put(name, new Texture(Paths.get("textures", name + ".png").toString()));
        }
    }

    public static void addTexture(String name, Texture texture) {
        textures.put(name, texture);
    }

    public static void removeTexture(String name) {
        getTexture(name).destroy();
        textures.remove(name);
    }

    private static Texture getTexture(String name) {
        return textures.get(name);
    }

    private static int createVAO() {
        int vao = GL46.glGenVertexArrays();
        vertexArrayObjects.put(vao, new ArrayList<>());
        GL46.glBindVertexArray(vao);
        return vao;
    }

    public static void unloadModel(int vao) {
        for (int vbo : vertexArrayObjects.get(vao)) {
            GL46.glDeleteBuffers(vbo);
        }
        GL46.glDeleteVertexArrays(vao);
    }

    public static void unload() {
        for (var vao : vertexArrayObjects.entrySet()) {
            unloadModel(vao.getKey());
        }
    }

    private static void loadFloatDataIntoVBO(int vao, int attribute, int dimensions, Collection<Float> data) {
        int vbo = GL46.glGenBuffers();
        vertexArrayObjects.get(vao).add(vbo);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.size());
        for (float f : data) {
            buffer.put(f);
        }
        buffer.flip();

        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, buffer, GL46.GL_STATIC_DRAW);
        GL46.glVertexAttribPointer(attribute, dimensions, GL46.GL_FLOAT, false, 0, 0);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
    }

    private static void loadIndexesVBO(int vao, Collection<Integer> data) {
        int vbo = GL46.glGenBuffers();
        vertexArrayObjects.get(vao).add(vbo);

        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, vbo);

        IntBuffer buffer = BufferUtils.createIntBuffer(data.size());
        for (int i : data) {
            buffer.put(i);
        }
        buffer.flip();

        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, buffer, GL46.GL_STATIC_DRAW);
    }
}
