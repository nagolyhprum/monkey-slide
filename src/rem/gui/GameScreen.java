package rem.gui;

import com.jme3.audio.AudioNode;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.Scrollbar;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import rem.Main;

public class GameScreen extends SimpleScreen {

    private AudioNode action;

    @Override
    public void onStartScreen() {
        Main game = Main.getInstance();
        action = new AudioNode(game.getAssetManager(), "Sound/background/railjet.wav", false);
        action.setVolume(0.5f);
        action.setLooping(true);
        action.play();
    }

    @Override
    public void onEndScreen() {
        action.stop();
    }

    public void setCurrentCoins(int currentScore) {
        Element e = nifty.getCurrentScreen().findElementByName("coins");
        e.getRenderer(TextRenderer.class).setText("" + currentScore);
    }
    

    public void setCurrentScore(int currentScore) {
        Element e = nifty.getCurrentScreen().findElementByName("score");
        e.getRenderer(TextRenderer.class).setText("" + currentScore);
    }
    

    public void setHighScore(int currentScore) {
        Element e = nifty.getCurrentScreen().findElementByName("highscore");
        e.getRenderer(TextRenderer.class).setText("" + currentScore);
    }
    

    public void setTotalCoins(int currentScore) {
        Element e = nifty.getCurrentScreen().findElementByName("totalcoins");
        e.getRenderer(TextRenderer.class).setText("" + currentScore);
    }
}
