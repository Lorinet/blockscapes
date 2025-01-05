package ui;

import game.StageManager;
import game.Window;
import level.LevelManager;
import org.joml.Vector2i;

import java.io.IOException;

public class NewWorldMenu extends Widget {
    private final Container container;
    private final TextBox textBoxName;
    private final TextBox textBoxSeed;

    public NewWorldMenu() {
        super(new Vector2i(0, 0), new Vector2i(Window.getWidth() / UIManager.SCALE, Window.getHeight() / UIManager.SCALE));
        try {
            UIManager.loadImageScaled("menu2", new Vector2i(Window.getWidth() / 2, Window.getHeight() / 2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        textBoxName = new TextBox(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 120), new Vector2i(200, 25), "", false);
        textBoxSeed = new TextBox(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 165), new Vector2i(200, 25), "", true);
        container = new Container(position, size, null, "menu2", new Widget[]{
                Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 110), "Enter name:", 0xFFFFFFFF), true, false),
                Widget.centered(textBoxName, true, false),
                Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 155), "Seed:", 0xFFFFFFFF), true, false),
                Widget.centered(textBoxSeed, true, false),
                Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 200), new Vector2i(200, 25), "Create world", () -> {
                    Long seed = null;
                    try {
                        seed = Long.parseLong(textBoxSeed.getText());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid seed, generating a random seed");
                    }
                    String name = textBoxName.getText();
                    LevelManager.createLevel(name, seed);
                    ((LevelsMenu) UIManager.getWidget("levelsMenu")).recompute();
                    setVisible(false);
                    textBoxSeed.setText("");
                    textBoxName.setText("");
                    StageManager.play(name);
                }), true, false),
                Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 230), new Vector2i(200, 25), "Back", () -> {
                    setVisible(false);
                    UIManager.getWidget("levelsMenu").setVisible(true);
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
