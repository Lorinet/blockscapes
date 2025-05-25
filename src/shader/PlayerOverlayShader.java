package shader;

public class PlayerOverlayShader extends Shader {


    public PlayerOverlayShader() {
        super("playerOverlay", new String[] {"overlayState"}, false);
    }

    @Override
    protected void bindParameters() {
        bindParameter(0, "position");
    }

    public void loadOverlayState(int state) {
        loadInt("overlayState", state);
    }
}
