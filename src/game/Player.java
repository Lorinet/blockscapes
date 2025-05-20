package game;

import block.Block;
import block.Blocks;
import level.LevelManager;
import mesh.*;
import org.joml.*;
import ui.HotBar;
import ui.UIManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;
import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
    private static final float MOVE_SPEED = 4f;
    private static final float RUNNING_SPEED = 7;
    private static final float CROUCHING_SPEED = 2f;
    private static final float PLAYER_HEIGHT = 1.8f;
    private static final float CROUCHING_HEIGHT = 1.4f;
    private static final float REACH = 5;
    private static final float JUMP_HEIGHT = 1.25f;

    private static final Vector3d DRAG_FLY = new Vector3d(5, 5, 5);
    private static final Vector3d DRAG_JUMP = new Vector3d(1.8, 0, 1.8);
    private static final Vector3d DRAG_FALL = new Vector3d(1.8, 0, 1.8);
    private static final Collection<Float> playerVertexes = new ArrayList<>(List.of());
    private static final Collection<Integer> playerIndexes = new ArrayList<>(List.of());
    private static final Collection<Float> playerTextureCoords = new ArrayList<>(List.of());
    private static final Collection<Float> playerShading = new ArrayList<>(List.of());
    public Vector3f cameraRotation = new Vector3f(0, 0, 0);
    private Vector2f prevMouse = new Vector2f(0, 0);
    private double speed = MOVE_SPEED;
    private double targetSpeed = MOVE_SPEED;
    private boolean flying = false;
    private boolean grounded = false;
    private boolean crouching = false;

    public Player() {
        this(new Vector3f(LevelManager.getInitialPosition()), LevelManager.getFlying());
    }

    public Player(Vector3f position, boolean flying) {
        super(ModelManager.getModel("cactus"), position, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Collider(new Vector3f(-0.2f, 0f, -0.2f), new Vector3f(0.2f, -PLAYER_HEIGHT, 0.2f), true));
        fly(flying);
    }

    public Vector3f getCameraPosition() {
        Vector3f perspo = new Vector3f(position);
        perspo.y += (crouching ? CROUCHING_HEIGHT : PLAYER_HEIGHT) / 2 - 0.2f;
        return perspo;
    }

    private void placeBlock() {
        Raycast.Callback callback = (previous, next) -> {
            Byte bid = ((HotBar) UIManager.getWidget("hotbar")).getSelectedBlockID();
            Vector3i top = getWorldPosition();
            top.y += 2;
            if (bid != null && !Objects.equals(next, getBlockStandingOn()) && !Objects.equals(next, top) && !Objects.equals(next, getWorldPosition())) {
                Block b = Blocks.getBlock(bid);
                if (b.getBlockSounds() != null) {
                    b.getBlockSounds().playCycle(true);
                }
                LevelManager.setBlock(previous, b);
            }
        };

        Raycast rc = new Raycast(getCameraPosition(), cameraRotation, callback);
        while (rc.getDistance() < REACH) {
            if (rc.step()) {
                break;
            }
        }
    }

    private void breakBlock() {
        Raycast.Callback callback = (previous, next) -> {
            Block b = LevelManager.getBlock(next);
            if (b.getBlockSounds() != null) {
                b.getBlockSounds().playCycle(true);
            }
            LevelManager.setBlock(next, Blocks.getBlock(Blocks.ID_AIR));
        };

        Raycast rc = new Raycast(getCameraPosition(), cameraRotation, callback);
        while (rc.getDistance() < REACH) {
            if (rc.step()) {
                break;
            }
        }
    }

    public void move(double deltaTime) {
        if(Keyboard.getKeyUp(GLFW_KEY_T)) {
            Mesh mesh = ModelManager.getModel("cactus");
            Entity ent = new Entity(mesh, new Vector3f(0, 128, 0), new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f), new Collider(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0.5f, 0.5f, 0.5f), true));
            StageManager.createEntity(ent);
        }

        if (Keyboard.getKeyUp(GLFW_KEY_ESCAPE)) {
            UIManager.getWidget("pauseMenu").setVisible(true);
            Window.releaseCursor();
        }

        if (Keyboard.getKeyUp(GLFW_KEY_I)) {
            UIManager.getWidget("inventory").setVisible(true);
            Window.releaseCursor();
        }

        if (deltaTime * 20 > 1) {
            speed = targetSpeed;
        } else {
            speed += (targetSpeed - speed) * deltaTime * 20;
        }

        double by = speed;
        if (flying) {
            by *= 2;
        }
        int mx = 0;
        int mz = 0;

        if (Keyboard.getKey(GLFW_KEY_W)) {
            mz -= 1;
        }
        if (Keyboard.getKey(GLFW_KEY_S)) {
            mz += 1;
        }
        if (Keyboard.getKey(GLFW_KEY_A)) {
            mx -= 1;
        }
        if (Keyboard.getKey(GLFW_KEY_D)) {
            mx += 1;
        }

        if (mx != 0 || mz != 0) {
            double angle = toRadians(cameraRotation.y) - atan2(mx, mz) + (PI * 2) / 4;
            acceleration.x = cos(angle) * by;
            acceleration.z = sin(angle) * by;
            if (grounded) {
                playFootsteps();
            }
        } else {
            run(false);
        }

        if (Mouse.getKeyDown(GLFW_MOUSE_BUTTON_LEFT)) {
            breakBlock();
        }
        if (Mouse.getKeyDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            placeBlock();
        }
        if (Keyboard.getKeyDown(GLFW_KEY_F)) {
            fly(!flying);
        }

        if (flying) {
            if (Keyboard.getKey(GLFW_KEY_SPACE)) {
                acceleration.y = by;
            }
            if (Keyboard.getKey(GLFW_KEY_LEFT_SHIFT)) {
                if (grounded) {
                    fly(false);
                }
                acceleration.y = -by;
            }
        } else {
            if (Keyboard.getKey(GLFW_KEY_SPACE) && grounded) {
                jump();
            }

            if (Keyboard.getKeyDown(GLFW_KEY_LEFT_SHIFT)) {
                crouch(true);
            }
            if (Keyboard.getKeyUp(GLFW_KEY_LEFT_SHIFT)) {
                crouch(false);
            }

            if (Keyboard.getKeyDown(GLFW_KEY_LEFT_CONTROL)) {
                run(true);
            }
        }

        LevelManager.loadChunks(new Vector2i((int) position.x, (int) position.z));
    }

    private void jump() {
        if (grounded) {
            velocity.y = sqrt(-2 * GRAVITY.y * JUMP_HEIGHT);
            grounded = false;
        }
    }

    private void crouch(boolean yes) {
        crouching = yes;
        targetSpeed = yes ? CROUCHING_SPEED : MOVE_SPEED;
    }

    private void run(boolean yes) {
        targetSpeed = yes ? RUNNING_SPEED : MOVE_SPEED;
    }

    private void fly(boolean yes) {
        if (yes) {
            velocity.y = sqrt(-2 * GRAVITY.y * JUMP_HEIGHT);
        }
        grounded = false;
        flying = yes;
    }

    public void look() {
        if (Window.getMouseLocked()) {
            Vector2f currentMouse = Mouse.getPosition();

            double deltaX = currentMouse.x() - ((double) Window.getWidth() / 2);
            double deltaY = currentMouse.y() - ((double) Window.getHeight() / 2);

            boolean rotX = currentMouse.x() != prevMouse.x();
            boolean rotY = currentMouse.y() != prevMouse.y();

            if (rotY) {
                if ((deltaY >= 0 && cameraRotation.x + deltaY < 90) || (deltaY <= 0 && cameraRotation.x > -90)) {
                    cameraRotation.x += (float) deltaY;
                } else {
                    cameraRotation.x = 90 * (cameraRotation.x < 0 ? -1 : 1);
                }
            }
            if (rotX) {
                cameraRotation.y += (float) deltaX;
            }

            prevMouse = currentMouse;

            glfwSetCursorPos(Window.getWindowID(), (double) Window.getWidth() / 2, (double) Window.getHeight() / 2);
        }
    }

    private void playFootsteps() {
        new Thread(() -> {
            Vector3i on = getBlockStandingOn();
            Block b = LevelManager.getBlock(on);
            if (b.getFootstepsSounds() != null) {
                if (!b.getFootstepsSounds().isPlaying()) {
                    b.getFootstepsSounds().playForSecs(false, 1.0f - ((float) targetSpeed / 10f));

                }
            }
        }).start();
    }

    public Vector3i getWorldPosition() {
        return new Vector3i((int) (ceil(position.x - 0.5)), (int) ceil(position.y - (PLAYER_HEIGHT / 2)), (int) ceil(position.z - 0.5f));
    }

    private Vector3i getBlockStandingOn() {
        return new Vector3i((int) (ceil(position.x - 0.5)), (int) floor(position.y - (PLAYER_HEIGHT / 2)), (int) ceil(position.z - 0.5f));
    }

    public boolean getFlying() {
        return flying;
    }

    @Override
    public Vector3d getFriction() {
        if (flying) {
            return DRAG_FLY;
        } else if (grounded) {
            return FRICTION;
        } else if (velocity.y > 0) {
            return DRAG_JUMP;
        } else {
            return DRAG_FALL;
        }
    }

    @Override
    public boolean getAffectedByGravity() {
        return !flying;
    }

    @Override
    protected void onCollision(Quaterniond collision) {
        if (collision.y == 1) {
            grounded = true;
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        move(deltaTime);
        look();
        Renderman.perspective(getCameraPosition(), cameraRotation);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
