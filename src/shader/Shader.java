package shader;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public abstract class Shader {
    protected final int program;
    protected final int vertexShader;
    protected final int fragmentShader;

    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public Shader(String name) {
        vertexShader = GL46.glCreateShader(GL46.GL_VERTEX_SHADER);
        fragmentShader = GL46.glCreateShader(GL46.GL_FRAGMENT_SHADER);

        String vertexCode;
        try {
            vertexCode = Files.lines(Paths.get("shaders", name + ".vert.glsl"))
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String fragmentCode;
        try {
            fragmentCode = Files.lines(Paths.get("shaders", name + ".frag.glsl"))
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Shader name: " + name);
        GL46.glShaderSource(vertexShader, vertexCode);
        GL46.glCompileShader(vertexShader);
        System.out.println(GL46.glGetShaderInfoLog(vertexShader));
        System.out.println("Frag");
        GL46.glShaderSource(fragmentShader, fragmentCode);
        GL46.glCompileShader(fragmentShader);
        System.out.println(GL46.glGetShaderInfoLog(fragmentShader));

        program = GL46.glCreateProgram();
        GL46.glAttachShader(program, vertexShader);
        GL46.glAttachShader(program, fragmentShader);

        bindParameters();

        GL46.glLinkProgram(program);

        GL46.glDetachShader(program, vertexShader);
        GL46.glDetachShader(program, fragmentShader);

        GL46.glDeleteShader(vertexShader);
        GL46.glDeleteShader(fragmentShader);
    }

    public void use() {
        GL46.glUseProgram(program);
    }

    public void disable() {
        GL46.glUseProgram(0);
    }

    public void destroy() {
        disable();
        GL46.glDetachShader(program, vertexShader);
        GL46.glDetachShader(program, fragmentShader);
        GL46.glDeleteShader(vertexShader);
        GL46.glDeleteShader(fragmentShader);
        GL46.glDeleteProgram(program);
    }

    protected abstract void bindParameters();

    protected void bindParameter(int vbo, String parameter) { GL46.glBindAttribLocation(program, vbo, parameter); }

    protected int getUniformParameterLocation(String name) {
        return GL46.glGetUniformLocation(program, name);
    }

    protected void loadFloat(String name, float value) {
        GL46.glUniform1f(getUniformParameterLocation(name), value);
    }

    protected void loadInt(String name, int value) {
        GL46.glUniform1i(getUniformParameterLocation(name), value);
    }

    protected void loadVec2(String name, float x, float y) {
        GL46.glUniform2f(getUniformParameterLocation(name), x, y);
    }

    protected void loadVec3(String name, float x, float y, float z) {
        GL46.glUniform3f(getUniformParameterLocation(name), x, y, z);
    }

    protected void loadVec4(String name, float x, float y, float z, float w) {
        GL46.glUniform4f(getUniformParameterLocation(name), x, y, z, w);
    }

    protected void loadBool(String name, boolean value) {
        GL46.glUniform1i(getUniformParameterLocation(name), value ? 1 : 0);
    }

    protected void loadMatrix(String name, Matrix4f matrix) {
        matrixBuffer.clear();
        float[] xfl = new float[16];
        matrix.get(xfl);
        matrixBuffer.put(xfl);
        matrixBuffer.flip();
        GL46.glUniformMatrix4fv(getUniformParameterLocation(name), false, matrixBuffer);
    }

    protected void loadFloatBuffer(String name, FloatBuffer buffer) {
        GL46.glUniform1fv(getUniformParameterLocation(name), buffer);
    }

    protected void loadIntBuffer(String name, IntBuffer buffer) {
        GL46.glUniform1iv(getUniformParameterLocation(name), buffer);
    }

}
