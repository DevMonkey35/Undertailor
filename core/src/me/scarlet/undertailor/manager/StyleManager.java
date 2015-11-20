package me.scarlet.undertailor.manager;

import com.badlogic.gdx.Gdx;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaStyle;
import me.scarlet.undertailor.lua.lib.ColorsLib;
import me.scarlet.undertailor.lua.lib.MathUtilLib;
import me.scarlet.undertailor.lua.lib.TextLib;
import me.scarlet.undertailor.texts.Style;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

public class StyleManager {
    
    private Map<String, Style> styles;
    
    public static Globals newGlobals() {
        Globals returned = JsePlatform.standardGlobals();
        returned.load(new ColorsLib());
        returned.load(new TextLib());
        returned.load(new MathUtilLib());
        return returned;
    }
    
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
            Gdx.app.log("styleman", "loading lua style " + styleName);
            Globals globals = StyleManager.newGlobals();
            globals.loadfile(file.getAbsolutePath()).invoke();
            try {
                styles.put(styleName, new LuaStyle(globals));
            } catch(LuaScriptException e) {
                Gdx.app.error("styleman", "failed to load style: " + e.getMessage());
            } catch(LuaError e) {
                Gdx.app.error("styleman", "failed to load style: lua error");
                Gdx.app.error("luaparser", e.getMessage());
            }
        }
    }
    
    public Style getStyle(String name) {
        return styles.get(name);
    }
}
