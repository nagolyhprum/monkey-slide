package rem;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class Bedroom extends Node {

    public Bedroom() {
        Spatial furniture = Main.getInstance().getAssetManager().loadModel("Models/House-set/House-set.j3o");
        furniture.setLocalTranslation(-0.5f, BezierCurve.RADIUS + 1, -0.25f);
        furniture.setLocalScale(0.25f);
        attachChild(furniture);

        Material wallPaper = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        wallPaper.setTexture("ColorMap", Main.getInstance().getAssetManager().loadTexture("Textures/wall.png"));

        Material woodenFloor = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        woodenFloor.setTexture("ColorMap", Main.getInstance().getAssetManager().loadTexture("Textures/floor.png"));
        //wall 1 - left
        Geometry wall = new Geometry("wall", new Box(0.01f, 0.5f, 1));
        wall.setMaterial(wallPaper);
        wall.setLocalTranslation(0.25f, BezierCurve.RADIUS + 1.5f, 0);
        attachChild(wall);
        //wall 2 - right
        wall = new Geometry("wall", new Box(0.01f, 0.5f, 1));
        wall.setMaterial(wallPaper);
        wall.setLocalTranslation(-2f, BezierCurve.RADIUS + 1.5f, 0);
        attachChild(wall);
        //wall 3 - back
        wall = new Geometry("wall", new Box(1.15f, 0.5f, 0.01f));
        wall.setMaterial(wallPaper);
        wall.setLocalTranslation(-0.85f, BezierCurve.RADIUS + 1.5f, 1f);
        attachChild(wall);
        //floor
        wall = new Geometry("floor", new Box(1.15f, 0.01f, 1));
        wall.setMaterial(woodenFloor);
        wall.setLocalTranslation(-0.85f, BezierCurve.RADIUS + 1, 0);
        attachChild(wall);
    }
}
