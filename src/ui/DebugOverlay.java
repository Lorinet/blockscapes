package ui;

import game.Keyboard;
import game.Renderman;
import game.StageManager;
import level.LevelManager;
import org.joml.Vector2i;
import org.joml.Vector3i;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;

public class DebugOverlay extends Widget {
    private boolean active = false;

    public DebugOverlay() {
        super(new Vector2i(0, 0), new Vector2i(0, 0));
    }

    @Override
    public void input() {
        if (Keyboard.getKeyDown(GLFW_KEY_F3)) {
            active = !active;
        }
    }

    @Override
    public void draw() {
        if (active && StageManager.getInGame()) {
            long totalMem = Runtime.getRuntime().totalMemory();
            long freeMem = Runtime.getRuntime().freeMemory();
            Vector3i pos = Renderman.getPlayer().getWorldPosition();
            UIManager.drawTextInGrid(new Vector2i(0, 0), "Blockscapes [Version f0.0.4]", 0xFFFFFFFF, true);
            UIManager.drawTextInGrid(new Vector2i(0, 1), "FPS: " + (int) StageManager.getFPS(), 0xFFFFFFFF, true);
            UIManager.drawTextInGrid(new Vector2i(0, 2), "Used Mem: " + String.format("%.2f", ((float) (totalMem - freeMem) / 1048576f)) + " MB", 0xFFFFFFFF, true);
            UIManager.drawTextInGrid(new Vector2i(0, 3), "Total Mem: " + String.format("%.2f", ((float) totalMem / 1048576f)) + " MB", 0xFFFFFFFF, true);
            UIManager.drawTextInGrid(new Vector2i(0, 4), "C: " + LevelManager.loadedChunks.size() + " D: " + StageManager.getSettings().getRenderDistance(), 0xFFFFFFFF, true);
            UIManager.drawTextInGrid(new Vector2i(0, 5), "E: " + game.Renderman.getRenderedEntities() + " O: " + game.Renderman.getCulledEntities(), 0xFFFFFFFF, true);
            UIManager.drawTextInGrid(new Vector2i(0, 6), "P: " + (int) game.Renderman.getPlayer().cameraRotation.x + " " + (int) game.Renderman.getPlayer().cameraRotation.y + " " + (int) game.Renderman.getPlayer().cameraRotation.z, 0xFFFFFFFF, true);
            UIManager.drawTextInGrid(new Vector2i(0, 7), "Block: " + pos.x + " " + pos.y + " " + pos.z, 0xFFFFFFFF, true);
        }
    }
}
