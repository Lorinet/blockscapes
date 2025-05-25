package game;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
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

    public static Matrix4f createViewMatrixFirstPerson(Vector3f translation, Vector3f rotation) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotateXYZ((float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), 0);
        matrix.translate(new Vector3f(-translation.x(), -translation.y(), -translation.z()));
        return matrix;

    }

    public static Matrix4f createViewMatrixThirdPerson(Vector3f cameraPosition, Vector3f targetPosition) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.lookAt(cameraPosition, targetPosition, new Vector3f(0, 1, 0));
        return matrix;
    }

    public static Vector3f rotateVector(Vector3f position, Vector3f rotation) {
        Matrix4f rotationMatrix = new Matrix4f();
        float rx = (float)Math.toRadians(rotation.x);
        float ry = (float)Math.toRadians(rotation.y);
        rotationMatrix.rotateY((float)(-ry));
        rotationMatrix.rotateX((float)(-rx));
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.mul(rotationMatrix);
        matrix.translate(position);
        Vector3f result = new Vector3f();
        matrix.transformPosition(result, result);
        return result;
    }
}
