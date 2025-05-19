package game;

import mesh.Mesh;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import shader.EntityShader;
import shader.UIShader;
import ui.UIManager;

import static org.lwjgl.opengl.GL11.*;

public class Renderman {
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private static EntityShader entityShader;
    private static UIShader uiShader;
    private static Matrix4f projectionMatrix;
    private static Matrix4f viewMatrix;
    private static Matrix4f projectionViewMatrix;
    private static FrustumIntersection frustumIntersection = new FrustumIntersection();
    private static Player player;
    private static int renderedEntities = 0;
    private static int culledEntities = 0;

    private static int uiTickMod = 0;

    public static void init() {
        entityShader = new EntityShader();
        entityShader.use();
        createMatrixes();
        entityShader.loadProjection(projectionMatrix);
        entityShader.initTextureSampler();
        entityShader.disable();
        uiShader = new UIShader();
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    public static void unload() {
        entityShader.destroy();
        entityShader = null;
        uiShader.destroy();
        uiShader = null;
        player = null;
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        GL46.glDisable(GL46.GL_CULL_FACE);
    }


    public static void perspective(Vector3f translation, Vector3f rotation) {
        viewMatrix = MathUtils.createViewMatrix(translation, rotation);
        projectionViewMatrix = new Matrix4f(projectionMatrix);
        projectionViewMatrix.mul(viewMatrix);
        entityShader.use();
        entityShader.loadView(viewMatrix);
        entityShader.disable();
    }

    public static void clearScreen() {
        GL46.glClearColor(0.72f, 0.96f, 1f, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void render() {
        renderedEntities = 0;
        culledEntities = 0;
        clearScreen();
        for (Entity e : StageManager.getEntities()) {
            renderEntity(e);
        }
        renderUI();
        Window.update();
    }

    public static void renderUI() {
        if(uiTickMod >= 10) {
            UIManager.draw();
            uiTickMod = 0;
        }
        uiTickMod++;
        uiShader.use();
        GL46.glClear(GL_DEPTH_BUFFER_BIT);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_ONE, GL46.GL_ONE_MINUS_SRC_ALPHA);
        GL46.glBindVertexArray(UIManager.getModel().getVAO());
        GL46.glEnableVertexAttribArray(0);
        GL46.glEnableVertexAttribArray(1);
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, UIManager.getModel().getTextures()[0]);
        GL46.glDrawElements(GL46.GL_TRIANGLES, UIManager.getModel().getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glDisableVertexAttribArray(1);
        GL46.glDisableVertexAttribArray(0);
        GL46.glBindVertexArray(0);
        GL46.glDisable(GL46.GL_BLEND);
        uiShader.disable();
        int error = GL46.glGetError();
        if (error != GL46.GL_NO_ERROR) {
            System.err.println("UI OpenGL Error: " + error);
        }
    }

    public static void renderEntity(Entity entity) {
        frustumIntersection.set(projectionViewMatrix);
        if (!frustumIntersection.testAab(entity.getHitbox().getStart(), entity.getHitbox().getEnd())) {
            culledEntities++;
            return;
        }
        renderedEntities++;
        Mesh model = entity.getModel();
        entityShader.use();
        Matrix4f transformation = MathUtils.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
        entityShader.loadTransformation(transformation);

        entityShader.loadMaterials(entity.getModel().getMaterialsIntBuffer(), entity.getModel().getMaterialsFloatBuffer());
        entityShader.loadTextures(entity.getModel().getTextures());
        GL46.glBindVertexArray(model.getVAO());
        GL46.glEnableVertexAttribArray(0);
        GL46.glEnableVertexAttribArray(1);
        GL46.glEnableVertexAttribArray(2);
        GL46.glEnableVertexAttribArray(3);
        GL46.glEnableVertexAttribArray(4);
        GL46.glDrawElements(GL46.GL_TRIANGLES, model.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);

        int error = GL46.glGetError();
        if (error != GL46.GL_NO_ERROR) {
            System.err.println("UI OpenGL Error: " + error);
        }
        GL46.glDisableVertexAttribArray(4);
        GL46.glDisableVertexAttribArray(3);
        GL46.glDisableVertexAttribArray(2);
        GL46.glDisableVertexAttribArray(1);
        GL46.glDisableVertexAttribArray(0);
        GL46.glBindVertexArray(0);
        entityShader.unloadTextures(entity.getModel().getTextures().length);
        entityShader.disable();
    }

    public static Player getPlayer() {
        return player;
    }

    public static void setPlayer(Player player) {
        Renderman.player = player;
    }

    private static void createMatrixes() {
        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(FOV), (float) Window.getWidth() / (float) Window.getHeight(), NEAR_PLANE, FAR_PLANE);
        viewMatrix = MathUtils.createViewMatrix(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        projectionViewMatrix = new Matrix4f(projectionMatrix);
        projectionViewMatrix.mul(viewMatrix);
    }

    public static int getRenderedEntities() {
        return renderedEntities;
    }

    public static int getCulledEntities() {
        return culledEntities;
    }
}
