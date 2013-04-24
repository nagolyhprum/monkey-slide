package rem.gui;

import de.lessvoid.nifty.controls.Scrollbar;

public class SettingsScreen extends SimpleScreen {

    private float volume = 100;
    private float graphics = 50;
    private float prevVolume, preGraphics;

    public float getVolume() {
        return volume;
    }

    public float getGraphics() {
        return graphics;
    }

    @Override
    public void onStartScreen() {
        prevVolume = volume;
        preGraphics = graphics;
    }

    @Override
    public void onEndScreen() {
    }

    public void confirm() {
        Scrollbar graphicsControl = (Scrollbar) nifty.getCurrentScreen().findElementByName("graphics").getAttachedInputControl().getController();
        Scrollbar volumeControl = (Scrollbar) nifty.getCurrentScreen().findElementByName("volume").getAttachedInputControl().getController();

        volume = volumeControl.getValue();
        graphics = graphicsControl.getValue();

        nifty.gotoScreen("start");
    }

    public void cancel() {
        volume = prevVolume;
        graphics = preGraphics;
        nifty.gotoScreen("start");
    }
}
