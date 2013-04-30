package rem;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class Bedroom extends Node {

    private Spatial leftWall, backWall, rightWall, furniture, floor;
    private boolean isExploding;

    public Bedroom() {
        furniture = Main.getInstance().getAssetManager().loadModel("Models/House-set/House-set.j3o");
        furniture.setLocalScale(0.25f);
        attachChild(furniture);

        Material wallPaper = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        wallPaper.setTexture("DiffuseMap", Main.getInstance().getAssetManager().loadTexture("Textures/wall.png"));

        Material woodenFloor = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        woodenFloor.setTexture("DiffuseMap", Main.getInstance().getAssetManager().loadTexture("Textures/floor.png"));
        //wall 1 - left
        leftWall = new Geometry("wall", new Box(0.01f, 0.5f, 1));
        leftWall.setMaterial(wallPaper);
        attachChild(leftWall);
        //wall 2 - right
        rightWall = new Geometry("wall", new Box(0.01f, 0.5f, 1));
        rightWall.setMaterial(wallPaper);
        attachChild(rightWall);
        //wall 3 - back
        backWall = new Geometry("wall", new Box(1.15f, 0.5f, 0.01f));
        backWall.setMaterial(wallPaper);
        attachChild(backWall);
        //floor
        floor = new Geometry("floor", new Box(1.15f, 0.01f, 1));
        floor.setMaterial(woodenFloor);
        attachChild(floor);
        assemble();
    }

    public void assemble() {
        isExploding = false;
        furniture.setLocalTranslation(-0.5f, BezierCurve.RADIUS + 1, -0.25f);
        leftWall.setLocalTranslation(0.25f, BezierCurve.RADIUS + 1.5f, 0);
        rightWall.setLocalTranslation(-2f, BezierCurve.RADIUS + 1.5f, 0);
        backWall.setLocalTranslation(-0.85f, BezierCurve.RADIUS + 1.5f, 1f);
        floor.setLocalTranslation(-0.85f, BezierCurve.RADIUS + 1, 0);
    }

    public void explode() {
        isExploding = true;
    }

    public void update(float tpf) {
        if (isExploding) {
            int speed = 10;
            furniture.move(0, -tpf * speed, 0);
            floor.move(0, -tpf * speed, 0);
            leftWall.move(tpf * speed, 0, 0);
            rightWall.move(-tpf * speed, 0, 0);
            backWall.move(0, 0, tpf * speed);
        }
    }

    public boolean isExploding() {
        return isExploding;
    }
}
