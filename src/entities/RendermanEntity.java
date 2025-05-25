package entities;

import block.Block;
import game.Collider;
import game.Entity;
import game.Mob;
import game.Renderman;
import mesh.Mesh;
import mesh.ModelManager;
import org.joml.Quaterniond;
import org.joml.Vector3f;

import java.util.ArrayList;

public class RendermanEntity extends Mob {
    public static final int SPAWN_CHANCE = 100000;
    public static final int MAX_PER_CHUNK = 2;

    public static float heightNormal = 3.4f;
    public RendermanEntity(Vector3f position, Vector3f rotation) {
        super(new Mesh[] {ModelManager.getModel("renderman")}, position, rotation, new Vector3f(40, 40, 40), new Collider(new Vector3f(-0.2f, -heightNormal / 2, -0.2f), new Vector3f(0.2f, heightNormal / 2, 0.2f), true), true, 30);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if(!moving && position.distance(Renderman.getPlayer().getPosition()) < 20) {
            moveTo(Renderman.getPlayer().getPosition());
        }
    }

    @Override
    protected void moveTowardsTarget() {
        if(movementTarget != null && position.distance(movementTarget) > 20) {
            movementTarget = null;
            moving = false;
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
        return Entity.ENTITY_RENDERMAN;
    }

    @Override
    public float getModelHeight() {
        return heightNormal;
    }
}
