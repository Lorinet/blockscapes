package ui;

import game.StageManager;
import game.Window;
import org.joml.Vector2i;

import java.io.IOException;

public class MainMenu extends Widget {
    private final Container container;

    public MainMenu() {
        super(new Vector2i(0, 0), new Vector2i(Window.getWidth() / UIManager.SCALE, Window.getHeight() / UIManager.SCALE));
        try {
            UIManager.loadImageScaled("title", new Vector2i(Window.getWidth() / 2, Window.getHeight() / 2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        container = new Container(position, size, null, "title", new Widget[]{
                Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, (int) ((float) Window.getHeight() / UIManager.SCALE * 0.45f)), new Vector2i(300, 30), "Singleplayer", () -> {
                    setVisible(false);
                    UIManager.getWidget("levelsMenu").setVisible(true);
                }), true, false),
                Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, (int) ((float) Window.getHeight() / UIManager.SCALE * 0.45f) + 35), new Vector2i(300, 30), "Settings", () -> {
                    setVisible(false);
                    ((SettingsMenu) UIManager.getWidget("settingsMenu")).setFrom("mainMenu");
                    UIManager.getWidget("settingsMenu").setVisible(true);
                }), true, false),
                Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, (int) ((float) Window.getHeight() / UIManager.SCALE * 0.45f) + 70), new Vector2i(300, 30), "Quit game", StageManager::exit), true, false),
                new Text(new Vector2i(0, Window.getHeight() / UIManager.SCALE - UIManager.getFont().getHeight() * UIManager.SCALE), "Linfinity Technologies 2024", 0xFFFFFFFF)
        });
        setVisible(false);
    }

    @Override
    public void draw() {
        container.draw();
    }

    @Override
    public void input() {
        container.input();
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
