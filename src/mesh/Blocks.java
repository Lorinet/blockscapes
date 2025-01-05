package mesh;

import game.AudioController;

import java.util.*;

import static java.util.Map.entry;
import static mesh.Block.adjacentFaces;

public class Blocks {
    public static byte ID_AIR = 0;

    private static final Map<String, AudioController> sounds = Map.ofEntries(
            entry("footsteps_dirt", new AudioController(-10f, new String[]{
                    "footsteps_dirt1",
                    "footsteps_dirt2",
                    "footsteps_dirt3",
                    "footsteps_dirt4",
                    "footsteps_dirt5",
                    "footsteps_dirt6",
            })),
            entry("footsteps_grass", new AudioController(-15f, new String[]{
                    "footsteps_grass1",
                    "footsteps_grass2",
                    "footsteps_grass3",
                    "footsteps_grass4",
                    "footsteps_grass5",
                    "footsteps_grass6",
                    "footsteps_grass7",
            })),
            entry("footsteps_stone", new AudioController(-10f, new String[]{
                    "footsteps_stone1",
                    "footsteps_stone2",
                    "footsteps_stone3",
                    "footsteps_stone4",
                    "footsteps_stone5",
                    "footsteps_stone6",
                    "footsteps_stone7",
                    "footsteps_stone8",
            })),
            entry("footsteps_wood", new AudioController(-10f, new String[]{
                    "footsteps_wood1",
                    "footsteps_wood2",
                    "footsteps_wood3",
                    "footsteps_wood4",
            })),
            entry("block_stone", new AudioController(2.0f, new String[]{"block_stone1", "block_stone2", "block_stone3", "block_stone4"})),
            entry("block_wood", new AudioController(2.0f, new String[]{"block_wood1", "block_wood2", "block_wood3"})),
            entry("block_dirt", new AudioController(2.0f, new String[]{"block_dirt1", "block_dirt2", "block_dirt3", "block_dirt4"})));


