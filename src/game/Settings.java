package game;

public class Settings {
    private boolean fullscreen;
    private int renderDistance;

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
}
