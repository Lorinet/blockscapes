package level;

import block.Block;
import block.Blocks;
import com.google.gson.Gson;
import game.Entity;
import game.Renderman;
import game.StageManager;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.joml.Vector2i;
import org.joml.Vector3i;
import ui.HotBar;
import ui.UIManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LevelManager {
    public static final Object chunksEntitiesLock = new Object();
    private static Map<Vector2i, Chunk> chunks = new HashMap<>();
    private static Deque<Vector2i> loadedChunks = new LinkedList<>();
    private static Map<Vector2i, List<EntityStateData>> chunksEntities = new HashMap<>();

    private static String levelName;
    private static File levelFile;
    private static Level level;

    public static void loadSpawnChonk() {
        loadChunk(0, 0);
        while (!chunks.get(new Vector2i(0, 0)).getLoaded()) {
        }
    }

    public static Collection<Vector2i> getLoadedChunks() {
        return loadedChunks;
    }

    public static Map<Vector2i, Chunk> getChunks() {
        return chunks;
    }

    public static String[] getLevels() {
        File folder = new File("levels");
        FileFilter filtir = File::isDirectory;
        File[] folders = folder.listFiles(filtir);
        assert folders != null;
        return Arrays.stream(folders).map(File::getName).toArray(String[]::new);
    }

    public static void loadLevel(String name) throws Exception {
        levelName = name;
        Path levelDir = Paths.get("levels", name);
        levelFile = Paths.get(levelDir.toString(), "level.dat").toFile();
        Gson jsp = new Gson();

        if (!levelFile.exists()) {
            throw new Exception("Could not load level.dat");
        }
        String json = Files.readString(levelFile.toPath());
        level = jsp.fromJson(json, Level.class);
        StageManager.setGameTime(level.getTime());

        synchronized (chunksEntitiesLock) {
            chunksEntities = new HashMap<>();
            for (EntityStateData entData : level.getEntities()) {
                Vector2i chomk = entData.getChunk();
                if (!chunksEntities.containsKey(chomk)) {
                    chunksEntities.put(chomk, new ArrayList<>());
                }
                chunksEntities.get(chomk).add(entData);
            }
        }

        ((HotBar) UIManager.getWidget("hotbar")).setHotbarItems(level.getInventory().toArray(new Byte[0]));
    }

    public static void createLevel(String name, Long seed) {
        Path levelDir = Paths.get("levels", name);
        levelDir.toFile().mkdirs();
        try (FileWriter levelFile = new FileWriter(Paths.get(levelDir.toString(), "level.dat").toFile())) {
            Level newlev = new Level(name, seed);
            Gson g = new Gson();
            levelFile.write(g.toJson(newlev));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setHotBarItem(int slot, Block block) {
        level.getInventory().set(slot, block.getId());
        ((HotBar) UIManager.getWidget("hotbar")).setHotbarItem(slot, block);
    }

    public static void saveLevel() throws IOException, InterruptedException {
        Vector3i pos = Renderman.getPlayer().getWorldPosition();
        level.setPlayer(new ArrayList<>());
        level.getPlayer().add(pos.x);
        level.getPlayer().add(pos.y);
        level.getPlayer().add(pos.z);
        level.setFlying(Renderman.getPlayer().getFlying());
        level.setTime(StageManager.getGameTime());
        for (Vector2i chunk : loadedChunks) {
            saveChunkData(chunk.x, chunk.y);
        }
        saveAllEntityStates();
        ArrayList<EntityStateData> entityStates = new ArrayList<>();
        for (List<EntityStateData> ents : chunksEntities.values()) {
            entityStates.addAll(ents);
        }
        level.setEntities(entityStates);
        FileWriter writer = new FileWriter(levelFile);
        Gson g = new Gson();
        writer.write(g.toJson(level));
        writer.close();
        System.out.println("Saved level.dat");
    }

    public static void saveAllEntityStates() {
        for (Vector2i chunk : loadedChunks) {
            saveEntityStates(chunk, false);
        }

    }

    public static void saveChunkData(int cx, int cz) {
        Vector2i chunk = new Vector2i(cx, cz);
        if (chunks.containsKey(chunk)) {
            chunks.get(chunk).setModified(false);
            Path chunkPath = Paths.get("levels", levelName, "c_" + cx + "_" + cz + ".dat");
            new Thread(() -> {
                try (FileOutputStream writer = new FileOutputStream(chunkPath.toFile())) {
                    try (LZ4BlockOutputStream zip = new LZ4BlockOutputStream(writer)) {
                        byte[][][] blocks = chunks.get(chunk).getBlocks();
                        for (int x = 0; x < Chunk.SIZE_XZ; x++) {
                            for (int z = 0; z < Chunk.SIZE_XZ; z++) {
                                zip.write(blocks[x][z]);
                            }
                        }
                        System.out.println("Saved chunk X: " + cx + " Z:" + cz + ".dat");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public static void saveEntityStates(Vector2i chunk, boolean despawn) {
        synchronized (chunksEntitiesLock) {
            ArrayList<EntityStateData> entityStates = new ArrayList<>();
            for (Entity entity : StageManager.getEntities()) {
                if (entity.getChunk().equals(chunk) && entity.getEntityTypeId() != null) {
                    entityStates.add(entity.getEntityStateData());
                    if (despawn) {
                        entity.despawn();
                    }
                }
            }
            chunksEntities.put(chunk, entityStates);
        }
    }

    private static Vector2i getChunkFromCoordinate(Vector2i coordinate) {
        int cx = Math.floorDiv(coordinate.x, Chunk.SIZE_XZ);
        int cy = Math.floorDiv(coordinate.y, Chunk.SIZE_XZ);
        return new Vector2i(cx, cy);
    }

    private static Vector3i getLocalPosition(Vector2i chonk, Vector3i position) {
        return new Vector3i(position.x - (chonk.x * Chunk.SIZE_XZ), position.y, position.z - (chonk.y * Chunk.SIZE_XZ));
    }

    public static void loadChunks(Vector2i playerPos) {

        Vector2i chunkPos = getChunkFromCoordinate(playerPos);
        int cx = chunkPos.x;
        int cz = chunkPos.y;
        loadChunk(cx, cz);
        for (int i = -StageManager.getSettings().getRenderDistance(); i <= StageManager.getSettings().getRenderDistance(); i++) {
            for (int j = -StageManager.getSettings().getRenderDistance(); j <= StageManager.getSettings().getRenderDistance(); j++) {
                if (i != 0 || j != 0) {
                    loadChunk(cx + i, cz + j);
                }
            }
        }
    }

    public static void loadChunk(int cx, int cz) {
        Vector2i chunkPos = new Vector2i(cx, cz);
        if (!chunks.containsKey(chunkPos)) {
            chunks.put(chunkPos, new Chunk(chunkPos.x, chunkPos.y));
        }
        if (!chunks.get(chunkPos).getLoaded() && !chunks.get(chunkPos).getLoading()) {
            if (!loadedChunks.contains(chunkPos)) {
                loadedChunks.addLast(chunkPos);
            }
            chunks.get(chunkPos).load();
        } else if (chunks.get(chunkPos).getLoaded() && chunks.get(chunkPos).getLoading()) {
            chunks.get(chunkPos).setLoading(false);
            chunks.get(chunkPos).createModel();
            while (loadedChunks.size() > (StageManager.getSettings().getRenderDistance() * 2 + 1) * (StageManager.getSettings().getRenderDistance() * 2 + 1)) {
                Vector2i chonk = loadedChunks.peekFirst();
                chunks.get(chonk).unload();
                loadedChunks.pollFirst();
            }
        } else {
            if (chunks.get(chunkPos).getModified()) {
                saveChunkData(cx, cz);
            }
            if (loadedChunks.peekLast() != chunkPos) {
                loadedChunks.remove(chunkPos);
                loadedChunks.addLast(chunkPos);
            }
        }
    }

    public static Block getBlock(Vector3i position) {
        Vector2i chonk = getChunkFromCoordinate(new Vector2i(position.x, position.z));
        Vector3i pos = getLocalPosition(chonk, position);
        if (chunks.containsKey(chonk)) {
            if (!chunks.get(chonk).getLoaded()) {
                return Blocks.getBlock(Blocks.getBlockID("Stone"));
            }
            byte[][][] b = chunks.get(chonk).getBlocks();
            return Blocks.getBlock(b[pos.x][pos.z][pos.y]);
        } else {
            return Blocks.getBlock(Blocks.getBlockID("Stone"));
        }
    }

    public static void unload() {
        for (Vector2i chunkPos : chunks.keySet()) {
            chunks.get(chunkPos).unload();
        }
        loadedChunks.clear();
        chunks.clear();
    }

    public static void setBlock(Vector3i position, Block block) {
        Vector2i chunkPos = getChunkFromCoordinate(new Vector2i(position.x, position.z));
        Vector3i pos = getLocalPosition(chunkPos, position);
        if (!chunks.containsKey(chunkPos)) {
            chunks.put(chunkPos, new Chunk(chunkPos.x, chunkPos.y));
        }
        chunks.get(chunkPos).getBlocks()[pos.x][pos.z][pos.y] = block.getId();
        chunks.get(chunkPos).setModified(true);
        chunks.get(chunkPos).reload();
        System.out.println("Placed block " + position.x + " " + position.y + " " + position.z);
    }

    public static String getLevelName() {
        return levelName;
    }

    public static long getSeed() {
        return level.getSeed();
    }

    public static Vector3i getInitialPosition() {
        return new Vector3i(level.getPlayer().get(0), level.getPlayer().get(1), level.getPlayer().get(2));
    }

    public static boolean getFlying() {
        return level.getFlying();
    }

    public static Map<Vector2i, List<EntityStateData>> getChunksEntities() {
        return chunksEntities;
    }

    public static void saveNewEntityState(EntityStateData entity) {
        synchronized (chunksEntitiesLock) {
            Vector2i chonk = entity.getChunk();
            if (!chunksEntities.containsKey(chonk)) {
                chunksEntities.put(chonk, new ArrayList<>());
            }
            chunksEntities.get(chonk).add(entity);
        }
    }
}
