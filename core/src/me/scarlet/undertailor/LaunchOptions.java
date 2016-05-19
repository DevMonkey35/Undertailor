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
package me.scarlet.undertailor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Retrieves and saves launch options used and set by the
 * initial launcher that pops open before the game is
 * launched, through the use of {@link Preferences}.
 * 
 * <p>Usage is intended to be through accessing the public
 * variables of the instance and reading/modifying as
 * needed.</p>
 */
public class LaunchOptions {
    
    /**
     * An enumeration of possible rendering methods the game
     * can use when resized to differ from its intended
     * resolution.
     */
    public enum ViewportType {
        /**
         * Renders the game stretched to fit the new
         * resolution, ignoring the intended aspect ratio.
         */
        STRETCH,
        /**
         * Resizes the game to fit within the new
         * resolution, keeping the intended aspect ratio.
         */
        FIT
    }
    
    public static final String KEY_SCALING = "scaling";
    public static final String KEY_DEBUG_MODE = "debug";
    public static final String KEY_FRAMECAP = "frameCap";
    public static final String KEY_WINDOW_SIZE = "windowSize";
    public static final String KEY_ASSET_DIRECTORY = "assetDir";
    public static final String KEY_SYSTEM_BINDS = "systemBinds";
    public static final String KEY_USE_CUSTOM_DIR = "useCustomDir";
    public static final String KEY_SKIP_LAUNCHER = "skipLauncher";
    
    static File ASSETS_DIRECTORY;
    static Logger log;
    
    static {
        log = LoggerFactory.getLogger(LaunchOptions.class);
        
        try {
            ASSETS_DIRECTORY = new File(Undertailor.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch(URISyntaxException e) {
            ASSETS_DIRECTORY = null;
            
            IllegalStateException thrown = new IllegalStateException("Failed to find jar directory; program will exit");
            thrown.initCause(e);
            throw thrown;
        }
    }
    
    /** Whether or not these launch options are allowed to be saved. */
    public boolean save;
    
    /** The maximum framerate the game is allowed to achieve. */
    public int frameCap;
    /** The directory of the game scripts to launch (where main.lua is). */
    public File assetDir;
    /** Whether or not to enable debug features. */
    public boolean debug;
    /** Whether or not to use the directory denoted by {@link #assetDir}, or to use the jar directory. */
    public boolean useCustomDir;
    /** Whether or not to skip the launcher. */
    public boolean skipLauncher;
    /** The scaling type employed by the application, in response to the rendering area being resized. */
    public ViewportType scaling;
    /** The width and height of the game window when initially launched. */
    public int windowWidth, windowHeight;
    
    public LaunchOptions(boolean dev) {
        this.save = false;
        this.scaling = ViewportType.FIT;
        this.debug = true;
        this.frameCap = 60;
        this.skipLauncher = true;
        this.windowWidth = 640;
        this.windowHeight = 480;
        this.useCustomDir = true;
        this.assetDir = new File(System.getProperty("user.dir"));
        
        if(!dev) {
            this.save = true;
            
            // differing values from dev defaults marked with #
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            this.scaling = ViewportType.valueOf(prefs.get(KEY_SCALING, this.scaling.name()));
            this.debug = prefs.getBoolean(KEY_DEBUG_MODE, false); // #
            this.frameCap = prefs.getInt(KEY_FRAMECAP, this.frameCap);
            this.skipLauncher = prefs.getBoolean(KEY_SKIP_LAUNCHER, false); // #
            
            String[] windowBounds = prefs.get(KEY_WINDOW_SIZE, this.windowWidth + "x" + this.windowHeight).split("x");
            this.windowWidth = Integer.parseInt(windowBounds[0]);
            this.windowHeight = Integer.parseInt(windowBounds[1]);
            
            this.useCustomDir = prefs.getBoolean(KEY_USE_CUSTOM_DIR, false); // #
            String assetDirPath = prefs.get(KEY_ASSET_DIRECTORY, ASSETS_DIRECTORY.getAbsolutePath());
            this.assetDir = assetDirPath.isEmpty() ? ASSETS_DIRECTORY : new File(assetDirPath); // #
        }
    }
    
    /**
     * Saves the properties written in these
     * {@link LaunchOptions} (assuming
     * {@link LaunchOptions#save} is true).
     */
    public void save() {
        if(!this.save) {
            return;
        }
        
        try {
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            prefs.put(KEY_SCALING, scaling.name());
            prefs.put(KEY_DEBUG_MODE, debug + "");
            prefs.put(KEY_FRAMECAP, frameCap + "");
            prefs.put(KEY_USE_CUSTOM_DIR, useCustomDir + "");
            prefs.put(KEY_WINDOW_SIZE, windowWidth + "x" + windowHeight);
            prefs.put(KEY_SKIP_LAUNCHER, skipLauncher + "");
            prefs.put(KEY_ASSET_DIRECTORY, assetDir == null ? "" : assetDir.getAbsolutePath());
            
            prefs.flush();
        } catch(BackingStoreException e) {
            System.out.println("Failed to save launcher preferences; ignoring");
        }
    }
}
