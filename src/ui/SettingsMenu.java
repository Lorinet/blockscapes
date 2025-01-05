package ui;

import game.Main;
import game.StageManager;
import game.Window;
import org.joml.Vector2i;

import java.io.IOException;
import java.util.ArrayList;

public class SettingsMenu extends Widget {
    private static final int MIN_RENDER_DISTANCE = 2;
    private static final int MAX_RENDER_DISTANCE = 10;
    private final Container container;
    private String from;
    private Button renderDistanceButton;

    public SettingsMenu() {
        super(new Vector2i(0, 0), new Vector2i(Window.getWidth() / UIManager.SCALE, Window.getHeight() / UIManager.SCALE));
        from = "mainMenu";
        try {
            UIManager.loadImageScaled("menu2", new Vector2i(Window.getWidth() / 2, Window.getHeight() / 2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        renderDistanceButton = new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 175), new Vector2i(200, 25), StageManager.getSettings().getRenderDistance() + " chunks", () -> {
            int dis = StageManager.getSettings().getRenderDistance();
            dis += 2;
            if (dis > MAX_RENDER_DISTANCE) {
                dis = MIN_RENDER_DISTANCE;
            }
            StageManager.getSettings().setRenderDistance(dis);
            renderDistanceButton.setText(dis + " chunks");
            StageManager.saveSettings();
        });

        ArrayList<Widget> what = new ArrayList<>();
        what.add(Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 80), "Settings", 0xFFFFFFFF), true, false));
        what.add(Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 120), "Fullscreen:", 0xFFFFFFFF), true, false));
        what.add(Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 130), new Vector2i(200, 25), StageManager.getSettings().getFullscreen() ? "On" : "Off", () -> {
            StageManager.getSettings().setFullscreen(!StageManager.getSettings().getFullscreen());
            StageManager.saveSettings();
            Main.restart();
        }), true, false));
        what.add(Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 165), "Render distance:", 0xFFFFFFFF), true, false));
        what.add(Widget.centered(renderDistanceButton, true, false));


        what.add(Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 220), new Vector2i(200, 25), "Okay", () -> {
            UIManager.getWidget(from).setVisible(true);
            setVisible(false);
        }), true, false));

        container = new Container(position, size, null, "menu2", what.toArray(new Widget[0]));
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

    public void setFrom(String from) {
        this.from = from;
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
