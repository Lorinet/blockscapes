package block;

import audio.AudioController;
import game.Collider;
import mesh.Material;
import mesh.ModelData;
import mesh.ModelLoader;
import mesh.ModelManager;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class Block {
    public static final Vector3i[] adjacentFaces = new Vector3i[] {
            new Vector3i(1, 0, 0),
            new Vector3i(-1, 0, 0),
            new Vector3i(0, 1, 0),
            new Vector3i(0, -1, 0),
            new Vector3i(0, 0, 1),
            new Vector3i(0, 0, -1),
    };
    public static final Collider blockCollider = new Collider(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0.5f, 0.5f, 0.5f), true);

    private final byte id;
    private final String name;
    private final Map<Vector3i, FaceVertex> faces;
    private final ModelData customModelData;
    private final int materialIndex;
    private final Collider collider;
    private final boolean showInInventory;
    private final boolean isTransparent;
    private final AudioController footstepsSounds;
    private final AudioController blockSounds;

    public Block(int id, String name, boolean showInInventory, boolean isTransparent, Map<Vector3i, FaceVertex> faces, ModelData customModelData, int materialIndex, Collider collider, AudioController footstepsSounds, AudioController blockSounds) {
        this.id = (byte)id;
        this.name = name;
        this.showInInventory = showInInventory;
        this.isTransparent = isTransparent;
        this.faces = new HashMap<>(faces);
        this.collider = collider;
        this.footstepsSounds = footstepsSounds;
        this.blockSounds = blockSounds;
        this.customModelData = customModelData;
        this.materialIndex = materialIndex;
    }

    public byte getId() {
        return id;
    }

    public boolean getShowInInventory() {
        return showInInventory;
    }

    public boolean getIsTransparent() {
        return isTransparent;
    }

    public String getName() {
        return name;
    }

    public FaceVertex getFace(Vector3i face) {
        return faces.get(face);
    }

    public Material getMaterial() {
        return Blocks.blockMaterials.get(materialIndex);
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public boolean hasCustomModel() {
        return customModelData != null;
    }

    public ModelData getCustomModel() {
        return customModelData;
    }

    public Collider getCollider() {
        return collider;
    }

    public AudioController getFootstepsSounds() {
        return footstepsSounds;
    }

    public AudioController getBlockSounds() {
        return blockSounds;
    }
}
