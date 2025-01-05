package ui;

import game.Keyboard;
import game.Window;
import mesh.Block;
import mesh.Blocks;
import org.joml.Vector2i;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

public class HotBar extends Widget {
    public static final int HOTBAR_SIZE = 10;
    private static final int[] HOTBAR_KEYS = {GLFW_KEY_1, GLFW_KEY_2, GLFW_KEY_3, GLFW_KEY_4, GLFW_KEY_5, GLFW_KEY_6, GLFW_KEY_7, GLFW_KEY_8, GLFW_KEY_9, GLFW_KEY_0};
    private final ItemSlot[] blocks;
    private int selectedBlock = 0;

    public HotBar() {
        super(new Vector2i((Window.getWidth() / UIManager.SCALE) - HOTBAR_SIZE * (ItemSlot.SIZE + 2), (Window.getHeight() / 2 - (ItemSlot.TEXTURE_SIZE + 4)) * UIManager.SCALE), new Vector2i((ItemSlot.TEXTURE_SIZE + 2) * HOTBAR_SIZE + 2, ItemSlot.TEXTURE_SIZE + 4));
        setVisible(false);
        for (Block b : Blocks.getBlocks()) {
            String textureID = "ui_" + b.getName().replace(" ", "").toLowerCase();
            try {
                UIManager.loadImage(textureID);
            } catch (IOException e) {
                System.out.println("Could not load item texture for block " + b.getName());
                try {
                    UIManager.loadImageWithID(textureID, "ui_missing", null);
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        blocks = new ItemSlot[HOTBAR_SIZE];
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            blocks[i] = new ItemSlot(new Vector2i(position.x / UIManager.SCALE + (i * (ItemSlot.SIZE + 2)), position.y / UIManager.SCALE), null, null);
        }
    }

    public Byte getSelectedBlockID() {
        if (blocks[selectedBlock].getBlock() != null) {
            return blocks[selectedBlock].getBlock().getId();
        } else {
            return null;
        }
    }

    @Override
    public void input() {
        boolean[] hotbarKeys = Keyboard.selectKeysDown(HOTBAR_KEYS);
        boolean should = false;
        for (boolean b : hotbarKeys) {
            if (b) {
                should = true;
                break;
            }
        }
        if (!should) {
            return;
        }

        for (int i = 0; i < hotbarKeys.length; i++) {
            if (hotbarKeys[i]) {
                blocks[selectedBlock].setSelected(false);
                selectedBlock = i;
                blocks[i].setSelected(true);
                break;
            }
        }
    }

    @Override
    public void draw() {
        for (ItemSlot slot : blocks) {
            slot.draw();
        }
        /*

        Vector2i pos = new Vector2i(position);
        UIManager.drawRectangle(new Vector2i(position.x / UIManager.SCALE - UIManager.SCALE, position.y / UIManager.SCALE - UIManager.SCALE), size, 0, 0, 0x44000000);
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (i == selectedBlock) {
                UIManager.drawRectangle(new Vector2i(pos.x / UIManager.SCALE - UIManager.SCALE, pos.y / UIManager.SCALE - UIManager.SCALE), new Vector2i(HOTBAR_TEXTURE_SIZE + 4, HOTBAR_TEXTURE_SIZE + 4), 0, 0, 0xccaaaaaa);
            }
            if (hotbarItems[i] != null) {
                UIManager.drawImage(pos, "ui_" + Blocks.getBlock(hotbarItems[i]).getName().toLowerCase());
            }
            pos.x += (HOTBAR_TEXTURE_SIZE + 2) * UIManager.SCALE;
        }*/
    }

    public Byte[] getHotbarItems() {
        Byte[] items = new Byte[HOTBAR_SIZE];
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (blocks[i].getBlock() != null) {
                items[i] = blocks[i].getBlock().getId();
            }
        }
        return items;
    }

    public void setHotbarItems(Byte[] hotbarItems) {
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (hotbarItems[i] != null) {
                blocks[i].setBlock(Blocks.getBlock(hotbarItems[i]));
            } else {
                blocks[i].setBlock(null);
            }
        }
    }

    public int getSelectedSlot() {
        return selectedBlock;
    }

    public void setHotbarItem(int slot, Block block) {
        blocks[slot].setBlock(block);
    }
}
