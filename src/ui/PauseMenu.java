package ui;

import game.Keyboard;
import game.Window;
import game.StageManager;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class PauseMenu extends Widget {
    private final Container container;

    public PauseMenu() {
        super(new Vector2i(0, 0), new Vector2i(Window.getWidth() / UIManager.SCALE, Window.getHeight() / UIManager.SCALE));
        container = new Container(position, size, 0xaa000000, null, new Widget[]{
                Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 80), "Game paused", 0xFFFFFFFF), true, false),
                Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 120), new Vector2i(200, 25), "Resume", () -> {
                    if (!Window.getMouseLocked() && visible) {
                        setVisible(false);
                        Window.holdCursorHostage();
                    }
                }), true, false),
                Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 155), new Vector2i(200, 25), "Settings", () -> {
                    setVisible(false);
                    ((SettingsMenu) UIManager.getWidget("settingsMenu")).setFrom("pauseMenu");
                    UIManager.getWidget("settingsMenu").setVisible(true);
                }), true, false),
                Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 190), new Vector2i(200, 25), "Save and exit", () -> {
                    setVisible(false);
                    StageManager.unplay();
                    UIManager.getWidget("mainMenu").setVisible(true);
                }), true, false)
        });
        setVisible(false);
    }

    @Override
    public void draw() {
        container.draw();
    }

    @Override
    public void input() {
        if(Keyboard.getKeyUp(GLFW_KEY_ESCAPE) && visible) {
            System.out.println("Got escape");
            if (!Window.getMouseLocked()) {
                setVisible(false);
                System.out.println("SET VISIBLE:" + visible);
                Window.holdCursorHostage();
            }
        }
    }

    @Override
    public boolean onClickDetector() {
        return container.onClickDetector();
    }

    @Override
    public boolean onHoverDetector() {
        return container.onHoverDetector();
    }
}
