package me.scarlet.undertailor.manager;

import static me.scarlet.undertailor.Undertailor.error;
import static me.scarlet.undertailor.Undertailor.log;

import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaStyle;
import me.scarlet.undertailor.texts.Style;
import org.luaj.vm2.LuaError;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

public class StyleManager {
    
    private Map<String, Style> styles;
    
    public StyleManager() {
        this.styles = new HashMap<>();
    }
    
    public void loadStyles(File directory) {
        if(!directory.exists()) {
            return;
        }
        
        if(!directory.isDirectory()) {
            return;
        }
        
        for(File file : directory.listFiles((FilenameFilter) (File file, String string) -> {
            return string.endsWith(".lua");
        })) {
            String styleName = file.getName().substring(0, file.getName().length() - 4);
            log("styleman", "loading lua style " + styleName);
            try {
                styles.put(styleName, new LuaStyle(file));
            } catch(LuaScriptException e) {
                error("styleman", "failed to load style: " + e.getMessage());
            } catch(LuaError e) {
                error("styleman", "failed to load style: lua error");
                error("luaparser", e.getMessage());
            }
        }
    }
    
    public Style getStyle(String name) {
        if(styles.containsKey(name)) {
            return styles.get(name).duplicate();
        }
        
        error("styleman", "system requested a non-existing style (" + name + ")");
        return null;
    }
}
