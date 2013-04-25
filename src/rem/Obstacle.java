package rem;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Torus;

public class Obstacle extends Node {

    private Obstacle() {
        setName("obstacle");
    }

    public static class Jump extends Obstacle {

        public Jump(Material mat) {

            Spatial a = Main.getInstance().getAssetManager().loadModel("Models/grave/grave2.j3o");
            a.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            Spatial b = Main.getInstance().getAssetManager().loadModel("Models/grave/grave3.j3o");
            b.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            Spatial c = Main.getInstance().getAssetManager().loadModel("Models/grave/grave2.j3o");
            c.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            Spatial d = Main.getInstance().getAssetManager().loadModel("Models/grave/grave3.j3o");
            d.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            //crosses            
            for (int i = 0; i < 4; i++) {
                Spatial cross = Main.getInstance().getAssetManager().loadModel("Models/grave/grave1.j3o");
                cross.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
                Node n = new Node();
                n.rotate(0, 0, FastMath.QUARTER_PI + FastMath.HALF_PI * i);
                n.attachChild(cross);
                attachChild(n);
            }
            //a
            Node n = new Node();
            n.rotate(0, 0, 0);
            n.attachChild(a);
            attachChild(n);
            //b
            n = new Node();
            n.rotate(0, 0, FastMath.HALF_PI);
            n.attachChild(b);
            attachChild(n);
            //c
            n = new Node();
            n.rotate(0, 0, FastMath.PI);
            n.attachChild(c);
            attachChild(n);
            //d            
            n = new Node();
            n.rotate(0, 0, FastMath.HALF_PI * 3);
            n.attachChild(d);
            attachChild(n);
        }
    }

    public static class Duck extends Obstacle {

        public Duck(Material mat) {
            int birds = 10;
            for (int i = 0; i < birds; i++) {
                Spatial geo = Main.getInstance().getAssetManager().loadModel("Models/bird/bird.j3o");
                geo.rotate(0, FastMath.HALF_PI, 0);
                geo.setLocalTranslation(0, BezierCurve.RADIUS + 1, 0);
                geo.scale(0.5f);
                Node node = new Node();
                node.attachChild(geo);
                node.rotate(0, 0, FastMath.TWO_PI * i / birds);
                attachChild(node);
            }
        }
    }

    public static class Dodge extends Obstacle {

        public Dodge(Material mat) {
            Spatial geo = Main.getInstance().getAssetManager().loadModel("Models/Well/Well.j3o");
            geo.scale(0.2f);
            geo.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            geo.rotate(0, FastMath.HALF_PI, 0);
            attachChild(geo);
        }
    }
}
