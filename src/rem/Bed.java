package rem;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Bed extends Node {

    private Spatial bedVisible, sleeperVisible;
    private Spatial bedHidden, sleeperHidden;
    private Spatial bedActive, sleeperActive;

    public Bed(Material transparent) {
        bedVisible = Main.getInstance().getAssetManager().loadModel("Models/letto_small/letto_small.j3o");
        bedVisible.setName("bed");
        bedVisible.rotate(0, FastMath.PI, 0);
        sleeperVisible = Main.getInstance().getAssetManager().loadModel("Models/aldeano/aldeano.j3o");

        bedHidden = bedVisible.clone();
        sleeperHidden = sleeperVisible.clone();
        bedHidden.setMaterial(transparent);
        sleeperHidden.setMaterial(transparent);

        heal();
        wakeup();
    }

    public void hurt() {
        bedActive = bedHidden;
        sleeperActive = sleeperHidden;
        detachChild(bedVisible);
        detachChild(sleeperVisible);
        attachChild(bedHidden);
        attachChild(sleeperHidden);
        sleep();
    }

    public void heal() {
        bedActive = bedVisible;
        sleeperActive = sleeperVisible;
        attachChild(bedVisible);
        attachChild(sleeperVisible);
        detachChild(bedHidden);
        detachChild(sleeperHidden);
        sleep();
    }

    public void sleep() {
        sleeperActive.setLocalTranslation(0, 0.75f, 0.5f);
        sleeperActive.setLocalScale(0.3f);
        sleeperActive.setLocalRotation(Matrix3f.IDENTITY);
        sleeperActive.rotate(-FastMath.HALF_PI, FastMath.PI, 0);
    }

    public void wakeup() {
        sleeperActive.setLocalTranslation(0, 0.75f, 0.75f);
        sleeperActive.setLocalScale(0.3f);
        sleeperActive.setLocalRotation(Matrix3f.IDENTITY);
        sleeperActive.rotate(0, FastMath.PI, 0);
    }
}
