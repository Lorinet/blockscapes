package ui;

import org.joml.Vector2i;

public class Text extends Widget {
    private final int color;
    private String text;
    private Runnable click;

    public Text(Vector2i position, String text, int color) {
        super(position, UIManager.getTextSize(text));
        this.text = text;
        this.color = color;
    }

    @Override
    public void onClick() {
        this.click.run();
    }

    @Override
    public void draw() {
        UIManager.drawText(new Vector2i(position.x + (size.x / 2 - ((UIManager.getFont().getWidth() * text.length()) / 2)), position.y + (size.y / 2 - UIManager.getFont().getHeight() / 2)), text, color, false);
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
