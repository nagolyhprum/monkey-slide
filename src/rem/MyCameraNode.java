package rem;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class MyCameraNode extends Node {

    private Node follows, lookAt;
    private Camera camera;
    private Vector3f lookOffset;

    public MyCameraNode(Camera camera, Node follows, Node lookAt) {
        this.follows = follows;
        this.lookAt = lookAt;
        this.camera = camera;
    }

    public void setLookOffset(Vector3f lookOffset) {
        this.lookOffset = lookOffset;
    }

    public void update() {
        Transform wtf = follows.getWorldTransform();
        Transform cam = getWorldTransform();
        Quaternion rot = follows.getWorldRotation();
        Vector3f location = wtf.getTranslation().add(rot.mult(cam.getTranslation()));
        camera.setLocation(location);

        Transform wtl = lookAt.getWorldTransform();
        if (lookOffset == null) {
            camera.lookAt(wtl.getTranslation(), rot.mult(Vector3f.UNIT_Y));
        } else {
            camera.lookAt(wtl.getTranslation().add(lookOffset), rot.mult(Vector3f.UNIT_Y));
        }
    }
}
