package game;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import mesh.DirectionalLight;
import mesh.Mesh;
import mesh.ModelManager;
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
    private static final Vector3f nightMoonColor = new Vector3f(0.6f, 0.7f, 0.8f);
    private static final Vector3f moonsetMoonColor = new Vector3f(0.3f, 0.4f, 0.5f);

    private static final Vector3f nightSkyColor = new Vector3f(0.05f, 0.05f, 0.1f);
    private static final Vector3f sunsetSkyColor = new Vector3f(0.9f, 0.5f, 0f);
    private static final Vector3f daySkyColor = new Vector3f(0.6f, 0.8f, 0.9f);


    private static final Vector3f[] sunGradient = {nightSunColor, nightSunColor, sunsetSunColor, daySunColor, daySunColor, sunsetSunColor, nightSunColor, nightSunColor};
    private static final float[] sunTimes = {0.0f, 4.0f, 7.0f, 9.0f, 18.0f, 21.0f, 23.0f, 24.0f};

    private static final Vector3f[] moonGradient = {nightMoonColor, nightMoonColor, moonsetMoonColor, dayMoonColor, dayMoonColor, moonsetMoonColor, nightMoonColor, nightMoonColor};
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
    }

    public static void unload() {
        sunModel.destroy();
        moonModel.destroy();
        sunModel = null;
        moonModel = null;
    }

    public static void update(float time) {
        System.out.println("TIME: " + time);
        float normalizedTime = time / 24.0f;

        float sunElevationSineValue = (float) Math.sin(normalizedTime * 2.0f * Math.PI - Math.PI / 2.0f);
        float sunActualElevationDegrees = MIN_SUN_ELEVATION_ANGLE_DEGREES + (MAX_SUN_ELEVATION_ANGLE_DEGREES - MIN_SUN_ELEVATION_ANGLE_DEGREES) * (sunElevationSineValue * 0.5f + 0.5f);
        float sunActualElevationRad = (float) Math.toRadians(sunActualElevationDegrees);

        float sunAzimuthAngleRad = normalizedTime * 2.0f * (float) Math.PI + (float) Math.toRadians(270.0f);

        float sunX = (float) (Math.cos(sunActualElevationRad) * Math.sin(sunAzimuthAngleRad));
        float sunY = (float) Math.sin(sunActualElevationRad);
        float sunZ = (float) (Math.cos(sunActualElevationRad) * Math.cos(sunAzimuthAngleRad));
        sun.setDirection(new Vector3f(sunX, sunY, sunZ).normalize());

        float moonNormalizedTime = (normalizedTime + 0.5f) % 1.0f;

        float moonElevationSineValue = (float) Math.sin(moonNormalizedTime * 2.0f * Math.PI - Math.PI / 2.0f);
        float moonActualElevationDegrees = MIN_MOON_ELEVATION_ANGLE_DEGREES + (MAX_MOON_ELEVATION_ANGLE_DEGREES - MIN_MOON_ELEVATION_ANGLE_DEGREES) * (moonElevationSineValue * 0.5f + 0.5f);
        float moonActualElevationRad = (float) Math.toRadians(moonActualElevationDegrees);

        float moonAzimuthAngleRad = moonNormalizedTime * 2.0f * (float) Math.PI + (float) Math.toRadians(270.0f);

        float moonX = (float) (Math.cos(moonActualElevationRad) * Math.sin(moonAzimuthAngleRad));
        float moonY = (float) Math.sin(moonActualElevationRad);
        float moonZ = (float) (Math.cos(moonActualElevationRad) * Math.cos(moonAzimuthAngleRad));
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
}
