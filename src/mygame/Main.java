package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

    Vector3f start = new Vector3f(0, 0, 0),
            controlA = new Vector3f(5, 10, 5),
            controlB = new Vector3f(5, 10, 10),
            end = new Vector3f(0, 0, 15);
    float location = 0, rotation = 0;
    Node character;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings as = new AppSettings(true);
        as.setSamples(2);
        as.setResolution(800, 600);
        app.setSettings(as);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setDragToRotate(true);
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        flyCam.setMoveSpeed(20);
        Spline.addSpline(assetManager, rootNode, start, controlA, controlB, end);

        character = new Node();

        Geometry cylinder = new Geometry("character", new Cylinder(32, 32, 1, 2, true));
        Material blue = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        blue.setColor("Diffuse", ColorRGBA.White);
        blue.setColor("Ambient", ColorRGBA.Blue);
        blue.setBoolean("UseMaterialColors", true);
        cylinder.setMaterial(blue);

        cylinder.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
        cylinder.setLocalTranslation(0, 2, 0);

        Spatial oto = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        oto.setLocalTranslation(0, 2, 0);
        oto.scale(0.25f);

        character.attachChild(oto);

        rootNode.attachChild(character);

    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f l = Spline.getCubic(start, controlA, controlB, end, location),
                d = Spline.getDirection(start, controlA, controlB, end, location);
        float xrot = FastMath.atan(d.y / d.z);
        float yrot = FastMath.atan(d.x / d.z);
        Quaternion rot = new Quaternion();
        rot = rot.mult(new Quaternion().fromAngleAxis(rotation, d));
        rot = rot.mult(new Quaternion().fromAngleAxis(-xrot, Vector3f.UNIT_X));
        rot = rot.mult(new Quaternion().fromAngleAxis(yrot, Vector3f.UNIT_Y));
        character.setLocalRotation(rot);
        character.setLocalTranslation(l);
        location += tpf * 0.01;
        rotation += FastMath.TWO_PI * tpf / 4;
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
