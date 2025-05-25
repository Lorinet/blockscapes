package mesh;

import game.Renderman;
import game.StageManager;
import level.Chunk;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class ShadowMap {
    private static final float NEAR_PLANE = 00f;
    private static final float FAR_PLANE = 1000f;
    private static final Vector4f[] viewportCorners = {new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f), new Vector4f(-1.0f,
            -1.0f, 1.0f, 1.0f), new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f), new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f),
            new Vector4f(1.0f, -1.0f, -1.0f, 1.0f), new Vector4f(1.0f, -1.0f, 1.0f, 1.0f), new Vector4f(1.0f, 1.0f,
            -1.0f, 1.0f), new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),};
    private static int fboId;
    private static int depthMapTextureId;

    private static int size;

    public static void init() {
        fboId = GL46.glGenFramebuffers();
        depthMapTextureId = GL46.glGenTextures();
        size = StageManager.getSettings().getShadowMapSize();
        GL46.glActiveTexture(GL46.GL_TEXTURE1);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, depthMapTextureId);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_DEPTH_COMPONENT, size, size, 0, GL46.GL_DEPTH_COMPONENT,
                GL46.GL_FLOAT, (ByteBuffer) null);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_CLAMP_TO_BORDER);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_CLAMP_TO_BORDER);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_COMPARE_MODE, GL46.GL_COMPARE_REF_TO_TEXTURE);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_COMPARE_FUNC, GL46.GL_LEQUAL);

        float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f};
        GL46.glTexParameterfv(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_BORDER_COLOR, borderColor);

        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, fboId);
        GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, GL46.GL_DEPTH_ATTACHMENT, GL46.GL_TEXTURE_2D,
                depthMapTextureId, 0);
        GL46.glDrawBuffer(GL46.GL_NONE);
        GL46.glReadBuffer(GL46.GL_NONE);
        if (GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER) != GL46.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Could not init shadow map framebuffer!");
        }
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }

    public static void unload() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
        GL46.glDeleteFramebuffers(fboId);
        GL46.glDeleteTextures(depthMapTextureId);
    }

    public static Matrix4f calculateLightPerspectiveMatrix(Vector3f lightDirection) {
        float halfWidthX = StageManager.getSettings().getRenderDistance() * Chunk.SIZE_XZ;
        float halfWidthY = 2 * Chunk.SIZE_Y;
        float halfWidthZ  = StageManager.getSettings().getRenderDistance() * Chunk.SIZE_XZ;


        float near = 0;
        float far = halfWidthY;


        Matrix4f lightProjection = new Matrix4f().ortho(-halfWidthX, halfWidthX, -halfWidthZ, halfWidthZ, near, far);

        Vector3f playerChunkCenter = new Vector3f(Renderman.getPlayer().getWorldPosition());
        playerChunkCenter.x /= Chunk.SIZE_XZ;
        playerChunkCenter.y = (float) Chunk.SIZE_Y / 4;
        playerChunkCenter.z /= Chunk.SIZE_XZ;
        playerChunkCenter.x *= Chunk.SIZE_XZ;
        playerChunkCenter.z *= Chunk.SIZE_XZ;
        playerChunkCenter.x += (float) Chunk.SIZE_XZ / 2;
        playerChunkCenter.z += (float) Chunk.SIZE_XZ / 2;
        Vector3f lightDir = new Vector3f(lightDirection);
        //lightDir.y = -lightDir.y;
        Vector3f lightPosition = new Vector3f(playerChunkCenter).add(new Vector3f(lightDir).mul(512));
       /* System.out.println("L: " + lightPosition);
        System.out.println("D: " + lightDirection);
        System.out.println("C: " + playerChunkCenter);
        System.out.println("P: " + Renderman.getPlayer().getPosition());
        System.out.println("===================");*/
        Matrix4f lightView = new Matrix4f().lookAt(lightPosition, playerChunkCenter, new Vector3f(0.0f, -1.0f, 0.0f));
        //return calculateViewportAlignedLightSpaceMatrix(lightView, near, far);
        return new Matrix4f(lightProjection).mul(lightView);
    }

    public static int getDepthMapTextureId() {
        return depthMapTextureId;
    }

    public static int getFboId() {
        return fboId;
    }

    public static int getSize() {
        return size;
    }

    public static float getNearPlane() {
        return NEAR_PLANE;
    }

    public static float getFarPlane() {
        return FAR_PLANE;
    }
}
