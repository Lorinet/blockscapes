package shader;

public class ShadowMapDebugShader extends Shader {


    public ShadowMapDebugShader() {
        super("shadowMapDebug", new String[] {"textureSampler"}, false);
    }

    @Override
    protected void bindParameters() {
        bindParameter(0, "position");
        bindParameter(1, "textureCoords");
    }

    public void loadTexture(int textureUnit) {
        loadInt("textureSampler", textureUnit);
    }
}
