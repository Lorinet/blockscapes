package ui;

import org.joml.Vector2i;

public class Button extends Widget {
    private static final int COLOR = 0xFF999999;
    private static final int COLOR_HOVER = 0xddff6678;
    private String text;
    private final Action click;
    public Button(Vector2i position, Vector2i size, String text, Action click) {
        super(position, size);
        this.text = text;
        this.click = click;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void onClick() {
        this.click.onClick(this);
    }

    @Override
    public void draw() {
        int color = COLOR;
        if (hovered) {
            color = COLOR_HOVER;
        }
        UIManager.drawRectangle(position, size, color);
        UIManager.drawText(new Vector2i(position.x + (size.x / 2 - ((UIManager.getFont().getWidth() * (text.length() + 1)) / 2)), position.y + (size.y / 2 - UIManager.getFont().getHeight() / 2)), text, 0xFFFFFFFF, false);
    }

    public interface Action {
        void onClick(Button me);
    }
}
