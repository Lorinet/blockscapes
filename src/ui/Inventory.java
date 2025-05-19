package ui;

import game.Keyboard;
import game.Window;
import level.LevelManager;
import block.Block;
import block.Blocks;
import org.joml.Vector2i;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class Inventory extends Widget {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    private final Text nameText;
    private final Container container;

    public Inventory() {
        super(new Vector2i(Window.getWidth() / 2 / UIManager.SCALE - WIDTH / 2, Window.getHeight() / 2 / UIManager.SCALE - HEIGHT / 2), new Vector2i(WIDTH, HEIGHT));
        nameText = new Text(new Vector2i(WIDTH / 2, 180), "", 0xFFFFFFFF);
        ArrayList<Widget> iHateJava = new ArrayList<>();
        iHateJava.add(Widget.centered(new Text(new Vector2i(WIDTH / 2, 10), "Select Blocks", 0xFFFFFFFF), true, false));
        iHateJava.add(Widget.centered(nameText, true, false));
        Vector2i slotPosition = new Vector2i(5, 30);
        for (Block b : Blocks.getBlocks().stream().filter(Block::getShowInInventory).toList()) {
            iHateJava.add(new ItemSlot(new Vector2i(slotPosition.x, slotPosition.y), b, new ItemSlot.Action() {
                @Override
                public void onClick() {
                    LevelManager.setHotBarItem(((HotBar) UIManager.getWidget("hotbar")).getSelectedSlot(), b);
                }
                @Override
                public void onHover(boolean hover) {
                    if (hover) {
                        nameText.setText(b.getName());
                    }
                }
            }));
            slotPosition.x += ItemSlot.SIZE + 1;
            if (slotPosition.x + ItemSlot.SIZE + 1 >= WIDTH) {
                slotPosition.x = 5;
                slotPosition.y += ItemSlot.SIZE + 1;
            }
        }
        container = new Container(position, size, 0xaa000000, null, iHateJava.toArray(new Widget[0]));
        setVisible(false);
    }

    @Override
    public void draw() {
        container.draw();
    }


    @Override
    public void input() {
        if (Keyboard.getKeyUp(GLFW_KEY_ESCAPE)) {
            if (!Window.getMouseLocked() && visible) {
                setVisible(false);
                Window.holdCursorHostage();
            }
        }
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