    private static final List<Block> blockTypes = Arrays.asList(
            new Block(ID_AIR, "Air", false, true, Map.ofEntries(), null, null, null),
            new Block(-127, "Stone", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(0)),
                    entry(adjacentFaces[1], FaceVertex.left(0)),
                    entry(adjacentFaces[2], FaceVertex.up(0)),
                    entry(adjacentFaces[3], FaceVertex.down(0)),
                    entry(adjacentFaces[4], FaceVertex.forward(0)),
                    entry(adjacentFaces[5], FaceVertex.back(0))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-126, "Dirt", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(3)),
                    entry(adjacentFaces[1], FaceVertex.left(3)),
                    entry(adjacentFaces[2], FaceVertex.up(3)),
                    entry(adjacentFaces[3], FaceVertex.down(3)),
                    entry(adjacentFaces[4], FaceVertex.forward(3)),
                    entry(adjacentFaces[5], FaceVertex.back(3))
            ),
                    Block.blockCollider, sounds.get("footsteps_dirt"), sounds.get("block_dirt")),
            new Block(-125, "Grass", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(2)),
                    entry(adjacentFaces[1], FaceVertex.left(2)),
                    entry(adjacentFaces[2], FaceVertex.up(1)),
                    entry(adjacentFaces[3], FaceVertex.down(3)),
                    entry(adjacentFaces[4], FaceVertex.forward(2)),
                    entry(adjacentFaces[5], FaceVertex.back(2))
            ),
                    Block.blockCollider, sounds.get("footsteps_grass"), sounds.get("block_dirt")),
            new Block(-124, "Planks", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(4)),
                    entry(adjacentFaces[1], FaceVertex.left(4)),
                    entry(adjacentFaces[2], FaceVertex.up(4)),
                    entry(adjacentFaces[3], FaceVertex.down(4)),
                    entry(adjacentFaces[4], FaceVertex.forward(4)),
                    entry(adjacentFaces[5], FaceVertex.back(4))
            ),
                    Block.blockCollider, sounds.get("footsteps_wood"), sounds.get("block_wood")),
            new Block(-123, "Wood", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(5)),
                    entry(adjacentFaces[1], FaceVertex.left(5)),
                    entry(adjacentFaces[2], FaceVertex.up(6)),
                    entry(adjacentFaces[3], FaceVertex.down(6)),
                    entry(adjacentFaces[4], FaceVertex.forward(5)),
                    entry(adjacentFaces[5], FaceVertex.back(5))
            ),
                    Block.blockCollider, sounds.get("footsteps_wood"), sounds.get("block_wood")),
            new Block(-122, "Stone bricks", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(7)),
                    entry(adjacentFaces[1], FaceVertex.left(7)),
                    entry(adjacentFaces[2], FaceVertex.up(7)),
                    entry(adjacentFaces[3], FaceVertex.down(7)),
                    entry(adjacentFaces[4], FaceVertex.forward(7)),
                    entry(adjacentFaces[5], FaceVertex.back(7))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-121, "Bricks", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(8)),
                    entry(adjacentFaces[1], FaceVertex.left(8)),
                    entry(adjacentFaces[2], FaceVertex.up(8)),
                    entry(adjacentFaces[3], FaceVertex.down(8)),
                    entry(adjacentFaces[4], FaceVertex.forward(8)),
                    entry(adjacentFaces[5], FaceVertex.back(8))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-120, "Iron Ore", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(9)),
                    entry(adjacentFaces[1], FaceVertex.left(9)),
                    entry(adjacentFaces[2], FaceVertex.up(9)),
                    entry(adjacentFaces[3], FaceVertex.down(9)),
                    entry(adjacentFaces[4], FaceVertex.forward(9)),
                    entry(adjacentFaces[5], FaceVertex.back(9))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-119, "Gold Ore", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(10)),
                    entry(adjacentFaces[1], FaceVertex.left(10)),
                    entry(adjacentFaces[2], FaceVertex.up(10)),
                    entry(adjacentFaces[3], FaceVertex.down(10)),
                    entry(adjacentFaces[4], FaceVertex.forward(10)),
                    entry(adjacentFaces[5], FaceVertex.back(10))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-118, "Diamond Ore", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(11)),
                    entry(adjacentFaces[1], FaceVertex.left(11)),
                    entry(adjacentFaces[2], FaceVertex.up(11)),
                    entry(adjacentFaces[3], FaceVertex.down(11)),
                    entry(adjacentFaces[4], FaceVertex.forward(11)),
                    entry(adjacentFaces[5], FaceVertex.back(11))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-117, "Iron Block", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(12)),
                    entry(adjacentFaces[1], FaceVertex.left(12)),
                    entry(adjacentFaces[2], FaceVertex.up(12)),
                    entry(adjacentFaces[3], FaceVertex.down(12)),
                    entry(adjacentFaces[4], FaceVertex.forward(12)),
                    entry(adjacentFaces[5], FaceVertex.back(12))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-116, "Gold Block", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(13)),
                    entry(adjacentFaces[1], FaceVertex.left(13)),
                    entry(adjacentFaces[2], FaceVertex.up(13)),
                    entry(adjacentFaces[3], FaceVertex.down(13)),
                    entry(adjacentFaces[4], FaceVertex.forward(13)),
                    entry(adjacentFaces[5], FaceVertex.back(13))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-115, "Diamond Block", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(14)),
                    entry(adjacentFaces[1], FaceVertex.left(14)),
                    entry(adjacentFaces[2], FaceVertex.up(14)),
                    entry(adjacentFaces[3], FaceVertex.down(14)),
                    entry(adjacentFaces[4], FaceVertex.forward(14)),
                    entry(adjacentFaces[5], FaceVertex.back(14))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-114, "Cobblestone", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(15)),
                    entry(adjacentFaces[1], FaceVertex.left(15)),
                    entry(adjacentFaces[2], FaceVertex.up(15)),
                    entry(adjacentFaces[3], FaceVertex.down(15)),
                    entry(adjacentFaces[4], FaceVertex.forward(15)),
                    entry(adjacentFaces[5], FaceVertex.back(15))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-113, "Sand", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(16)),
                    entry(adjacentFaces[1], FaceVertex.left(16)),
                    entry(adjacentFaces[2], FaceVertex.up(16)),
                    entry(adjacentFaces[3], FaceVertex.down(16)),
                    entry(adjacentFaces[4], FaceVertex.forward(16)),
                    entry(adjacentFaces[5], FaceVertex.back(16))
            ),
                    Block.blockCollider, sounds.get("footsteps_grass"), sounds.get("block_dirt")),
            new Block(-112, "Sandstone", true, false, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(17)),
                    entry(adjacentFaces[1], FaceVertex.left(17)),
                    entry(adjacentFaces[2], FaceVertex.up(17)),
                    entry(adjacentFaces[3], FaceVertex.down(17)),
                    entry(adjacentFaces[4], FaceVertex.forward(17)),
                    entry(adjacentFaces[5], FaceVertex.back(17))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone")),
            new Block(-111, "Glass", true, true, Map.ofEntries(
                    entry(adjacentFaces[0], FaceVertex.right(18)),
                    entry(adjacentFaces[1], FaceVertex.left(18)),
                    entry(adjacentFaces[2], FaceVertex.up(18)),
                    entry(adjacentFaces[3], FaceVertex.down(18)),
                    entry(adjacentFaces[4], FaceVertex.forward(18)),
                    entry(adjacentFaces[5], FaceVertex.back(18))
            ),
                    Block.blockCollider, sounds.get("footsteps_stone"), sounds.get("block_stone"))
    );


    private static final List<Byte> transparentBlocks = blockTypes.stream().filter(Block::getIsTransparent).map(Block::getId).toList();

    private static final Map<Byte, Block> blocks = new HashMap<>() {
        {
            for (Block block : blockTypes) {
                put(block.getId(), block);
            }
        }
    };

    private static final Map<String, Byte> blockIds = new HashMap<>() {
        {
            for (Map.Entry<Byte, Block> entry : blocks.entrySet()) {
                put(entry.getValue().getName(), entry.getKey());
            }
        }
    };

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
