package game;

import audio.AudioController;
import audio.AudioManager;
import block.Blocks;
import com.google.gson.Gson;
import entities.Cupcake;
import entities.RendermanEntity;
import level.Chunk;
import level.EntityStateData;
import generator.Generator;
import level.LevelManager;
import mesh.Mesh;
import org.joml.Vector2i;
import org.joml.Vector3f;
import ui.UIManager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class StageManager {
    public static final float TIME_SPEED = 0.2f;
    public static final float SPAWN_TICK_RES = 0.2f;
    public static final Object entitiesLock = new Object();
    private static final Map<Integer, Entity> entities = new HashMap<>();
    private static int uid = 0;
    private static double prevSecond = time();
    private static double prevTime = time();
    private static double fps = 0;
    private static float gameTime = 13;
    private static Thread musicThread;
    private static Integer playerID;
    private static boolean stopRequested = false;
    private static boolean gameExitRequested = false;
    private static Settings settings;
    private static float prevSpawnTick = -1;

    private static int frames = 0;

    public static void init() {
        loadSettings();
        startMusicThread();
    }

    public static int createEntity(Entity entity) {
        synchronized (entitiesLock) {
            while (entities.containsKey(uid)) {
                uid++;
            }
            entities.put(uid, entity);
            entities.get(uid).attach(uid);
            return uid;
        }
    }

    public static int createEntity(Mesh[] m, Vector3f position, Vector3f rotation, Vector3f scale, Collider hitbox,
                                   boolean invincible, float health) {
        synchronized (entitiesLock) {
            while (entities.containsKey(uid)) {
                uid++;
            }
            entities.put(uid, new Entity(m, position, rotation, scale, hitbox, invincible, health, true));
            entities.get(uid).attach(uid);
            return uid;
        }
    }

    public static Entity getEntity(int id) {
        return entities.get(id);
    }

    public static void destroyEntity(int id) {
        synchronized (entitiesLock) {
            if (entities.containsKey(id)) {
                entities.get(id).destroy();
                entities.get(id).destroy();
                entities.remove(id);
            }
        }
    }

    public static void destroyAllEntities() {
        for (int entity : entities.keySet()) {
            destroyEntity(entity);
        }
        while (entities.size() > 0) {
        }
    }

    public static List<Entity> getEntities() {
        synchronized (entitiesLock) {
            return new ArrayList<>(entities.values());
        }
    }

    public static void updateEntities(double deltaTime) {
        Collection<Integer> ids = null;
        synchronized (entitiesLock) {
            ids = new ArrayList<>(entities.keySet());
        }
        for (int id : ids) {
            Entity entity = entities.get(id);
            if (entity != null) {
                entity.update(deltaTime);
            }
        }

    }

    public static void play(String world) {
        if (!getInGame()) {
            try {
                AudioManager.getSound("beginning").stop();
                destroyAllEntities();
                LevelManager.loadLevel(world);
                LevelManager.loadSpawnChonk();
                Player player = new Player();
                Renderman.setPlayer(player);
                UIManager.getWidget("crosshair").setVisible(true);
                UIManager.getWidget("hotbar").setVisible(true);
                System.out.println("Set player");
                playerID = StageManager.createEntity(player);
                Window.holdCursorHostage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void unplay() {
        if (getInGame()) {
            stopRequested = true;
        }
    }

    public static void closeGame() {
        try {
            LevelManager.saveLevel();
            LevelManager.unload();
            UIManager.getWidget("crosshair").setVisible(false);
            UIManager.getWidget("hotbar").setVisible(false);
            Renderman.setPlayer(null);
            playerID = null;
            Window.releaseCursor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AudioManager.getSound("ambient").stop();
        AudioManager.getSound("beginning").playCycle(true);
    }

    public static void unload() {

    }

    public static void mainLoop() {
        while (!glfwWindowShouldClose(Window.getWindowID())) {
            if (stopRequested) {
                stopRequested = false;
                closeGame();
            }
            if (gameExitRequested) {
                break;
            }

            double currentTime = time();
            double frameTime = currentTime - prevTime;
            prevTime = currentTime;

            if (getInGame()) {
                gameTime += frameTime * TIME_SPEED;
                //System.out.println("Game time: " + gameTime);
                if (gameTime >= 24.0f) {
                    gameTime -= 24.0f;
                }
                float spawnTick = getGameTimeLowRes(SPAWN_TICK_RES);
                if (spawnTick != prevSpawnTick) {
                    prevSpawnTick = spawnTick;
                    for (Vector2i chunk : LevelManager.getLoadedChunks()) {
                        if (ThreadLocalRandom.current().nextInt(0, RendermanEntity.SPAWN_CHANCE) < 10) {
                            spawnRandom(chunk, false, new RendermanEntity(new Vector3f(0, 0, 0), new Vector3f(0, 0,
                                    0)), RendermanEntity.MAX_PER_CHUNK);
                        }
                        if (ThreadLocalRandom.current().nextInt(0, Cupcake.SPAWN_CHANCE) < 10) {
                            spawnRandom(chunk, false, new Cupcake(new Vector3f(0, 0, 0), new Vector3f(0, 0,
                                    0)), Cupcake.MAX_PER_CHUNK);
                        }
                    }
                }
            }

            if (Window.getMouseLocked()) {
                updateEntities(frameTime);
            }

            Renderman.render();

            frames++;
            if (currentTime - prevSecond >= 1f) {
                fps = frames / (currentTime - prevSecond);
                prevSecond = currentTime;
                frames = 0;
            }
        }
        gameExitRequested = false;
    }

    public static void spawnRandom(Vector2i chunk, boolean surface, Entity entity, int maxPerChunk) {
        int count = 0;
        synchronized (LevelManager.chunksEntitiesLock) {
            if (LevelManager.getChunks().get(chunk).getLoading() || !LevelManager.getChunks().get(chunk).getLoaded() || !LevelManager.getChunksEntities().containsKey(chunk)) {
                return;
            }
            Collection<EntityStateData> chunkEnts = LevelManager.getChunksEntities().get(chunk);
            for (EntityStateData ent : chunkEnts) {
                if (ent.getType() == entity.getEntityTypeId()) {
                    count++;
                }
            }
        }
        if (count >= maxPerChunk) {
            return;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Vector2i worldSpace = new Vector2i(chunk.x * Chunk.SIZE_XZ, chunk.y * Chunk.SIZE_XZ);

        int cx = random.nextInt(0, Chunk.SIZE_XZ);
        int cz = random.nextInt(0, Chunk.SIZE_XZ);
        int x = cx + worldSpace.x;
        int z = cz + worldSpace.y;
        Chunk c = LevelManager.getChunks().get(chunk);
        boolean canSpawn = false;
        if (surface) {
            for (int i = Chunk.SIZE_Y - 1; i >= Generator.BASE_LAYER; i--) {
                if (c.getBlocks()[cx][cz][i] != Blocks.ID_AIR) {
                    entity.position = new Vector3f(x, i + entity.getModelHeight(), z);
                    canSpawn = true;
                    break;
                }
            }
        } else {
            int y = random.nextInt(0, Chunk.SIZE_Y);
            byte prevId = c.getBlocks()[cx][cz][y];
            for (int i = y - 1; i >= 0; i--) {
                byte currentBlock = c.getBlocks()[cx][cz][i];
                if (prevId == Blocks.ID_AIR && currentBlock != Blocks.ID_AIR) {
                    entity.position = new Vector3f(x, i + entity.getModelHeight(), z);
                    canSpawn = true;
                    break;
                }
                prevId = currentBlock;
            }
        }
        if (canSpawn) {
            createEntity(entity);
            LevelManager.saveNewEntityState(entity.getEntityStateData());
            System.out.println("Spawned entity in chunk X: " + chunk.x + " Z: " + chunk.y + ", entity position X: " + entity.getWorldPosition().x + " Y: " + entity.getWorldPosition().y + " Z: " + entity.getWorldPosition().z + ", rendermen per chunk: " + count);
        } else {
            System.out.println("Could not find place to spawn entity in chunk " + chunk);
        }
    }

    public static double getFPS() {
        return fps;
    }

    public static void despawnEntity(int id) {
        try {
            entities.get(id).destroy();
            entities.remove(id);
            uid = id;
        } catch (Exception e) {
            System.out.println("Could not despawn entity " + id + ": " + e);
        }
    }

    public static float getGameTime() {
        return gameTime;
    }

    public static void setGameTime(float time) {
        gameTime = time;
    }

    public static float getGameTimeLowRes(float res) {
        return (float) ((int) (getGameTime() / res)) * res;
    }

    private static double time() {
        return System.nanoTime() / 1000000000d;
    }

    private static void startMusicThread() {
        musicThread = new Thread(() -> {
            int nextMusicDelay = ThreadLocalRandom.current().nextInt(40, 200);
            while (true) {
                AudioController controller = getInGame() ? AudioManager.getSound("ambient") : AudioManager.getSound(
                        "beginning");
                controller.setCurrentClip(ThreadLocalRandom.current().nextInt(controller.getClips().length));
                if (!controller.isPlaying()) {
                    controller.playCycle(true);
                }
                int delay = nextMusicDelay;
                nextMusicDelay = ThreadLocalRandom.current().nextInt(40, 200);
                try {
                    Thread.sleep(delay * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        musicThread.start();
    }

    public static Settings getSettings() {
        return settings;
    }

    public static void loadSettings() {
        try (FileReader rex = new FileReader("settings.json")) {
            Gson help = new Gson();
            settings = help.fromJson(rex, Settings.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveSettings() {
        try (FileWriter rex = new FileWriter("settings.json")) {
            Gson help = new Gson();
            rex.write(help.toJson(settings));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void exit() {
        unplay();
        gameExitRequested = true;
        try {
            if (musicThread != null) {
                musicThread.stop();
                musicThread = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean getInGame() {
        return playerID != null;
    }
}
