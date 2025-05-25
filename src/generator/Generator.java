package generator;

import block.Blocks;
import game.Noise;
import level.Chunk;
import level.LevelManager;
import org.joml.Random;

import java.util.HashMap;


public class Generator {
    public static final float NOISE_SCALE = 0.01f;

    public static final float CAVE_NOISE_SCALE = 0.06f;
    public static final float CAVE_SCALE = 0.4f;

    public static final float SANDBALL_NOISE_SCALE = 0.04f;
    public static final float SANDBALL_SCALE = 0.5f;


    public static final int HIGH_AMPLITUDE = 40;
    public static final int MEDIUM_AMPLITUDE = 20;
    public static final int LOW_AMPLITUDE = 1;
    public static final int BASE_LAYER = 128;
    public static final int WATER_LEVEL = 124;

    public static final int GOLD_CHANCE = 500;
    public static final int IRON_CHANCE = 300;
    public static final int DIAMOND_CHANCE = 1000;

    public static HashMap<Integer, Biome> biomes = new HashMap<>();

    static {
        biomes.put(Forest.BIOME_ID, new Forest());
        biomes.put(Desert.BIOME_ID, new Desert());
        biomes.put(Plains.BIOME_ID, new Plains());
        biomes.put(Mountain.BIOME_ID, new Mountain());
    }

    public static byte[][][] generateChunkData(int chunkX, int chunkY) {
        long seed = LevelManager.getSeed();
        Random featuresRandom = new Random(seed);
        byte[][][] data = new byte[Chunk.SIZE_XZ][Chunk.SIZE_XZ][Chunk.SIZE_Y];
        int[][] surface = new int[Chunk.SIZE_XZ][Chunk.SIZE_XZ];
        int[][] biomeMap = new int[Chunk.SIZE_XZ][Chunk.SIZE_XZ];
        // terrain
        for (int x = 0; x < Chunk.SIZE_XZ; x++) {
            for (int z = 0; z < Chunk.SIZE_XZ; z++) {
                int nx = chunkX * Chunk.SIZE_XZ + x;
                int nz = chunkY * Chunk.SIZE_XZ + z;
                float amp = biomeAmplitude(biomeMap, seed, x, z, nx, nz);
                float noise = Noise.noise2(seed, nx * NOISE_SCALE, nz * NOISE_SCALE) * amp + BASE_LAYER;
                biomes.get(biomeMap[x][z]).generateTerrain(seed, noise, x, z, nx, nz, data, surface, amp);

                for (int y = 2; y < Chunk.SIZE_Y; y++) {
                    float cave = Noise.noise3_ImproveXZ(seed, nx * CAVE_NOISE_SCALE, y * CAVE_NOISE_SCALE,
                            nz * CAVE_NOISE_SCALE);
                    if (cave > CAVE_SCALE) {
                        data[x][z][y] = Blocks.ID_AIR;
                    }

                    float sandball = Noise.noise3_ImproveXZ(seed, nx * SANDBALL_NOISE_SCALE, y * SANDBALL_NOISE_SCALE
                            , nz * SANDBALL_NOISE_SCALE);
                    if (sandball > SANDBALL_SCALE && y < noise && y > noise - 20) {
                        data[x][z][y] = Blocks.getBlockID("Sand");
                    }
                }

                for (int y = WATER_LEVEL; y >= 0; y--) {
                    if (data[x][z][y] != Blocks.ID_AIR) {
                        break;
                    }
                    data[x][z][y] = Blocks.getBlockID("Water");
                }
            }
        }
        byte idStone = Blocks.getBlockID("Stone");
        // features
        for (int x = 0; x < Chunk.SIZE_XZ; x++) {
            for (int z = 0; z < Chunk.SIZE_XZ; z++) {
                int sy = surface[x][z];
                biomes.get(biomeMap[x][z]).generateFeatures(featuresRandom, x, sy, z, data);
                for (int y = 0; y <= surface[x][z]; y++) {
                    if (data[x][z][y] == idStone) {
                        if (randInt(featuresRandom, 0, DIAMOND_CHANCE) <= 10) {
                            data[x][z][y] = Blocks.getBlockID("Diamond Ore");
                        } else if (randInt(featuresRandom, 0, GOLD_CHANCE) <= 10) {
                            data[x][z][y] = Blocks.getBlockID("Gold Ore");
                        } else if (randInt(featuresRandom, 0, IRON_CHANCE) <= 10) {
                            data[x][z][y] = Blocks.getBlockID("Iron Ore");
                        }
                    }
                }
            }
        }
        return data;
    }

    private static int biomeEx(long seed, int x, int z) {
        HashMap<Biome, Float> masses = new HashMap<>();
        float totalMass = 0;

        for (Biome biome : biomes.values()) {
            float noiseValue = biome.getBiomeMapNoise(seed, x, z);
            float intensity = (noiseValue + 1) * 0.5f;
            float mass = intensity * 2;
            masses.put(biome, mass);
            totalMass += mass;
        }

        if (totalMass == 0) {
            return Forest.BIOME_ID;
        }

        HashMap<Biome, Float> probabilities = new HashMap<>();
        for (Biome biome : biomes.values()) {
            probabilities.put(biome, masses.get(biome) / totalMass);
        }

        Biome selectedBiome = biomes.get(Forest.BIOME_ID);
        float maxProbability = -1;
        for (HashMap.Entry<Biome, Float> entry : probabilities.entrySet()) {
            if (entry.getValue() > maxProbability) {
                maxProbability = entry.getValue();
                selectedBiome = entry.getKey();
            }
        }
        return selectedBiome.getBiomeID();
    }

    private static float biomeAmplitude(int[][] biomeMap, long seed, int x, int z, int nx, int nz) {
        HashMap<Biome, Float> masses = new HashMap<>();
        for (Biome biome : biomes.values()) {
            masses.put(biome, 0.0f);
        }
        float totalMass = 0;
        int sampleRadius = 4;

        for (int dx = -sampleRadius; dx <= sampleRadius; dx++) {
            for (int dz = -sampleRadius; dz <= sampleRadius; dz++) {
                int sx = x + dx;
                int sz = z + dz;
                int biomeId = biomeEx(seed, nx + dx, nz + dz);
                if (sx >= 0 && sx < Chunk.SIZE_XZ && sz >= 0 && sz < Chunk.SIZE_XZ) {
                    biomeMap[sx][sz] = biomeId;
                }
                Biome sampledBiome = biomes.get(biomeId);
                float dis = (float) Math.sqrt(dx * dx + dz * dz);
                float mass = 1.0f / (dis + 1);
                masses.put(sampledBiome, masses.get(sampledBiome) + mass);
                totalMass += mass;
            }
        }

        if (totalMass == 0) {
            int currentBiomeId = biomeMap[x][z];
            return biomes.get(currentBiomeId).getTerrainAmplitude();
        }

        float blamp = 0;
        for (Biome biome : biomes.values()) {
            float probability = masses.get(biome) / totalMass;
            blamp += biome.getTerrainAmplitude() * probability;
        }

        return blamp;
    }


    public static boolean validate(int x, int y, int z) {
        return (x >= 0 && x < Chunk.SIZE_XZ && y >= 0 && y < Chunk.SIZE_Y && z >= 0 && z < Chunk.SIZE_XZ);
    }

    public static int randInt(Random rand, int origin, int bound) {
        return origin + (rand.nextInt(bound - origin));
    }
}
