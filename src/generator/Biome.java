package generator;

import game.Noise;
import org.joml.Random;

public class Biome {
    private int biomeID;
    private String name;
    private int terrainAmplitude;
    private float noiseScale;
    private float noiseThreshold;

    protected Biome(int biomeID, String name, float noiseScale, float noiseThreshold, int terrainAmplitude) {
        this.biomeID = biomeID;
        this.name = name;
        this.noiseScale = noiseScale;
        this.noiseThreshold = noiseThreshold;
        this.terrainAmplitude = terrainAmplitude;
    }

    public int getTerrainAmplitude() {
        return terrainAmplitude;
    }

    public float getNoiseScale() {
        return noiseScale;
    }

    public float getNoiseThreshold() {
        return noiseThreshold;
    }

    public String getName() {
        return name;
    }

    public int getBiomeID() {
        return biomeID;
    }

    public void generateTerrain(long seed, float noise, int x, int z, float nx, float nz, byte[][][] data,
                                int[][] surface, float amplitude) {

    }

    public void generateFeatures(Random featuresRandom, int x, int y, int z, byte[][][] data) {

    }

    public float getBiomeMapNoise(long seed, float x, float z) {
        return Noise.noise2(seed + getBiomeID(), x * noiseScale, z * noiseScale);
    }
}
