package me.scarlet.undertailor.manager;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.impl.StyleImplementable;
import me.scarlet.undertailor.texts.Style;
import me.scarlet.undertailor.util.LuaUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

public class StyleManager extends Manager<Style> {
    
    public static final String MANAGER_TAG = "styleman";
    
    private Map<String, Style> styles;
    
    public StyleManager() {
        this.styles = new HashMap<>();
    }
    
    public void loadObjects(File directory) {
        loadStyles(directory, null);
        Undertailor.instance.log(MANAGER_TAG, styles.keySet().size() + " style(s) currently loaded");
    }
    
    private void loadStyles(File directory, String heading) {
        String dirPath = directory.getAbsolutePath();
        if(!directory.exists()) {
            return;
        }
        
        if(!directory.isDirectory()) {
            return;
        }
        
        if(heading == null) {
            heading = "";
        }
        
        Undertailor.instance.log(MANAGER_TAG, "loading styles from directory " + dirPath);
        for(File file : directory.listFiles((FileFilter) (File file) -> {
            return file.getName().endsWith(".lua") || file.isDirectory();
        })) {
            if(file.isDirectory()) {
                loadStyles(file, heading + (heading.isEmpty() ? "" : ".") + file.getName());
                continue;
            }
            
            String styleName = heading + (heading.isEmpty() ? "" : ".") + file.getName().substring(0, file.getName().length() - 4);
            Undertailor.instance.debug("styleman", "loading lua style " + styleName);
            try {
                styles.put(styleName, Undertailor.getScriptManager().generateImplementation(StyleImplementable.class, file));
            } catch(LuaScriptException e) {
                Undertailor.instance.error("styleman", "failed to load style: lua error: ", e);
            } catch(Exception e) {
                Undertailor.instance.error("styleman", "failed to load style: " + LuaUtil.formatJavaException(e), e);
            }
        }
    }
    
    public Style getStyle(String name) {
        if(styles.containsKey(name)) {
            return styles.get(name).duplicate();
        }
        
        Undertailor.instance.error("styleman", "system requested a non-existing style (" + name + ")");
        return null;
    }
}
