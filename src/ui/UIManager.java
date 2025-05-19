package ui;

import game.Font;
import game.Texture;
import game.Window;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import mesh.ModelManager;
import mesh.Mesh;
import org.joml.Vector2i;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UIManager {
    public static final int SCALE = 2;
    private static final Font font = new Font5x8();
    private static final Map<String, int[][]> images = new HashMap<>();
    public static TreeMap<String, Widget> widgets = new TreeMap<>();
    private static Texture surfaceTexture;
    private static Mesh model;

    public static void init() {
        surfaceTexture = new Texture(Window.getWidth(), Window.getHeight());
        ModelManager.addTexture("ui", surfaceTexture);
        model = ModelManager.createFlatModelUsingTexture("ui",
                new FloatArrayList(Arrays.asList(-1f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f, 1f, 1f, 0f)),
                new FloatArrayList(Arrays.asList(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f)),
                new IntArrayList(Arrays.asList(0, 1, 2, 2, 3, 0)));

        addWidget("crosshair", new CrossHair());
        addWidget("debugOverlay", new DebugOverlay());
        addWidget("hotbar", new HotBar());
        addWidget("mainMenu", new MainMenu());
        addWidget("levelsMenu", new LevelsMenu());
        addWidget("createWorldMenu", new NewWorldMenu());
        addWidget("pauseMenu", new PauseMenu());
        addWidget("settingsMenu", new SettingsMenu());
        addWidget("inventory", new Inventory());
        getWidget("mainMenu").setVisible(true);
    }

    public static void unload() {
        removeAllWidgets();
        model.destroy();
        model = null;
        ModelManager.removeTexture("ui");
        surfaceTexture = null;
        images.clear();
    }

    public static void drawRectangle(Vector2i pos, Vector2i size, int fillColor) {
        for (int i = pos.x; i < pos.x + size.x; i++) {
            for (int j = pos.y; j < pos.y + size.y; j++) {
                putPixelScaled(i, j, fillColor);
            }
        }
    }

    public static void drawText(Vector2i pos, String text, int color, boolean background) {
        int ox = pos.x;
        int oy = pos.y;
        int x;
        int y;
        for (int ci = 0; ci < text.length(); ci++) {
            char[] pix = font.getChar((char) (text.toCharArray()[ci] - 0x20));
            for (x = ox - 1; x <= ox + font.getWidth(); x++) {
                int mask = 0b00000001;
                for (y = oy - 1; y <= oy + font.getHeight(); y++) {
                    if (x >= ox && x < ox + font.getWidth() && y >= oy && y < oy + font.getHeight()) {
                        int p = pix[x - ox] & mask;
                        mask = mask << 1;
                        if (p != 0) {
                            putPixelScaled(x, y, color);
                        } else {
                            if (background) {
                                putPixelScaled(x, y, 0x44000000);
                            }
                        }
                    } else {
                        if (background) {
                            putPixelScaled(x, y, 0x44000000);
                        }
                    }
                }
            }
            if (background) {
                for (y = oy; y < oy + font.getHeight(); y++) {
                    putPixelScaled(x, y, 0x44000000);
                }
            }
            ox += font.getWidth() + 1;
        }
    }

    public static Font getFont() {
        return font;
    }

    public static void drawTextInGrid(Vector2i pos, String text, int color, boolean background) {
        drawText(new Vector2i(pos.x * (font.getWidth() + 2) + 1, pos.y * (font.getHeight() + 2) + 1), text, color, background);
    }

    private static void putPixelScaled(int x, int y, int value) {
        if (x < 0 || x >= Window.getWidth() / SCALE || y < 0 || y >= Window.getHeight() / SCALE) {
            return;
        }
        for (int i = 0; i < UIManager.SCALE; i++) {
            for (int j = 0; j < UIManager.SCALE; j++) {
                surfaceTexture.getPixels()[getIndex(x * UIManager.SCALE + i, y * UIManager.SCALE + j)] = value;
            }
        }
    }

    public static void loadImage(String name) throws IOException {
        loadImageWithID(name, name, null);
    }

    public static void loadImageScaled(String name, Vector2i scale) throws IOException {
        loadImageWithID(name, name, scale);
    }

    public static void loadImageWithID(String id, String filename, Vector2i scale) throws IOException {
        if (images.containsKey(id)) {
            return;
        }
        Path path = Paths.get("textures", filename + ".png");
        BufferedImage image = ImageIO.read(new FileInputStream(path.toString()));
        int width = image.getWidth();
        int height = image.getHeight();
        if (scale != null) {
            float scaleW = (float) scale.x / width, scaleH = (float) scale.y / height;
            BufferedImage dstImg = new BufferedImage(scale.x, scale.y, image.getType());
            AffineTransform scalingTransform = new AffineTransform();
            scalingTransform.scale(scaleW, scaleH);
            AffineTransformOp scaleOp = new AffineTransformOp(scalingTransform, AffineTransformOp.TYPE_BILINEAR);
            dstImg = scaleOp.filter(image, dstImg);
            image = dstImg;
            width = image.getWidth();
            height = image.getHeight();
        }

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        int[][] img = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int a = (pixels[y * width + x] & 0xff000000) >> 24;
                int r = (pixels[y * width + x] & 0xff0000) >> 16;
                int g = (pixels[y * width + x] & 0xff00) >> 8;
                int b = (pixels[y * width + x] & 0xff);
                img[x][y] = a << 24 | b << 16 | g << 8 | r;
            }
        }
        images.put(id, img);
    }

    public static void drawImage(Vector2i pos, String name) {
        int[][] pixels = images.get(name);
        int ox = pos.x / SCALE;
        int oy = pos.y / SCALE;
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[x].length; y++) {
                putPixelScaled(x + ox, y + oy, pixels[x][y]);
            }
        }
    }

    public static void drawImageCentered(Vector2i pos, String name) {
        drawImage(new Vector2i(pos.x - images.get(name).length, pos.y - images.get(name)[0].length), name);
    }

    public static void clear() {
        for (int i = 0; i < surfaceTexture.getWidth() * surfaceTexture.getHeight(); i++) {
            surfaceTexture.getPixels()[i] = 0;
        }
    }

    public static void draw() {
        clear();
        for (Widget w : widgets.values()) {
            if (w.getVisible()) {
                w.input();
                w.onHoverDetector();
                w.onClickDetector();
                w.draw();
            }
        }
        commit();
    }

    public static void commit() {
        surfaceTexture.updatePixels();
    }

    public static Vector2i getTextureSize(String texture) {
        if (images.containsKey(texture)) {
            return new Vector2i(images.get(texture).length, images.get(texture)[0].length);
        }
        return null;
    }

    private static int getIndex(int x, int y) {
        return y * surfaceTexture.getWidth() + x;
    }

    public static Texture getSurface() {
        return surfaceTexture;
    }

    public static Mesh getModel() {
        return model;
    }

    public static Widget getWidget(String id) {
        return widgets.get(id);
    }

    public static void addWidget(String name, Widget widget) {
        widgets.put(name, widget);
    }

    public static void removeWidget(String name) {
        widgets.remove(name);
    }

    public static void removeAllWidgets() {
        widgets.clear();
    }

    public static Vector2i getTextSize(String text) {
        return new Vector2i(text.length() * (getFont().getWidth() + 1), getFont().getHeight());
    }
}
