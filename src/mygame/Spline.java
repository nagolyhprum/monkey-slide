package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

public class Spline {

    private static final float ADD_WEIGHT = 0.01f, RADIUS = 1;

    public static void addSpline(AssetManager am, Node node, Vector3f start, Vector3f controlA, Vector3f controlB, Vector3f end) {
        float weight = 0;
        Material red = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        red.setColor("Diffuse", ColorRGBA.White);
        red.setColor("Ambient", ColorRGBA.Red);
        red.setBoolean("UseMaterialColors", true);
        while (weight < 1) {
            Vector3f a = getCubic(start, controlA, controlB, end, weight - ADD_WEIGHT),
                    b = getCubic(start, controlA, controlB, end, weight + ADD_WEIGHT);
            Geometry g = new Geometry("slide", new Cylinder(36, 36, RADIUS, 1, false));
            g.setMaterial(red);
            setConnectiveTransform(new float[]{a.x, a.y, a.z}, new float[]{b.x, b.y, b.z}, g);
            node.attachChild(g);
            weight = weight + ADD_WEIGHT;
        }
    }

    public static Vector3f getDirection(Vector3f start, Vector3f controlA, Vector3f controlB, Vector3f end, float weight) {
        Vector3f s = getCubic(start, controlA, controlB, end, weight),
                e = getCubic(start, controlA, controlB, end, weight + ADD_WEIGHT);
        return e.subtract(s).normalize();
    }

    public static Vector3f getCubic(Vector3f start, Vector3f controlA, Vector3f controlB, Vector3f end, float weight) {
        Vector3f a = getQuadratic(start, controlA, controlB, weight), b = getQuadratic(controlA, controlB, end, weight);
        return new Vector3f(a.x * (1 - weight) + b.x * weight, a.y * (1 - weight) + b.y * weight, a.z * (1 - weight) + b.z * weight);
    }

    private static Vector3f getQuadratic(Vector3f start, Vector3f control, Vector3f end, float weight) {
        float x1 = start.x + (control.x - start.x) * weight,
                y1 = start.y + (control.y - start.y) * weight,
                z1 = start.z + (control.z - start.z) * weight,
                x2 = control.x + (end.x - control.x) * weight,
                y2 = control.y + (end.y - control.y) * weight,
                z2 = control.z + (end.z - control.z) * weight;
        return new Vector3f(x1 * (1 - weight) + x2 * weight, y1 * (1 - weight) + y2 * weight, z1 * (1 - weight) + z2 * weight);

    }

    private static void setConnectiveTransform(float[] p1, float[] p2, Geometry c) {
        // 1. direction
        Vector3f u = new Vector3f(p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]);
        float length = u.length();
        u = u.normalize();
        // 2. rotation matrix
        Vector3f v = u.cross(Vector3f.UNIT_Z);
        Vector3f w = v.cross(u);
        Matrix3f m = new Matrix3f(w.x, v.x, u.x, w.y, v.y, u.y, w.z, v.z, u.z);
        c.setLocalRotation(m);
        // 3. scaling
        c.setLocalScale(1, 1, length);
        // 4. translation
        float[] center = {(p1[0] + p2[0]) / 2f, (p1[1] + p2[1]) / 2f, (p1[2] + p2[2]) / 2f};
        c.setLocalTranslation(center[0], center[1], center[2]);
    }
}
