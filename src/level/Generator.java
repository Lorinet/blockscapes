package level;

import game.Noise;
import block.Blocks;

public class Generator {
    public static final float NOISE_SCALE = 0.01f;
    public static final float CAVE_NOISE_SCALE = 0.06f;
    public static final float CAVE_SCALE = 0.4f;
    public static final int TERRAIN_AMPLITUDE = 20;
    public static final int BASE_LAYER = 128;


    public static byte[][][] generateChunkData(int chunkX, int chunkY) {
        long seed = LevelManager.getSeed();
        byte[][][] data = new byte[Chunk.SIZE_XZ][Chunk.SIZE_XZ][Chunk.SIZE_Y];
        for (int x = 0; x < Chunk.SIZE_XZ; x++) {
            for (int z = 0; z < Chunk.SIZE_XZ; z++) {
                float nx = chunkX * Chunk.SIZE_XZ + x;
                float nz = chunkY * Chunk.SIZE_XZ + z;
                float noise = Noise.noise2(seed, nx * NOISE_SCALE, nz * NOISE_SCALE) * TERRAIN_AMPLITUDE + BASE_LAYER;
                float dirt_layer = Noise.noise2(seed, nx * NOISE_SCALE, nz * NOISE_SCALE) * TERRAIN_AMPLITUDE / 2 + 2;
                for (int y = 0; y < noise - dirt_layer; y++) {
                    data[x][z][y] = Blocks.getBlockID("Stone");
                }
                for (int y = (int) noise - (int) dirt_layer; y < noise && y >= 0; y++) {
                    if (y >= noise - 1) {
                        data[x][z][y] = Blocks.getBlockID("Grass");
                    } else {
                        data[x][z][y] = Blocks.getBlockID("Dirt");
                    }
                }

                for (int y = 2; y < Chunk.SIZE_Y; y++) {
                    float cave = Noise.noise3_ImproveXZ(seed, nx * CAVE_NOISE_SCALE, y * CAVE_NOISE_SCALE, nz * CAVE_NOISE_SCALE);
                    if (cave > CAVE_SCALE) {
                        data[x][z][y] = Blocks.ID_AIR;
                    }
                }
            }
        }
        return data;
    }
}
