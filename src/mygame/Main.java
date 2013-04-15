package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.*;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.*;
import com.jme3.system.AppSettings;
import java.util.*;

public class Main extends SimpleApplication implements AnalogListener, ActionListener {
    //these are all of the slides in memory

    private ArrayList<BezierCurve> slides;
    //where is the character on the slide
    private float location = 0,
            //what is the characters rotation on the slide
            rotation = 0;
    //the parent node of the character
    private Node path,
            //this node only contains the camera and the character
            character;
    //the is where the last spline ends
    private Vector3f lastEnd,
            //this is the direction the last spline ends in
            lastDirection;
    //this is the random number generator used to generate the splines
    private Random random;
    //this is the spline we are currently at
    private int index;
    //the current y offset of the character
    private float y,
            //the rate of change for the characters y offset
            vy;
    //the y coordinate when the character is standing
    private static final float STANDING_Y = 1,
            //the y coordinate when the character is ducking
            DUCKING_Y = 1;
    //the scale when the character is standing
    private static final float STANDING_SCALE = 1,
            //the scale when the character is ducking
            DUCKING_SCALE = 0.5f;
    //is the character ducking?
    private boolean isDucking;

    public static void main(String[] args) {
        Main app = new Main();
        //set up the settings
        AppSettings as = new AppSettings(true);
        as.setSamples(2);
        as.setResolution(800, 600);
        app.setSettings(as);
        app.setShowSettings(false);
        //start the application
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //simple initialization
        random = new Random();
        slides = new ArrayList<BezierCurve>();
        lastEnd = Vector3f.ZERO;
        lastDirection = BezierCurve.generateDirection(random, new Vector3f(0, 0, 5));
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
        Geometry cylinder = new Geometry("character", new Cylinder(32, 32, BezierCurve.RADIUS / 2, 2, true));
        Material blue = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        blue.setColor("Diffuse", ColorRGBA.White);
        blue.setColor("Ambient", ColorRGBA.Blue);
        blue.setBoolean("UseMaterialColors", true);
        cylinder.setMaterial(blue);
        cylinder.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
        cylinder.setLocalTranslation(0, BezierCurve.RADIUS, 0);
        character = new Node();
        character.attachChild(cylinder);
        path.attachChild(character);
        rootNode.attachChild(path);
        //generate the slides
        generateSlide(random);
        generateSlide(random);
        //set up the camera
        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(20);
        flyCam.setEnabled(false);
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        character.attachChild(camNode);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(0, 10, -20));
        //Rotate the camNode to look at the target:
        camNode.lookAt(character.getLocalTranslation(), Vector3f.UNIT_Y);
        //create key events
        InputManager im = getInputManager();
        im.addMapping("clockwise", new KeyTrigger(KeyInput.KEY_A));
        im.addMapping("counterclockwise", new KeyTrigger(KeyInput.KEY_D));
        im.addMapping("duck", new KeyTrigger(KeyInput.KEY_S));
        im.addMapping("jump", new KeyTrigger(KeyInput.KEY_SPACE));
        im.addListener(this, "clockwise", "counterclockwise");
        im.addListener(this, "duck", "jump");
    }

    /**
     * Generates a spline that smoothly connects to the previous spline
     *
     * @param random number generator used when generating the next slide
     */
    public void generateSlide(Random random) {
        //set up the material for this whole section
        Material color = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        color.setColor("Diffuse", ColorRGBA.White);
        color.setColor("Ambient", ColorRGBA.randomColor());
        color.setBoolean("UseMaterialColors", true);
        //figure out how to set up the bezier curve
        Vector3f end = BezierCurve.generateLandmark(lastEnd, random),
                direction = BezierCurve.generateDirection(random, lastDirection);
        //create the bezier curve
        BezierCurve bc = new BezierCurve(color, lastEnd, lastEnd.add(lastDirection), end.subtract(direction), end);
        //add the bezier curve to the scene and list
        slides.add(bc);
        rootNode.attachChild(bc);
        //this is the new ending location and direction
        lastEnd = end;
        lastDirection = direction;
        if ((slides.size() + 1) % 3 == 0) { //if this is the 3rd spline then generate an obstacle
            //get all of the declared obstacles (i did this because i am lazy)
            Class[] clazzez = Obstacle.class.getDeclaredClasses();
            Class clazz = clazzez[(int) (FastMath.rand.nextFloat() * clazzez.length)];
            try {
                //create and place the obstacle
                Node node = (Node) clazz.getConstructor(Material.class).newInstance(color);
                putItHere(node, bc, FastMath.rand.nextFloat(), FastMath.rand.nextFloat() * FastMath.TWO_PI);
                rootNode.attachChild(node);
            } catch (Exception e) {
                System.exit(1);
            }
        } else if (slides.size() > 1) { //if this is not the first slide or a slide with obstacles           
            addCoins(rootNode, bc, color); //then add coins to it
        }
    }

    /**
     * Adds mat colored coins to root which follow the path of the bc
     *
     * @param root node to add the coins to
     * @param bc the bezier curve that the coins should follow
     * @param mat the material to be used for the coins
     */
    public static void addCoins(Node root, BezierCurve bc, Material mat) {
        //which rotation do we start
        float start = FastMath.TWO_PI * FastMath.rand.nextFloat(),
                //how fast does the rotation go?
                progress = FastMath.TWO_PI * FastMath.rand.nextFloat();
        //add coins to certain locations
        for (float i = 0.25f; i <= 0.75; i += 0.05) {
            //create the coin
            Geometry coin = new Geometry("coin", new Sphere(32, 32, 0.1f));
            coin.setLocalTranslation(0, BezierCurve.RADIUS + 0.2f, 0);
            coin.setMaterial(mat);
            Node node = new Node();
            node.attachChild(coin);
            root.attachChild(node);
            //place the coin
            putItHere(node, bc, i, start + progress * i);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //set up the orientation of the player
        character.setLocalTranslation(0, STANDING_Y + y, 0);
        if (isDucking) {
            character.setLocalScale(DUCKING_SCALE);
            character.setLocalTranslation(0, DUCKING_Y, 0);
        } else {
            character.setLocalScale(STANDING_SCALE);
            character.setLocalTranslation(0, STANDING_Y + y, 0);
        }
        putItHere(path, slides.get(index), location, rotation);
        //update the orientation of your character
        if (vy != 0) {
            vy -= tpf * 3;
            y += vy;
            if (y <= 0) {
                y = 0;
                vy = 0;
            }
        }
        location += tpf * 0.5;
        while (location >= 1) {
            generateSlide(random);
            index++;
            location -= 1;
        }
    }

    /**
     * Places it on the spline at weight with rotation
     *
     * @param it is the spatial to place
     * @param spline the spline to place it on
     * @param weight where on the spline to place it
     * @param rotation the rotation on the spline
     */
    public static void putItHere(Spatial it, BezierCurve spline, float weight, float rotation) {
        //get the location and direction at the specified location
        Vector3f l = spline.getLocation(weight), d = spline.getDirection(weight);
        //determine the rotation along x and y
        float xrot = FastMath.atan(d.y / d.z);
        float yrot = FastMath.atan(d.x / d.z);
        Quaternion rot = new Quaternion();
        //this is the roation of it on the spline
        rot = rot.mult(new Quaternion().fromAngleAxis(-rotation, d));
        //this is the direction the spatial is facing
        rot = rot.mult(new Quaternion().fromAngleAxis(-xrot, Vector3f.UNIT_X));
        rot = rot.mult(new Quaternion().fromAngleAxis(yrot, Vector3f.UNIT_Y));
        it.setLocalRotation(rot);
        it.setLocalTranslation(l);
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
