package rem.gui;

import de.lessvoid.nifty.controls.Scrollbar;

public class SettingsScreen extends SimpleScreen {

    private float volume = 100;
    private float prevVolume;

    public float getVolume() {
        return volume;
    }

    @Override
    public void onStartScreen() {
        prevVolume = volume;
    }

    @Override
    public void onEndScreen() {
    }

    public void confirm() {
        Scrollbar volumeControl = (Scrollbar) nifty.getCurrentScreen().findElementByName("volume").getAttachedInputControl().getController();

        volume = volumeControl.getValue();

        nifty.gotoScreen("start");
    }

    public void cancel() {
        volume = prevVolume;
        
        Scrollbar volumeControl = (Scrollbar) nifty.getCurrentScreen().findElementByName("volume").getAttachedInputControl().getController();
        
        volumeControl.setValue(volume);
        nifty.gotoScreen("start");
    }
}
