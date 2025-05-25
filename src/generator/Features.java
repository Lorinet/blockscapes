package generator;

import block.Blocks;
import level.Chunk;
import org.joml.Random;

public class Features {
    public static void genTree(Random treeRandom, byte[][][] blocks, int x, int y, int z, int minHeight, int maxHeight, int minRadius, int maxRadius) {
        int height = Generator.randInt(treeRandom, minHeight, maxHeight);
        int leafRad = Generator.randInt(treeRandom, minRadius, maxRadius + 1);
        int leafStartHeight = height - leafRad - 1;
        if (x < leafRad || x >= Chunk.SIZE_XZ - leafRad || z < leafRad || z >= Chunk.SIZE_XZ - leafRad) {
            return;
        }
        for (int i = y + 1; i < y + height; i++) {
            blocks[x][z][i] = Blocks.getBlockID("Wood");
            if (i >= leafStartHeight + y) {
                for (int lx = -leafRad; lx <= leafRad; lx++) {
                    for (int lz = -leafRad; lz <= leafRad; lz++) {
                        if ((lx != 0 || lz != 0) && !((lx == -leafRad && lz == -leafRad) || (lx == leafRad && lz == -leafRad) || (lx == -leafRad && lz == leafRad) || (lx == leafRad && lz == leafRad))) {
                            int decay = Generator.randInt(treeRandom, 0, 30);
                            if (decay > 0) {
                                int objX = x + lx;
                                int objZ = z + lz;
                                if (Generator.validate(objX, i, objZ)) {
                                    blocks[objX][objZ][i] = Blocks.getBlockID("Leaves");
                                }
                            }
                        }
                    }
                }
                if (leafRad != 1) {
                    leafRad -= 1;
                }
            }
        }
        blocks[x][z][y + height] = Blocks.getBlockID("Leaves");
    }

    public static void genCactus(Random treeRandom, byte[][][] blocks, int x, int y, int z) {
        int height = Generator.randInt(treeRandom , 1, 4);
        for (int i = y + 1; i < y + height; i++) {
            blocks[x][z][i] = Blocks.getBlockID("Cactus");

        }
    }
}
