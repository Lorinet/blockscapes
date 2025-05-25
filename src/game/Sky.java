package game;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import mesh.DirectionalLight;
import mesh.Mesh;
import mesh.ModelManager;
import mesh.TextureCubemap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;


public class Sky {

    private static final float MAX_SUN_ELEVATION_ANGLE_DEGREES = 90.0f;
    private static final float MIN_SUN_ELEVATION_ANGLE_DEGREES = -20.0f;

    private static final float MAX_MOON_ELEVATION_ANGLE_DEGREES = 80.0f;
    private static final float MIN_MOON_ELEVATION_ANGLE_DEGREES = -10.0f;

    private static final Vector3f daySunColor = new Vector3f(1.0f, 1.0f, 1.0f);
    private static final Vector3f sunsetSunColor = new Vector3f(1.0f, 0.6f, 0.2f);
    private static final Vector3f nightSunColor = new Vector3f(0.0f, 0.0f, 0.0f);

    private static final Vector3f dayMoonColor = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Vector3f nightMoonColor = new Vector3f(0.2f, 0.25f, 0.3f);
    private static final Vector3f moonsetMoonColor = new Vector3f(0.15f, 0.2f, 0.25f);

    private static final Vector3f nightSkyColor = new Vector3f(0.05f, 0.05f, 0.1f);
    private static final Vector3f sunsetSkyColor = new Vector3f(0.9f, 0.5f, 0f);
    private static final Vector3f daySkyColor = new Vector3f(0.6f, 0.8f, 0.9f);


    private static final Vector3f[] sunGradient = {nightSunColor, nightSunColor, sunsetSunColor, daySunColor, daySunColor, sunsetSunColor, nightSunColor, nightSunColor};
    private static final float[] sunTimes = {0.0f, 4.0f, 7.0f, 9.0f, 18.0f, 21.0f, 23.0f, 24.0f};

    private static final Vector3f[] moonGradient = {nightMoonColor, nightMoonColor, nightMoonColor, dayMoonColor, dayMoonColor, nightMoonColor, nightMoonColor, nightMoonColor};
    private static final float[] moonTimes = {0.0f, 4.0f, 7.0f, 9.0f, 17.0f, 19.0f, 21.0f, 24.0f};

    private static final Vector3f[] skyGradient = {nightSkyColor, nightSkyColor, sunsetSkyColor, daySkyColor, daySkyColor, sunsetSkyColor, nightSkyColor, nightSkyColor};
    private static final float[] skyTimes = {0.0f, 4.0f, 7.0f, 10.0f, 19.0f, 20.0f, 23.0f, 24.0f};

    private static DirectionalLight sun;
    private static DirectionalLight moon;
    private static Vector3f skyColor;

    private static FloatArrayList celestialBodyVertexes = FloatArrayList.of(-0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f);

    private static FloatArrayList celestialBodyTextureCoords = FloatArrayList.of( 0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f);
    private static IntArrayList celestialBodyIndices = IntArrayList.of(0, 1, 2, 2, 3, 0);

    private static final float DISTANCE_TO_SKY = 1000.0f;
    private static final float MOON_SIZE  = 125.0f;
    private static final float SUN_SIZE = 150.0f;

    private static Vector3f skyAxis = new Vector3f(-1f, 0f, -0.5f);

    private static TextureCubemap textureCubemap;
    private static Mesh skyboxMesh;

    private static Mesh sunModel;
    private static Mesh moonModel;

