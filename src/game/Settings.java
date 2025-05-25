package game;

public class Settings {
    private boolean fullscreen;
    private int renderDistance;
    private boolean fancyTransparency;
    private int shadowMapSize;
    private boolean shadowsEnabled;
    private boolean thirdPerson;

    public Settings(boolean fullscreen, int renderDistance) {
        this.fullscreen = fullscreen;
        this.renderDistance = renderDistance;
    }

    public boolean getFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public int getRenderDistance() {
        return renderDistance;
    }

    public void setRenderDistance(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    public boolean getFancyTransparency() {
        return fancyTransparency;
    }

    public void setFancyTransparency(boolean fancyTransparency) {
        this.fancyTransparency = fancyTransparency;
    }

    public int getShadowMapSize() {
        return shadowMapSize;
    }

    public void setShadowMapSize(int shadowMapSize) {
        this.shadowMapSize = shadowMapSize;
    }

    public boolean getShadowsEnabled() {
        return shadowsEnabled;
    }

    public void setShadowsEnabled(boolean shadowsEnabled) {
        this.shadowsEnabled = shadowsEnabled;
    }

    public boolean getThirdPerson() {
        return thirdPerson;
    }

    public void setThirdPerson(boolean thirdPerson) {
        this.thirdPerson = thirdPerson;
    }
}
