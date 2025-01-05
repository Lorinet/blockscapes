package game;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard extends GLFWKeyCallback {
    private static final boolean[] keys = new boolean[65536];
    private static final boolean[] keysDown = new boolean[65536];
    private static final boolean[] keysUp = new boolean[65536];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if(key < 0 || key >= keys.length) {
            return;
        }
        keys[key] = action != GLFW_RELEASE;
        if(action == GLFW_PRESS) {
            keysDown[key] = true;
        } else if(action == GLFW_RELEASE) {
            keysUp[key] = true;
        }
    }

    public static boolean getKey(int key) {
        return keys[key];
    }

    public static boolean getKeyDown(int key) {
        boolean kd = keysDown[key];
        keysDown[key] = false;
        return kd;
    }

    public static boolean getKeyUp(int key) {
        boolean kd = keysUp[key];
        keysUp[key] = false;
        return kd;
    }

    public static void cancelAll() {
        for(int i = 0; i < keys.length; i++) {
            //keys[i] = false;
            keysDown[i] = false;
            keysUp[i] = false;
        }
    }

    public static boolean[] selectKeysDown(int[] keys) {
        boolean[] res = new boolean[keys.length];
        for(int i = 0; i < keys.length; i++) {
            if(keysDown[keys[i]]) {
                res[i] = true;
                keysDown[keys[i]] = false;
            }
        }
        return res;
    }
}
