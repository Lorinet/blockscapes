package ui;

import game.Keyboard;
import game.StageManager;
import game.Window;
import level.LevelManager;
import org.joml.Vector2i;

import java.io.IOException;
import java.util.ArrayList;

public class LevelsMenu extends Widget {
    private Container container;

    public LevelsMenu() {
        super(new Vector2i(0, 0), new Vector2i(Window.getWidth() / UIManager.SCALE, Window.getHeight() / UIManager.SCALE));
        try {
            UIManager.loadImageScaled("menu2", new Vector2i(Window.getWidth() / 2, Window.getHeight() / 2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        recompute();
        setVisible(false);
    }

    public void recompute() {
        ArrayList<Widget> what = new ArrayList<>();
        what.add(Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 80), "Select World", 0xFFFFFFFF), true, false));

        Vector2i pos = new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 120);

        for (String s : LevelManager.getLevels()) {
            what.add(Widget.centered(new Button(new Vector2i(pos), new Vector2i(200, 25), s, (me) -> {
                setVisible(false);
                StageManager.play(s);
            }), true, false));
            pos.y += 30;
        }
        pos.y += 30;

        what.add(Widget.centered(new Button(new Vector2i(pos), new Vector2i(200, 25), "Create new world", (me) -> {
            setVisible(false);
            Keyboard.cancelAll();
            UIManager.getWidget("createWorldMenu").setVisible(true);
        }), true, false));
        pos.y += 30;
        what.add(Widget.centered(new Button(new Vector2i(pos), new Vector2i(200, 25), "Back to menu", (me) -> {
            setVisible(false);
            UIManager.getWidget("mainMenu").setVisible(true);
        }), true, false));
        container = new Container(position, size, null, "menu2", what.toArray(new Widget[0]));
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
