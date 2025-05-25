package game;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import mesh.*;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import shader.*;
import ui.DebugOverlay;
import ui.UIManager;

import java.util.Arrays;

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
    private static SkyboxShader skyboxShader;
    private static ShadowMapShader shadowMapShader;
    private static ShadowMapDebugShader shadowMapDebugShader;
    private static PlayerOverlayShader playerOverlayShader;


    private static Matrix4f projectionMatrix;
    private static Matrix4f viewMatrix;
    private static Matrix4f projectionViewMatrix;

    private static DirectionalLightArray directionalLightArray;
    private static DirectionalLight[] directionalLights = {new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0
            , 1, 0)), new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0))};

    private static FrustumIntersection frustumIntersection = new FrustumIntersection();
    private static Player player;
    private static int renderedEntities = 0;
    private static int culledEntities = 0;

    private static int uiTickMod = 0;
    private static float shadowsRenderedAt = -1;

    private static Mesh testShadowMapSurfax;
    private static Mesh playerOverlaySurfax;

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
        skyboxShader = new SkyboxShader();
        skyboxShader.use();
        skyboxShader.loadProjection(projectionMatrix);
        skyboxShader.disable();
        shadowMapShader = new ShadowMapShader();
        shadowMapDebugShader = new ShadowMapDebugShader();
        testShadowMapSurfax = ModelManager.createFlatModelUsingStaticTexture(null, FloatArrayList.of(-1f, -0.5f, 0f,
                        -1f, -1f, 0f, -0.5f, -1f, 0f, -0.5f, -0.5f, 0f), FloatArrayList.of(0f, 0f, 0f, 1f, 1f, 1f, 1f
                        , 0f),
                IntArrayList.of(0, 1, 2, 2, 3, 0));
        playerOverlayShader = new PlayerOverlayShader();
        playerOverlaySurfax = ModelManager.createFlatModelUsingStaticTexture(null, new FloatArrayList(Arrays.asList(-1f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f, 1f, 1f, 0f)),
                new FloatArrayList(Arrays.asList(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f)),
                new IntArrayList(Arrays.asList(0, 1, 2, 2, 3, 0)));
        playerOverlayShader.use();
        playerOverlayShader.loadOverlayState(0);
        playerOverlayShader.disable();
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
        shadowMapShader.destroy();
        shadowMapShader = null;
        player = null;
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        GL46.glDisable(GL46.GL_CULL_FACE);
    }


    public static void perspective(Matrix4f viewMatrix, Vector3f translation) {
        Renderman.viewMatrix = viewMatrix;
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

    public static FrustumIntersection getFrustumIntersection() {
        return frustumIntersection;
    }

    public static void render() {
        renderedEntities = 0;
        culledEntities = 0;
        if (StageManager.getInGame()) {
            renderSky();
            if (StageManager.getSettings().getShadowsEnabled()) {
                renderShadowMap();
            }
            renderEntities();
            renderPlayerOverlay();
            if (((DebugOverlay) UIManager.getWidget("debugOverlay")).getActive()) {
                renderDebugShadowMap();
            }
        } else {
            clearScreen(new Vector3f(0.7f, 0.9f, 1.0f));
        }
        renderUI();
        Window.update();
    }

    public static void renderPlayerOverlay() {
        playerOverlayShader.use();
        playerOverlayShader.loadOverlayState(player.getUnderwater() ? 2 : 0);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBindVertexArray(playerOverlaySurfax.getVAO());
        GL46.glDrawElements(GL46.GL_TRIANGLES, playerOverlaySurfax.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glDisable(GL46.GL_BLEND);
        playerOverlayShader.disable();
    }

    public static void renderEntities() {
        entityShader.use();
        GL46.glActiveTexture(GL46.GL_TEXTURE1);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, ShadowMap.getDepthMapTextureId());
        entityShader.loadFancyTransparency(StageManager.getSettings().getFancyTransparency() ? 1 : 0);
        entityShader.loadShadowMap(1);
        for (Entity e : StageManager.getEntities()) {
            for (int i = 0; i < e.getModels().length; i++) {
                if (!e.getModels()[i].hasTransparency()) {
                    renderEntity(e, i);
                }
            }
        }
        if (StageManager.getSettings().getFancyTransparency()) {
            for (Entity e : StageManager.getEntities()) {
                for (int i = 0; i < e.getModels().length; i++) {
                    if (e.getModels()[i].hasTransparency()) {
                        renderEntity(e, i);
                    }
                }
            }
        }
    }

    public static void renderUI() {
        if (uiTickMod >= 10) {
            UIManager.draw();
            uiTickMod = 0;
        }
        uiTickMod++;
        uiShader.use();
        //GL46.glClear(GL_DEPTH_BUFFER_BIT);
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_ONE, GL46.GL_ONE_MINUS_SRC_ALPHA);
        GL46.glBindVertexArray(UIManager.getModel().getVAO());
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, UIManager.getModel().getTextures()[0]);
        GL46.glDrawElements(GL46.GL_TRIANGLES, UIManager.getModel().getVertexCount(), GL46.GL_UNSIGNED_INT, 0);

        GL46.glBindVertexArray(0);
        GL46.glDisable(GL46.GL_BLEND);
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        //uiShader.disable();
        errorCheck("ui");
    }

    public static void renderEntity(Entity entity, int modelIndex) {
        frustumIntersection.set(projectionViewMatrix);
        if (!frustumIntersection.testAab(entity.getHitbox().getStart(), entity.getHitbox().getEnd()) || !entity.getVisible()) {
            culledEntities++;
            return;
        }
        renderedEntities++;
        Mesh model = entity.getModels()[modelIndex];
        entityShader.use();
        Matrix4f transformation = MathUtils.createTransformationMatrix(entity.getPosition(), entity.getRotation(),
                entity.getScale());
        entityShader.loadTransformation(transformation);

        entityShader.loadMaterials(entity.getModels()[modelIndex].getMaterialArray());
        //directionalLightArray.update(directionalLights);
        entityShader.loadDirectionalLights(directionalLightArray);
        entityShader.loadTextures();
        GL46.glBindVertexArray(model.getVAO());
        if (entity.getModels()[modelIndex].hasTransparency()) {
            GL46.glDepthMask(false);
            GL46.glEnable(GL46.GL_BLEND);
            GL46.glBlendFunc(GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
            entityShader.loadRenderPass(1);
        } else {
            GL46.glDepthMask(true);
            entityShader.loadHurting(entity.getHurting());
            entityShader.loadRenderPass(0);
        }
        GL46.glCullFace(GL46.GL_BACK);
        GL46.glDrawElements(GL46.GL_TRIANGLES, model.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        if (entity.getModels()[modelIndex].hasTransparency()) {
            GL46.glDisable(GL46.GL_BLEND);
            GL46.glDepthMask(true);
        } else {
            entityShader.loadHurting(0);
        }
        errorCheck("entity");
        entityShader.unloadTextures();
        //entityShader.disable();
    }

    public static void renderEntityShadows(Entity entity, int modelIndex) {
        frustumIntersection.set(projectionViewMatrix);
        if (!frustumIntersection.testAab(entity.getHitbox().getStart(), entity.getHitbox().getEnd())) {
            return;
        }
        Mesh model = entity.getModels()[modelIndex];
        Matrix4f transformation = MathUtils.createTransformationMatrix(entity.getPosition(), entity.getRotation(),
                entity.getScale());
        shadowMapShader.loadTransformation(transformation);

        GL46.glBindVertexArray(model.getVAO());
        GL46.glDrawElements(GL46.GL_TRIANGLES, model.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);

        errorCheck("shadows");
    }

    private static void renderSky() {
        clearScreen(Sky.getSkyColor());
        GL46.glEnable(GL46.GL_BLEND);
        skyboxShader.use();
        skyboxShader.loadProjection(projectionMatrix);
        skyboxShader.loadView(viewMatrix);
        skyboxShader.loadRotation(Sky.getStarsRotation());
        skyboxShader.loadTexture(Sky.getSkyboxTextureID());
        skyboxShader.loadSunlightColor(Sky.getSun().getColor());
        GL46.glBindVertexArray(Sky.getSkyboxModel().getVAO());
        GL46.glDepthMask(false);
        GL46.glDisable(GL_CULL_FACE);
        GL46.glDrawElements(GL46.GL_TRIANGLES, Sky.getSkyboxModel().getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
        GL46.glEnable(GL_CULL_FACE);
        GL46.glDepthMask(true);
        skyboxShader.disable();

        skyShader.use();
        skyShader.loadTransformation(Sky.getSunTransformation());
        GL46.glBlendFunc(GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        GL46.glBindVertexArray(Sky.getSunModel().getVAO());
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, Sky.getSunModel().getTextures()[0]);
        GL46.glDrawElements(GL46.GL_TRIANGLES, Sky.getSunModel().getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        errorCheck("sun1");
        skyShader.loadTransformation(Sky.getMoonTransformation());
        GL46.glBindVertexArray(Sky.getMoonModel().getVAO());
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, Sky.getMoonModel().getTextures()[0]);
        GL46.glDrawElements(GL46.GL_TRIANGLES, Sky.getMoonModel().getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
        skyShader.disable();

        GL46.glDisable(GL46.GL_BLEND);

        errorCheck("sun");
        setSun();
    }

    private static void renderShadowMap() {
        float shadowTime = StageManager.getGameTimeLowRes(0.02f);
        if (shadowTime != shadowsRenderedAt) {
            shadowsRenderedAt = shadowTime;
        } else {
            return;
        }
        Matrix4f lightPerspective = ShadowMap.calculateLightPerspectiveMatrix(Sky.getSun().getDirection());
        entityShader.use();
        entityShader.loadLightPerspectiveMatrix(lightPerspective);
        entityShader.disable();
        shadowMapShader.use();
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, ShadowMap.getFboId());
        GL46.glViewport(0, 0, ShadowMap.getSize(), ShadowMap.getSize());
        GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        //GL46.glCullFace(GL46.GL_FRONT);

        shadowMapShader.loadLightPerspectiveTransformation(lightPerspective);
        for (Entity e : StageManager.getEntities()) {
            for (int i = 0; i < e.getModels().length; i++) {
                if (!e.getModels()[i].hasTransparency()) {
                    renderEntityShadows(e, i);
                }
            }
        }

        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
        GL46.glViewport(0, 0, Window.getWidth(), Window.getHeight());
        shadowMapShader.disable();

        //GL46.glCullFace(GL46.GL_BACK);
    }

    private static void renderDebugShadowMap() {
        shadowMapDebugShader.use();
        GL46.glBindVertexArray(testShadowMapSurfax.getVAO());
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, ShadowMap.getDepthMapTextureId());
        shadowMapDebugShader.loadTexture(0);
        GL46.glDrawElements(GL46.GL_TRIANGLES, testShadowMapSurfax.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    public static void errorCheck(String where) {
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
        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(FOV),
                (float) Window.getWidth() / (float) Window.getHeight(), NEAR_PLANE, FAR_PLANE);
        viewMatrix = MathUtils.createViewMatrixFirstPerson(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
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

    public static Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public static Matrix4f getProjectionViewMatrix() {
        return projectionViewMatrix;
    }
}
