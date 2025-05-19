package mesh;

import org.joml.Vector3f;

import java.io.Serializable;

public class ModelDataFile implements Serializable {
    private final String name;
    private final String modelFile;

    public ModelDataFile(String name, String modelFile, String texturePath, Vector3f[] collider) {
        this.name = name;
        this.modelFile = modelFile;
    }

    public String getName() {
        return name;
    }

    public String getModelFile() {
        return modelFile;
    }

}
