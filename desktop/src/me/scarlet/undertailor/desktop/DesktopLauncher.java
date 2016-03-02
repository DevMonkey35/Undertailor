/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import me.scarlet.undertailor.LaunchOptions;
import me.scarlet.undertailor.Undertailor;

import java.io.File;

public class DesktopLauncher extends Application {
    
    public static DesktopLauncher instance;
    
    public static void initRuntime() {
        try {
            DesktopLauncher.class.getClassLoader().loadClass("javafx.embed.swing.JFXPanel");
        } catch(Exception ignored) {}
    }
    
    public static void main(String[] args) {
        initRuntime();
        
        if(args.length > 0 && args[0].equalsIgnoreCase("-dev")) {
            LaunchOptions options = new LaunchOptions();
            options.assetDir = new File(System.getProperty("user.dir"));
            options.debug = true;
            options.dev = true;
            
            System.out.println("Running in development mode!");
            launchGame(options);
            return;
        }
        
        Application.launch(args);
    }
    
    public static void launchGame(LaunchOptions options) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = true;
        config.backgroundFPS = options.frameCap;
        config.foregroundFPS = options.frameCap;
        config.vSyncEnabled = true;
        config.addIcon("assets/defaultIcon_small.png", Files.FileType.Classpath);
        config.addIcon("assets/defaultIcon.png", Files.FileType.Classpath);
        config.title = "UNDERTAILOR";
        config.width = options.windowWidth;
        config.height = options.windowHeight;
        
        new LwjglApplication(new Undertailor(config, options), config);
        Platform.setImplicitExit(false);
    }
    
    public DesktopLauncher() {
        DesktopLauncher.instance = this;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        LaunchOptions options = new LaunchOptions();
        if(options.skipLauncher) {
            System.out.println("Launcher was skipped");
            launchGame(options);
        } else {
            stage.setScene(new Launcher(stage, options));
            stage.centerOnScreen();
            stage.show();
        }
    }
}
