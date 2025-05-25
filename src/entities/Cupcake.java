package entities;

import block.Block;
import game.*;
import mesh.Mesh;
import mesh.ModelManager;
import org.joml.Quaterniond;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Cupcake extends Mob {
    public static final int SPAWN_CHANCE = 300;
    public static final int MAX_PER_CHUNK = 10;

    public static float heightNormal = 0.5f;
    private static final float targetRange = 10f;

    private float jumpTime = -1;
    public Cupcake(Vector3f position, Vector3f rotation) {
        super(new Mesh[] {ModelManager.getModel("cupcake")}, position, rotation, new Vector3f(5, 5, 5), new Collider(new Vector3f(-0.2f, -heightNormal / 2, -0.2f), new Vector3f(0.2f, heightNormal / 2, 0.2f), true), true, 30);
        speed = 1;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if(jumpTime < 0) {
            jumpTime = StageManager.getGameTimeLowRes(0.1f) + ThreadLocalRandom.current().nextFloat() * 0.5f;
        }
        float newJumpTime = StageManager.getGameTimeLowRes(0.1f);
        if(jumpTime < newJumpTime) {
            jump();
            jumpTime = newJumpTime;
        }
        if(!moving) {
            moveTo(new Vector3f(position.x + ThreadLocalRandom.current().nextFloat() * targetRange, position.y, position.z + ThreadLocalRandom.current().nextFloat() * targetRange));
        }
        if(position.distance(Renderman.getPlayer().getPosition()) > 50f) {
            despawn();
        }
    }

    @Override
    protected void moveTowardsTarget() {
        if(movementTarget != null) {
            Vector2f pos = new Vector2f(position.x, position.z);
            Vector2f targetPos = new Vector2f(movementTarget.x, movementTarget.z);

            if (pos.distance(targetPos) < 2f) {
                movementTarget = null;
                moving = false;
            }
        }
        super.moveTowardsTarget();
    }

    @Override
    public void onCollision(Quaterniond collision, ArrayList<Entity> entity, ArrayList<Block> block) {
        super.onCollision(collision, entity, block);
        if(collision.x != 0 || collision.z != 0) {
            jump();
        }
    }

    @Override
    public Integer getEntityTypeId() {
        return Entity.ENTITY_CUPCAKE;
    }

    @Override
    public float getModelHeight() {
        return heightNormal;
    }
}
