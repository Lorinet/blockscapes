package game;

import audio.AudioManager;
import block.Blocks;
import mesh.ModelManager;
import ui.UIManager;

public class Main {
    private static boolean shouldRestart = true;

    public static void init() {
        AudioManager.init();
        StageManager.init();
        Window.create();
        Blocks.init();
        Renderman.init();
        ModelManager.init();
        UIManager.init();
        StageManager.mainLoop();
    }
    public static void unload() {
        StageManager.unload();
        AudioManager.unload();
        UIManager.unload();
        Renderman.unload();
        Blocks.unload();
        StageManager.unload();
        ModelManager.unload();
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
