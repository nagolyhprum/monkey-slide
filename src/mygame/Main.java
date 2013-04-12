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
import java.util.ArrayList;
import java.util.Random;

public class Main extends SimpleApplication {

    private ArrayList<BezierCurve> slides;
    private float location = 0, rotation = 0;
    private Node character;
    private Vector3f lastEnd, lastDirection;
    private Random random;
    private int index;

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
        //simple initialization
        random = new Random();
        slides = new ArrayList<BezierCurve>();
        lastEnd = Vector3f.ZERO;
        lastDirection = BezierCurve.generateDirection(random);
        //set up the camera
        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(20);
        //add ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
        //add sunlight
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        //create the character
        character = new Node();
        /*
         Geometry cylinder = new Geometry("character", new Cylinder(32, 32, 1, 2, true));
         Material blue = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
         blue.setColor("Diffuse", ColorRGBA.White);
         blue.setColor("Ambient", ColorRGBA.Blue);
         blue.setBoolean("UseMaterialColors", true);
         cylinder.setMaterial(blue);
         cylinder.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
         cylinder.setLocalTranslation(0, 2, 0);
         */
        Spatial oto = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        oto.setLocalTranslation(0, 2, 0);
        oto.scale(0.25f);
        character.attachChild(oto);
        rootNode.attachChild(character);



        generateSlide(random);
        generateSlide(random);
        generateSlide(random);
        generateSlide(random);
        generateSlide(random);
        generateSlide(random);
        generateSlide(random);
        generateSlide(random);
        generateSlide(random);
        generateSlide(random);
        generateSlide(random);
        generateSlide(random);

        for (int i = 1; i < slides.size(); i++) {
            System.out.println(slides.get(i - 1).getDirection(1).subtract(slides.get(i).getDirection(0)));
        }
    }

    public void generateSlide(Random random) {
        Material color = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        color.setColor("Diffuse", ColorRGBA.White);
        color.setColor("Ambient", ColorRGBA.randomColor());
        color.setBoolean("UseMaterialColors", true);
        Vector3f end = BezierCurve.generateLandmark(lastEnd, random),
                direction = BezierCurve.generateDirection(random);
        BezierCurve bc = new BezierCurve(color, lastEnd, lastEnd.add(lastDirection), end.subtract(direction), end);
        slides.add(bc);
        rootNode.attachChild(bc);

        lastEnd = end;
        lastDirection = direction;
    }

    @Override
    public void simpleUpdate(float tpf) {
        BezierCurve bc = slides.get(index);
        Vector3f l = bc.getLocation(location), d = bc.getDirection(location);
        float xrot = FastMath.atan(d.y / d.z);
        float yrot = FastMath.atan(d.x / d.z);
        Quaternion rot = new Quaternion();
        rot = rot.mult(new Quaternion().fromAngleAxis(rotation, d));
        rot = rot.mult(new Quaternion().fromAngleAxis(-xrot, Vector3f.UNIT_X));
        rot = rot.mult(new Quaternion().fromAngleAxis(yrot, Vector3f.UNIT_Y));
        character.setLocalRotation(rot);
        character.setLocalTranslation(l);
        //location += tpf * 0.1;
        //rotation += FastMath.TWO_PI * tpf / 4;

        if (location >= 1) {
            generateSlide(random);
            index++;
            location -= 1;
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
