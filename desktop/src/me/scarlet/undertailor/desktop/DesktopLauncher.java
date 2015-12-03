package me.scarlet.undertailor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import javafx.application.Application;
import javafx.stage.Stage;
import me.scarlet.undertailor.Undertailor;

public class DesktopLauncher extends Application {
    
    private static boolean runtimeReady;
    
    static {
        runtimeReady = false;
    }
    
    static class TailorThread extends Thread {
        @Override
        public void run() {
            while(!runtimeReady) {}
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.resizable = true;
            config.backgroundFPS = 60;
            config.foregroundFPS = 60;
            new LwjglApplication(new Undertailor(config), config);
        }
    }
    
    public static void initRuntime() {
        if(!runtimeReady) {
            try {
                DesktopLauncher.class.getClassLoader().loadClass("javafx.embed.swing.JFXPanel");
                runtimeReady = true;
            } catch(Exception e) {}
        }
    }
    
    public static boolean isRuntimeReady() {
        return runtimeReady;
    }
    
    public static void main (String[] args) {
        initRuntime();
        new TailorThread().start();
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {}
}
