package mygame;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Torus;

public class Trap extends Node {

    public static class Jump extends Trap {

        public Jump(Material mat) {
            Geometry geo = new Geometry("trap", new Cylinder(32, 32, BezierCurve.RADIUS + 1, 1, true));
            geo.rotate(0, 0, 0);
            geo.setMaterial(mat);
            attachChild(geo);
        }
    }

    public static class Duck extends Trap {

        public Duck(Material mat) {
            Geometry geo = new Geometry("trap", new Torus(32, 32, 1, 2));
            geo.scale(2.5f);
            geo.setMaterial(mat);
            attachChild(geo);
        }
    }

    public static class Dodge extends Trap {

        public Dodge(Material mat) {
            Geometry geo = new Geometry("trap", new Cylinder(32, 32, BezierCurve.RADIUS, 2, true));
            geo.setMaterial(mat);
            geo.setLocalTranslation(0, BezierCurve.RADIUS, 0);
            geo.rotate(FastMath.HALF_PI, 0, 0);
            attachChild(geo);
        }
    }
}
