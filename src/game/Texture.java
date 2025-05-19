package game;

import org.lwjgl.opengl.GL46;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {

    private int width, height;
    private int[] pixels;
    private int textureID;
    private IntBuffer buffer;
    private boolean texed = false;

    public Texture(String path) {
        loadFile(path);
    }

    public Texture(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
        loadPixels(pixels);
    }

    private void loadPixels(int[] pixels) {
        textureID = GL46.glGenTextures();
        this.pixels = pixels;
        buffer = ByteBuffer.allocateDirect(pixels.length << 2)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        updatePixels();
    }

    public void updatePixels() {
        buffer.put(pixels).flip();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        if (texed) {
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL46.GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            texed = true;
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void updatePixelsNextGen() {
        buffer.put(pixels).flip();

    }

    private void loadFile(String path) {
        int[] pixels = null;
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(path));
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
            for (int i = 0; i < width * height; i++) {
                int a = (pixels[i] & 0xff000000) >> 24;
                int r = (pixels[i] & 0xff0000) >> 16;
                int g = (pixels[i] & 0xff00) >> 8;
                int b = (pixels[i] & 0xff);
                pixels[i] = a << 24 | b << 16 | g << 8 | r;
            }
            loadPixels(pixels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        glDeleteTextures(textureID);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getTextureID() {
        return textureID;
    }

}