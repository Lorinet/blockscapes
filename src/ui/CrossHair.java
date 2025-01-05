package ui;

import game.Window;
import org.joml.Vector2i;

import java.io.IOException;

import static ui.UIManager.loadImage;

public class CrossHair extends Widget {
    public CrossHair() {
        super(new Vector2i(Window.getWidth() / 2, Window.getHeight() / 2), new Vector2i());
        setVisible(false);
        try {
            loadImage("cross");
        } catch (IOException e) {
            System.out.println("Could not load crosshair texture");
        }
    }

    @Override
    public void draw() {
        UIManager.drawImageCentered(position, "cross");
    }
}
