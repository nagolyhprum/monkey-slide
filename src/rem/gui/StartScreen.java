package rem.gui;

public class StartScreen extends SimpleScreen {

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    public void play() {
        nifty.gotoScreen("game");
    }

    public void settings() {
        nifty.gotoScreen("settings");
    }
}
