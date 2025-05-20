package mesh;

import com.google.gson.Gson;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import ui.Text;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelManager {
    private static final Map<Integer, ArrayList<Integer>> vertexArrayObjects = new HashMap<>();
    private static final Map<String, Texture> staticTextures = new HashMap<>();
    private static final Map<String, Mesh> models = new HashMap<>();
    private static TextureArray textureArray;
    private static int textureCollectionID = 0;

    public static void init() {
        textureArray = new TextureArray(4096, 4096, 128);
        File[] dataFiles = new File("models").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".dat");
            }
        });

        Gson jsp = new Gson();

        assert dataFiles != null;
        for (File file : dataFiles) {
            String json = null;
            try {
                json = Files.readString(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ModelDataFile modelData = jsp.fromJson(json, ModelDataFile.class);
            ModelData model = ModelLoader.loadModel(modelData.getName(), modelData.getModelFile());
            models.put(modelData.getName(), createModelFromData(model, true));
        }
    }

    public static Mesh getModel(String name) {
        if (models.containsKey(name)) {
            return models.get(name);
        } else {
            System.err.println("Could not find model " + name);
            return null;
        }
    }

    public static Mesh createFlatModelUsingStaticTexture(String textureName, FloatArrayList vertexes, FloatArrayList textureCoords, IntArrayList indexes) {
        int vao = createVAO();
        loadFloatDataIntoVBO(vao, 0, 3, vertexes);
        loadFloatDataIntoVBO(vao, 1, 2, textureCoords);
        loadIndexesVBO(vao, indexes);
        GL46.glBindVertexArray(0);
        return new Mesh(vao, null, new int[]{getStaticTexture(textureName).getTextureID()}, indexes.size(), null);
    }

    public static Mesh createModelFromData(ModelData meshData, boolean saveModelData) {
        ArrayList<Integer> textureIDs = new ArrayList<>();
        int texCunt = 0;
        for (Material mat : meshData.getMaterials()) {
            if (mat != null && mat.getDiffuseTexturePath() != null) {
                TextureArray.BufferedTexture diffuse = getTextureArray().getOrLoadTexture(mat.getDiffuseTexturePath());
                mat.setDiffuseTextureIndex(diffuse.getTextureID());
                mat.setDiffuseTextureSize(diffuse.getSize());
                texCunt += 1;
                textureIDs.add(diffuse.getTextureID());
            }
        }
        int vao = createVAO();
        loadFloatDataIntoVBO(vao, 0, 3, meshData.getVertexes());
        loadFloatDataIntoVBO(vao, 1, 2, meshData.getTextureCoords());
        loadFloatDataIntoVBO(vao, 2, 3, meshData.getNormals());
        loadFloatDataIntoVBO(vao, 3, 1, meshData.getShading());
        loadIntDataIntoVBO(vao, 4, 1, meshData.getMaterialIndices());
        loadIndexesVBO(vao, meshData.getIndices());
        GL46.glBindVertexArray(0);
        ModelData data = null;
        if (saveModelData) {
            data = meshData;
        }
        return new Mesh(vao, meshData.getMaterials().toArray(new Material[0]), textureIDs.stream().mapToInt(Integer::intValue).toArray(), meshData.getIndices().size(), data);
    }

    public static TextureArray getTextureArray() {
        return textureArray;
    }

    public static void addStaticTexture(String name, Texture texture) {
        staticTextures.put(name, texture);
    }

    public static void loadStaticTexture(String name) {
        Texture tex = new Texture(Paths.get("textures", name).toString());
        staticTextures.put(name, tex);
    }

    public static void removeStaticTexture(String name) {
        getStaticTexture(name).destroy();
        staticTextures.remove(name);
    }

    public static Texture getStaticTexture(String name) {
        return staticTextures.get(name);
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
        models.clear();
    }

    private static void loadFloatDataIntoVBO(int vao, int attribute, int dimensions, FloatArrayList data) {
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
        int error = GL46.glGetError();
        if (error != GL46.GL_NO_ERROR) {
            System.err.println("UI OpenGL Error: " + error);
        }
    }

    private static void loadIntDataIntoVBO(int vao, int attribute, int dimensions, IntArrayList data) {
        int vbo = GL46.glGenBuffers();
        vertexArrayObjects.get(vao).add(vbo);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vbo);

        IntBuffer buffer = BufferUtils.createIntBuffer(data.size());
        for (int f : data) {
            buffer.put(f);
        }
        buffer.flip();
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, buffer, GL46.GL_STATIC_DRAW);
        GL46.glVertexAttribIPointer(attribute, dimensions, GL46.GL_INT, 0, 0);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        int error = GL46.glGetError();
        if (error != GL46.GL_NO_ERROR) {
            System.err.println("UI OpenGL Error: " + error);
        }
    }

    private static void loadIndexesVBO(int vao, IntArrayList data) {
        int vbo = GL46.glGenBuffers();
        vertexArrayObjects.get(vao).add(vbo);

        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, vbo);

        IntBuffer buffer = BufferUtils.createIntBuffer(data.size());
        for (int i : data) {
            buffer.put(i);
        }
        buffer.flip();

        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, buffer, GL46.GL_STATIC_DRAW);
        int error = GL46.glGetError();
        if (error != GL46.GL_NO_ERROR) {
            System.err.println("UI OpenGL Error: " + error);
        }
    }

    public static IntBuffer createMaterialiUniformBuffer(Material[] materials) {
        int intsPerMaterial = 1 + 2;
        int totalFloats = materials.length * intsPerMaterial;

        IntBuffer buffer = BufferUtils.createIntBuffer(totalFloats);

        for (Material mat : materials) {
            /**/
            buffer.put(mat.getDiffuseTextureIndex());
            buffer.put(mat.getDiffuseTextureSize().x);
            buffer.put(mat.getDiffuseTextureSize().y);
        }

        buffer.flip();
        return buffer;
    }

    public static FloatBuffer createMaterialfUniformBuffer(Material[] materials) {
        int floatsPerMaterial = 3 * 3 + 1;

        int totalFloats = materials.length * floatsPerMaterial;

        FloatBuffer buffer = BufferUtils.createFloatBuffer(totalFloats);

        for (Material mat : materials) {
            if (mat.getAmbientColor() != null) {
                buffer.put(mat.getAmbientColor().x).put(mat.getAmbientColor().y).put(mat.getAmbientColor().z);
            } else {
                buffer.put(1).put(1).put(1);
            }
            if (mat.getDiffuseColor() != null) {
                buffer.put(mat.getDiffuseColor().x).put(mat.getDiffuseColor().y).put(mat.getDiffuseColor().z);
            } else {
                buffer.put(1).put(1).put(1);
            }
            if (mat.getSpecularColor() != null) {
                buffer.put(mat.getSpecularColor().x).put(mat.getSpecularColor().y).put(mat.getSpecularColor().z);
            } else {
                buffer.put(1).put(1).put(1);
            }
            buffer.put(mat.getShininess());
        }

        buffer.flip();
        return buffer;
    }
}
