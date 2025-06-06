package level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

public class Level {
    private String name;
    private long seed;
    private float time;
    private ArrayList<Byte> inventory;
    private ArrayList<Integer> player;
    private ArrayList<EntityStateData> entities;
    private boolean flying;

    public Level(String name, Long seed) {
        this.name = name;
        this.seed = Objects.requireNonNullElseGet(seed, () -> ThreadLocalRandom.current().nextLong());
        inventory = new ArrayList<>(Arrays.asList(new Byte[]{null, null, null, null, null, null, null, null, null, null}));
        player = new ArrayList<>(Arrays.asList(0, 128, 0));
        flying = false;
        this.time = 7;
        this.entities = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public ArrayList<Byte> getInventory() {
        return inventory;
    }

    public void setInventory(ArrayList<Byte> inventory) {
        this.inventory = inventory;
    }

    public ArrayList<Integer> getPlayer() {
        return player;
    }

    public void setPlayer(ArrayList<Integer> player) {
        this.player = player;
    }

    public boolean getFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public List<EntityStateData> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityStateData> entities) {
        this.entities = new ArrayList<>(entities);
    }
}
