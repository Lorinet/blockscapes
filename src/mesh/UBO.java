package mesh;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class UBO {
    protected int uboId;
    protected int structSize;
    protected int elementCount;
    protected ByteBuffer buffer;
    protected UBO(int structSize, int elementCount) {
        this.structSize = structSize;
        this.elementCount = elementCount;
        uboId = GL46.glGenBuffers();
        reset();
    }

    protected void bind() {
        int totalSize = structSize * elementCount;
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, uboId);
        GL46.glBufferData(GL46.GL_UNIFORM_BUFFER, totalSize, GL46.GL_DYNAMIC_DRAW);
    }

    protected void bufferData() {
        buffer.flip();
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, uboId);
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, 0, buffer);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    protected void putVec4(Vector3f vec) {
        buffer.putFloat(vec.x);
        buffer.putFloat(vec.y);
        buffer.putFloat(vec.z);
        buffer.putFloat(1.0f);
    }

    protected void putFloat(float f) {
        buffer.putFloat(f);
    }

    protected void putInt(int i) {
        buffer.putInt(i);
    }

    protected void putDummy() {
        buffer.putInt(0);
    }

    protected void reset() {
        buffer = BufferUtils.createByteBuffer(structSize * elementCount);
    }

    protected void setElementCount(int elementCount) {
        this.elementCount = elementCount;
    }

    public int getUboId() {
        return uboId;
    }

    public void destroy() {
        GL46.glDeleteBuffers(uboId);
        buffer.clear();
    }
}
