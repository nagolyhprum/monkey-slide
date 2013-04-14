package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Random;

public class Main extends SimpleApplication implements AnalogListener, ActionListener {

    private ArrayList<BezierCurve> slides;
    private float location = 0, rotation = 0;
    private Node path, oto;
    private Vector3f lastEnd, lastDirection;
    private Random random;
    private int index;
    private float y, vy;
    private static final float STANDING_Y = 2, DUCKING_Y = 2;
    private static final float STANDING_SCALE = 0.25f, DUCKING_SCALE = 0.125f;
    private boolean isDucking;

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
        lastDirection = BezierCurve.generateDirection(random, new Vector3f(0, 0, 5));
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
        path = new Node();
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
        oto = new Node();
        oto.attachChild(assetManager.loadModel("Models/Oto/Oto.mesh.xml"));
        path.attachChild(oto);
        rootNode.attachChild(path);



        generateSlide(random);
        generateSlide(random);

        for (int i = 1; i < slides.size(); i++) {
            System.out.println(slides.get(i - 1).getDirection(1).subtract(slides.get(i).getDirection(0)));
        }

        // Disable the default flyby cam
        flyCam.setEnabled(false);
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        oto.attachChild(camNode);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(0, 10, -20));
        //Rotate the camNode to look at the target:
        camNode.lookAt(oto.getLocalTranslation(), Vector3f.UNIT_Y);

        InputManager im = getInputManager();
        im.addMapping("clockwise", new KeyTrigger(KeyInput.KEY_A));
        im.addMapping("counterclockwise", new KeyTrigger(KeyInput.KEY_D));
        im.addMapping("duck", new KeyTrigger(KeyInput.KEY_S));
        im.addMapping("jump", new KeyTrigger(KeyInput.KEY_SPACE));
        im.addListener(this, "clockwise", "counterclockwise");
        im.addListener(this, "duck", "jump");
    }

    public void generateSlide(Random random) {
        Material color = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        color.setColor("Diffuse", ColorRGBA.White);
        color.setColor("Ambient", ColorRGBA.randomColor());
        color.setBoolean("UseMaterialColors", true);
        Vector3f end = BezierCurve.generateLandmark(lastEnd, random),
                direction = BezierCurve.generateDirection(random, lastDirection);
        BezierCurve bc = new BezierCurve(color, lastEnd, lastEnd.add(lastDirection), end.subtract(direction), end);
        slides.add(bc);
        rootNode.attachChild(bc);
        lastEnd = end;
        lastDirection = direction;
        if ((slides.size() + 1) % 3 == 0) {
            Class[] clazzez = Trap.class.getDeclaredClasses();
            Class clazz = clazzez[(int) (FastMath.rand.nextFloat() * clazzez.length)];
            try {
                Constructor constructor = clazz.getConstructor(Material.class);
                Node node = (Node) constructor.newInstance(color);
                putItHere(node, bc, FastMath.rand.nextFloat(), FastMath.rand.nextFloat() * FastMath.TWO_PI);
                rootNode.attachChild(node);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        oto.setLocalTranslation(0, STANDING_Y + y, 0);
        if (vy != 0) {
            y += vy;
            vy -= tpf * 3;
            if (y <= 0) {
                y = 0;
                vy = 0;
            }
        }

        if (isDucking) {
            oto.setLocalScale(DUCKING_SCALE);
            oto.setLocalTranslation(0, DUCKING_Y, 0);
        } else {
            oto.setLocalScale(STANDING_SCALE);
            oto.setLocalTranslation(0, STANDING_Y + y, 0);
        }

        putItHere(path, slides.get(index), location, rotation);
        location += tpf * 0.75;
        while (location >= 1) {
            generateSlide(random);
            index++;
            location -= 1;
        }
    }

    public static void putItHere(Node it, BezierCurve spline, float weight, float rotation) {
        Vector3f l = spline.getLocation(weight), d = spline.getDirection(weight);
        float xrot = FastMath.atan(d.y / d.z);
        float yrot = FastMath.atan(d.x / d.z);
        Quaternion rot = new Quaternion();
        rot = rot.mult(new Quaternion().fromAngleAxis(-rotation, d));
        rot = rot.mult(new Quaternion().fromAngleAxis(-xrot, Vector3f.UNIT_X));
        rot = rot.mult(new Quaternion().fromAngleAxis(yrot, Vector3f.UNIT_Y));
        it.setLocalRotation(rot);
        it.setLocalTranslation(l);
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

    public void onAnalog(String name, float value, float tpf) {
        if ("clockwise".equals(name)) {
            rotation += FastMath.TWO_PI * tpf / 4;
        } else if ("counterclockwise".equals(name)) {
            rotation -= FastMath.TWO_PI * tpf / 4;
        }
    }

    public void onAction(String name, boolean keyPressed, float tpf) {
        if ("duck".equals(name) && vy == 0) {
            isDucking = keyPressed;
        } else if ("jump".equals(name) && keyPressed) {
            if (vy == 0 && !isDucking) {
                vy = 1;
            }
        }
    }
}
