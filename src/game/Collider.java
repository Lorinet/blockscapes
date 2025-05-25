package game;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import static java.lang.Double.max;
import static java.lang.Double.min;
import static java.lang.Float.max;
import static java.lang.Float.min;
import static java.lang.Math.abs;

public class Collider {
    private final Vector3f scale;
    private final Vector3f start;
    private final Vector3f end;
    private final boolean collisionsEnabled;

    public Collider(Vector3f start, Vector3f end, boolean collisionsEnabled) {
        this.start = new Vector3f(start);
        this.end = new Vector3f(end);
        this.scale = new Vector3f(abs(start.x - end.x), abs(start.y - end.y), abs(start.z - end.z));
        this.collisionsEnabled = collisionsEnabled;
    }

    public Collider clone() {
        return new Collider(start, end, collisionsEnabled);
    }

    public Vector3f getStart() {
        return start;
    }

    public Vector3f getEnd() {
        return end;
    }

    public Vector3f getScale() {
        return scale;
    }

    public boolean getCollisionsEnabled() {
        return collisionsEnabled;
    }

    public void setPosition(Vector3f position) {
        this.start.x = position.x - scale.x / 2;
        this.end.x = position.x + scale.x / 2;

        this.start.y = position.y - scale.y / 2;
        this.end.y = position.y + scale.y / 2;

        this.start.z = position.z - scale.z / 2;
        this.end.z = position.z + scale.z / 2;
    }

    public void translate(Vector3f direction) {
        start.add(direction);
        end.add(direction);
    }

    public boolean intersect(Collider other) {
        float x = min(end.x, other.end.x) - max(start.x, other.start.x);
        float y = min(end.y, other.end.y) - max(start.y, other.start.y);
        float z = min(end.z, other.end.z) - max(start.z, other.start.z);
        return x > 0 && y > 0 && z > 0;
    }

    private double time(double distance, double velocity) {
        if (velocity == 0) {
            return distance > 0 ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
        } else {
            return distance / velocity;
        }
    }

    public Quaterniond collide(Collider other, Vector3d velocity) {
        if(!other.getCollisionsEnabled()) {
            return null;
        }
        double xEntry = velocity.x > 0 ? time(other.start.x - end.x, velocity.x) : time(other.end.x - start.x, velocity.x);
        double xExit = velocity.x > 0 ? time(other.end.x - start.x, velocity.x) : time(other.start.x - end.x, velocity.x);
        double yEntry = velocity.y > 0 ? time(other.start.y - end.y, velocity.y) : time(other.end.y - start.y, velocity.y);
        double yExit = velocity.y > 0 ? time(other.end.y - start.y, velocity.y) : time(other.start.y - end.y, velocity.y);
        double zEntry = velocity.z > 0 ? time(other.start.z - end.z, velocity.z) : time(other.end.z - start.z, velocity.z);
        double zExit = velocity.z > 0 ? time(other.end.z - start.z, velocity.z) : time(other.start.z - end.z, velocity.z);


        if (xEntry < 0 && yEntry < 0 && zEntry < 0) {
            return null;
        }

        if (xEntry > 1 || yEntry > 1 || zEntry > 1) {
            return null;
        }

        double entryTime = max(xEntry, max(yEntry, zEntry));
        double exitTime = min(xExit, min(yExit, zExit));

        if (entryTime > exitTime) {
            return null;
        }

        return new Quaterniond(
                entryTime != xEntry ? 0 : (velocity.x > 0 ? -1 : 1),
                entryTime != yEntry ? 0 : (velocity.y > 0 ? -1 : 1),
                entryTime != zEntry ? 0 : (velocity.z > 0 ? -1 : 1),
                entryTime);
    }
}
