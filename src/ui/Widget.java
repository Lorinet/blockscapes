package ui;

import game.Mouse;
import org.joml.Vector2f;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Widget {
    protected Vector2i position;
    protected Vector2i size;
    protected boolean visible = true;
    protected boolean hovered = false;

    public Widget(Vector2i position, Vector2i size) {
        this.position = position;
        this.size = size;
    }

    public static Widget centered(Widget w, boolean centerX, boolean centerY) {
        if (centerX) {
            w.position.x -= w.size.x / 2;
        }
        if (centerY) {
            w.position.y -= w.size.y / 2;
        }
        return w;
    }

    public Vector2i getPosition() {
        return position;
    }

    public void setPosition(Vector2i position) {
        this.position = position;
    }

    public Vector2i getSize() {
        return size;
    }

    public void setSize(Vector2i size) {
        this.size = size;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean getHovered() {
        return this.hovered;
    }

    public void input() {
    }

    public void draw() {
    }

    public void onClick() {
    }

    public void onHover(boolean hovered) {
    }

    public boolean onClickDetector() {
        if (Mouse.getKeyDown(GLFW_MOUSE_BUTTON_LEFT, false)) {
            if (hovered) {
                Mouse.getKeyDown(GLFW_MOUSE_BUTTON_LEFT, true);
                onClick();
                return true;
            }
        }
        return false;
    }

    public boolean onHoverDetector() {
        Vector2f pos = Mouse.getPosition();
        pos.x /= UIManager.SCALE;
        pos.y /= UIManager.SCALE;
        if (pos.x >= position.x && pos.y >= position.y && pos.x < position.x + size.x && pos.y < position.y + size.y && !hovered) {
            hovered = true;
            onHover(true);
            return true;
        } else if (pos.x < position.x || pos.y < position.y || pos.x >= position.x + size.x || pos.y >= position.y + size.y && hovered) {
            hovered = false;
            onHover(false);
        }
        return false;
    }
}
