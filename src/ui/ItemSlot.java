package ui;

import block.Block;
import org.joml.Vector2i;

public class ItemSlot extends Widget {
    public static final int SIZE = 28;
    public static final int TEXTURE_SIZE = 24;
    private static final int COLOR = 0x44000000;
    private static final int COLOR_HOVER = 0xccaaaaaa;
    private final Action actionDelegate;
    private Block block;
    private boolean selected;

    public ItemSlot(Vector2i position, Block block, Action actionDelegate) {
        super(position, new Vector2i(SIZE, SIZE));
        this.block = block;
        this.actionDelegate = actionDelegate;
    }

    @Override
    public void onClick() {
        if (actionDelegate != null) {
            actionDelegate.onClick();
        }
    }

    @Override
    public void onHover(boolean hovered) {
        if (actionDelegate != null) {
            actionDelegate.onHover(hovered);
        }
    }

    @Override
    public void draw() {
        int color = COLOR;
        if (hovered || selected) {
            color = COLOR_HOVER;
        }
        UIManager.drawRectangle(position, size, color);
        Vector2i pos = new Vector2i(position);
        pos.x += (SIZE - TEXTURE_SIZE) / 2;
        pos.y += (SIZE - TEXTURE_SIZE) / 2;
        pos.x *= UIManager.SCALE;
        pos.y *= UIManager.SCALE;
        if (block != null) {
            UIManager.drawImage(pos, "ui_" + block.getName().replace(" ", "").toLowerCase());
        }
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public interface Action {
        default void onClick() {
        }

        default void onHover(boolean hover) {
        }
    }
}
