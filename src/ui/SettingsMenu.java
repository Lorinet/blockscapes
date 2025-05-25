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
    private static final int MIN_SHADOW_MAP_SIZE = 512;
    private static final int MAX_SHADOW_MAP_SIZE = 8192;
    private final Container container;
    private String from;

    public SettingsMenu() {
        super(new Vector2i(0, 0), new Vector2i(Window.getWidth() / UIManager.SCALE,
                Window.getHeight() / UIManager.SCALE));
        from = "mainMenu";
        try {
            UIManager.loadImageScaled("menu2", new Vector2i(Window.getWidth() / 2, Window.getHeight() / 2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        ArrayList<Widget> what = new ArrayList<>();
        what.add(Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 40), "Settings",
                0xFFFFFFFF), true, false));
        what.add(Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 3, 120), "Fullscreen:",
                0xFFFFFFFF), true, false));
        what.add(Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 3, 130),
                new Vector2i(180, 25), StageManager.getSettings().getFullscreen() ? "On" : "Off", (me) -> {
            StageManager.getSettings().setFullscreen(!StageManager.getSettings().getFullscreen());
            StageManager.saveSettings();
            Main.restart();
        }), true, false));
        what.add(Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 3, 165), "Render " +
                "distance:", 0xFFFFFFFF), true, false));
        what.add(Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 3, 175),
                new Vector2i(180, 25), StageManager.getSettings().getRenderDistance() + " chunks", (me) -> {
            int dis = StageManager.getSettings().getRenderDistance();
            dis += 2;
            if (dis > MAX_RENDER_DISTANCE) {
                dis = MIN_RENDER_DISTANCE;
            }
            StageManager.getSettings().setRenderDistance(dis);
            me.setText(dis + " chunks");
            StageManager.saveSettings();
        }), true, false));
        what.add(Widget.centered(new Text(new Vector2i(Window.getWidth() / UIManager.SCALE / 3, 210), "Third person " +
                "view:", 0xFFFFFFFF), true, false));
        what.add(Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 3, 220),
                new Vector2i(180, 25), StageManager.getSettings().getThirdPerson() ? "On" : "Off", (me) -> {
            StageManager.getSettings().setThirdPerson(!StageManager.getSettings().getThirdPerson());
            StageManager.saveSettings();
            me.setText(StageManager.getSettings().getThirdPerson() ? "On" : "Off");
        }), true, false));

        what.add(Widget.centered(new Text(new Vector2i((int) (Window.getWidth() / UIManager.SCALE / 1.5f), 120),
                "Fancy transparency:", 0xFFFFFFFF), true, false));
        what.add(Widget.centered(new Button(new Vector2i((int) (Window.getWidth() / UIManager.SCALE / 1.5f), 130),
                new Vector2i(180, 25), StageManager.getSettings().getFancyTransparency() ? "On" : "Off", (me) -> {
            StageManager.getSettings().setFancyTransparency(!StageManager.getSettings().getFancyTransparency());
            StageManager.saveSettings();
            me.setText(StageManager.getSettings().getFancyTransparency() ? "On" : "Off");
        }), true, false));

        what.add(Widget.centered(new Text(new Vector2i((int) (Window.getWidth() / UIManager.SCALE / 1.5f), 165),
                "Realistic shadows:", 0xFFFFFFFF), true, false));
        what.add(Widget.centered(new Button(new Vector2i((int) (Window.getWidth() / UIManager.SCALE / 1.5f), 175),
                new Vector2i(180, 25), StageManager.getSettings().getShadowsEnabled() ? "On" : "Off", (me) -> {
            StageManager.getSettings().setShadowsEnabled(!StageManager.getSettings().getShadowsEnabled());
            StageManager.saveSettings();
            Main.restart();
            me.setText(StageManager.getSettings().getShadowsEnabled() ? "On" : "Off");
        }), true, false));
        what.add(Widget.centered(new Text(new Vector2i((int) (Window.getWidth() / UIManager.SCALE / 1.5f), 210),
                "Shadow map size:", 0xFFFFFFFF), true, false));
        what.add(Widget.centered(new Button(new Vector2i((int) (Window.getWidth() / UIManager.SCALE / 1.5f), 220),
                new Vector2i(180, 25), Integer.toString(StageManager.getSettings().getShadowMapSize()), (me) -> {
            int dis = StageManager.getSettings().getShadowMapSize();
            dis *= 2;
            if (dis > MAX_SHADOW_MAP_SIZE) {
                dis = MIN_SHADOW_MAP_SIZE;
            }
            StageManager.getSettings().setShadowMapSize(dis);
            me.setText(Integer.toString(dis));
            StageManager.saveSettings();
            Main.restart();
        }), true, false));

        what.add(Widget.centered(new Button(new Vector2i(Window.getWidth() / UIManager.SCALE / 2, 320),
                new Vector2i(200, 25), "Okay", (me) -> {
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
