package game;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MathUtils {
    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation);
        matrix.rotateXYZ(rotation);
        matrix.scale(scale);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Vector3f translation, Vector3f rotation) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotateXYZ((float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), 0);
        matrix.translate(new Vector3f(-translation.x(), -translation.y(), -translation.z()));
        return matrix;
    }
}
