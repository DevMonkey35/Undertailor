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

import me.scarlet.undertailor.SystemHandler.SystemKeybind;
import me.scarlet.undertailor.manager.EnvironmentManager.ViewportType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Retrieves and saves launch options used and set by the initial launcher that
 * pops open before the game is launched, through the use of {@link Preferences}
 * .
 */
public class LaunchOptions {
    
    public static final String KEY_SCALING = "scaling";
    public static final String KEY_DEBUG_MODE = "debug";
    public static final String KEY_FRAMECAP = "frameCap";
    public static final String KEY_WINDOW_SIZE = "windowSize";
    public static final String KEY_ASSET_DIRECTORY = "assetDir";
    public static final String KEY_SYSTEM_BINDS = "systemBinds";
    public static final String KEY_USE_CUSTOM_DIR = "useCustomDir";
    public static final String KEY_SKIP_LAUNCHER = "skipLauncher";

    public boolean dev;
    public int frameCap;
    public File assetDir;
    public boolean debug;
    public boolean useCustomDir;
    public boolean skipLauncher;
    public ViewportType scaling;
    public int windowWidth, windowHeight;
    public Map<SystemKeybind, Integer> keybinding;
    
    public LaunchOptions(boolean dev) {
        this.dev = dev;
        if(dev) { // use all defaults and some dev options
            this.scaling = ViewportType.FIT;
            this.debug = true;
            this.frameCap = 60;
            this.skipLauncher = true;
            this.windowWidth = 640;
            this.windowHeight = 480;
            this.useCustomDir = true;
            this.assetDir = new File(System.getProperty("user.dir"));
            this.keybinding = new HashMap<>();
        } else {
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            this.scaling = ViewportType.valueOf(prefs.get(KEY_SCALING, ViewportType.FIT.name()));
            this.debug = prefs.getBoolean(KEY_DEBUG_MODE, false);
            this.frameCap = prefs.getInt(KEY_FRAMECAP, 60);
            this.skipLauncher = prefs.getBoolean(KEY_SKIP_LAUNCHER, false);
            
            String[] windowBounds = prefs.get(KEY_WINDOW_SIZE, "640x480").split("x");
            this.windowWidth = Integer.parseInt(windowBounds[0]);
            this.windowHeight = Integer.parseInt(windowBounds[1]);
            
            this.useCustomDir = prefs.getBoolean(KEY_USE_CUSTOM_DIR, false);
            String assetDirPath = prefs.get(KEY_ASSET_DIRECTORY, Undertailor.ASSETS_DIRECTORY.getAbsolutePath());
            this.assetDir = assetDirPath.isEmpty() ? Undertailor.ASSETS_DIRECTORY : new File(assetDirPath);
            
            Preferences bindingsNode = prefs.node(KEY_SYSTEM_BINDS);
            this.keybinding = new HashMap<>();
            for(SystemKeybind bind : SystemKeybind.values()) {
                this.keybinding.put(bind, bindingsNode.getInt(bind.name(), bind.getDefaultKey()));
            }
        }
    }
    
    public void save() {
        if(this.dev) { // we don't save options in dev mode
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
            
            Preferences bindingsNode = prefs.node(KEY_SYSTEM_BINDS);
            for(SystemKeybind bind : this.keybinding.keySet()) {
                bindingsNode.put(bind.name(), keybinding.get(bind).toString());
            }
            
            prefs.flush();
        } catch(BackingStoreException e) {
            System.out.println("Failed to save launcher preferences; ignoring");
        }
    }
}
