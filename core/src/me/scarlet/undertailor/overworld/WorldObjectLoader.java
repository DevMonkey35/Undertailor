package me.scarlet.undertailor.overworld;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.LuaWorldObject;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

public class WorldObjectLoader {
    
    public static final String MANAGER_TAG = "worldobjectloader";
    
    private Map<String, File> map;
    
    public WorldObjectLoader() {
        this.map = new HashMap<>();
    }
    
    public LuaWorldObject newWorldObject(String objectName) {
        if(map.containsKey(objectName)) {
            try {
                return new LuaWorldObject(map.get(objectName));
            } catch(LuaScriptException e) {
                Undertailor.instance.error(MANAGER_TAG, "could not retrieve object " + objectName + ":" + e.getMessage(), e.getStackTrace());
            }
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing object");
        return null;
    }
    
    public void loadObjects(File directory) {
        loadObjects(directory, null);
        Undertailor.instance.log(MANAGER_TAG, map.entrySet().size() + " object(s) currently loaded");
    }
    
    public void loadObjects(File directory, String heading) {
        if(heading == null) {
            heading = "";
        }
        
        String dirPath = directory.getAbsolutePath();
        if(!directory.exists()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load objects from directory: " + dirPath + " (doesn't exist)");
            return;
        }
        
        if(directory.isFile()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load objects from directory: " + dirPath + " (not a directory)");
            return;
        }
        
        Undertailor.instance.log(MANAGER_TAG, "loading objects scripts from directory " + dirPath);
        for(File file : directory.listFiles((FileFilter) (File file) -> {
            return file.getName().endsWith(".lua") || file.isDirectory();
        })) {
            if(file.isDirectory()) {
                loadObjects(file, heading + "");
            }
            
            String name = heading + (heading.isEmpty() ? "" : ".") + file.getName().split("\\.")[0];
            Undertailor.instance.debug(MANAGER_TAG, "registered object " + name);
            map.put(name, file);
        }
    }
}
