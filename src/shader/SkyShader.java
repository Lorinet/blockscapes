package shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SkyShader extends Shader {
    public SkyShader() {
        super("sky");
    }

    @Override
    protected void bindParameters() {
        bindParameter(0, "position");

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

    public void loadCelestialColor(Vector3f celestialColor) {
        loadVec3("celestialColor", celestialColor.x, celestialColor.y, celestialColor.z);
    }
}
