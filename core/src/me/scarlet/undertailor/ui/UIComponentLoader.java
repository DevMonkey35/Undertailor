package me.scarlet.undertailor.ui;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.impl.UIComponentImplementable;
import me.scarlet.undertailor.lua.lib.meta.LuaUIComponentMeta;
import me.scarlet.undertailor.manager.ScriptManager;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

public class UIComponentLoader {
    
    public static final String MANAGER_TAG = "uicomploader";
    
    private Map<String, File> map;
    
    public UIComponentLoader() {
        this.map = new HashMap<>();
    }
    
    public LuaObjectValue<UIComponent> newLuaComponent(String componentName, Varargs args) {
        if(map.containsKey(componentName)) {
            try {
                ScriptManager scriptMan = Undertailor.getScriptManager();
                return LuaUIComponentMeta.create(scriptMan.generateImplementation(UIComponentImplementable.class, new UIComponentImplementable.LoadData(map.get(componentName), args)));
            } catch(LuaScriptException e) {
                RuntimeException thrown = new RuntimeException("could not retrieve uicomponent " + componentName + ":" + LuaUtil.formatJavaException(e));
                thrown.initCause(e);
                throw thrown;
            }
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing uicomponent");
        return null;
    }
    
    public void loadComponents(File directory) {
        loadComponents(directory, null);
        Undertailor.instance.log(MANAGER_TAG, map.entrySet().size() + " component(s) currently loaded");
    }
    
    public void loadComponents(File directory, String heading) {
        if(heading == null) {
            heading = "";
        }
        
        String dirPath = directory.getAbsolutePath();
        if(!directory.exists()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load uicomponents from directory: " + dirPath + " (doesn't exist)");
            return;
        }
        
        if(directory.isFile()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load uicomponents from directory: " + dirPath + " (not a directory)");
            return;
        }
        
        Undertailor.instance.log(MANAGER_TAG, "loading uicomponent scripts from directory " + dirPath);
        for(File file : directory.listFiles((FileFilter) (File file) -> {
            return file.getName().endsWith(".lua") || file.isDirectory();
        })) {
            if(file.isDirectory()) {
                loadComponents(file, heading.isEmpty() ? file.getName() : heading + "." + file.getName());
                continue;
            }
            
            String name = heading + (heading.isEmpty() ? "" : ".") + file.getName().split("\\.")[0];
            Undertailor.instance.debug(MANAGER_TAG, "registered component " + name);
            map.put(name, file);
        }
    }
}
