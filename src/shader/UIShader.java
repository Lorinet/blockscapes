package shader;

public class UIShader extends Shader {


    public UIShader() {
        super("ui", new String[] {}, false);
    }

    @Override
    protected void bindParameters() {
        bindParameter(0, "position");
        bindParameter(1, "textureCoords");
    }
}
