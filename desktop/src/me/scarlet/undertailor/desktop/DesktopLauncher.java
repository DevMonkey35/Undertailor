package me.scarlet.undertailor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.scarlet.undertailor.Undertailor;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = true;
        config.backgroundFPS = 0;
        config.foregroundFPS = 0;
        new LwjglApplication(new Undertailor(), config);
    }
}
