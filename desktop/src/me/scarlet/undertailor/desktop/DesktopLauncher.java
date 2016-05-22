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

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.LaunchOptions;
import me.scarlet.undertailor.Undertailor;

/**
 * The entrypoint object for launching the game on a desktop platform.
 * 
 * <p>Launches a JavaFX-powered {@link Launcher} as a gateway to launching the
 * game, or directly launches the game should the system find the
 * <code>-dev</code> flag or if the loaded options skip the launcher.</p>
 */
public class DesktopLauncher extends Application {
    
    static Logger log = LoggerFactory.getLogger(DesktopLauncher.class);
    public static DesktopLauncher instance;
    
    public static void initRuntime() {
        try {
            DesktopLauncher.class.getClassLoader().loadClass("javafx.embed.swing.JFXPanel");
        } catch(Exception ignored) {}
    }
    
    public static void main(String[] args) {
        initRuntime();
        
        String devCheck = System.getenv("tailor-dev-mode");
        if(devCheck != null && devCheck.equalsIgnoreCase("true")) {
            LaunchOptions options = new LaunchOptions(true);
            
            log.info("Running in development mode!");
            launchGame(options);
            return;
        }
        
        Application.launch(args);
    }
    
    public static void launchGame(LaunchOptions options) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        
        config.backgroundFPS = options.frameCap;
        config.foregroundFPS = options.frameCap;
        config.height = options.windowHeight;
        config.width = options.windowWidth;
        
        new LwjglApplication(new Undertailor(options, config), config);
    }
    
    // TODO -- Override the launcher options using command-line options.
    
    @Override
    public void start(Stage stage) throws Exception {
        LaunchOptions options = new LaunchOptions(false);
        if(options.skipLauncher) {
            log.warn("Launcher was skipped");
            launchGame(options);
        } else {
            stage.setScene(new Launcher(stage, options));
            stage.centerOnScreen();
            stage.show();
        }
    }
}
