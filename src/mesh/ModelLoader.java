package mesh;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ModelLoader {
    public static ModelData loadModel(String modelFile) {
        try (BufferedReader objReader = new BufferedReader(new FileReader(Paths.get("models", "files", modelFile).toString()))) {
            ArrayList<Vector3f> tempVex = new ArrayList<>();
            ArrayList<Vector2f> tempTex = new ArrayList<>();
            ArrayList<Vector3f> tempNox = new ArrayList<>();
            ArrayList<OBJVertexRef[]> faces = new ArrayList<>();
            HashMap<String, Material> materials = new HashMap<>();
            String currentMaterial = null;

            String line;
            while ((line = objReader.readLine()) != null) {
                String[] split = line.trim().split("\\s+");
                switch (split[0]) {
                    case "v":
                        tempVex.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                        break;
                    case "vn":
                        tempNox.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                        break;
                    case "vt":
                        tempTex.add(new Vector2f(Float.parseFloat(split[1]), 1 - Float.parseFloat(split[2])));
                        break;
                    case "f":
                        ArrayList<OBJVertexRef> face = new ArrayList<>();
                        for (int i = 1; i < split.length; i++) {
                            if (!split[i].isEmpty()) {
                                String[] faceSplit = split[i].split("/");
                                Integer v = null;
                                Integer t = null;
                                Integer n = null;

                                try {
                                    v = parseObjIndex(faceSplit[0], tempVex.size());
                                    if (faceSplit.length > 1 && !faceSplit[1].isEmpty()) {
                                        t = parseObjIndex(faceSplit[1], tempTex.size());
                                    }
                                    if (faceSplit.length > 2 && !faceSplit[2].isEmpty()) {
                                        n = parseObjIndex(faceSplit[2], tempNox.size());
                                    }
                                } catch (NumberFormatException e) {
                                    throw new RuntimeException("Invalid obj");
                                }

                                face.add(new OBJVertexRef(v, n, t, currentMaterial));
                            }
                        }

                        if (face.size() >= 3) {
                            OBJVertexRef firstVertex = face.get(0);
                            for (int i = 1; i < face.size() - 1; i++) {
                                faces.add(new OBJVertexRef[]{firstVertex, face.get(i), face.get(i + 1)});
                            }
                        } else if (!face.isEmpty()) {
                            System.err.println("Warning: Face has fewer than 3 vertices (ignored): " + line);
                        }
                        break;
                    case "mtllib":
                        String mtlPath = split[1];
                        materials.putAll(loadMaterials(Paths.get("models", "files", mtlPath).toString()));
                        break;
                    case "usemtl":
                        currentMaterial = split[1];
                        break;
                }
            }

            HashMap<VertexDef, Integer> vexMap = new HashMap<>();
            HashMap<String, Integer> matMap = new HashMap<>();

            ArrayList<Material> mats = new ArrayList<>();
            ArrayList<Float> vex = new ArrayList<>();
            ArrayList<Float> tex = new ArrayList<>();
            ArrayList<Float> nox = new ArrayList<>();
            ArrayList<Integer> max = new ArrayList<>();

            ArrayList<Integer> ix = new ArrayList<>();
            int vexIndex = 0;
            int matIndex = 0;
            for (OBJVertexRef[] face : faces) {
                for (OBJVertexRef ref : face) {
                    Vector3f currentVertex = (ref.vertex != null && ref.vertex >= 0 && ref.vertex < tempVex.size()) ? tempVex.get(ref.vertex) : new Vector3f(0, 0, 0);
                    Vector3f currentNormal = (ref.normal != null && ref.normal >= 0 && ref.normal < tempNox.size()) ? tempNox.get(ref.normal) : new Vector3f(0, 1, 0);
                    Vector2f currentTexCoord = (ref.texCoord != null && ref.texCoord >= 0 && ref.texCoord < tempTex.size()) ? tempTex.get(ref.texCoord) : new Vector2f(0, 0);

                    VertexDef def = new VertexDef(currentVertex, currentNormal, currentTexCoord, ref.material);

                    if (vexMap.containsKey(def)) {
                        ix.add(vexMap.get(def));
                    } else {
                        vex.addAll(List.of(def.vertex.x, def.vertex.y, def.vertex.z));
                        tex.addAll(List.of(def.texCoord.x, def.texCoord.y));
                        nox.addAll(List.of(def.normal.x, def.normal.y, def.normal.z));
                        vexMap.put(def, vexIndex);
                        ix.add(vexIndex);
                        vexIndex++;
                    }

                    if(ref.material != null) {
                        if (matMap.containsKey(ref.material)) {
                            max.add(matMap.get(ref.material));
                        } else {
                            mats.add(materials.get(ref.material));
                            matMap.put(ref.material, matIndex);
                            max.add(matIndex);
                            matIndex++;
                        }
                    }
                }
            }

            return new ModelData(new FloatArrayList(vex), new FloatArrayList(tex), new FloatArrayList(nox), new IntArrayList(max), new IntArrayList(ix), mats);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Material> loadMaterials(String filePathString) throws IOException {
        Path filePath = Paths.get(filePathString);
        HashMap<String, Material> materials = new HashMap<>();
        Material currentMaterial = null;

        if (!Files.exists(filePath)) {
            throw new IOException("MTL file not found: " + filePath);
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] tokens = line.split("\\s+");
                if (tokens.length == 0) {
                    continue;
                }

                String command = tokens[0];

                switch (command) {
                    case "newmtl":
                        if (tokens.length >= 2) {
                            String materialName = tokens[1];
                            currentMaterial = new Material(materialName);
                            materials.put(materialName, currentMaterial);
                        } else {
                            throw new RuntimeException("Invalid material newmtl: " + line);
                        }
                        break;

                    case "Ns":
                        if (currentMaterial != null && tokens.length >= 2) {
                            currentMaterial.setShininess(Float.parseFloat(tokens[1]));
                        } else if (currentMaterial == null) {
                            throw new RuntimeException("Invalid material: " + line);
                        } else {
                            throw new RuntimeException("Invalid material: " + line);
                        }
                        break;
                    case "Ka":
                    case "Kd":
                    case "Ks":
                    case "Ke":
                        if (currentMaterial != null && tokens.length >= 4) {
                            Vector3f color = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                            switch (command) {
                                case "Ka":
                                    currentMaterial.setAmbientColor(color);
                                    break;
                                case "Kd":
                                    currentMaterial.setDiffuseColor(color);
                                    break;
                                case "Ks":
                                    currentMaterial.setSpecularColor(color);
                                    break;
                                case "Ke":
                                    currentMaterial.setEmissiveColor(color);
                                    break;
                            }
                        } else if (currentMaterial == null) {
                            throw new RuntimeException("Invalid material color: " + line);
                        } else {
                            throw new RuntimeException("Invalid material color: " + line);
                        }
                        break;
                    case "Ni":
                        if (currentMaterial != null && tokens.length >= 2) {
                            currentMaterial.setOpticalDensity(Float.parseFloat(tokens[1]));
                        } else if (currentMaterial == null) {
                            throw new RuntimeException("Invalid material: " + line);
                        } else {
                            throw new RuntimeException("Invalid material: " + line);
                        }
                        break;
                    case "d":
                    case "Tr":
                        if (currentMaterial != null && tokens.length >= 2) {
                            float dissolveValue = Float.parseFloat(tokens[1]);
                            if (command.equals("Tr")) {
                                dissolveValue = 1.0f - dissolveValue;
                            }
                            currentMaterial.setDissolve(dissolveValue);
                        } else if (currentMaterial == null) {
                            throw new RuntimeException("Invalid material: " + line);
                        } else {
                            throw new RuntimeException("Invalid material: " + line);
                        }
                        break;
                    case "illum":
                        if (currentMaterial != null && tokens.length >= 2) {
                            try {
                                currentMaterial.setIlluminationModel(Integer.parseInt(tokens[1]));
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("Invalid material: " + line);
                            }
                        } else if (currentMaterial == null) {
                            throw new RuntimeException("Invalid material: " + line);
                        } else {
                            throw new RuntimeException("Invalid material: " + line);
                        }
                        break;

                    case "map_Ka":
                    case "map_Kd":
                    case "map_Ks":
                    case "map_Ke":
                    case "map_d":
                        if (currentMaterial != null && tokens.length >= 2) {
                            String texturePath = line.substring(line.indexOf(tokens[1]));
                            switch (command) {
                                case "map_Ka":
                                    currentMaterial.setAmbientTexturePath(texturePath);
                                    break;
                                case "map_Kd":
                                    currentMaterial.setDiffuseTexturePath(texturePath);
                                    break;
                                case "map_Ks":
                                    currentMaterial.setSpecularTexturePath(texturePath);
                                    break;
                                case "map_Ke":
                                    currentMaterial.setEmissiveTexturePath(texturePath);
                                    break;
                                case "map_d":
                                    currentMaterial.setDissolveTexturePath(texturePath);
                                    break;
                            }
                        } else if (currentMaterial == null) {
                            throw new RuntimeException("Invalid material: " + line);
                        } else {
                            throw new RuntimeException("Invalid material: " + line);
                        }
                        break;
                    case "map_Bump":
                    case "bump":
                        if (currentMaterial != null && tokens.length >= 2) {
                            String texturePath = line.substring(line.indexOf(tokens[1]));
                            currentMaterial.setNormalMapPath(texturePath);
                        } else if (currentMaterial == null) {
                            throw new RuntimeException("Invalid material: " + line);
                        } else {
                            throw new RuntimeException("Invalid material: " + line);
                        }
                        break;

                    default:
                        break;
                }
            }
        }

        return materials;
    }

    private static int parseObjIndex(String s, int size) throws NumberFormatException {
        int index = Integer.parseInt(s);
        if (index < 0) {
            index = size + index;
        } else {
            index = index - 1;
        }
        return index;
    }

    private static class OBJVertexRef {
        public Integer vertex;
        public Integer normal;
        public Integer texCoord;
        public String material;

        public OBJVertexRef(Integer vertex, Integer normal, Integer texCoord, String material) {
            this.vertex = vertex;
            this.normal = normal;
            this.texCoord = texCoord;
            this.material = material;
        }
    }

    private static class VertexDef {
        public Vector3f vertex;
        public Vector3f normal;
        public Vector2f texCoord;
        public String material;

        public VertexDef(Vector3f vertex, Vector3f normal, Vector2f texCoord, String material) {
            this.vertex = vertex;
            this.normal = normal;
            this.texCoord = texCoord;
            this.material = material;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VertexDef vertexDef = (VertexDef) o;
            return Objects.equals(vertex, vertexDef.vertex) && Objects.equals(normal, vertexDef.normal) && Objects.equals(texCoord, vertexDef.texCoord);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vertex, normal, texCoord);
        }
    }
}
