package rem;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class Obstacle extends Node {

    public static final int NUM_BIRDS = 10;
    public static final int NUM_SKULLS = 13;
    public static final float DANGER_DUCK_DIP_DRIVE = 2f;

    private Obstacle() {
        setName("obstacle");
    }

    abstract void update(float tpf);

    abstract String audioName();

    public static class Jump extends Obstacle {

        public Jump() {

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

        public String audioName() {
            return "grunt";
        }
    }

    public static class Duck extends Obstacle {

        private Material mat1, mat2;

        public Duck() {
            float hue1 = Main.getInstance().random.nextFloat();
            float hue2 = hue1 + 0.5f;
            if (hue2 > 1.0f) {
                hue2 = hue2 - 1.0f;
            }
            Color color = new Color(Color.HSBtoRGB(hue1, 0.7f, 1.0f));
            ColorRGBA color1 = new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0f);
            color = new Color(Color.HSBtoRGB(hue2, 0.7f, 1.0f));
            ColorRGBA color2 = new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0f);
            mat1 = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            mat1.setColor("Diffuse", color1);
            mat1.setColor("Ambient", color1.mult(0.1f));
            mat1.setBoolean("UseMaterialColors", true);
            mat2 = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            mat2.setColor("Diffuse", color2);
            mat2.setColor("Ambient", color2.mult(0.1f));
            mat2.setBoolean("UseMaterialColors", true);
            int birds = NUM_BIRDS;
            for (int i = 0; i < birds; i++) {
                Spatial geo = Main.getInstance().getAssetManager().loadModel("Models/bird/bird.j3o");
                geo.rotate(0, FastMath.HALF_PI, 0);
                geo.setLocalTranslation(0, BezierCurve.RADIUS + 1f, 0);
                geo.scale(0.5f);
                if ((i & 1) == 0) {
                    geo.setMaterial(mat1);
                } else {
                    geo.setMaterial(mat2);
                }
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

        public String audioName() {
            return "birds";
        }
    }

    public static class DangerDuck extends Obstacle {

        private List<Spatial> skullList;
        private int leadingSkull;
        private boolean[] movingUp;
        private float maxHeight;

        public DangerDuck() {
            leadingSkull = 0;
            maxHeight = BezierCurve.RADIUS + 1.65f;
            skullList = new ArrayList<Spatial>();
            movingUp = new boolean[NUM_SKULLS];
            for (int i = 0; i < NUM_SKULLS; i++) {
                Spatial geo = Main.getInstance().getAssetManager().loadModel("Models/skull/skull_monster.j3o");
                geo.rotate(0, FastMath.PI, 0);
                geo.setLocalTranslation(0, maxHeight, 0);
                geo.scale(0.25f);
                Node node = new Node();
                node.attachChild(geo);
                node.rotate(0, 0, FastMath.TWO_PI * i / NUM_SKULLS);
                attachChild(node);
                skullList.add(geo);
                movingUp[i] = true;
            }
            movingUp[leadingSkull] = false;
        }

        @Override
        void update(float tpf) {
            float lead_y = skullList.get(leadingSkull).getLocalTranslation().getY();
            if (lead_y <= 0.66f * maxHeight) {
                leadingSkull = (leadingSkull + 1) % NUM_SKULLS;
                movingUp[leadingSkull] = false;
            }
            for (int i = 0; i < skullList.size(); i++) {
                float skull_y = skullList.get(i).getLocalTranslation().getY();
                if (movingUp[i]) {
                    if (skull_y < maxHeight) {
                        //move up
                        skullList.get(i).setLocalTranslation(0, skull_y + DANGER_DUCK_DIP_DRIVE * tpf, 0);
                    }
                } else {
                    if (skull_y <= BezierCurve.RADIUS + 0.1f) {
                        movingUp[i] = true;
                    } else {
                        //move down
                        skullList.get(i).setLocalTranslation(0, skull_y + -DANGER_DUCK_DIP_DRIVE * tpf, 0);
                    }
                }
            }
        }

        public String audioName() {
            return "ghost";
        }
    }

    public static class DangerDodge extends Obstacle {

        private Material mat1, mat2;
        private boolean rng = Main.getInstance().random.nextFloat() < .5f;

        public DangerDodge() {
            int birds = 4;
            for (int i = 0; i < birds; i++) {
                Spatial geo = Main.getInstance().getAssetManager().loadModel("Models/ghost/ghost.j3o");
                geo.rotate(0, FastMath.HALF_PI, 0);
                geo.setLocalTranslation(0, BezierCurve.RADIUS + 0f, 0);
                geo.scale(2f);
                Node node = new Node();
                node.attachChild(geo);
                node.rotate(0, 0, FastMath.TWO_PI * i / birds);
                attachChild(node);
            }
        }

        @Override
        void update(float tpf) {
            Quaternion roll = new Quaternion();
            roll.fromAngles(0, 0, (rng ? -1 : 1) * FastMath.HALF_PI * tpf);
            this.rotate(roll);
        }

        public String audioName() {
            return "ghost";
        }
    }

    public static class Dodge extends Obstacle {

        public Dodge() {
            Spatial geo = Main.getInstance().getAssetManager().loadModel("Models/WellMoogan/WellMoogan.j3o");
            geo.scale(0.9f);
            geo.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            geo.rotate(0, FastMath.HALF_PI, 0);
            attachChild(geo);
        }

        @Override
        void update(float tpf) {
        }

        public String audioName() {
            return "water";
        }
    }

    public static class DoubleDodge extends Obstacle {

        public DoubleDodge() {
            Spatial geo = Main.getInstance().getAssetManager().loadModel("Models/WellMoogan/WellMoogan.j3o");
            geo.scale(0.9f);
            geo.setLocalTranslation(0, BezierCurve.RADIUS - 0.1f, 0);
            geo.rotate(0, FastMath.HALF_PI, 0);
            attachChild(geo);
            geo = Main.getInstance().getAssetManager().loadModel("Models/WellMoogan/WellMoogan.j3o");
            geo.scale(0.9f);
            geo.setLocalTranslation(0, -BezierCurve.RADIUS + 0.1f, 0);
            geo.rotate(FastMath.PI, FastMath.PI * 1.5f, 0);
            attachChild(geo);
        }

        @Override
        void update(float tpf) {
        }

        public String audioName() {
            return "water";
        }
    }
}
