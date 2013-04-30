package rem;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.jme3.util.SkyFactory;

public class MySkyBox extends Node {

    private Spatial brown, yellow, gray, blue, night;
    private Spatial[] brightness;
    private Spatial brown_nm, yellow_nm, gray_nm, blue_nm;
    private Spatial[] brightness_nm;
    private int index;

    public MySkyBox() {
        setCullHint(Node.CullHint.Never);
        night = SkyFactory.createSky(Main.getInstance().getAssetManager(), "Textures/skybox/StarrySky.dds", false);
        Texture[] texs;
        texs = LoadTextures("yellow");
        yellow = SkyFactory.createSky(Main.getInstance().getAssetManager(), texs[0], texs[1], texs[2], texs[3], texs[4], texs[5]);
        texs = LoadTextures("brown");
        brown = SkyFactory.createSky(Main.getInstance().getAssetManager(), texs[0], texs[1], texs[2], texs[3], texs[4], texs[5]);
        texs = LoadTextures("gray");
        gray = SkyFactory.createSky(Main.getInstance().getAssetManager(), texs[0], texs[1], texs[2], texs[3], texs[4], texs[5]);
        texs = LoadTextures("blue");
        blue = SkyFactory.createSky(Main.getInstance().getAssetManager(), texs[0], texs[1], texs[2], texs[3], texs[4], texs[5]);
        brightness = new Spatial[]{blue, gray, brown, yellow, night};
        yellow_nm = generateSkyBox("yellow");
        brown_nm = generateSkyBox("brown");
        gray_nm = generateSkyBox("gray");
        blue_nm = generateSkyBox("blue");
        brightness_nm = new Spatial[]{blue_nm, yellow_nm, gray_nm, brown_nm, night};
        setLocalScale(50f);
    }

    private Texture[] LoadTextures(String color) {
        Texture[] texs = new Texture[6];
        texs[1] = Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_lf.jpg");
        texs[0] = Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_rt.jpg");
        texs[2] = Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_ft.jpg");
        texs[3] = Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_bk.jpg");
        texs[4] = Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_up.jpg");
        texs[5] = Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud_dn.jpg");
        return texs;
    }

