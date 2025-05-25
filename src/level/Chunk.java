package level;

import block.Block;
import block.Blocks;
import block.FaceVertex;
import game.Collider;
import game.StageManager;
import generator.Generator;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import mesh.*;
import net.jpountz.lz4.LZ4BlockInputStream;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import game.Entity;

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
    private FloatArrayList[] vertexes;
    private FloatArrayList[] textureCoords;
    private FloatArrayList[] normals;
    private FloatArrayList[] shading;
    private IntArrayList[] materialIndexes;
    private IntArrayList[] indexes;
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
            synchronized (LevelManager.chunksEntitiesLock) {
                List<EntityStateData> entityStates = LevelManager.getChunksEntities().getOrDefault(new Vector2i(chunkX, chunkZ), new ArrayList<>());
                for (EntityStateData entData : entityStates) {
                    StageManager.createEntity(Entity.instantiateEntity(entData));
                }
            }
        });
        thread.start();
    }

    public void updateMesh() {
        System.out.println("Update mesh");
        loaded = false;
        loading = true;

        vertexes = new FloatArrayList[2];
        textureCoords = new FloatArrayList[2];
        normals = new FloatArrayList[2];
        shading = new FloatArrayList[2];
        materialIndexes = new IntArrayList[2];
        indexes = new IntArrayList[2];

        vertexes[0] = new FloatArrayList(150000);
        textureCoords[0] = new FloatArrayList(10000);
        normals[0] = new FloatArrayList(150000);
        shading[0] = new FloatArrayList(60000);
        materialIndexes[0] = new IntArrayList(60000);
        indexes[0] = new IntArrayList(100000);

        vertexes[1] = new FloatArrayList(1500);
        textureCoords[1] = new FloatArrayList(100);
        normals[1] = new FloatArrayList(1500);
        shading[1] = new FloatArrayList(600);
        materialIndexes[1] = new IntArrayList(600);
        indexes[1] = new IntArrayList(1000);

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
                    if(b.hasCustomModel()) {
                        addCustomModel(pos, b.getCustomModel(), 0);
                    } else {
                        for (int i = 0; i < Block.adjacentFaces.length; i++) {
                            Vector3i toCheck = new Vector3i(pos);
                            toCheck.add(Block.adjacentFaces[i]);
                            try {
                                byte nbr = blocks[toCheck.x][toCheck.z][toCheck.y];
                                boolean xpant = Blocks.exposesNeighbor(nbr) && !(Blocks.refractory(nbr) && Blocks.refractory(b.getId()));
                                if (xpant) {
                                    FaceVertex face = b.getFace(Block.adjacentFaces[i]);
                                    addQuad(pos, face, b.getMaterialIndex(), b.getIsTransparent() && b.getId() != nbr, 0);
                                    if (b.getIsTransparent()) {
                                        addQuad(pos, face, b.getMaterialIndex(), b.getIsTransparent() && b.getId() != nbr, 1);
                                    }
                                }
                            } catch (Exception e) {
                                Vector3i adjFace = Block.adjacentFaces[i];
                                if((adjFace.x == 0 && adjFace.z == 0) || !Blocks.refractory(b.getId())) {
                                    FaceVertex face = b.getFace(adjFace);
                                    addQuad(pos, face, b.getMaterialIndex(), false, 0);
                                    if (b.getIsTransparent()) {
                                        addQuad(pos, face, b.getMaterialIndex(), false, 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        loaded = true;
    }

    public void createModel() {
        /*System.out.println("------------------------------------------");
        System.out.println("Vertices: " + vertexes.size());
        System.out.println("Texture Coords: " + textureCoords.size());
        System.out.println("Normals: " + normals.size());
        System.out.println("Shading: " + shading.size());
        System.out.println("Texture Indexes: " + textureIndexes.size());
        System.out.println("Indexes: " + indexes.size());
        System.out.println("------------------------------------------");*/
        ModelData modelDataOpaque = new ModelData(vertexes[0], textureCoords[0], normals[0], shading[0], materialIndexes[0], indexes[0], Blocks.blockMaterials, false);
        ModelData modelDataXpant = new ModelData(vertexes[1], textureCoords[1], normals[1], shading[1], materialIndexes[1], indexes[1], Blocks.blockMaterials, true);
        Mesh modelOpaque = ModelManager.createModelFromData(modelDataOpaque, false);
        Mesh modelXpant = ModelManager.createModelFromData(modelDataXpant, false);
        Mesh[] models = new Mesh[] {modelOpaque, modelXpant};
        for(int i = 0; i < 2; i++) {
            vertexes[i].clear();
            textureCoords[i].clear();
            normals[i].clear();
            shading[i].clear();
            materialIndexes[i].clear();
            indexes[i].clear();
        }
        Vector3f chunkPosA = new Vector3f(chunkX * SIZE_XZ, 0, chunkZ * SIZE_XZ);
        Vector3f chunkPosB = new Vector3f(chunkX * SIZE_XZ + SIZE_XZ, SIZE_Y, chunkZ * SIZE_XZ + SIZE_XZ);
        if (entity == null) {
            entity = StageManager.createEntity(models, chunkPosA, new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), new Collider(chunkPosA, chunkPosB, false), true, 1);
        } else {
            StageManager.getEntity(entity).updateModels(models);
        }
    }

    private void addQuad(Vector3i pos, FaceVertex face, int materialIndex, boolean backFaceCullEvasion, int whichBuffer) {
        int indexOffset = vertexes[whichBuffer].size() / 3;
        for (Vector3f vx : face.getVertexes()) {
            vertexes[whichBuffer].add(vx.x + pos.x);
            vertexes[whichBuffer].add(vx.y + pos.y);
            vertexes[whichBuffer].add(vx.z + pos.z);
        }

        for (Vector2f tx : face.getTexCoords()) {
            textureCoords[whichBuffer].add(tx.x);
            textureCoords[whichBuffer].add(tx.y);
        }

        for (Vector3f vn : face.getNormals()) {
            normals[whichBuffer].add(vn.x);
            normals[whichBuffer].add(vn.y);
            normals[whichBuffer].add(vn.z);
        }

        for (float sx : face.getShading()) {
            shading[whichBuffer].add(sx);
        }

        for(int i = 0; i < 4; i++) {
            materialIndexes[whichBuffer].add(materialIndex);
        }


        for (int ix : face.getIndices()) {
            indexes[whichBuffer].add(ix + indexOffset);
        }
        if (backFaceCullEvasion) {
            for (int i = face.getIndices().length - 1; i >= 0; i--) {
                indexes[whichBuffer].add(face.getIndices()[i] + indexOffset);
            }
        }
    }

    private void addCustomModel(Vector3i pos, ModelData model, int whichBuffer) {
        int indexOffset = vertexes[whichBuffer].size() / 3;
        for (int i = 0; i < model.getVertexes().size(); i += 3) {
            vertexes[whichBuffer].add(model.getVertexes().getFloat(i) + pos.x);
            vertexes[whichBuffer].add(model.getVertexes().getFloat(i + 1) + pos.y);
            vertexes[whichBuffer].add(model.getVertexes().getFloat(i + 2) + pos.z);
        }

        for(float tx : model.getTextureCoords()) {
            textureCoords[whichBuffer].add(tx);
        }

        for(float vn : model.getNormals()) {
            normals[whichBuffer].add(vn);
        }

        for(float sx : model.getShading()) {
            shading[whichBuffer].add(sx);
        }

        for(float mix : model.getMaterialIndices()) {
            materialIndexes[whichBuffer].add((int) mix);
        }

        for(int i = 0; i < model.getIndices().size(); i++) {
            indexes[whichBuffer].add(model.getIndices().get(i) + indexOffset);
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
        for(int i = 0; i < 2; i++) {
            vertexes[i].clear();
            textureCoords[i].clear();
            shading[i].clear();
            indexes[i].clear();
        }
    }

    public void unload() {
        if (entity != null) {
            StageManager.despawnEntity(entity);
        }
        Vector2i chunk = new Vector2i(chunkX, chunkZ);
        LevelManager.saveEntityStates(chunk, true);
        destroy();
    }
}