    public static void init() {
        sun = new DirectionalLight(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, 0.0f));
        moon = new DirectionalLight(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, 0.0f));
        skyColor = new Vector3f(0, 0, 0);
        ModelManager.loadStaticTexture("sun.png");
        ModelManager.loadStaticTexture("moon.png");
        sunModel = ModelManager.createFlatModelUsingStaticTexture("sun.png", celestialBodyVertexes, celestialBodyTextureCoords, celestialBodyIndices);
        moonModel = ModelManager.createFlatModelUsingStaticTexture("moon.png", celestialBodyVertexes, celestialBodyTextureCoords, celestialBodyIndices);
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        textureCubemap = new TextureCubemap("sky");
        skyboxMesh = ModelManager.getModel("skycube");
    }

    public static void unload() {
        sunModel.destroy();
        moonModel.destroy();
        sunModel = null;
        moonModel = null;
        textureCubemap.destroy();
        textureCubemap = null;
        skyboxMesh = null;
    }

    public static void update(float time) {
        //System.out.println("TIME: " + time);
        float normalizedTime = time / 24.0f;

        float sinusSun = (float) Math.sin(normalizedTime * 2.0f * Math.PI - Math.PI / 2.0f);
        float sunAngle = MIN_SUN_ELEVATION_ANGLE_DEGREES + (MAX_SUN_ELEVATION_ANGLE_DEGREES - MIN_SUN_ELEVATION_ANGLE_DEGREES) * (sinusSun * 0.5f + 0.5f);
        float sunAngleRadian = (float) Math.toRadians(sunAngle);

        float sunAzimute = normalizedTime * 2.0f * (float) Math.PI + (float) Math.toRadians(270.0f);

        float sunX = (float) (Math.cos(sunAngleRadian) * Math.sin(sunAzimute));
        float sunY = (float) Math.sin(sunAngleRadian);
        float sunZ = (float) (Math.cos(sunAngleRadian) * Math.cos(sunAzimute));
        sun.setDirection(new Vector3f(sunX, sunY, sunZ).normalize());

        float moonNormalizedTime = (normalizedTime + 0.5f) % 1.0f;

        float sinusMoon = (float) Math.sin(moonNormalizedTime * 2.0f * Math.PI - Math.PI / 2.0f);
        float moonAngle = MIN_MOON_ELEVATION_ANGLE_DEGREES + (MAX_MOON_ELEVATION_ANGLE_DEGREES - MIN_MOON_ELEVATION_ANGLE_DEGREES) * (sinusMoon * 0.5f + 0.5f);
        float moonAngleRadian = (float) Math.toRadians(moonAngle);

        float moonAzimute = moonNormalizedTime * 2.0f * (float) Math.PI + (float) Math.toRadians(270.0f);

        float moonX = (float) (Math.cos(moonAngleRadian) * Math.sin(moonAzimute));
        float moonY = (float) Math.sin(moonAngleRadian);
        float moonZ = (float) (Math.cos(moonAngleRadian) * Math.cos(moonAzimute));
        moon.setDirection(new Vector3f(moonX, moonY, moonZ).normalize());

        sun.setColor(interpole(time, sunTimes, sunGradient));
        moon.setColor(interpole(time, moonTimes, moonGradient));
        skyColor = interpole(time, skyTimes, skyGradient);

    }

    private static Vector3f interpole(float time, float[] times, Vector3f[] grad) {
        for (int i = 0; i < times.length - 1; i++) {
            if (time >= times[i] && time < times[i + 1]) {
                float blendFactor = (time - times[i]) / (times[i + 1] - times[i]);
                return new Vector3f(grad[i]).lerp(grad[i + 1], blendFactor);
            }
        }
        throw new RuntimeException("No gradient found for time " + time);
    }

    private static Matrix4f getTransformation(Vector3f direction, float size) {
        Vector3f celestialPosition = new Vector3f(direction).mul(DISTANCE_TO_SKY);
        celestialPosition.add(Renderman.getPlayer().getPosition());
        Matrix4f celestialXform = new Matrix4f();
        celestialXform.translate(celestialPosition);
        Matrix4f anticameraRotation = new Matrix4f(Renderman.getViewMatrix()).invert().setTranslation(0,0,0);
        celestialXform.mul(anticameraRotation);
        celestialXform.scale(size);
        return celestialXform;
    }

    public static Matrix4f getSunTransformation() {
        return getTransformation(sun.getDirection(), SUN_SIZE);
    }

    public static Matrix4f getMoonTransformation() {
        return getTransformation(moon.getDirection(), MOON_SIZE);
    }

    public static Matrix4f getStarsRotation() {
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.rotate(StageManager.getGameTime() / 24.0f * 2.0f * (float) Math.PI, skyAxis);
        return rotationMatrix;
    }

    public static Mesh getSunModel() {
        return sunModel;
    }

    public static Mesh getMoonModel() {
        return moonModel;
    }

    public static DirectionalLight getSun() {
        return sun;
    }

    public static DirectionalLight getMoon() {
        return moon;
    }

    public static Vector3f getSkyColor() {
        return skyColor;
    }

    public static int getSkyboxTextureID() {
        return textureCubemap.getTextureID();
    }

    public static Mesh getSkyboxModel() {
        return skyboxMesh;
    }
}
