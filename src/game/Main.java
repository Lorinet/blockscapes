package game;

import mesh.GeometryManager;
import ui.UIManager;

public class Main {
    private static boolean shouldRestart = true;

    public static void init() {
        StageManager.init();
        Window.create();
        Renderman.init();
        UIManager.init();
        StageManager.mainLoop();
    }
    public static void unload() {
        StageManager.unload();
        UIManager.unload();
        Renderman.unload();
        StageManager.unload();
        GeometryManager.unload();
    }

    public static void restart() {
        shouldRestart = true;
        StageManager.exit();
    }

    public static void main(String[] args) {
        while(shouldRestart) {
            shouldRestart = false;
            init();
            unload();
        }

        Window.destroy();
    }
}
