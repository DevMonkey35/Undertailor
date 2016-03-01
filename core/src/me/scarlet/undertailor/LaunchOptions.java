package me.scarlet.undertailor;

import me.scarlet.undertailor.SystemHandler.SystemKeybind;
import me.scarlet.undertailor.manager.EnvironmentManager.ViewportType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class LaunchOptions {
    
    public static final String KEY_SCALING = "scaling";
    public static final String KEY_DEBUG_MODE = "debug";
    public static final String KEY_FRAMECAP = "frameCap";
    public static final String KEY_WINDOW_SIZE = "windowSize";
    public static final String KEY_ASSET_DIRECTORY = "assetDir";
    public static final String KEY_SYSTEM_BINDS = "systemBinds";
    public static final String KEY_USE_CUSTOM_DIR = "useCustomDir";
    
    public int frameCap;
    public File assetDir;
    public boolean debug;
    public boolean useCustomDir;
    public ViewportType scaling;
    public int windowWidth, windowHeight;
    public Map<SystemKeybind, Integer> keybinding;
    
    public LaunchOptions() {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        this.scaling = ViewportType.valueOf(prefs.get(KEY_SCALING, ViewportType.FIT.name()));
        this.debug = prefs.getBoolean(KEY_DEBUG_MODE, false);
        this.frameCap = prefs.getInt(KEY_FRAMECAP, 60);
        
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
    
    public void save() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            prefs.put(KEY_SCALING, scaling.name());
            prefs.put(KEY_DEBUG_MODE, debug + "");
            prefs.put(KEY_FRAMECAP, frameCap + "");
            prefs.put(KEY_USE_CUSTOM_DIR, useCustomDir + "");
            prefs.put(KEY_WINDOW_SIZE, windowWidth + "x" + windowHeight);
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
