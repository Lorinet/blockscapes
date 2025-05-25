package game;

import audio.AudioManager;
import block.Block;
import block.Blocks;
import level.LevelManager;
import mesh.Mesh;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;

import static java.lang.Math.*;

public class Mob extends Entity {
    protected static float moveSpeed = 4f;
    protected static float runSpeed = 7;
    protected static float jumpHeight = 1.25f;
    protected static float crouchSpeed = 2f;
    protected static float heightCrouch = 1.4f;
    protected double speed = moveSpeed;
    protected double targetSpeed = moveSpeed;
    protected boolean moving = false;
    protected boolean flying = false;
    protected boolean grounded = false;
    protected boolean crouching = false;

    protected Vector3d dragFly = new Vector3d(5, 5, 5);
    protected Vector3d dragJump = new Vector3d(1.8, 0, 1.8);
    protected Vector3d dragFall = new Vector3d(1.8, 0, 1.8);

    protected Vector3f movementTarget = null;


    protected Mob(Mesh[] model, Vector3f position, Vector3f rotation, Vector3f scale, Collider hitbox, boolean visible, float health) {
        super(model, position, rotation, scale, hitbox, false, health, visible);
    }

    protected void moveTo(Vector3f target) {
        movementTarget = target;
        moving = true;
    }

    protected void jump() {
        if (grounded) {
            velocity.y = sqrt(-2 * GRAVITY.y * jumpHeight);
            grounded = false;
        }
    }

    protected void crouch(boolean yes) {
        crouching = yes;
        targetSpeed = yes ? crouchSpeed : moveSpeed;
    }

    protected void run(boolean yes) {
        targetSpeed = yes ? runSpeed : moveSpeed;
    }

    protected void fly(boolean yes) {
        if (yes) {
            velocity.y = sqrt(-2 * GRAVITY.y * jumpHeight);
        }
        grounded = false;
        flying = yes;
    }

    protected void hurt(float howMuch) {
        super.hurt(howMuch);
        new Thread(() -> AudioManager.getSound("bonk").playForSecs(false, 0.1f)).start();
    }

    public boolean getFlying() {
        return flying;
    }

    protected Vector3i getBlockStandingOn() {
        return new Vector3i((int) (ceil(position.x - 0.5)), (int) floor(position.y - (heightNormal / 2)),
                (int) ceil(position.z - 0.5f));
    }


    protected void moveTowardsTarget() {
        if (movementTarget != null) {
            Vector3f dir = new Vector3f(movementTarget).sub(position);
            float dist = dir.length();
            if (dist > 0.01f) {
                lookAt(movementTarget);
                dir.normalize();
                dir.mul((float) speed);
                if (dist < dir.length()) {
                    movementTarget = null;
                    moving = false;
                } else {
                    move(dir);
                }
            } else {
                moving = false;
            }
        }
    }

    private void lookAt(Vector3f where) {

        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.lookAt(position, where, new Vector3f(0.0f, 1.0f, 0.0f));

        Quaternionf rotation = new Quaternionf();
        viewMatrix.getNormalizedRotation(rotation);

        Matrix4f rotationMatrix = new Matrix4f().rotation(rotation);
        this.rotation.y = Math.atan2(rotationMatrix.m02(), rotationMatrix.m22());

    }


    public void move(Vector3f by) {
        Vector3d velo = new Vector3d(by);
        if (!flying) {
            velo.y = velocity.y;
        }
        velocity = velo;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        moveTowardsTarget();
    }

    @Override
    public Vector3d getFriction() {
        if (flying) {
            return dragFly;
        } else if (grounded) {
            return FRICTION;
        } else if (velocity.y > 0) {
            return dragJump;
        } else {
            return dragFall;
        }
    }

    @Override
    public boolean getAffectedByGravity() {
        return !flying;
    }

    @Override
    protected void onCollision(Quaterniond collision, ArrayList<Entity> entity, ArrayList<Block> block) {
        if (collision.y == 1) {
            grounded = true;
        }
    }
}
