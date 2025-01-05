package level;

import game.Collider;
import game.StageManager;
import mesh.*;
import net.jpountz.lz4.LZ4BlockInputStream;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Chunk {
    public static final int SIZE_XZ = 16;
    public static final int SIZE_Y = 512;

    private final int chunkX;
    private final int chunkZ;
    private boolean loading = false;
    private boolean loaded;
    private boolean modified = false;
    private Integer entity = null;

    private byte[][][] blocks;
    private List<Float> vertexes;
    private List<Float> textureCoords;
    private List<Float> shading;
    private List<Integer> indexes;
    private Thread thread;

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.loaded = false;
    }

    public void reload() {
        loading = false;
        loaded = false;
    }

    public void load() {
        loaded = false;
        loading = true;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            while (thread.isAlive()) {
            }
        }
        thread = new Thread(() -> {
            loadChunkData();
            updateMesh();
        });
        thread.start();
    }

    public void updateMesh() {
        loaded = false;
        loading = true;

        vertexes = new ArrayList<>();
        textureCoords = new ArrayList<>();
        shading = new ArrayList<>();
        indexes = new ArrayList<>();

        for (int x = 0; x < SIZE_XZ; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                for (int z = 0; z < SIZE_XZ; z++) {
                    if (Thread.interrupted()) {
                        return;
                    }
                    if (blocks[x][z][y] == Blocks.ID_AIR) {
                        continue;
                    }
                    Block b = Blocks.getBlock(blocks[x][z][y]);
                    Vector3i pos = new Vector3i(x, y, z);
                    for (int i = 0; i < Block.adjacentFaces.length; i++) {
                        Vector3i toCheck = new Vector3i(pos);
                        toCheck.add(Block.adjacentFaces[i]);
                        try {
                            byte nbr = blocks[toCheck.x][toCheck.z][toCheck.y];
                            boolean xpant = Blocks.isTransparent(nbr);
                            if (xpant) {
                                FaceVertex face = new FaceVertex(b.getFace(Block.adjacentFaces[i]));
                                addQuad(pos, face, b.getIsTransparent() && b.getId() != nbr);
                            }
                        } catch (Exception e) {
                            FaceVertex face = new FaceVertex(b.getFace(Block.adjacentFaces[i]));
                            addQuad(pos, face, false);
                        }
                    }
                }
            }
        }

        loaded = true;
    }

    public void createModel() {
        Mesh model = GeometryManager.createModel("stone", vertexes, textureCoords, shading, indexes);
        vertexes.clear();
        textureCoords.clear();
        shading.clear();
        indexes.clear();
        Vector3f chunkPosA = new Vector3f(chunkX * SIZE_XZ, 0, chunkZ * SIZE_XZ);
        Vector3f chunkPosB = new Vector3f(chunkX * SIZE_XZ + SIZE_XZ, SIZE_Y, chunkZ * SIZE_XZ + SIZE_XZ);
        if (entity == null) {
            entity = StageManager.createEntity(model, chunkPosA, new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), new Collider(chunkPosA, chunkPosB, false));
        } else {
            StageManager.getEntity(entity).updateModel(model);
        }
    }

    private void addQuad(Vector3i pos, FaceVertex face, boolean backFaceCullEvasion) {
        face.offsetPos(pos);
        face.offsetIndices(vertexes.size() / 3);
        for (Vector3f vx : face.getVertexes()) {
            vertexes.add(vx.x);
            vertexes.add(vx.y);
            vertexes.add(vx.z);
        }

        for (Vector2f tx : face.getTexCoords()) {
            textureCoords.add(tx.x);
            textureCoords.add(tx.y);
        }
        for (float sx : face.getShading()) {
            shading.add(sx);
        }
        for (int ix : face.getIndices()) {
            indexes.add(ix);
        }
        if (backFaceCullEvasion) {
            for (int i = face.getIndices().length - 1; i >= 0; i--) {
                indexes.add(face.getIndices()[i]);
            }
        }
    }

    public void loadChunkData() {
        if (blocks != null) {
            return;
        }
        System.out.println("Loading chunk X: " + chunkX + " Z:" + chunkZ);
        File chunkFile = Paths.get("levels", LevelManager.getLevelName(), "c_" + chunkX + "_" + chunkZ + ".dat").toFile();
        if (!chunkFile.exists()) {
            blocks = Generator.generateChunkData(chunkX, chunkZ);
            modified = true;
        } else {
            try {
                FileInputStream input = new FileInputStream(chunkFile);
                LZ4BlockInputStream zip = new LZ4BlockInputStream(input);
                blocks = new byte[Chunk.SIZE_XZ][Chunk.SIZE_XZ][Chunk.SIZE_Y];
                for (int x = 0; x < Chunk.SIZE_XZ; x++) {
                    for (int z = 0; z < Chunk.SIZE_XZ; z++) {
                        zip.read(blocks[x][z], 0, Chunk.SIZE_Y);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        System.out.println("Loaded chunk X: " + chunkX + " Z:" + chunkZ);
    }

    public boolean getModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean getLoaded() {
        return loaded;
    }

    public boolean getLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public byte[][][] getBlocks() {
        return blocks;
    }

    private void destroy() {
        loading = false;
        loaded = false;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            while (thread.isAlive()) {
            }
        }
        entity = null;
        vertexes.clear();
        textureCoords.clear();
        shading.clear();
        indexes.clear();
    }

    public void unload() {
        if (entity != null) {
            StageManager.despawnEntity(entity);
        }
        destroy();
    }
}
