package block;

import com.google.gson.Gson;
import mesh.Material;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Blocks {
    public static final byte ID_AIR = 0;
    public static final String TEXTURE_ATLAS_NAME = "stone.png";
    public static ArrayList<Material> blockMaterials = new ArrayList<>(List.of(new Material[]{new Material(TEXTURE_ATLAS_NAME, TEXTURE_ATLAS_NAME),}));

    private static ArrayList<Block> blockTypes;

    private static Map<Byte, Block> blocks;

    private static Map<String, Byte> blockIds;

    private static List<Byte> transparentBlocks;

    public static void init() {
        blockTypes = new ArrayList<>();
        File[] dataFiles = new File("blocks").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".dat");
            }
        });

        Gson jsp = new Gson();

        assert dataFiles != null;
        for (File file : dataFiles) {
            String json = null;
            try {
                json = Files.readString(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BlockDataFile blockData = jsp.fromJson(json, BlockDataFile.class);
            blockTypes.add(blockData.toBlock());
        }

        blocks = new HashMap<>() {
            {
                for (Block block : blockTypes) {
                    put(block.getId(), block);
                }
            }
        };

        blockIds = new HashMap<>() {
            {
                for (Map.Entry<Byte, Block> entry : blocks.entrySet()) {
                    put(entry.getValue().getName(), entry.getKey());
                }
            }
        };
        transparentBlocks = blockTypes.stream().filter(Block::getIsTransparent).map(Block::getId).toList();
    }

    public static void unload() {
        blockTypes.clear();
        blocks.clear();
        blockIds.clear();
        transparentBlocks = null;
        blockTypes = null;
        blocks = null;
        blockIds = null;
    }

    public static Block getBlock(int id) {
        return blocks.get((byte) id);
    }

    public static byte getBlockID(String name) {
        return blockIds.get(name);
    }

    public static Collection<Block> getBlocks() {
        return blocks.values();
    }

    public static boolean isTransparent(byte b) {
        return transparentBlocks.contains(b);
    }
}
