package mesh;

import org.lwjgl.opengl.GL46;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13C.glActiveTexture;

public class TextureCubemap {

    private int textureID;

    public TextureCubemap(String path) {
        loadFile(path);
    }

    public void texSubImage(String path, int what) {
        int[] pixels = null;
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(path));
            int width = image.getWidth();
            int height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
            for (int i = 0; i < width * height; i++) {
                int a = (pixels[i] & 0xff000000) >> 24;
                int r = (pixels[i] & 0xff0000) >> 16;
                int g = (pixels[i] & 0xff00) >> 8;
                int b = (pixels[i] & 0xff);
                pixels[i] = a << 24 | b << 16 | g << 8 | r;
            }
            IntBuffer buffer =
                    ByteBuffer.allocateDirect(pixels.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
            buffer.put(pixels).flip();

            glTexImage2D(what, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void loadFile(String name) {
        glActiveTexture(GL_TEXTURE0);
        textureID = GL46.glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_WRAP_R, GL46.GL_CLAMP_TO_EDGE);
        String[] dirs = {"up", "down", "left", "right", "front", "back"};
        int[] targets = {GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL46.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, GL46.GL_TEXTURE_CUBE_MAP_POSITIVE_Z};
        for(int i = 0; i < 6; i++) {
            texSubImage("textures/" + name + "_" + dirs[i] + ".png", targets[i]);
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
    }

    public void destroy() {
        glDeleteTextures(textureID);
    }

    public int getTextureID() {
        return textureID;
    }

}