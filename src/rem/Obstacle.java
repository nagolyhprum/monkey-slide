package rem;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Torus;

public abstract class Obstacle extends Node {

    private Obstacle() {
        setName("obstacle");
    }

    abstract void update(float tpf);

    public static class Jump extends Obstacle {

        public Jump(Material mat) {

            Spatial a = Main.getInstance().getAssetManager().loadModel("Models/grave/grave2.j3o");
            a.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            a.setLocalScale(0.8f);
            Spatial b = Main.getInstance().getAssetManager().loadModel("Models/grave/grave3.j3o");
            b.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            b.setLocalScale(0.8f);
            Spatial c = Main.getInstance().getAssetManager().loadModel("Models/grave/grave2.j3o");
            c.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            c.setLocalScale(0.8f);
            Spatial d = Main.getInstance().getAssetManager().loadModel("Models/grave/grave3.j3o");
            d.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            d.setLocalScale(0.8f);
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

        @Override
        void update(float tpf) {
        }
    }

    public static class Duck extends Obstacle {

        public Duck(Material mat) {
            int birds = 10;
            for (int i = 0; i < birds; i++) {
                Spatial geo = Main.getInstance().getAssetManager().loadModel("Models/bird/bird.j3o");
                geo.rotate(0, FastMath.HALF_PI, 0);
                geo.setLocalTranslation(0, BezierCurve.RADIUS + 1.3f, 0);
                geo.scale(0.5f);
                Node node = new Node();
                node.attachChild(geo);
                node.rotate(0, 0, FastMath.TWO_PI * i / birds);
                attachChild(node);
            }
        }

        @Override
        void update(float tpf) {
            Quaternion roll = new Quaternion();
            roll.fromAngles(0, 0, FastMath.HALF_PI * tpf);
            this.rotate(roll);
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

        @Override
        void update(float tpf) {
        }
    }
}
