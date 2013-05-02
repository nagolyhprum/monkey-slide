package rem;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.*;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.*;
import com.jme3.scene.control.LightControl;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import rem.Obstacle.DangerDodge;
import rem.Obstacle.DangerDuck;
import rem.Obstacle.Dodge;
import rem.Obstacle.DoubleDodge;
import rem.Obstacle.Duck;
import rem.Obstacle.Jump;
import rem.gui.GameScreen;
import rem.gui.SettingsScreen;
import rem.gui.StartScreen;

public class Main extends SimpleApplication implements AnalogListener, ActionListener {

    private boolean debugMode = false;
    //the color for the ceiling light in the bedroom
    public static final ColorRGBA YELLOW_LIGHT_COLOR = new ColorRGBA(1.0f, 1.0f, 0.8f, 1.0f);
    //the y coordinate when the character is standing
    public static final float STANDING_Y = 2;
    //the scale when the character is standing
    public static final float SCALE = 0.33f;
    //the forward movement speed of the character
    public static final float START_FORWARD_SPEED = 0.33f;
    public static final float MAX_FORWARD_SPEED = 0.66f;
    //how fast the character rotates
    public static final float START_TURN_SPEED = FastMath.TWO_PI / 3.2f;
    public static final float MAX_TURN_SPEED = FastMath.TWO_PI / 1.6f;
    //the fall acceleration of the character
    public static final float GRAVITY = 9.8f;
    //initial velocity of a jump
    public static final float JUMP_POWER = 5.5f;
    //rise speed for returning from duck position
    public static final float HOVER_RISE = 1.5f;
    //initial velocity of a duck
    public static final float DUCK_POWER = 4f;
    //kinect constants
    public static final float SHOULDER_TILT_MIN = 100f;
    public static final float CROUCH_DIST_MAX = 280f;
    public static final float HAND_RAISE_MIN = 100f;
    private Bedroom bedroom;
    private Nifty nifty;
    private HashMap<String, AudioNode> obstacleAudio = new HashMap<String, AudioNode>();
    private AudioNode[] coinAudio = new AudioNode[10];
    private AudioNode jump, duck;
    //the current hover
    private float hover;
    //the number of splines in existance
    public int experienced;
    //these are all of the slides in memory
    private ArrayList<BezierCurve> slides;
    //these are the coins in memory
    private ArrayList<ArrayList<Coin>> coins;
    //these are the obstacles in memory
    private ArrayList<ArrayList<Obstacle>> obstacles;
    //where is the character on the slide
    private float location = 0,
            //what is the characters rotation on the slide
            rotation = 0;
    private Node path;
    private Node characterNode; //the parent node of the character
    private Bed characterModel; //this node only contains the camera and the character
    //the is where the last spline ends
    private Vector3f lastEnd,
            //this is the direction the last spline ends in
            lastDirection;
    //this is the random number generator used to generate the splines
    Random random;
    //the current y offset of the character
    private float y;
    //the current y velocity of the character
    private float yVelocity;
    //is the character ducking?
    private boolean isDucking;
    private boolean isJumping;
    private boolean isRunning;
    private static final Main SINGLETON = new Main();
    private Material coinMat; //
    private Material rainbow; //
    private Material transparentMat;
    private Material redMat;
    private boolean isCameraTweening;
    private MyCameraNode cameraNode;
    private MySkyBox skyBox;
    private boolean isHurt;
    private PointLight ceilingLamp;
    private PointLight primaryLight;
    private boolean motionSicknessSafeMode = false;
    private int coinsCollected, highscore, totalCoins;
    private float pointsCollected;
    private float currentSpeed;
    private float currentTurnSpeed;
    private long startTime;
    private String kinectAddress;
    private Kinect kinect;

    public static void main(String[] args) {
        if (args.length > 0) {
            SINGLETON.kinectAddress = args[0];
        } else {
            SINGLETON.kinectAddress = "127.0.0.1";
        }
        AppSettings as = new AppSettings(true);
        as.setSamples(0);
        as.setResolution(1280, 720);
        as.setFrameRate(60);
        SINGLETON.setSettings(as);
        SINGLETON.setPauseOnLostFocus(true);
        SINGLETON.setDisplayStatView(false);
        SINGLETON.setShowSettings(false);
        SINGLETON.start();
    }

    private Main() {
    }