    private Geometry generateSkyBox(String color) {
        Mesh mesh = new Mesh();
        Vector3f[] vertices = new Vector3f[24];
        Vector2f[] textureCoordinates = new Vector2f[24];
        int[] indices = new int[36];
        float[] normals = new float[72];
        int vi = 0, tci = 0, ii = 0, ni = 0;
        float sqrt3 = FastMath.sqr(3);
        //TOP
        vertices[vi++] = new Vector3f(-1, 1, -1);
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 1);
        vertices[vi++] = new Vector3f(1, 1, -1);
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 1);
        vertices[vi++] = new Vector3f(1, 1, 1);
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 3 / 4f);
        vertices[vi++] = new Vector3f(-1, 1, 1);
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 3 / 4f);
        indices[ii++] = 0;
        indices[ii++] = 1;
        indices[ii++] = 2;
        indices[ii++] = 0;
        indices[ii++] = 2;
        indices[ii++] = 3;
        //BACK
        vertices[vi++] = new Vector3f(-1, 1, 1);
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 3 / 4f);
        vertices[vi++] = new Vector3f(1, 1, 1);
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 3 / 4f);
        vertices[vi++] = new Vector3f(1, -1, 1);
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 2 / 4f);
        vertices[vi++] = new Vector3f(-1, -1, 1);
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 2 / 4f);
        indices[ii++] = 4;
        indices[ii++] = 5;
        indices[ii++] = 6;
        indices[ii++] = 4;
        indices[ii++] = 6;
        indices[ii++] = 7;
        //LEFT
        vertices[vi++] = new Vector3f(-1, 1, -1);
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(1, 2 / 4f);
        vertices[vi++] = new Vector3f(-1, 1, 1);
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 2 / 4f);
        vertices[vi++] = new Vector3f(-1, -1, 1);
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 3 / 4f);
        vertices[vi++] = new Vector3f(-1, -1, -1);
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(1, 3 / 4f);
        indices[ii++] = 8;
        indices[ii++] = 9;
        indices[ii++] = 10;
        indices[ii++] = 8;
        indices[ii++] = 10;
        indices[ii++] = 11;
        //RIGHT
        vertices[vi++] = new Vector3f(1, 1, -1);
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(0, 2 / 4f);
        vertices[vi++] = new Vector3f(1, 1, 1);
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 2 / 4f);
        vertices[vi++] = new Vector3f(1, -1, 1);
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 3 / 4f);
        vertices[vi++] = new Vector3f(1, -1, -1);
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(0, 3 / 4f);
        indices[ii++] = 14;
        indices[ii++] = 13;
        indices[ii++] = 12;
        indices[ii++] = 15;
        indices[ii++] = 14;
        indices[ii++] = 12;
        //BOTTOM
        vertices[vi++] = new Vector3f(-1, -1, -1);
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 1 / 4f);
        vertices[vi++] = new Vector3f(1, -1, -1);
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 1 / 4f);
        vertices[vi++] = new Vector3f(1, -1, 1);
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 2 / 4f);
        vertices[vi++] = new Vector3f(-1, -1, 1);
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 2 / 4f);
        indices[ii++] = 18;
        indices[ii++] = 17;
        indices[ii++] = 16;
        indices[ii++] = 19;
        indices[ii++] = 18;
        indices[ii++] = 16;
        //FRONT
        vertices[vi++] = new Vector3f(-1, 1, -1);
        normals[ni++] = sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 0);
        vertices[vi++] = new Vector3f(1, 1, -1);
        normals[ni++] = -sqrt3;
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 0);
        vertices[vi++] = new Vector3f(1, -1, -1);
        normals[ni++] = -sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(1 / 3f, 1 / 4f);
        vertices[vi++] = new Vector3f(-1, -1, -1);
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        normals[ni++] = sqrt3;
        textureCoordinates[tci++] = new Vector2f(2 / 3f, 1 / 4f);
        indices[ii++] = 22;
        indices[ii++] = 21;
        indices[ii++] = 20;
        indices[ii++] = 23;
        indices[ii++] = 22;
        indices[ii++] = 20;

        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(textureCoordinates));
        mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        mesh.updateBound();

        Geometry skybox = new Geometry("skybox", mesh);
        Material mat = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", Main.getInstance().getAssetManager().loadTexture("Textures/skies/" + color + "cloud.jpg"));
        skybox.setMaterial(mat);
        skybox.setCullHint(Node.CullHint.Never);
        return skybox;
    }

    public void reset() {
        if (Main.getInstance().isMotionSicknessSafe()) {
            detachChild(brightness_nm[index]);
            index = brightness_nm.length - 1;
            attachChild(brightness_nm[index]);
        } else {
            detachChild(brightness[index]);
            index = brightness.length - 1;
            attachChild(brightness[index]);
        }
    }

    public boolean brighter() {
        if (index > 0) {
            if (Main.getInstance().isMotionSicknessSafe()) {
                detachChild(brightness_nm[index]);
                index--;
                attachChild(brightness_nm[index]);
                return true;
            } else {
                detachChild(brightness[index]);
                index--;
                attachChild(brightness[index]);
                return true;
            }
        }
        return false;
    }

    public boolean darker() {
        if (Main.getInstance().isMotionSicknessSafe()) {
            if (index + 1 < brightness_nm.length) {
                detachChild(brightness_nm[index]);
                index++;
                attachChild(brightness_nm[index]);
                return true;
            }
        } else {
            if (index + 1 < brightness.length) {
                detachChild(brightness[index]);
                index++;
                attachChild(brightness[index]);
                return true;
            }
        }
        return false;
    }
}
