package ui;

import game.Mouse;
import game.Window;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Container extends Widget {
    private final Widget[] children;
    private final Integer color;
    private final String texture;

    public Container(Vector2i position, Vector2i size, Integer color, String texture, Widget[] children) {
        super(position, size);
        this.color = color;
        this.texture = texture;
        this.children = children;
        for (Widget child : children) {
            child.position.x += this.position.x;
            child.position.y += this.position.y;
        }
    }

    public boolean onHoverDetector() {
        for (Widget child : children) {
            if (child.onHoverDetector()) {
                return true;
            }
        }
        return false;
    }

    public boolean onClickDetector() {
        for (Widget child : children) {
            if (child.onClickDetector()) {
                return true;
            }
        }
        Mouse.getKeyDown(GLFW_MOUSE_BUTTON_LEFT, true);
        return true;
    }


    @Override
    public void onClick() {
        for (Widget child : children) {
            if (child.onClickDetector()) {
                break;
            }
        }
    }

    @Override
    public void input() {
        for (Widget child : children) {
            child.input();
        }
    }

    @Override
    public void draw() {
        if (texture != null) {
            UIManager.drawImageCentered(new Vector2i(Window.getWidth() / 2, Window.getHeight() / 2), texture);
        }
        if (color != null) {
            UIManager.drawRectangle(position, size, color);
        }
        for (Widget child : children) {
            if (child.getVisible()) {
                child.draw();
            }
        }
    }
}
