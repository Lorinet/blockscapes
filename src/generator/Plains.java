package generator;

import block.Blocks;
import game.Noise;
import org.joml.Random;

import static generator.Features.genCactus;
import static generator.Generator.randInt;

public class Plains extends Biome {
    public static final int BIOME_ID = 2;
    private static final int TALL_GRASS_CHANCE = 200;
    public Plains() {
        super(BIOME_ID, "Plains", 0.0025f, 0.2f, Generator.LOW_AMPLITUDE);
    }

    @Override
    public void generateTerrain(long seed, float noise, int x, int z, float nx, float nz, byte[][][] data,
                                int[][] surface, float amplitude) {
        float dirt_layer = Noise.noise2(seed, nx * Generator.NOISE_SCALE, nz * Generator.NOISE_SCALE) * amplitude / 2 + 2;
        for (int y = 0; y < noise - dirt_layer; y++) {
            data[x][z][y] = Blocks.getBlockID("Stone");
        }
        for (int y = (int) noise - (int) dirt_layer; y < noise && y >= 0; y++) {
            if (y >= noise - 1) {
                data[x][z][y] = Blocks.getBlockID("Grass");
                surface[x][z] = y;
            } else {
                data[x][z][y] = Blocks.getBlockID("Dirt");
            }
        }
    }

    @Override
    public void generateFeatures(Random featuresRandom, int x, int y, int z, byte[][][] data) {
        if (Generator.randInt(featuresRandom, 0, TALL_GRASS_CHANCE) <= 10) {
            data[x][z][y + 1] = Blocks.getBlockID("Tall Grass");
        }
    }
}
