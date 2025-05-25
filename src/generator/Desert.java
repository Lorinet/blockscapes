package generator;

import block.Blocks;
import game.Noise;
import org.joml.Random;

import static generator.Features.genCactus;
import static generator.Generator.randInt;

public class Desert extends Biome {
    public static final int BIOME_ID = 1;
    private static final int CACTUS_CHANCE = 700;
    public Desert() {
        super(BIOME_ID, "Forest", 0.004f, 0.4f, Generator.MEDIUM_AMPLITUDE);
    }

    @Override
    public void generateTerrain(long seed, float noise, int x, int z, float nx, float nz, byte[][][] data,
                                int[][] surface, float amplitude) {
        float dirt_layer = Noise.noise2(seed, nx * Generator.NOISE_SCALE, nz * Generator.NOISE_SCALE) * amplitude / 2 + 2;
        for (int y = 0; y < noise - dirt_layer; y++) {
            data[x][z][y] = Blocks.getBlockID("Stone");
        }
        for (int y = (int) noise - (int) dirt_layer; y < noise && y >= 0; y++) {
            if (y >= noise - 2) {
                data[x][z][y] = Blocks.getBlockID("Sand");
                surface[x][z] = y;
            } else {
                data[x][z][y] = Blocks.getBlockID("Sandstone");
            }
        }
    }

    @Override
    public void generateFeatures(Random featuresRandom, int x, int y, int z, byte[][][] data) {
        if (data[x][z][y] == Blocks.getBlockID("Sand")) {
            if (randInt(featuresRandom, 0, CACTUS_CHANCE) <= 10) {
                genCactus(featuresRandom, data, x, y, z);
            }
        }
    }
}
