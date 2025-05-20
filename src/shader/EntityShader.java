package shader;

import mesh.DirectionalLightArray;
import mesh.MaterialArray;
import mesh.ModelManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class EntityShader extends Shader {

    public EntityShader() {
        super("block");
        int uniformBlockIndex = GL46.glGetUniformBlockIndex(program, "MaterialBlock");

        if (uniformBlockIndex == GL46.GL_INVALID_INDEX) {
            throw new RuntimeException("Could not find uniform block MaterialBlock");
        } else {
            GL46.glUniformBlockBinding(program, uniformBlockIndex, 0);
        }
        uniformBlockIndex = GL46.glGetUniformBlockIndex(program, "DirectionalLightBlock");

        if (uniformBlockIndex == GL46.GL_INVALID_INDEX) {
            throw new RuntimeException("Could not find uniform block MaterialBlock");
        } else {
            GL46.glUniformBlockBinding(program, uniformBlockIndex, 1);
        }

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

    public void loadView(Matrix4f matrix) {
        loadMatrix("viewMatrix", matrix);
    }

    public void loadViewPos(Vector3f viewPos) {
        loadVec3("viewPos", viewPos.x, viewPos.y, viewPos.z);
    }

    public void loadMaterials(MaterialArray materialArray) {
        GL46.glBindBufferBase(GL46.GL_UNIFORM_BUFFER, 0, materialArray.getUboId());
    }

    public void loadDirectionalLights(DirectionalLightArray directionalLightArray) {
        GL46.glBindBufferBase(GL46.GL_UNIFORM_BUFFER, 1, directionalLightArray.getUboId());

    }

    public void loadTextures() {
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D_ARRAY, ModelManager.getTextureArray().getTextureArrayID());
        loadInt("textures", 0);
    }

    public void unloadTextures() {
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D_ARRAY, 0);
    }
}
