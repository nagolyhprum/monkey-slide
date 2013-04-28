package rem;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

public class MySkyBox extends Node {

    private Spatial brown, yellow, gray, blue, night;
    private Spatial[] brightness;
    private int index;

    public MySkyBox() {
        setCullHint(Node.CullHint.Never);
        brown = Main.getInstance().getAssetManager().loadModel("Models/browncloud.j3o");
        brown.setCullHint(Node.CullHint.Inherit);
        yellow = Main.getInstance().getAssetManager().loadModel("Models/yellowcloud.j3o");
        yellow.setCullHint(Node.CullHint.Inherit);
        gray = Main.getInstance().getAssetManager().loadModel("Models/graycloud.j3o");
        gray.setCullHint(Node.CullHint.Inherit);
        blue = Main.getInstance().getAssetManager().loadModel("Models/bluecloud.j3o");
        blue.setCullHint(Node.CullHint.Inherit);
        night = SkyFactory.createSky(Main.getInstance().getAssetManager(), "Textures/skybox/StarrySky.dds", false);
        night.setCullHint(Node.CullHint.Inherit);
        brightness = new Spatial[]{blue, gray, yellow, brown, night};
        setLocalScale(50f);
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
