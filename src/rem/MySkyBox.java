package rem;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;

public class MySkyBox extends Node {

    private Spatial brown, yellow, gray, blue, night;
    private Spatial[] brightness;
    private int index;

    public MySkyBox() {
        setCullHint(Node.CullHint.Never);
        //brown = Main.getInstance().getAssetManager().loadModel("Models/browncloud.j3o");
        brown = generateSkyBox("brown");
        yellow = generateSkyBox("yellow");
        gray = generateSkyBox("gray");
        blue = generateSkyBox("blue");
        night = SkyFactory.createSky(Main.getInstance().getAssetManager(), "Textures/skybox/StarrySky.dds", false);
        brightness = new Spatial[]{blue, gray, yellow, brown, night};
        setLocalScale(-50f);
    }

    private Node generateSkyBox(String color) {
        //TOP
        Geometry top = new Geometry("top", new Box(1, 0.01f, 1));
        Material topMat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        topMat.setTexture("DiffuseMap", Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_up.jpg"));
        top.setMaterial(topMat);
        top.setLocalTranslation(0, 0.5f, 0);
        //BOTTOM
        Geometry bottom = new Geometry("bottom", new Box(1, 0.01f, 1));
        Material bottomMat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        bottomMat.setTexture("DiffuseMap", Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_dn.jpg"));
        bottom.setMaterial(bottomMat);
        bottom.setLocalTranslation(0, -0.5f, 0);
        //LEFT
        Geometry left = new Geometry("left", new Box(0.01f, 1, 1));
        Material leftMat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        leftMat.setTexture("DiffuseMap", Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_lf.jpg"));
        left.setMaterial(leftMat);
        left.setLocalTranslation(-0.5f, 0, 0);
        //RIGHT
        Geometry right = new Geometry("right", new Box(0.01f, 1, 1));
        Material rightMat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        rightMat.setTexture("DiffuseMap", Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_rt.jpg"));
        right.setMaterial(rightMat);
        right.setLocalTranslation(0.5f, 0, 0);
        //FRONT
        Geometry front = new Geometry("front", new Box(1, 1, 0.01f));
        Material frontMat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        frontMat.setTexture("DiffuseMap", Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_ft.jpg"));
        front.setMaterial(frontMat);
        front.setLocalTranslation(0, 0, 0.5f);
        //FRONT
        Geometry back = new Geometry("back", new Box(1, 1, 0.01f));
        Material backMat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        backMat.setTexture("DiffuseMap", Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_bk.jpg"));
        back.setMaterial(backMat);
        back.setLocalTranslation(0, 0, -0.5f);
        //BOX
        Node skyBox = new Node();
        skyBox.attachChild(top);
        skyBox.attachChild(left);
        skyBox.attachChild(bottom);
        skyBox.attachChild(right);
        skyBox.attachChild(back);
        skyBox.attachChild(front);
        return skyBox;
    }

    public void reset() {
        detachChild(brightness[index]);
        index = brightness.length - 1;
        attachChild(brightness[index]);
    }

    public boolean brighter() {
        if (index > 0) {
            detachChild(brightness[index]);
            index--;
            attachChild(brightness[index]);
            return true;
        }
        return false;
    }

    public boolean darker() {
        if (index + 1 < brightness.length) {
            detachChild(brightness[index]);
            index++;
            attachChild(brightness[index]);
            return true;
        }
        return false;
    }
}
