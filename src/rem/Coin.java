/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rem;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author jamesr
 */
public class Coin extends Node {

    public static final float DEAD_HEIGHT = 3f;
    private boolean collected;

    public Coin(Material mat) {
        Geometry coin = new Geometry("coin", new Cylinder(32, 32, 0.5f, 0.05f, true));
        coin.setLocalTranslation(0, BezierCurve.RADIUS + 1f, 0);
        coin.setMaterial(mat);
        attachChild(coin);
        collected = false;
    }

    public void update(float tpf) {
        if (collected) {
            Quaternion spin = new Quaternion();
            spin.fromAngles(0, FastMath.TWO_PI * tpf, 0);
            this.getChild("coin").getLocalTranslation().addLocal(0, 3 * tpf, 0);
            this.rotate(spin);
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean isCollected) {
        if(isCollected && !this.collected) {
            Main.getInstance().playCoin();
        }
        this.collected = isCollected;
    }

    public boolean isDead() {
        if (this.getChild("coin").getLocalTranslation().getY() > (BezierCurve.RADIUS + DEAD_HEIGHT)) {
            return true;
        } else {
            return false;
        }
    }
}
