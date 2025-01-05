package shader;

import org.joml.Matrix4f;

public class EntityShader extends Shader {


    public EntityShader() {
        super("block");
    }

    @Override
    protected void bindParameters() {
        bindParameter(0, "position");
        bindParameter(1, "textureCoords");
        bindParameter(2, "shading");
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
}
