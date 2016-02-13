package me.scarlet.undertailor.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import me.scarlet.undertailor.Undertailor;

import java.io.File;

public class DesktopLauncher extends Application {
    
    private static boolean runtimeReady;
    
    static {
        runtimeReady = false;
    }
    
    static class TailorThread extends Thread {
        
        private File assetDir;
        
        public TailorThread(File assetDir) {
            this.assetDir = assetDir;
        }
        
        @Override
        public void run() {
            while(!runtimeReady) {}
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.resizable = true;
            config.backgroundFPS = 60;
            config.foregroundFPS = 60;
            config.vSyncEnabled = true;
            config.addIcon("assets/defaultIcon_small.png", Files.FileType.Classpath);
            config.addIcon("assets/defaultIcon.png", Files.FileType.Classpath);
            new LwjglApplication(new Undertailor(config, assetDir), config);
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
        File assetDir;
        if(args[0].equalsIgnoreCase("-dev")) {
            assetDir = new File(System.getProperty("user.dir"));
            System.out.println("setting asset directory to work directory");
        } else {
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < args.length; i++) {
                builder.append(args[i]);
            }
            
            String path = builder.toString();
            if(path.startsWith("\"")) {
                path = path.substring(1);
            }
            
            if(path.endsWith("\"")) {
                path = path.substring(0, path.length() - 1);
            }
            
            assetDir = new File(path);
            if(!assetDir.exists() || !assetDir.isDirectory()) {
                throw new IllegalArgumentException("file at path \"" + path + "\" wasn't existing or wasn't a directory");
            }
            
            System.out.println("setting asset directory to dir at path \"" + path + "\"");
        }
        
        initRuntime();
        new TailorThread(assetDir).start();
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false); // we don't ever open primary stage; once we call hide on any other stages we make (console), jfx kills itself if this is true
    }
}
