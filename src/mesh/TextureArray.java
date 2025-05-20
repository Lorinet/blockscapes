package mesh;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class TextureArray {

    private int width;
    private int height;
    private int capacity;
    private int textureID;
    private int count;
    private HashMap<String, BufferedTexture> textures;


    public TextureArray(int width, int height, int capacity) {
        this.width = width;
        this.height = height;
        this.capacity = capacity;
        this.count = 0;
        textures = new HashMap<>();
        textureID = GL46.glGenTextures();
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D_ARRAY, textureID);
        GL46.glTexStorage3D(GL46.GL_TEXTURE_2D_ARRAY, 1, GL46.GL_RGBA8, width, height, capacity);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D_ARRAY, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D_ARRAY, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D_ARRAY, 0);
        int error = GL46.glGetError();
        if (error != GL46.GL_NO_ERROR) {
            System.err.println("TextureArray init OpenGL Error: " + error);
        }
    }

    private BufferedTexture loadTexture(String name) {
        try {
            if(count >= capacity) throw new RuntimeException("Texture array is full!");
            String path = Paths.get("textures", name).toString();
            BufferedImage image = ImageIO.read(new FileInputStream(path));
            int localWidth = image.getWidth();
            int localHeight = image.getHeight();
            int[] pixels = new int[localWidth * localHeight];
            image.getRGB(0, 0, localWidth, localHeight, pixels, 0, localWidth);
            for (int i = 0; i < localWidth * localHeight; i++) {
                int a = (pixels[i] & 0xff000000) >> 24;
                int r = (pixels[i] & 0xff0000) >> 16;
                int g = (pixels[i] & 0xff00) >> 8;
                int b = (pixels[i] & 0xff);
                pixels[i] = a << 24 | b << 16 | g << 8 | r;
            }

            int[] paddedPixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int paddedIndex = y * width + x;
                    if (x < localWidth && y < localHeight) {
                        int localIndex = y * localWidth + x;
                        paddedPixels[paddedIndex] = pixels[localIndex];
                    } else {
                        paddedPixels[paddedIndex] = 0xFFFFFFFF;
                    }
                }
            }
            IntBuffer buffer = ByteBuffer.allocateDirect(paddedPixels.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
            buffer.put(paddedPixels).flip();
            GL46.glActiveTexture(GL46.GL_TEXTURE0);
            GL46.glBindTexture(GL46.GL_TEXTURE_2D_ARRAY, textureID);
            GL46.glTexSubImage3D(GL46.GL_TEXTURE_2D_ARRAY, 0, 0, 0, count, width, height, 1, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, buffer);
            GL46.glBindTexture(GL46.GL_TEXTURE_2D_ARRAY, 0);
            int error = GL46.glGetError();
            if (error != GL46.GL_NO_ERROR) {
                System.err.println("TextureArray loadTexture OpenGL Error: " + error);
            }
            BufferedTexture bufTex = new BufferedTexture(count, new Vector2i(localWidth, localHeight));
            textures.put(name, bufTex);
            count++;
            return bufTex;
        } catch (IOException e) {
            throw new RuntimeException("Could not read texture " + name);
        }
    }

    public BufferedTexture getOrLoadTexture(String name) {
        if(textures.containsKey(name)) return textures.get(name);
        return loadTexture(name);
    }

    public BufferedTexture getTexture(String name) {
        return textures.get(name);
    }

    public void destroy() {
        glDeleteTextures(textureID);
    }

    public int getMaxWidth() {
        return width;
    }

    public int getMaxHeight() {
        return height;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTextureArrayID() {
        return textureID;
    }

    public class BufferedTexture {
        private Vector2i size;
        private int textureID;
        private BufferedTexture(int textureID, Vector2i size) {
            this.textureID = textureID;
            this.size = size;
        }
        public Vector2i getSize() {
            return size;
        }
        public int getTextureID() {
            return textureID;
        }
    }
}