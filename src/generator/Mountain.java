package generator;

import block.Blocks;
import game.Noise;
import org.joml.Random;

public class Mountain extends Biome {
    public static final int BIOME_ID = 3;
    private static final int TREE_CHANCE = 500;
    public Mountain() {
        super(BIOME_ID, "Mountain", 0.005f, 0.6f, Generator.HIGH_AMPLITUDE);
    }

    @Override
    public void generateTerrain(long seed, float noise, int x, int z, float nx, float nz, byte[][][] data,
                                int[][] surface, float amplitude) {
        for (int y = 0; y < noise - 1; y++) {
            data[x][z][y] = Blocks.getBlockID("Stone");
        }
        int surfy = (int)noise;
        if(surfy > 150) {
            data[x][z][surfy] = Blocks.getBlockID("Snow");
        } else {
            data[x][z][surfy] = Blocks.getBlockID("Stone");
        }
    }

    @Override
    public void generateFeatures(Random featuresRandom, int x, int y, int z, byte[][][] data) {
        if (data[x][z][y] == Blocks.getBlockID("Grass")) {
            if (Generator.randInt(featuresRandom, 0, TREE_CHANCE) <= 10) {
                Features.genTree(featuresRandom, data, x, y, z, 5, 9, 2, 3);
            }
        }
    }
}
