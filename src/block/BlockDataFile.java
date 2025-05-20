package block;

import audio.AudioController;
import audio.AudioManager;
import game.Collider;
import mesh.Material;
import mesh.ModelLoader;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class BlockDataFile {
    private Integer id;
    private String name;
    private Boolean showInInventory;
    private Boolean isTransparent;
    private Vector3f[] collider;
    private Integer[] atlasFaces;
    private String customModel;
    private String customMaterial;
    private String footstepsSound;
    private String blockSound;

    public BlockDataFile(Integer id, String name, Boolean showInInventory, Boolean isTransparent, Vector3f[] collider, Integer[] atlasFaces, String customModel, String customMaterial, String footstepsSound, String blockSound) {
        this.id = id;
        this.name = name;
        this.showInInventory = showInInventory;
        this.isTransparent = isTransparent;
        this.collider = collider;
        this.atlasFaces = atlasFaces;
        this.customModel = customModel;
        this.footstepsSound = footstepsSound;
        this.blockSound = blockSound;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getShowInInventory() {
        return showInInventory;
    }

    public Boolean getTransparent() {
        return isTransparent;
    }

    public Vector3f[] getCollider() {
        return collider;
    }

    public Integer[] getAtlasFaces() {
        return atlasFaces;
    }

    public String getCustomModel() {
        return customModel;
    }

    public String getCustomMaterial() {
        return customMaterial;
    }

    public String getFootstepsSound() {
        return footstepsSound;
    }

    public String getBlockSound() {
        return blockSound;
    }

    public Block toBlock() {
        HashMap<Vector3i, FaceVertex> faces = null;
        int materialIndex = 0;

        if (atlasFaces != null) {
            faces = new HashMap<>();
            if (atlasFaces.length == 0) {
                // air
            } else if (atlasFaces.length == 6) {
                faces.put(Block.adjacentFaces[0], FaceVertex.right(atlasFaces[0]));
                faces.put(Block.adjacentFaces[1], FaceVertex.left(atlasFaces[1]));
                faces.put(Block.adjacentFaces[2], FaceVertex.up(atlasFaces[2]));
                faces.put(Block.adjacentFaces[3], FaceVertex.down(atlasFaces[3]));
                faces.put(Block.adjacentFaces[4], FaceVertex.forward(atlasFaces[4]));
                faces.put(Block.adjacentFaces[5], FaceVertex.back(atlasFaces[5]));
            } else {
                throw new RuntimeException("Invalid block data file for " + name);
            }
            if (customMaterial != null) {
                try {
                    Material mat = new Material(Blocks.blockMaterials.get(0));
                    Material newMat = ModelLoader.loadMaterials(Paths.get("blocks", "materials", customMaterial).toString()).values().stream().findFirst().orElseThrow();
                    mat.setAmbientColor(newMat.getAmbientColor());
                    mat.setDiffuseColor(newMat.getDiffuseColor());
                    mat.setSpecularColor(newMat.getSpecularColor());
                    mat.setEmissiveColor(newMat.getEmissiveColor());
                    mat.setShininess(newMat.getShininess());
                    if (newMat.getDiffuseTexturePath() != null) {
                        mat.setDiffuseTexturePath(newMat.getDiffuseTexturePath());
                    }

                    if (Blocks.blockMaterials.contains(newMat)) {
                        materialIndex = Blocks.blockMaterials.indexOf(newMat);
                    } else {
                        materialIndex = Blocks.blockMaterials.size();
                        Blocks.blockMaterials.add(mat);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Collider coll = null;
        if (collider != null) {
            if (collider.length != 2) {
                throw new RuntimeException("Invalid block data file for " + name);
            }
            coll = new Collider(new Vector3f(collider[0]), new Vector3f(collider[1]), true);
        }
        AudioController footstepsAudio = null;
        if (footstepsSound != null) {
            footstepsAudio = AudioManager.getSound(footstepsSound);
        }
        AudioController blockAudio = null;
        if (blockSound != null) {
            blockAudio = AudioManager.getSound(blockSound);
        }
        return new Block(id, name, showInInventory, isTransparent, faces, null, materialIndex, coll, footstepsAudio, blockAudio);
    }

}

