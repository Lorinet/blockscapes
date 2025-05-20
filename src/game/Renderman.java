package game;

import mesh.DirectionalLight;
import mesh.DirectionalLightArray;
import mesh.Mesh;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import shader.EntityShader;
import shader.SkyShader;
import shader.UIShader;
import ui.UIManager;

import static org.lwjgl.opengl.GL11.*;

public class Renderman {
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private static final float MAX_SUN_ANGLE = 90.0f;
    private static final float MIN_SUN_ANGLE = -20.0f;
    private static final float MAX_MOON_ANGLE = 80.0f;
    private static final float MIN_MOON_ANGLE = -10.0f;

    private static EntityShader entityShader;
    private static UIShader uiShader;
    private static SkyShader skyShader;
    private static Matrix4f projectionMatrix;
    private static Matrix4f viewMatrix;
    private static Matrix4f projectionViewMatrix;

    private static DirectionalLightArray directionalLightArray;
    private static DirectionalLight[] directionalLights = {new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0)), new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0))};

    private static FrustumIntersection frustumIntersection = new FrustumIntersection();
    private static Player player;
    private static int renderedEntities = 0;
    private static int culledEntities = 0;

    private static int uiTickMod = 0;

    public static void init() {
        entityShader = new EntityShader();
        entityShader.use();
        directionalLightArray = new DirectionalLightArray(directionalLights);
        createMatrixes();
        entityShader.loadProjection(projectionMatrix);
        entityShader.disable();
        uiShader = new UIShader();
        skyShader = new SkyShader();
        skyShader.use();
        skyShader.loadProjection(projectionMatrix);
        skyShader.disable();
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    public static void unload() {
        entityShader.destroy();
        entityShader = null;
        directionalLightArray.destroy();
        uiShader.destroy();
        uiShader = null;
        skyShader.destroy();
        skyShader = null;
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
        entityShader.loadViewPos(translation);
        entityShader.disable();
        skyShader.use();
        skyShader.loadView(viewMatrix);
        skyShader.disable();
    }

    public static void clearScreen(Vector3f color) {
        GL46.glClearColor(color.x, color.y, color.z, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void render() {
        renderedEntities = 0;
        culledEntities = 0;
        if (StageManager.getInGame()) {
            renderSky();
        } else {
            clearScreen(new Vector3f(0.7f, 0.9f, 1.0f));
        }
        for (Entity e : StageManager.getEntities()) {
            renderEntity(e);
        }
        renderUI();
        Window.update();
    }

    public static void renderUI() {
        if (uiTickMod >= 10) {
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
        errorCheck("ui");
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

        entityShader.loadMaterials(entity.getModel().getMaterialArray());
        //directionalLightArray.update(directionalLights);
        entityShader.loadDirectionalLights(directionalLightArray);
        entityShader.loadTextures();
        GL46.glBindVertexArray(model.getVAO());
        GL46.glEnableVertexAttribArray(0);
        GL46.glEnableVertexAttribArray(1);
        GL46.glEnableVertexAttribArray(2);
        GL46.glEnableVertexAttribArray(3);
        GL46.glEnableVertexAttribArray(4);
        GL46.glDrawElements(GL46.GL_TRIANGLES, model.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glDisableVertexAttribArray(4);
        GL46.glDisableVertexAttribArray(3);
        GL46.glDisableVertexAttribArray(2);
        GL46.glDisableVertexAttribArray(1);
        GL46.glDisableVertexAttribArray(0);
        GL46.glBindVertexArray(0);
        errorCheck("entity");
        entityShader.unloadTextures();
        entityShader.disable();
    }

    private static void renderSky() {
        clearScreen(Sky.getSkyColor());
        skyShader.use();
        skyShader.loadTransformation(Sky.getSunTransformation());
        GL46.glBindVertexArray(Sky.getSunModel().getVAO());
        GL46.glEnableVertexAttribArray(0);
        GL46.glEnableVertexAttribArray(1);
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, Sky.getSunModel().getTextures()[0]);
        GL46.glDrawElements(GL46.GL_TRIANGLES, Sky.getSunModel().getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glDisableVertexAttribArray(1);
        GL46.glDisableVertexAttribArray(0);
        errorCheck("sun1");
        skyShader.loadTransformation(Sky.getMoonTransformation());
        GL46.glBindVertexArray(Sky.getMoonModel().getVAO());
        GL46.glEnableVertexAttribArray(0);
        GL46.glEnableVertexAttribArray(1);
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, Sky.getMoonModel().getTextures()[0]);
        GL46.glDrawElements(GL46.GL_TRIANGLES, Sky.getMoonModel().getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glDisableVertexAttribArray(1);
        GL46.glDisableVertexAttribArray(0);
        GL46.glBindVertexArray(0);
        skyShader.disable();
        errorCheck("sun");
        setSun();
    }

    private static void errorCheck(String where) {
        int error = GL46.glGetError();
        if (error != GL46.GL_NO_ERROR) {
            System.err.println(where + " OpenGL Error: " + error);
        }
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

    private static void setSun() {
        Sky.update(StageManager.getGameTime());
        directionalLights[0] = Sky.getSun();
        directionalLights[1] = Sky.getMoon();
        directionalLightArray.update(directionalLights);
    }

    public static Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
