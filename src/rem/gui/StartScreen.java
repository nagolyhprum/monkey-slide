package rem.gui;

import com.jme3.audio.AudioNode;
import rem.Main;

public class StartScreen extends SimpleScreen {

    private AudioNode sleep;

    public StartScreen() {
        super();
        Main game = Main.getInstance();
        this.sleep = new AudioNode(game.getAssetManager(), "Sound/background/storybook.wav", false);
    }

    @Override
    public void onStartScreen() {
        sleep.setVolume(0.33f * Main.getInstance().getVolume());
        sleep.setLooping(true);
        sleep.play();
    }

    @Override
    public void onEndScreen() {
    }

    public void play() {
        nifty.gotoScreen("game");
        Main.getInstance().go();
    }
    
    public void stop() {
        sleep.stop();
    }

    public void settings() {
        nifty.gotoScreen("settings");
    }

    public void setVolume(float vol) {
        sleep.pause();
        sleep.setVolume(vol);
        sleep.play();
    }
}
