package rem.gui;

import com.jme3.audio.AudioNode;
import rem.Main;

public class StartScreen extends SimpleScreen {

    private AudioNode sleep;

    @Override
    public void onStartScreen() {
        Main game = Main.getInstance();
        sleep = new AudioNode(game.getAssetManager(), "Sound/background/storybook.wav", false);
        sleep.setLooping(true);
        sleep.play();
    }

    @Override
    public void onEndScreen() {
        sleep.stop();
    }

    public void play() {
        nifty.gotoScreen("game");
        Main.getInstance().go();
    }

    public void settings() {
        nifty.gotoScreen("settings");
    }
}
