package ui;

import game.Keyboard;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

public class TextBox extends Widget {
    private static final int COLOR = 0xFF888888;
    private static final int COLOR_HOVER = 0xddff6678;
    private static final int[] INPUT_KEYS = new int[]{
            GLFW_KEY_Q,
            GLFW_KEY_W,
            GLFW_KEY_E,
            GLFW_KEY_R,
            GLFW_KEY_T,
            GLFW_KEY_Y,
            GLFW_KEY_U,
            GLFW_KEY_I,
            GLFW_KEY_O,
            GLFW_KEY_P,
            GLFW_KEY_A,
            GLFW_KEY_S,
            GLFW_KEY_D,
            GLFW_KEY_F,
            GLFW_KEY_G,
            GLFW_KEY_H,
            GLFW_KEY_J,
            GLFW_KEY_K,
            GLFW_KEY_L,
            GLFW_KEY_Z,
            GLFW_KEY_X,
            GLFW_KEY_C,
            GLFW_KEY_V,
            GLFW_KEY_B,
            GLFW_KEY_N,
            GLFW_KEY_M,
            GLFW_KEY_0,
            GLFW_KEY_1,
            GLFW_KEY_2,
            GLFW_KEY_3,
            GLFW_KEY_4,
            GLFW_KEY_5,
            GLFW_KEY_6,
            GLFW_KEY_7,
            GLFW_KEY_8,
            GLFW_KEY_9,
            GLFW_KEY_0,
            GLFW_KEY_EQUAL,
            GLFW_KEY_MINUS,
            GLFW_KEY_SLASH,
            GLFW_KEY_PERIOD,
            GLFW_KEY_COMMA,
            GLFW_KEY_SEMICOLON,
            GLFW_KEY_APOSTROPHE,
            GLFW_KEY_BACKSLASH,
            GLFW_KEY_LEFT_BRACKET,
            GLFW_KEY_RIGHT_BRACKET,
            GLFW_KEY_SPACE
    };
    private String text;
    private boolean focused = false;
    private final boolean numbersOnly;

    public TextBox(Vector2i position, Vector2i size, String text, boolean numbersOnly) {
        super(position, size);
        this.text = text;
        this.numbersOnly = numbersOnly;
    }

    private static char keyCodeToCharacter(int key, boolean shift) {
        char res = (char) key;
        if (key >= 59 && key <= 96 && !shift) {
            res += 32;
        } else if (shift) {
            if (key == GLFW_KEY_MINUS) {
                res = '_';
            } else if (key == GLFW_KEY_APOSTROPHE) {
                res = '"';
            } else if (key == GLFW_KEY_COMMA) {
                res = '<';
            } else if (key == GLFW_KEY_PERIOD) {
                res = '>';
            } else if (key == GLFW_KEY_SLASH) {
                res = '?';
            } else if (key == GLFW_KEY_SEMICOLON) {
                res = ':';
            } else if (key == GLFW_KEY_EQUAL) {
                res = '+';
            } else if (key == GLFW_KEY_LEFT_BRACKET) {
                res = '{';
            } else if (key == GLFW_KEY_RIGHT_BRACKET) {
                res = '}';
            } else if (key == GLFW_KEY_BACKSLASH) {
                res = '|';
            }
        }
        return res;
    }

    @Override
    public void onClick() {
        focused = hovered;
    }

    @Override
    public void onHover(boolean hovered) {
        if (!hovered) {
            focused = hovered;
        }
    }

    @Override
    public void input() {
        if (focused) {
            boolean shift = Keyboard.getKey(GLFW_KEY_LEFT_SHIFT) || Keyboard.getKey(GLFW_KEY_RIGHT_SHIFT);
            boolean backspace = Keyboard.getKeyDown(GLFW_KEY_BACKSPACE);
            boolean[] keys = Keyboard.selectKeysDown(INPUT_KEYS);
            Keyboard.cancelAll();
            if (backspace && !text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
            } else {
                int key = 0;
                for (int i = 0; i < 48; i++) {
                    if (keys[i]) {
                        key = i;
                        break;
                    }
                }
                if (key == 0) {
                    return;
                }
                char c = keyCodeToCharacter(INPUT_KEYS[key], shift);
                if (numbersOnly && !Character.isDigit(c)) {
                    return;
                }
                text += c;
            }
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw() {
        int color = COLOR;
        if (focused) {
            color = COLOR_HOVER;
        }
        UIManager.drawRectangle(position, size, color);
        UIManager.drawText(new Vector2i(position.x + 5, position.y + (size.y / 2 - UIManager.getFont().getHeight() / 2)), text, 0xFFFFFFFF, false);
    }
}
