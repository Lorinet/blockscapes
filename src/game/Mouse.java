package game;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse extends GLFWMouseButtonCallback {
    private static final boolean[] keys = new boolean[8];
    private static final boolean[] keysDown = new boolean[8];
    private static final boolean[] keysUp = new boolean[8];

    public static boolean getKey(int key) {
        return keys[key];
    }

    public static boolean getKeyDown(int key) {
        return getKeyDown(key, true);
    }

    public static boolean getKeyDown(int key, boolean cancel) {
        boolean kd = keysDown[key];
        if (cancel) {
            keysDown[key] = false;
        }
        return kd;
    }

    public static boolean getKeyUp(int key) {
        boolean kd = keysUp[key];
        keysUp[key] = false;
        return kd;
    }

    public static Vector2f getPosition() {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

        glfwGetCursorPos(Window.getWindowID(), x, y);
        x.rewind();
        y.rewind();

        return new Vector2f((float) x.get(), (float) y.get());
    }

    @Override
    public void invoke(long window, int key, int action, int mods) {
        keys[key] = action != GLFW_RELEASE;
        if (action == GLFW_PRESS) {
            keysDown[key] = true;
        } else if (action == GLFW_RELEASE) {
            keysUp[key] = true;
        }
    }
}
