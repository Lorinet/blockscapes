package shader;

import mesh.DirectionalLightArray;
import mesh.MaterialArray;
import mesh.ModelManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

public class SkyboxShader extends Shader {

    public SkyboxShader() {
        super("skyBox", new String[] {"projection", "viewMatrix", "rotation", "sunlightColor", "textureSampler"}, false);
    }

    @Override
    protected void bindParameters() {
        bindParameter(0, "position");
    }

    public void loadProjection(Matrix4f matrix) {
        loadMatrix("projection", matrix);
    }

    public void loadView(Matrix4f matrix) {
        loadMatrix("viewMatrix", matrix);
    }

    public void loadRotation(Matrix4f matrix) {
        loadMatrix("rotation", matrix);
    }
    public void loadSunlightColor(Vector3f color) {
        loadVec3("sunlightColor", color.x, color.y, color.z);
    }

    public void loadTexture(int textureID) {
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, textureID);
        loadInt("textureSampler", 0);
    }
}
