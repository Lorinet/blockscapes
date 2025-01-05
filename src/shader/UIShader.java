package shader;

public class UIShader extends Shader {


    public UIShader() {
        super("ui");
    }

    @Override
    protected void bindParameters() {
        bindParameter(0, "position");
        bindParameter(1, "textureCoords");
    }
}