    public static Main getInstance() {
        return SINGLETON;
    }

    public float getVolume() {
        return ((SettingsScreen) nifty.getScreen("settings").getScreenController()).getVolume() / 100f;
    }

    public void updateAllStatistics() {
        GameScreen gs = (GameScreen) nifty.getScreen("game").getScreenController();
        gs.setCurrentScore((int) pointsCollected);
        gs.setCurrentCoins((int) coinsCollected);
        gs.setTotalCoins(coinsCollected + totalCoins);
        if (pointsCollected > highscore) {
            highscore = (int) pointsCollected;
        }
        gs.setHighScore(highscore);
    }

    public void reset(boolean firstLaunch) {
        Data.addToTotalCoins(coinsCollected);
        totalCoins += coinsCollected;
        if (pointsCollected > highscore) {
            Data.setHighscore((int) pointsCollected);
        }
        pointsCollected = 0;
        coinsCollected = 0;
        currentSpeed = START_FORWARD_SPEED;
        currentTurnSpeed = START_TURN_SPEED;
        for (BezierCurve bc : slides) {
            rootNode.detachChild(bc);
        }
        skyBox.reset();
        rotation = 0;
        hover = 0;
        y = 0;
        experienced = 0;
        slides.clear();
        coins.clear();
        obstacles.clear();
        location = 0;
        lastEnd = Vector3f.ZERO;
        lastDirection = BezierCurve.generateDirection(random, new Vector3f(0, 0, 5));
        //generate the slides
        generateSlide(random, 5);
        putItHere(bedroom, slides.get(1), 0, 0);
        bedroom.assemble();
        rootNode.attachChild(bedroom);
        ceilingLamp.setColor(YELLOW_LIGHT_COLOR);
        primaryLight.setRadius(50f);
        isJumping = isDucking = false;
        isRunning = true;
        simpleUpdate(0);
        isRunning = false;
        if (kinect == null) {
            try {
                kinect = new Kinect(kinectAddress, 8001);
            } catch (Throwable e) {
                kinect = null;
            }
        }

        if (!firstLaunch) {
            nifty.gotoScreen("start");
        }
    }

    public void setEffectsVolume(float vol) {
        for (AudioNode a : coinAudio) {
            a.setVolume(vol);
        }
        for (String s : obstacleAudio.keySet()) {
            obstacleAudio.get(s).setVolume(vol);
        }
        jump.setVolume(vol);
        duck.setVolume(vol);
    }

    public void setBackgroundVolume(float vol) {
        ((GameScreen) (nifty.getScreen("game").getScreenController())).setVolume(vol);
        ((StartScreen) (nifty.getScreen("start").getScreenController())).setVolume(vol);
    }

    @Override
    public void simpleInitApp() {
        totalCoins = Data.getTotalCoins();
        highscore = Data.getHighscore();
        initGUI();
        initSound();
        initObstacleAudio();
        initActionAudio();

        // disable the fly cam
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);

        bedroom = new Bedroom();
        rootNode.attachChild(bedroom);

