package game;

import level.LevelManager;
import block.Blocks;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Raycast {
    private final Vector3f rayVector;
    private final Callback callback;
    private Vector3f rayPosition;
    private Vector3i block;
    private float distance;

    public Raycast(Vector3f position, Vector3f rotation, Callback call) {
        rayPosition = new Vector3f(position);
        Vector2f rotRadians = new Vector2f((float) Math.toRadians(rotation.y) - (float) Math.PI / 2, 2 * (float) Math.PI - (float) Math.toRadians(rotation.x));
        rayVector = new Vector3f((float) (Math.cos(rotRadians.x) * Math.cos(rotRadians.y)), (float) Math.sin(rotRadians.y), (float) (Math.sin(rotRadians.x) * Math.cos(rotRadians.y)));
        block = new Vector3i(Math.round(rayPosition.x), Math.round(rayPosition.y), Math.round(rayPosition.z));
        distance = 0;
        callback = call;
    }

    private boolean check(float by, Vector3i current, Vector3i next) {
        //System.out.println("CHECK " + by + " " + current + " " + next);
        if (LevelManager.getBlock(next).getId() != Blocks.ID_AIR) {
            callback.raycastHit(current, next);
            return true;
        } else {
            rayPosition = new Vector3f(rayPosition.x + rayVector.x * by, rayPosition.y + rayVector.y * by, rayPosition.z + rayVector.z * by);
            block = next;
            distance += by;
            return false;
        }
    }

    public boolean step() {
        Vector3i b = new Vector3i(block);
        Vector3f localPos = new Vector3f(rayPosition.x - b.x, rayPosition.y - b.y, rayPosition.z - b.z);
        Vector3f absVector = new Vector3f(rayVector);

        Vector3i sign = new Vector3i(1, 1, 1);
        if (rayVector.x < 0) {
            sign.x = -1;
            absVector.x = -absVector.x;
            localPos.x = -localPos.x;
        }
        if (rayVector.y < 0) {
            sign.y = -1;
            absVector.y = -absVector.y;
            localPos.y = -localPos.y;
        }
        if (rayVector.z < 0) {
            sign.z = -1;
            absVector.z = -absVector.z;
            localPos.z = -localPos.z;
        }

        if (absVector.x != 0) {
            Vector3f n = new Vector3f(0.5f, (0.5f - localPos.x) / absVector.x * absVector.y + localPos.y, (0.5f - localPos.x) / absVector.x * absVector.z + localPos.z);

            if (n.y >= -0.5f && n.y <= 0.5f && n.z >= -0.5f && n.z <= 0.5f) {
                float dist = (float) Math.sqrt((n.x - localPos.x) * (n.x - localPos.x) + (n.y - localPos.y) * (n.y - localPos.y) + (n.z - localPos.z) * (n.z - localPos.z));
                return check(dist, block, new Vector3i(b.x + sign.x, b.y, b.z));
            }
        }
        if (absVector.y != 0) {
            Vector3f n = new Vector3f((0.5f - localPos.y) / absVector.y * absVector.x + localPos.x, 0.5f, (0.5f - localPos.y) / absVector.y * absVector.z + localPos.z);

            if (n.x >= -0.5f && n.x <= 0.5f && n.z >= -0.5f && n.z <= 0.5f) {
                float dist = (float) Math.sqrt((n.x - localPos.x) * (n.x - localPos.x) + (n.y - localPos.y) * (n.y - localPos.y) + (n.z - localPos.z) * (n.z - localPos.z));
                return check(dist, block, new Vector3i(b.x, b.y + sign.y, b.z));
            }
        }
        if (absVector.z != 0) {
            Vector3f n = new Vector3f((0.5f - localPos.z) / absVector.z * absVector.x + localPos.x, (0.5f - localPos.z) / absVector.z * absVector.y + localPos.y, 0.5f);

            if (n.x >= -0.5f && n.x <= 0.5f && n.y >= -0.5f && n.y <= 0.5f) {
                float dist = (float) Math.sqrt((n.x - localPos.x) * (n.x - localPos.x) + (n.y - localPos.y) * (n.y - localPos.y) + (n.z - localPos.z) * (n.z - localPos.z));
                return check(dist, block, new Vector3i(b.x, b.y, b.z + sign.z));
            }
        }
        return false;
    }

    public float getDistance() {
        return distance;
    }

    public interface Callback {
        void raycastHit(Vector3i previous, Vector3i next);
    }
}
