package mygame;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.BufferUtils;
import java.util.Random;

public class BezierCurve extends Node {

    //how much to step when generating bezier curves
    private static final float ADD_WEIGHT = 0.01f;
    //the radius of the spline to generate
    public static final float RADIUS = 1;
    //instance of spline points
    private Vector3f start, controlA, controlB, end;
    private Material mat;

    public BezierCurve(Material mat, Vector3f start, Vector3f controlA, Vector3f controlB, Vector3f end) {
        this.start = start;
        this.controlA = controlA;
        this.controlB = controlB;
        this.end = end;
        this.mat = mat;
        addMeshSpline(mat);
    }

    public Vector3f getDirection(float weight) {
        return getDirection(start, controlA, controlB, end, weight);
    }

    public Vector3f getLocation(float weight) {
        return getCubic(start, controlA, controlB, end, weight);
    }

    private void addMeshSpline(Material mat) {
        //init
        float step = 0.01f;
        int samples = 10;
        int vi = 0, tci = 0, ii = 0;
        Vector3f[] vertices = new Vector3f[(int) (samples * 1 / step) + samples];
        Vector2f[] textureCoordinates = new Vector2f[(int) (1 / step * samples * 2) * 3];
        int[] indices = new int[(int) (1 / step * samples * 2) * 3];
        Vector3f base = new Vector3f(0, 1, 0);
        for (int i = 0; i < samples; i++) {
            float yRot = FastMath.TWO_PI / samples * i;
            vertices[vi++] = Main.getRotation(this, 0, yRot).mult(base).add(getLocation(0));
        }
        //done initializing
        int steps = 1, toDo = (int) (1 / step);
        for (; steps <= toDo; steps++) {
            float f = (float) steps / toDo;
            //create the points
            for (int i = 0; i < samples; i++) {
                float yRot = FastMath.TWO_PI / samples * i;
                vertices[vi++] = Main.getRotation(this, f, yRot).mult(base).add(getLocation(f));
            }
            //connect and texture the points
            for (int i = 0; i < samples; i++) {
                //connect base to next
                textureCoordinates[tci++] = new Vector2f((float) i / samples, f - step);
                textureCoordinates[tci++] = new Vector2f((float) i / samples, f);
                textureCoordinates[tci++] = new Vector2f((float) (i + 1) / samples, f - step);
                indices[ii++] = i + (steps - 1) * samples;
                indices[ii++] = samples + i + (steps - 1) * samples;
                indices[ii++] = (i + 1) % samples + (steps - 1) * samples;
                //connect next to base                
                textureCoordinates[tci++] = new Vector2f((float) i / samples, f);
                textureCoordinates[tci++] = new Vector2f((float) (i + 1) / samples, f);
                textureCoordinates[tci++] = new Vector2f((float) (i + 1) / samples, f - step);
                indices[ii++] = samples + i + (steps - 1) * samples;
                indices[ii++] = (i + 1) % samples + samples + (steps - 1) * samples;
                indices[ii++] = (i + 1) % samples + (steps - 1) * samples;
            }
        }

        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(textureCoordinates));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        mesh.updateBound();

        Geometry geo = new Geometry("spline", mesh);
        geo.setMaterial(mat);
        attachChild(geo);
    }

    public static Vector3f getDirection(Vector3f start, Vector3f controlA, Vector3f controlB, Vector3f end, float weight) {
        Vector3f s = getCubic(start, controlA, controlB, end, weight - ADD_WEIGHT),
                e = getCubic(start, controlA, controlB, end, weight + ADD_WEIGHT);
        return e.subtract(s).normalize();
    }

    public static Vector3f getCubic(Vector3f start, Vector3f controlA, Vector3f controlB, Vector3f end, float weight) {
        Vector3f a = getQuadratic(start, controlA, controlB, weight), b = getQuadratic(controlA, controlB, end, weight);
        return new Vector3f(a.x * (1 - weight) + b.x * weight, a.y * (1 - weight) + b.y * weight, a.z * (1 - weight) + b.z * weight);
    }

    /**
     *
     * @param start where to start the spline
     * @param control determines the smoothness factor of the spline
     * @param end where the spline ends
     * @param weight where on the spline
     * @return the location of the weight on the spline
     */
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

    public static Vector3f generateLandmark(Vector3f min, Random random) {
        return new Vector3f(min.x + (random.nextFloat() - 0.5f) * 20, min.y + (random.nextFloat() - 0.5f) * 20, min.z + 10);
    }

    public static Vector3f generateDirection(Random random, Vector3f lastDirection) {
        Quaternion rot = new Quaternion();
        rot = rot.mult(new Quaternion().fromAngleAxis(FastMath.TWO_PI * (random.nextFloat() - 0.5f) / 360, Vector3f.UNIT_Y));
        rot = rot.mult(new Quaternion().fromAngleAxis(FastMath.TWO_PI * (random.nextFloat() - 0.5f) / 360, Vector3f.UNIT_X));
        return rot.mult(lastDirection);
    }

    public void alpha() {
        MatParam param = this.mat.getParam("Diffuse");
        ColorRGBA color = (ColorRGBA) param.getValue();
        color = color.set(color.r, color.g, color.b, 0.1f);
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", new ColorRGBA(1.0f, 1.0f, 1.0f, 0.1f));
        mat.setBoolean("UseAlpha", true);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    }
}
