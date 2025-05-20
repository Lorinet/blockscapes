package game;

import audio.AudioController;
import audio.AudioManager;
import com.google.gson.Gson;
import level.LevelManager;
import mesh.Mesh;
import org.joml.Vector3f;
import ui.UIManager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class StageManager {
    public static final float TIME_SPEED = 1.0f;

    private static final Map<Integer, Entity> entities = new HashMap<>();
    private static int uid = 0;
    private static double prevSecond = time();
    private static double prevTime = time();
    private static double fps = 0;
    private static float gameTime = 12;
    private static Thread musicThread;
    private static Integer playerID;
    private static boolean stopRequested = false;
    private static boolean gameExitRequested = false;
    private static Settings settings;

    private static int frames = 0;

    public static void init() {
        loadSettings();
        startMusicThread();
    }

    public static int createEntity(Entity entity) {
        while (entities.containsKey(uid)) {
            uid++;
        }
        entities.put(uid, entity);
        return uid;
    }

    public static int createEntity(Mesh m, Vector3f position, Vector3f rotation, Vector3f scale, Collider hitbox) {
        while (entities.containsKey(uid)) {
            uid++;
        }
        entities.put(uid, new Entity(m, position, rotation, scale, hitbox));
        return uid;
    }

    public static Entity getEntity(int id) {
        return entities.get(id);
    }

    public static void destroyEntity(int id) {
        if (entities.containsKey(id)) {
            entities.get(id).destroy();
            entities.get(id).destroy();
            entities.remove(id);
        }
    }

    public static void destroyAllEntities() {
        for (int entity : entities.keySet()) {
            destroyEntity(entity);
        }
    }

    public static List<Entity> getEntities() {
        return new ArrayList<>(entities.values());
    }

    public static void updateEntities(double deltaTime) {
        Collection<Integer> ids = new ArrayList<>(entities.keySet());
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
            destroyAllEntities();
            playerID = null;
            Window.releaseCursor();
        } catch (IOException e) {
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

            if(getInGame()) {
                gameTime += frameTime * TIME_SPEED;
                if (gameTime >= 24.0f) {
                    gameTime -= 24.0f;
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

    public static double getFPS() {
        return fps;
    }


    public static void despawnEntity(int id) {
        entities.get(id).destroy();
        entities.remove(id);
        uid = id;
    }

    public static float getGameTime() {
        return gameTime;
    }

    private static double time() {
        return System.nanoTime() / 1000000000d;
    }

    private static void startMusicThread() {
        musicThread = new Thread(() -> {
            int nextMusicDelay = ThreadLocalRandom.current().nextInt(40, 200);
            while (true) {
                AudioController controller = getInGame() ? AudioManager.getSound("ambient") : AudioManager.getSound("beginning");
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
