package rem.gui;

import com.jme3.audio.AudioNode;
import rem.Main;

public class GameScreen extends SimpleScreen {

    private AudioNode action;

    @Override
    public void onStartScreen() {
        Main game = Main.getInstance();
        action = new AudioNode(game.getAssetManager(), "Sound/background/railjet.wav", false);
        action.setLooping(true);
        action.play();
    }

    @Override
    public void onEndScreen() {
        action.stop();
    }
}
