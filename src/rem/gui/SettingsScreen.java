package rem.gui;

import de.lessvoid.nifty.controls.Scrollbar;
import rem.Main;

public class SettingsScreen extends SimpleScreen {

    private float volume = 100;

    public float getVolume() {
        return volume;
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    public void confirm() {
        Scrollbar volumeControl = (Scrollbar) nifty.getCurrentScreen().findElementByName("volume").getAttachedInputControl().getController();

        volume = volumeControl.getValue();
        System.out.println("volume = " + volume / 100f);
        Main.getInstance().setEffectsVolume(volume / 100f);
        Main.getInstance().setBackgroundVolume(volume / 100f);
        nifty.gotoScreen("start");
    }

    public void cancel() {
        Scrollbar volumeControl = (Scrollbar) nifty.getCurrentScreen().findElementByName("volume").getAttachedInputControl().getController();

        volumeControl.setValue(volume);
        nifty.gotoScreen("start");
    }
}
