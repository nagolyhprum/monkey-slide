package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.*;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.*;
import com.jme3.system.AppSettings;
import java.util.*;

public class Main extends SimpleApplication implements AnalogListener, ActionListener {

    //the current hover
    private float hover;
    //the number of splines in existance
    public int experienced;
    //how fast the character rotates
    public static final float TURN_SPEED = FastMath.TWO_PI / 4;
    //these are all of the slides in memory
    private ArrayList<BezierCurve> slides;
    //these are the coins in memory
    private ArrayList<ArrayList<Node>> coins;
    //these are the obstacles in memory
    private ArrayList<ArrayList<Node>> obstacles;
    //where is the character on the slide
    private float location = 0,
            //what is the characters rotation on the slide
            rotation = 0;
    //the parent node of the character
    private Node path,
            //this node only contains the camera and the character
            characterNode,
            characterModel;
    //the is where the last spline ends
    private Vector3f lastEnd,
            //this is the direction the last spline ends in
            lastDirection;
    //this is the random number generator used to generate the splines
    private Random random;
    //the current y offset of the character
    private float y;
    //the y coordinate when the character is standing
    private static final float STANDING_Y = 2,
            //the amount to decrease from standing
            DUCKING_Y = 0.75f,
            //the amount to increase from standing
            JUMPING_Y = 0.75f;
    //the scale when the character is standing
    private static final float SCALE = 0.25f;
    //is the character ducking?
    private boolean isDucking, isJumping;
    private static final Main SINGLETON = new Main();

    public static void main(String[] args) {
        AppSettings as = new AppSettings(true);
        as.setSamples(2);
        as.setResolution(800, 600);
        SINGLETON.setSettings(as);
        SINGLETON.setShowSettings(false);
        SINGLETON.start();
    }

    private Main() {
    }

    public static Main getInstance() {
        return SINGLETON;
    }

    public void reset() {
        for (BezierCurve bc : slides) {
            rootNode.detachChild(bc);
        }
        slides.clear();
        coins.clear();
        obstacles.clear();
        location = 0;
        lastEnd = Vector3f.ZERO;
        lastDirection = BezierCurve.generateDirection(random, new Vector3f(0, 0, 5));
        //generate the slides
        generateSlide(random, 4);
    }

