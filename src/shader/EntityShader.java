package shader;

import mesh.Material;
import mesh.ModelManager;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;

public class EntityShader extends Shader {

    public EntityShader() {
        super("block");
    }

    @Override
    protected void bindParameters() {
        bindParameter(0, "position");
        bindParameter(1, "textureCoords");
        bindParameter(2, "normals");
        bindParameter(3, "shading");
        bindParameter(4, "materialIndex");
    }

    public void loadTransformation(Matrix4f matrix) {
        loadMatrix("transformation", matrix);
    }

    public void loadProjection(Matrix4f matrix) {
        loadMatrix("projection", matrix);
    }

    public void initTextureSampler() {
        loadIntBuffer("textures", ModelManager.createTextureSamplerUniformBuffer(16));
    }

    public void loadView(Matrix4f matrix) {
        loadMatrix("viewMatrix", matrix);
    }

    public void loadMaterials(IntBuffer materialsIntBuffer, FloatBuffer materialsFloatBuffer) {
        loadIntBuffer("materialis", materialsIntBuffer);
        loadFloatBuffer("materialfs", materialsFloatBuffer);
    }

    public void loadTextures(int[] textures) {
        for (int i = 0; i < textures.length; i++) {
            GL46.glActiveTexture(GL46.GL_TEXTURE0 + i);
            GL46.glBindTexture(GL46.GL_TEXTURE_2D, textures[i]);
        }
    }

    public void unloadTextures(int textureCount) {
        for (int i = 0; i < textureCount; i++) {
            GL46.glActiveTexture(GL46.GL_TEXTURE0 + i);
            GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
        }
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
    }
}
