package shader;

import org.joml.Matrix4f;

public class ShadowMapShader extends Shader {
    public ShadowMapShader() {
        super("shadowMap", new String[] {"transformationMatrix", "lightPerspectiveTransformationMatrix"}, false);
    }

    public void bindParameters() {

    }

    public void loadTransformation(Matrix4f transformation) {
        loadMatrix("transformationMatrix", transformation);
    }

    public void loadLightPerspectiveTransformation(Matrix4f transformation) {
        loadMatrix("lightPerspectiveTransformationMatrix", transformation);
    }
}
