package game;

import block.Block;
import block.Blocks;
import entities.Cupcake;
import entities.RendermanEntity;
import level.LevelManager;
import mesh.Mesh;
import mesh.ModelManager;
import org.joml.*;
import ui.HotBar;
import ui.UIManager;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;
import static org.lwjgl.glfw.GLFW.*;

public class Player extends Mob {
    private static final float REACH = 5;

    private static final Collection<Float> playerVertexes = new ArrayList<>(List.of());
    private static final Collection<Integer> playerIndexes = new ArrayList<>(List.of());
    private static final Collection<Float> playerTextureCoords = new ArrayList<>(List.of());
    private static final Collection<Float> playerShading = new ArrayList<>(List.of());
    public Vector3f cameraRotation = new Vector3f(0, 0, 0);
    private static final Vector3f thirdPersonVector = new Vector3f(0, 0, -5);
    private Vector2f prevMouse = new Vector2f(0, 0);

    public Player() {
        this(new Vector3f(LevelManager.getInitialPosition()), LevelManager.getFlying());
    }

    public Player(Vector3f position, boolean flying) {
        super(new Mesh[]{ModelManager.getModel("player")}, position, new Vector3f(0, 0, 0), new Vector3f(32, 32, 32),
                new Collider(new Vector3f(-0.2f, 0f, -0.2f), new Vector3f(0.2f, -heightNormal, 0.2f), true), false, 100);
        fly(flying);
        setVisible(StageManager.getSettings().getThirdPerson());
    }

    public Vector3f getCameraPosition() {
        Vector3f perspo = new Vector3f(position);
        perspo.y += (crouching ? heightCrouch : heightNormal) / 2 - 0.2f;
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
        Vector3f cameraPosition = getCameraPosition();
        if(StageManager.getSettings().getThirdPerson()) {
            //cameraPosition = position;
        }
        Raycast rc = new Raycast(cameraPosition, cameraRotation, callback, new ArrayList<>());
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

        ArrayList<Integer> hurtables = new ArrayList<>();
        try {
            for (Entity e : StageManager.getEntities()) {
                if (!e.isInvincible() && e.id != id) {
                    FrustumIntersection frustumIntersection = Renderman.getFrustumIntersection();
                    frustumIntersection.set(Renderman.getProjectionViewMatrix());
                    if (frustumIntersection.testAab(e.getHitbox().getStart(), e.getHitbox().getEnd())) {
                        hurtables.add(e.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to get hurtable entities: " + e.toString());
        }

        Vector3f cameraPosition = getCameraPosition();
        if(StageManager.getSettings().getThirdPerson()) {
            //cameraPosition = position;
        }
        Raycast rc = new Raycast(cameraPosition, cameraRotation, callback, hurtables);
        while (rc.getDistance() < REACH) {
            if (rc.step()) {
                break;
            }
        }
    }

    private void playerMovement(double deltaTime) {
        if (Keyboard.getKeyUp(GLFW_KEY_T)) {
            StageManager.createEntity(new Cupcake(new Vector3f(position.x, position.y + 5, position.z), new Vector3f(0, 0, 0)));
        }
        if (Keyboard.getKeyUp(GLFW_KEY_R)) {
            StageManager.createEntity(new RendermanEntity(new Vector3f(position.x, position.y + 5, position.z), new Vector3f(0, 0, 0)));
        }

        if(Keyboard.getKeyUp(GLFW_KEY_F5)) {
            StageManager.getSettings().setThirdPerson(!StageManager.getSettings().getThirdPerson());
            StageManager.saveSettings();
            setVisible(StageManager.getSettings().getThirdPerson());
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
            boolean inWater = getInWater();
            if (Keyboard.getKey(GLFW_KEY_SPACE) && grounded) {
                if(inWater) {
                    acceleration.y = by * BUOYANCY.y;
                } else {
                    jump();
                }
            }
            if(!inWater) {
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
            if (Keyboard.getKey(GLFW_KEY_LEFT_SHIFT)) {
                if (inWater) {
                    acceleration.y = -(by * BUOYANCY.y + 1);
                }
            }

        }

        LevelManager.loadChunks(new Vector2i((int) position.x, (int) position.z));
    }

    public boolean getUnderwater() {
        Vector3i blockStandngOn = getBlockStandingOn();
        blockStandngOn.y += 2;
        return LevelManager.getBlock(blockStandngOn).getId() == Blocks.ID_WATER;
    }

    public void playerLook() {
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
                    cameraRotation.x = 89 * (cameraRotation.x < 0 ? -1 : 1);
                }
            }
            if (rotX) {
                cameraRotation.y += (float) deltaX;
            }

            if(cameraRotation.x < -89) {
                cameraRotation.x = -89;
            } else if(cameraRotation.x > 89) {
                cameraRotation.x = 89;
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

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        playerMovement(deltaTime);
        playerLook();
        setVisible(StageManager.getSettings().getThirdPerson());
        Vector3f cameraPosition = new Vector3f(this.getCameraPosition());
        if(StageManager.getSettings().getThirdPerson()) {
            Vector3f rot = MathUtils.rotateVector(thirdPersonVector, cameraRotation);
            Vector3f newCameraPosition = new Vector3f(cameraPosition);
            newCameraPosition.sub(rot);
            rotation.y = -(float)Math.toRadians(cameraRotation.y);
            Renderman.perspective(MathUtils.createViewMatrixThirdPerson(newCameraPosition, cameraPosition), newCameraPosition);
        } else {
            Renderman.perspective(MathUtils.createViewMatrixFirstPerson(cameraPosition, cameraRotation), cameraPosition);
        }
        //Renderman.perspective(cameraPosition, cameraRotation);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
