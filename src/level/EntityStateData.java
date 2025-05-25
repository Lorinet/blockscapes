package level;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class EntityStateData {
    private int type;
    private int[] position;
    private float[] rotation;
    private float health;

    public EntityStateData(int type, Vector3i position, Vector3f rotation, float health) {
        this.type = type;
        this.position = new int[]{position.x, position.y, position.z};
        this.rotation = new float[]{rotation.x, rotation.y, rotation.z};
        this.health = health;
    }

    public int getType() {
        return type;
    }

    public Vector3f getPosition() {
        return new Vector3f(position[0], position[1], position[2]);
    }

    public Vector3f getRotation() {
        return new Vector3f(rotation[0], rotation[1], rotation[2]);
    }

    public float getHealth() {
        return health;
    }

    public Vector2i getChunk() {
        Vector3i worldPos = new Vector3i(position[0], position[1], position[2]);
        int chunkX = worldPos.x / Chunk.SIZE_XZ;
        int chunkZ = worldPos.z / Chunk.SIZE_XZ;
        if(worldPos.x < 0) {
            chunkX--;
        }
        if(worldPos.z < 0) {
            chunkZ--;
        }
        return new Vector2i(chunkX, chunkZ);
    }
}