        //simple initialization
        random = new Random();
        slides = new ArrayList<BezierCurve>();
        coins = new ArrayList<ArrayList<Coin>>();
        obstacles = new ArrayList<ArrayList<Obstacle>>();
        //bloom postprocess filter for glow effects
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        //more initializations
        initMaterials();
        initCharacter();
        initLightAndShadow();
        initSkybox();
        initCamera();
        initControls();
        reset(true);
    }

    public void initGUI() {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/rem.xml", "start");

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
    }

    public void initSound() {
        for (int i = 1; i <= 10; i++) {
            coinAudio[i - 1] = new AudioNode(assetManager, String.format("Sound/coin/coin%s.wav", i), false);
            coinAudio[i - 1].setVolume(getVolume());
        }
    }

    public void playCoin() {
        playCoin((int) (Math.random() * coinAudio.length));
    }

    public void playCoin(int num) {
        coinAudio[num].playInstance();
    }

    private void initObstacleAudio() {
        obstacleAudio.put("grunt", new AudioNode(assetManager, "Sound/obstacle/grunt.wav"));
        obstacleAudio.put("water", new AudioNode(assetManager, "Sound/obstacle/watersplash.wav"));
        obstacleAudio.put("birds", new AudioNode(assetManager, "Sound/obstacle/chrip.wav"));
        obstacleAudio.put("ghost", new AudioNode(assetManager, "Sound/obstacle/qubodup-GhostMoan01mod.wav"));
    }

    private void initActionAudio() {
        jump = new AudioNode(assetManager, "Sound/action/jump.ogg");
        duck = new AudioNode(assetManager, "Sound/action/duck.wav");
    }

    private void initCamera() {
        //set up the camera
        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(10);
        flyCam.setEnabled(!debugMode);

        cameraNode = new MyCameraNode(cam, characterModel, characterModel);
    }

    private void initCharacter() {
        //create the character
        path = new Node();
        characterModel = new Bed(redMat);
        characterNode = new Node();


        characterModel.scale(SCALE);
        characterNode.attachChild(characterModel);
        path.attachChild(characterNode);
        rootNode.attachChild(path);
    }

    private void initLightAndShadow() {
        //add ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
        //primary light source
        Vector3f charPos = new Vector3f(characterModel.getWorldTranslation());
        charPos.addLocal(5f, 5f, 0);
        primaryLight = new PointLight();
        primaryLight.setColor(ColorRGBA.White);
        primaryLight.setPosition(charPos);
        primaryLight.setRadius(50f);
        rootNode.addLight(primaryLight);
        // make the light follow the player
        Vector3f lightPos = new Vector3f(7f, 7f, 2f);
        LightControl lightCon = new LightControl(primaryLight);
        Node lampNode = new Node("lamp node");
        lampNode.setLocalTranslation(lightPos);
        characterNode.attachChild(lampNode);
        lampNode.addControl(lightCon);
        //ceiling light
        ceilingLamp = new PointLight();
        ceilingLamp.setColor(YELLOW_LIGHT_COLOR);
        ceilingLamp.setRadius(12f);
        ceilingLamp.setPosition(new Vector3f(characterModel.getWorldTranslation()));
        rootNode.addLight(ceilingLamp);
        lightCon = new LightControl(ceilingLamp);
        lampNode = new Node("ceiling lamp node");
        lampNode.setLocalTranslation(-2f, 3f, 0);
        lampNode.addControl(lightCon);
        bedroom.attachChild(lampNode);
    }

    private void initMaterials() {
        coinMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        coinMat.setColor("Ambient", ColorRGBA.Brown);
        coinMat.setColor("Diffuse", ColorRGBA.Yellow);
        coinMat.setColor("Specular", ColorRGBA.White);
        coinMat.setFloat("Shininess", 96f);
        coinMat.setBoolean("UseMaterialColors", true);

        rainbow = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture texture = assetManager.loadTexture("Textures/rainbow.jpg");
        rainbow.setTexture("DiffuseMap", texture);
        rainbow.setFloat("Shininess", 32);
        rainbow.setColor("Specular", ColorRGBA.White);
        rainbow.setColor("Diffuse", ColorRGBA.White);

        transparentMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        ColorRGBA transparency = new ColorRGBA(1.0f, 1.0f, 1.0f, 0.1f);
        transparentMat.setColor("Diffuse", transparency);
        transparentMat.setColor("Ambient", transparency);
        transparentMat.setBoolean("UseAlpha", true);
        transparentMat.setBoolean("UseMaterialColors", true);
        transparentMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        redMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        redMat.setColor("Diffuse", ColorRGBA.Red);
        redMat.setColor("Ambient", ColorRGBA.Red.mult(0.2f));
        redMat.setColor("Specular", ColorRGBA.White);
        rainbow.setFloat("Shininess", 64);
        redMat.setBoolean("UseMaterialColors", true);
    }

    private void initSkybox() {
        skyBox = new MySkyBox();
        characterNode.attachChild(skyBox);
    }

    /**
     * Generates a spline that smoothly connects to the previous spline
     *
     * @param random number generator used when generating the next slide
     */
    public void generateSlide(Random random, int count) {
        isHurt = false;
        characterModel.heal();
        for (int i = 0; i < count; i++) {
            //figure out how to set up the bezier curve
            Vector3f end = BezierCurve.generateLandmark(lastEnd, random);
            Vector3f direction = BezierCurve.generateDirection(random, lastDirection);
            //create the bezier curve
            BezierCurve bc = new BezierCurve(rainbow, lastEnd, lastEnd.add(lastDirection), end.subtract(direction), end);
            //add the bezier curve to the scene and list
            slides.add(bc);
            //this is the new ending location and direction
            lastEnd = end;
            lastDirection = direction;
            ArrayList<Obstacle> os = new ArrayList<Obstacle>();
            ArrayList<Coin> cs = new ArrayList<Coin>();
            if ((experienced + 1) % 3 == 0) { //if this is the 3rd spline then generate an obstacle
                //get all of the declared obstacles (i did this because i am lazy)
                Class[] clazzez;
                if (currentSpeed - START_FORWARD_SPEED < (MAX_FORWARD_SPEED - START_FORWARD_SPEED) / 2) {
                    clazzez = new Class[]{Duck.class, Dodge.class, Jump.class, DangerDuck.class, DoubleDodge.class, Jump.class, DangerDodge.class};
                } else {
                    clazzez = new Class[]{DangerDuck.class, DoubleDodge.class, Jump.class, DangerDodge.class};
                }
                Class clazz = clazzez[(int) (FastMath.rand.nextFloat() * clazzez.length)];
                try {
                    if (clazz.equals(Dodge.class)) {
                        for (float j = 0.10f; j <= 0.85; j += 0.15) {
                            //create and place the obstacle
                            Obstacle node = (Obstacle) clazz.getConstructor().newInstance();
                            putItHere(node, bc, j, FastMath.rand.nextFloat() * FastMath.TWO_PI);
                            bc.attachChild(node);
                            os.add(node);
                        }
                    } else if (clazz.equals(DoubleDodge.class)) {
                        boolean odd = true;
                        for (float j = 0.10f; j <= 0.85; j += 0.15) {
                            //create and place the obstacle
                            Obstacle node;
                            if (odd) {
                                node = (Obstacle) clazz.getConstructor().newInstance();
                            } else {
                                node = (Obstacle) Dodge.class.getConstructor().newInstance();
                            }
                            putItHere(node, bc, j, FastMath.rand.nextFloat() * FastMath.TWO_PI);
                            bc.attachChild(node);
                            os.add(node);
                            odd = !odd;
                        }
                    } else {
                        //create and place the obstacle
                        Obstacle node = (Obstacle) clazz.getConstructor().newInstance();
                        putItHere(node, bc, FastMath.rand.nextFloat() * 0.8f + 0.1f, FastMath.rand.nextFloat() * FastMath.TWO_PI);
                        bc.attachChild(node);
                        os.add(node);
                    }
                } catch (Exception e) {
                    throw new Error(e);
                }
            } else if (slides.size() > 1) { //if this is not the first slide or a slide with obstacles           
                addCoins(bc, coinMat, cs); //then add coins to it
            }
            this.obstacles.add(os);
            this.coins.add(cs);
            experienced++;

            bc.setQueueBucket(RenderQueue.Bucket.Transparent);
            rootNode.attachChild(bc);
        }
    }

    /**
     * Adds mat colored coins to bc which follow the path of the bc
     *
     * @param root node to add the coins to
     * @param bc the bezier curve that the coins should follow
     * @param mat the material to be used for the coins
     */
    public void addCoins(BezierCurve bc, Material mat, ArrayList<Coin> coins) {
        //which rotation do we start
        float start = START_TURN_SPEED * FastMath.rand.nextFloat();
        //how fast does the rotation go?
        float progress = FastMath.TWO_PI * (FastMath.rand.nextFloat() - 0.5f) * 2;
        //add coins to certain locations
        for (float i = 0.25f; i <= 0.75; i += 0.05) {
            //create the coin
            Coin c = new Coin(mat);
            bc.attachChild(c);
            coins.add(c);
            //place the coin
            putItHere(c, bc, i, start + progress * i);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (isRunning) {
            if (kinect != null) {
                kinect.update();
                //kinect controls
                Vector3f ls = kinect.getJoint(0, Kinect.Joint.SHOULDERLEFT);
                Vector3f rs = kinect.getJoint(0, Kinect.Joint.SHOULDERRIGHT);
                Vector3f wst = kinect.getJoint(0, Kinect.Joint.HIPCENTER);
                Vector3f lw = kinect.getJoint(0, Kinect.Joint.WRISTLEFT);
                Vector3f rw = kinect.getJoint(0, Kinect.Joint.WRISTRIGHT);
                if (ls == null || rs == null || wst == null || lw == null || rw == null) {
                    //no skeleton means do nothing
                } else {
                    float shoulder_diff = ls.getY() - rs.getY();
                    if (Math.abs(shoulder_diff) > SHOULDER_TILT_MIN) {
                        if (shoulder_diff > 0) {
                            //turn counterclockwise
                            onAnalog("kinect_counterclockwise", shoulder_diff, tpf);
                        } else {
                            //turn clockwise
                            onAnalog("kinect_clockwise", shoulder_diff, tpf);
                        }
                    }
                    if (!isJumping && !isDucking) {
                        float lshoulderToWaist = ls.getY() - wst.getY();
                        float rshoulderToWaist = rs.getY() - wst.getY();
                        float lhandRaise = lw.getY() - ls.getY();
                        float rhandRaise = rw.getY() - rs.getY();
                        System.out.println("HANDS=" + Math.min(lhandRaise, rhandRaise));
                        if (lshoulderToWaist < CROUCH_DIST_MAX && rshoulderToWaist < CROUCH_DIST_MAX) {
                            onAction("duck", true, tpf);
                        } else if (lhandRaise > HAND_RAISE_MIN && rhandRaise > HAND_RAISE_MIN) {
                            onAction("jump", true, tpf);
                        }
                    }
                }
            }
            if (tpf > 0) {
                if (!isCameraTweening) {
                    pointsCollected += 100 * tpf;
                }
                updateAllStatistics();
            }
            if (isCameraTweening) {
                Vector3f tweenTo = new Vector3f(0, 2.0f, -5);
                Vector3f translate = tweenTo.subtract(cameraNode.getLocalTranslation());
                float pctTweened = Math.max(translate.y / tweenTo.y - 0.15f, 0);
                ceilingLamp.setColor(YELLOW_LIGHT_COLOR.mult(pctTweened));
                translate.normalizeLocal().multLocal(tpf * 2f);
                cameraNode.move(translate);
                if (tweenTo.subtract(cameraNode.getLocalTranslation()).length() < 0.1f) {
                    cameraNode.setLocalTranslation(tweenTo);
                    isCameraTweening = false;
                    rootNode.detachChild(bedroom);
                    startTime = System.currentTimeMillis();
                }
            } else {
                //set up the orientation of the player        
                hover += FastMath.PI * tpf;
                if (isDucking) {
                    y -= yVelocity * tpf;
                    yVelocity -= GRAVITY * tpf;
                    if (y >= 0) {
                        isDucking = false;
                    }
                    characterModel.setLocalTranslation(0, STANDING_Y + y, 0);
                } else if (isJumping) {
                    y += yVelocity * tpf;
                    yVelocity -= GRAVITY * tpf;
                    if (y <= 0) {
                        isJumping = false;
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
                location += tpf * currentSpeed;
                while (location >= 1) {
                    generateSlide(random, 1);
                    location -= 1;
                    rootNode.detachChild(slides.get(0));
                    slides.remove(0);
                    coins.remove(0);
                    obstacles.remove(0);

                    slides.get(0).setMat(transparentMat);
                    for (Node n : obstacles.get(0)) {
                        for (Spatial s : n.getChildren()) {
                            s.setMaterial(transparentMat);
                        }
                    }
                }
                Spatial car = characterModel.getChild("bed");
                for (int i = 0; i < coins.get(1).size(); i++) {
                    Spatial coin = coins.get(1).get(i).getChild("coin");
                    if (coin.collideWith(car.getWorldBound(), new CollisionResults()) != 0) {
                        Coin c = (Coin) coins.get(1).get(i);
                        if (!c.isCollected()) {
                            coinsCollected++;
                            pointsCollected += 100;
                            c.setCollected(true);
                        }
                    }
                }
                if (!isHurt) {
                    for (int i = 0; i < obstacles.get(1).size(); i++) {
                        Obstacle obstacle = obstacles.get(1).get(i);
                        if (obstacle.collideWith(car.getWorldBound(), new CollisionResults()) != 0) {
                            hitObstacle(obstacle.audioName());
                            isHurt = true;
                            characterModel.hurt();
                            if (!skyBox.brighter()) {
                                reset(false);
                                characterModel.wakeup();
                            } else {
                                //TODO adjust lighting?
                                //primaryLight.setRadius(primaryLight.getRadius() + 10f);
                            }
                        }
                    }
                }
                //update all obstacles
                for (ArrayList<Obstacle> al : obstacles) {
                    for (Node n : al) {
                        Obstacle ob = (Obstacle) n;
                        ob.update(tpf);
                    }
                }
                //update all coins
                for (ArrayList<Coin> al : coins) {
                    for (int i = al.size() - 1; i >= 0; i--) {
                        Coin c = (Coin) al.get(i);
                        c.update(tpf);
                        if (c.isDead()) {
                            c.removeFromParent();
                            al.remove(i);
                        }
                    }
                }
                //increase speed over time
                float timeRunning = (System.currentTimeMillis() - startTime) / 1000f;
                currentSpeed = Math.min(START_FORWARD_SPEED + 0.03f * (timeRunning / 25), MAX_FORWARD_SPEED);
                currentTurnSpeed = Math.min(START_TURN_SPEED + 0.15f * (timeRunning / 25), MAX_TURN_SPEED);
            }
        } else if (!debugMode && !bedroom.isExploding()) {
            cameraNode.setLocalTranslation(-1.4f, 1, -1.5f);
            cameraNode.setLookOffset(new Vector3f(0, 0, 0));
        }
        flyCam.setEnabled(debugMode);
        bedroom.update(tpf);
        if (!debugMode) {
            cameraNode.update();
        }
    }

    private void hitObstacle(String audioName) {
        obstacleAudio.get(audioName).playInstance();
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
        Vector3f l = spline.getLocation(weight);
        Quaternion rot = getRotation(spline, weight, rotation);
        it.setLocalRotation(rot);
        it.setLocalTranslation(l);
    }

    public static Quaternion getRotation(BezierCurve spline, float weight, float rotation) {
        Vector3f d = spline.getDirection(weight);
        //determine the rotation along x and y
        float xrot = FastMath.atan(d.y / d.z);
        float yrot = FastMath.atan(d.x / d.z);
        Quaternion rot = new Quaternion();
        //this is the roation of it on the spline
        rot = rot.mult(new Quaternion().fromAngleAxis(-rotation, d));
        //this is the direction the spatial is facing
        rot = rot.mult(new Quaternion().fromAngleAxis(-xrot, Vector3f.UNIT_X));
        rot = rot.mult(new Quaternion().fromAngleAxis(yrot, Vector3f.UNIT_Y));
        return rot;
    }

    public void onAnalog(String name, float value, float tpf) {
        if ("clockwise".equals(name)) {
            rotation += currentTurnSpeed * tpf;
        } else if ("counterclockwise".equals(name)) {
            rotation -= currentTurnSpeed * tpf;
        } else if ("kinect_clockwise".equals(name) || "kinect_counterclockwise".equals(name)) {
            rotation -= value / 60 * tpf;
        }
    }

    public void onAction(String name, boolean keyPressed, float tpf) {
        if (keyPressed) {
            if ("duck".equals(name) && !isDucking && !isJumping) {
                duck.playInstance();
                isDucking = true;
                yVelocity = DUCK_POWER;
            } else if ("jump".equals(name) && !isDucking && !isJumping) {
                jump.playInstance();
                isJumping = true;
                yVelocity = JUMP_POWER;
            } else if ("reset".equals(name)) {
                reset(false);
            } else if ("debug".equals(name)) {
                debugMode = !debugMode;
            }
        }
    }

    public void go() {
        if (!isCameraTweening) {
            if (!bedroom.isExploding()) {
                isCameraTweening = true;
                characterModel.sleep();
            }
            isRunning = !isRunning;
            bedroom.explode();
        }
    }

    boolean isMotionSicknessSafe() {
        return motionSicknessSafeMode;
    }

    public void initControls() {
        //create key events
        InputManager im = getInputManager();
        im.addMapping("clockwise", new KeyTrigger(KeyInput.KEY_J));
        im.addMapping("counterclockwise", new KeyTrigger(KeyInput.KEY_L));
        im.addMapping("duck", new KeyTrigger(KeyInput.KEY_K));
        im.addMapping("jump", new KeyTrigger(KeyInput.KEY_I));
        im.addMapping("reset", new KeyTrigger(KeyInput.KEY_O));
        im.addMapping("debug", new KeyTrigger(KeyInput.KEY_BACKSLASH));
        im.addListener(this, "clockwise", "counterclockwise");
        im.addListener(this, "duck", "jump", "reset", "debug");
    }
}
