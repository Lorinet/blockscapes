package game;

import level.LevelManager;
import block.Block;
import block.Blocks;
import mesh.Mesh;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Entity {
    public static final Vector3d GRAVITY = new Vector3d(0, -32f, 0);
    public static final Vector3d FRICTION = new Vector3d(20, 2, 20);

    protected Mesh model;
    protected Vector3f position;
    protected Vector3f rotation;
    protected Vector3f scale;
    protected Vector3d velocity;
    protected Vector3d acceleration;
    protected Collider hitbox;

    public Entity(Mesh model, Vector3f position, Vector3f rotation, Vector3f scale, Collider hitbox) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.velocity = new Vector3d();
        this.acceleration = new Vector3d();
        this.hitbox = hitbox;
    }


    public Mesh getModel() {
        return model;
    }

    public void setModel(Mesh model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public void move(Vector3f by) {
        position.add(by);
    }

    public void rotate(Vector3f by) {
        rotation.add(by);
    }

    public void scale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3d getVelocity() {
        return velocity;
    }

    public Collider getHitbox() {
        return hitbox;
    }

    public boolean getAffectedByGravity() {
        return false;
    }

    public Vector3d getFriction() {
        return FRICTION;
    }

    public void destroy() {
        model.destroy();
    }

    public void updateModel(Mesh model) {
        Mesh die = this.model;
        this.model = model;
        die.destroy();
    }

    public void update(double deltaTime) {
        /*if (velocity.y > 0) {
            System.out.println("DELTATIME: " + String.format("%f", deltaTime));
            System.out.println("VELOCITY: " + String.format("%f", velocity.y));
        }*/
        Vector3d fric = new Vector3d(getFriction());
        acceleration.mul(fric);
        acceleration.mul(deltaTime);
        velocity.add(acceleration);
        acceleration = new Vector3d();
        if (hitbox != null && hitbox.getCollisionsEnabled()) {
            collisions(deltaTime);
        }
        Vector3d velo = new Vector3d(velocity);
        velo.mul(deltaTime);
        position.add(new Vector3f((float) velo.x, (float) velo.y, (float) velo.z));
        if (getAffectedByGravity()) {
            Vector3d grav = new Vector3d(GRAVITY);
            grav.mul(deltaTime);
            velocity.add(grav);
        }
        if (velocity.x > 0) {
            velocity.x -= Math.min(Math.abs(velocity.x * fric.x * deltaTime), Math.abs(velocity.x));
        } else {
            velocity.x += Math.min(Math.abs(velocity.x * fric.x * deltaTime), Math.abs(velocity.x));
        }
        if (velocity.y > 0) {
            velocity.y -= Math.min(Math.abs(velocity.y * fric.y * deltaTime), Math.abs(velocity.y));
        } else {
            velocity.y += Math.min(Math.abs(velocity.y * fric.y * deltaTime), Math.abs(velocity.y));
        }
        if (velocity.z > 0) {
            velocity.z -= Math.min(Math.abs(velocity.z * fric.z * deltaTime), Math.abs(velocity.z));
        } else {
            velocity.z += Math.min(Math.abs(velocity.z * fric.z * deltaTime), Math.abs(velocity.z));
        }
    }

    private void collisions(double deltaTime) {
        hitbox.setPosition(position);
        for (int axe = 0; axe < 3; axe++) {
            Vector3d velocity = new Vector3d(this.velocity);
            velocity.mul(deltaTime);
            Vector3i step = new Vector3i(velocity.x > 0 ? 1 : -1, velocity.y > 0 ? 1 : -1, velocity.z > 0 ? 1 : -1);
            Vector3i steps = new Vector3i((int) (hitbox.getScale().x), (int) (hitbox.getScale().y), (int) (hitbox.getScale().z));
            Vector3i intPos = new Vector3i((int) position.x, (int) position.y, (int) position.z);
            Vector3i nextPos = new Vector3i((int) (position.x + velocity.x), (int) (position.y + velocity.y), (int) (position.z + velocity.z));
            ArrayList<Quaterniond> potentialCollisions = new ArrayList<>();

            int bx = intPos.x - step.x * (steps.x + 1);
            int ex = nextPos.x + step.x * (steps.x + 2);
            int by = intPos.y - step.y * (steps.y + 2);
            int ey = nextPos.y + step.y * (steps.y + 3);
            int bz = intPos.z - step.z * (steps.z + 1);
            int ez = nextPos.z + step.z * (steps.z + 2);


            for (int x = Math.min(bx, ex); x <= Math.max(bx, ex); x += Math.abs(step.x)) {
                for (int y = Math.min(by, ey); y <= Math.max(by, ey); y += Math.abs(step.y)) {
                    for (int z = Math.min(bz, ez); z <= Math.max(bz, ez); z += Math.abs(step.z)) {
                        Vector3i col = new Vector3i(x, y, z);
                        Block b = LevelManager.getBlock(col);
                        if (b.getId() != Blocks.ID_AIR) {
                            Collider blockCol = b.getCollider().clone();
                            blockCol.translate(new Vector3f(col));
                            Quaterniond collision = hitbox.collide(blockCol, velocity);
                            if (collision != null) {
                                potentialCollisions.add(collision);
                            }
                        }
                    }
                }
            }

            if (potentialCollisions.isEmpty()) {
                break;
            }

            Optional<Quaterniond> col = potentialCollisions.stream().min(Comparator.comparingDouble(o -> o.w));
            Quaterniond collision = col.get();
            collision.w -= 0.001d;
            if (collision.x != 0) {
                position.x += (float) (velocity.x * collision.w);
                this.velocity.x = 0;
            }
            if (collision.y != 0) {
                position.y += (float) (velocity.y * collision.w);
                this.velocity.y = 0;
            }
            if (collision.z != 0) {
                position.z += (float) (velocity.z * collision.w);
                this.velocity.z = 0;
            }
            onCollision(collision);
        }
    }

    protected void onCollision(Quaterniond collision) {

    }

}
