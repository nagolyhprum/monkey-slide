package rem;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Torus;

public class Obstacle extends Node {

    public static class Jump extends Obstacle {

        public Jump(Material mat) {
            Geometry geo = new Geometry("obstacle", new Cylinder(32, 32, BezierCurve.RADIUS + 0.75f, 1, true));
            geo.setMaterial(mat);
            attachChild(geo);
        }
    }

    public static class Duck extends Obstacle {

        public Duck(Material mat) {
            Geometry geo = new Geometry("obstacle", new Torus(32, 32, 0.5f, BezierCurve.RADIUS + 2f));
            geo.setMaterial(mat);
            attachChild(geo);
            geo = new Geometry("obstacle", new Torus(32, 32, 0.5f, BezierCurve.RADIUS + 3f));
            geo.setMaterial(mat);
            attachChild(geo);
            geo = new Geometry("obstacle", new Torus(32, 32, 0.5f, BezierCurve.RADIUS + 4f));
            geo.setMaterial(mat);
            attachChild(geo);
        }
    }

    public static class Dodge extends Obstacle {

        public Dodge(Material mat) {
            float height = 3;
            Geometry geo = new Geometry("obstacle", new Cylinder(32, 32, BezierCurve.RADIUS, height, true));
            geo.setMaterial(mat);
            geo.setLocalTranslation(0, height / 2, 0);
            geo.rotate(FastMath.HALF_PI, 0, 0);
            attachChild(geo);
        }
    }
}
