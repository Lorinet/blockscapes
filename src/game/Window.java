package game;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.CGL;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    public static int width = WINDOW_WIDTH;
    public static int height = WINDOW_HEIGHT;
    private static Long window;
    private static boolean mouseLocked = false;

    public static void create() {
        if (window == null) {
            GLFWErrorCallback.createPrint(System.err).set();

            if (!glfwInit())
                throw new IllegalStateException("Unable to initialize GLFW");

            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
            window = glfwCreateWindow(width, height, "Game", NULL, NULL);

            if (window == NULL) {
                throw new RuntimeException("Failed to create the GLFW window");
            }

            glfwSetKeyCallback(window, new Keyboard());
            glfwSetMouseButtonCallback(window, new Mouse());

            glfwMakeContextCurrent(window);
            glfwSwapInterval(1);
            glfwShowWindow(window);
            GL.createCapabilities();
            CGL.CGLSetParameter(CGL.CGLGetCurrentContext(), CGL.kCGLCPSwapInterval, 0);
            resizeWindow();
            GL46.glViewport(0, 0, width, height);
        } else {
            resizeWindow();
        }
    }

    public static void resizeWindow() {
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        boolean fullscreen = StageManager.getSettings().getFullscreen();
        if (fullscreen) {
            assert vidmode != null;
            width = vidmode.width();
            height = vidmode.height();
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
            glfwSetWindowSize(window, width, height);
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, width, height, GLFW_DONT_CARE);
        } else {
            width = WINDOW_WIDTH;
            height = WINDOW_HEIGHT;
            glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
            glfwSetWindowSize(window, width, height);
            assert vidmode != null;
            glfwSetWindowMonitor(window, NULL, vidmode.width() / 2, vidmode.height() / 2, width, height, GLFW_DONT_CARE);
        }
        GL46.glViewport(0, 0, width, height);
    }

    public static void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public static void holdCursorHostage() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPos(Window.getWindowID(), (double) width / 2, (double) height / 2);
        mouseLocked = true;
    }

    public static void releaseCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        mouseLocked = false;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static boolean getMouseLocked() {
        return mouseLocked;
    }

    public static long getWindowID() {
        return window;
    }

    public static void destroy() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