    @Override
    public void simpleInitApp() {
        //simple initialization
        random = new Random();
        slides = new ArrayList<BezierCurve>();
        coins = new ArrayList<ArrayList<Node>>();
        obstacles = new ArrayList<ArrayList<Node>>();
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
        characterModel = new Node();
        characterNode = new Node();

        Node car = (Node) assetManager.loadModel("Models/car/_car_04.j3o");
        car.setName("car");
        car.detachChildAt(0);

        Geometry wb = makeWireBB(car);
        car.attachChild(wb);

        characterModel.attachChild(car);
        characterModel.scale(SCALE);
        characterNode.attachChild(characterModel);
        path.attachChild(characterNode);
        rootNode.attachChild(path);
        //set up the camera
        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(50);
        flyCam.setEnabled(false);
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        characterNode.attachChild(camNode);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(0, 5, -10));
        //Rotate the camNode to look at the target:
        camNode.lookAt(characterNode.getLocalTranslation(), Vector3f.UNIT_Y);
        //create key events
        InputManager im = getInputManager();
        im.addMapping("clockwise", new KeyTrigger(KeyInput.KEY_A));
        im.addMapping("counterclockwise", new KeyTrigger(KeyInput.KEY_D));
        im.addMapping("duck", new KeyTrigger(KeyInput.KEY_S));
        im.addMapping("jump", new KeyTrigger(KeyInput.KEY_W));
        im.addMapping("reset", new KeyTrigger(KeyInput.KEY_BACKSLASH));
        im.addListener(this, "clockwise", "counterclockwise");
        im.addListener(this, "duck", "jump", "reset");
        reset();
    }

    public static Geometry makeWireBB(Spatial object) {
        WireBox wb = new WireBox();
        wb.fromBoundingBox((BoundingBox) object.getWorldBound());
        Geometry geo = new Geometry("bb", wb);
        Material red = new Material(SINGLETON.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        red.setColor("Color", ColorRGBA.Red);
        geo.setMaterial(red);
        return geo;
    }
    
    /**
     * Generates a spline that smoothly connects to the previous spline
     *
     * @param random number generator used when generating the next slide
     */
    public void generateSlide(Random random, int count) {
        for (int i = 0; i < count; i++) {
            //set up the material for this whole section
            Material color = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            //Texture texture = assetManager.loadTexture("Textures/road.jpg");
            //color.setTexture("ColorMap", texture);
            color.setColor("Diffuse", ColorRGBA.White);
            color.setColor("Ambient", ColorRGBA.randomColor());
            color.setBoolean("UseMaterialColors", true);
            //figure out how to set up the bezier curve
            Vector3f end = BezierCurve.generateLandmark(lastEnd, random);
            Vector3f direction = BezierCurve.generateDirection(random, lastDirection);
            //create the bezier curve
            BezierCurve bc = new BezierCurve(color, lastEnd, lastEnd.add(lastDirection), end.subtract(direction), end);
            //add the bezier curve to the scene and list
            slides.add(bc);
            rootNode.attachChild(bc);
            //this is the new ending location and direction
            lastEnd = end;
            lastDirection = direction;
            ArrayList<Node> os = new ArrayList<Node>();
            ArrayList<Node> cs = new ArrayList<Node>();
            if ((experienced + 1) % 3 == 0) { //if this is the 3rd spline then generate an obstacle
                //get all of the declared obstacles (i did this because i am lazy)
                Class[] clazzez = Obstacle.class.getDeclaredClasses();
                Class clazz = clazzez[(int) (FastMath.rand.nextFloat() * clazzez.length)];
                Geometry wb;
                try {
                    //create and place the obstacle
                    Node node = (Node) clazz.getConstructor(Material.class).newInstance(color);
                    putItHere(node, bc, FastMath.rand.nextFloat() * 0.8f + 0.1f, FastMath.rand.nextFloat() * FastMath.TWO_PI);
                    bc.attachChild(node);
                    wb = makeWireBB(node.getChild("obstacle"));
                    wb.setLocalTransform(node.getChild("obstacle").getLocalTransform());
                    node.attachChild(wb);
                    os.add(node);
                } catch (Exception e) {
                    System.exit(1);
                }
            } else if (slides.size() > 1) { //if this is not the first slide or a slide with obstacles           
                addCoins(bc, color, cs); //then add coins to it
            }
            this.obstacles.add(os);
            this.coins.add(cs);
            experienced++;
        }
    }

    /**
     * Adds mat colored coins to bc which follow the path of the bc
     *
     * @param root node to add the coins to
     * @param bc the bezier curve that the coins should follow
     * @param mat the material to be used for the coins
     */
    public static void addCoins(BezierCurve bc, Material mat, ArrayList<Node> coins) {
        //which rotation do we start
        float start = TURN_SPEED * FastMath.rand.nextFloat(),
                //how fast does the rotation go?
                progress = FastMath.TWO_PI * (FastMath.rand.nextFloat() - 0.5f) * 2;
        //add coins to certain locations
        for (float i = 0.25f; i <= 0.75; i += 0.05) {
            //create the coin
            Geometry coin = new Geometry("coin", new Cylinder(32, 32, 0.5f, 0.05f, true));
            coin.setLocalTranslation(0, BezierCurve.RADIUS + 1f, 0);
            coin.setMaterial(mat);
            Node node = new Node();
            node.attachChild(coin);
            bc.attachChild(node);
            coins.add(node);
            //place the coin
            putItHere(node, bc, i, start + progress * i);
            Geometry wb = getInstance().makeWireBB(coin);
            wb.setLocalTranslation(coin.getLocalTranslation());
            node.attachChild(wb);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //set up the orientation of the player        
        hover += FastMath.PI * tpf;
        if (isDucking) {
            if (y > -DUCKING_Y) {
                y -= tpf;
            }
            if (y < -DUCKING_Y) {
                y = -DUCKING_Y;
            }
            characterModel.setLocalTranslation(0, STANDING_Y + y, 0);
        } else if (isJumping) {
            if (y < JUMPING_Y) {
                y += tpf;
            }
            if (y > JUMPING_Y) {
                y = JUMPING_Y;
            }
            characterModel.setLocalTranslation(0, STANDING_Y + y, 0);
        } else {
            float ty = this.y;
            if (ty != 0) {
                ty -= tpf * FastMath.abs(ty) / ty;
            }
            if ((FastMath.abs(ty) / ty) != (FastMath.abs(y) / y)) {
                ty = 0;
            }
            y = ty;
            characterModel.setLocalTranslation(0, STANDING_Y + y + FastMath.sin(hover) * 0.125f, 0);
        }
        if (!slides.isEmpty()) {
            putItHere(path, slides.get(1), location, rotation);
        }
        location += tpf * 0.25;
        while (location >= 1) {
            generateSlide(random, 1);
            location -= 1;
            rootNode.detachChild(slides.get(0));
            slides.remove(0);
            coins.remove(0);
            obstacles.remove(0);
        }
        //start colliding
        try {
            Spatial car = characterModel.getChild("car");
            for (int i = 0; i < coins.get(1).size(); i++) {
                Spatial coin = coins.get(1).get(i).getChild("coin");
                if (coin.collideWith(car.getWorldBound(), new CollisionResults()) != 0) {
                    System.out.println("points!");
                    coin.getParent().removeFromParent();
                    coins.get(1).remove(i);
                    i--;
                }
            }
            for (int i = 0; i < obstacles.get(1).size(); i++) {
                Spatial obstacle = obstacles.get(1).get(i).getChild("obstacle");
                if (obstacle.collideWith(car.getWorldBound(), new CollisionResults()) != 0) {
                    System.out.println("dead!");
                    reset();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
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
            rotation += TURN_SPEED * tpf;
        } else if ("counterclockwise".equals(name)) {
            rotation -= TURN_SPEED * tpf;
        }
    }

    public void onAction(String name, boolean keyPressed, float tpf) {
        if ("duck".equals(name) && !isJumping) {
            isDucking = keyPressed;
        } else if ("jump".equals(name) && !isDucking) {
            isJumping = keyPressed;
        } else if ("reset".equals(name)) {
            reset();
        }
    }
}
