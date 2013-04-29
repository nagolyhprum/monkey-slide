package rem.gui;

import rem.Main;

public class StartScreen extends SimpleScreen {

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    public void play() {
        nifty.gotoScreen("game");
        Main.getInstance().go(true);
    }

    public void settings() {
        nifty.gotoScreen("settings");
    }
}
