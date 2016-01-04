package me.scarlet.undertailor.manager;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AnimationManager extends Manager<AnimationSetWrapper> {
    
    public static final String MANAGER_TAG = "animman";
    
    // owning object name, animation name, animation
    private Map<String, AnimationSetWrapper> animationMap;
    public AnimationManager() {
        this.animationMap = new HashMap<>();
    }
    
    public void loadObjects(File dir) {
        loadObjects(dir, null);
        Undertailor.instance.log(MANAGER_TAG, animationMap.keySet().size() + " animation set(s) currently loaded");
    }
    
    private void loadObjects(File dir, String heading) {
        String dirPath = dir.getAbsolutePath();
        if(!dir.exists()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load animation directory " + dirPath + " (did not exist)");
            return;
        }
        
        if(!dir.isDirectory()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load animation directory " + dirPath + " (not a directory)");
            return;
        }
        
        if(heading == null) {
            heading = "";
        }
        Undertailor.instance.log(MANAGER_TAG, "searching for animations in " + dirPath);
        for(File file : dir.listFiles(file -> {
            return file.getName().endsWith(".tailoranim") || file.isDirectory();
        })) {
            if(file.isDirectory()) {
                loadObjects(file, heading + (heading.isEmpty() ? "" : ".") + file.getName());
                continue;
            }
            
            String name = file.getName().substring(0, file.getName().length() - 11);
            String entryName = heading + (heading.isEmpty() ? "" : ".") + name;
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setFile(file).build();
            try {
                ConfigurationNode node = loader.load();
                if(node.getNode("meta").isVirtual() || node.getNode("animation").isVirtual()) {
                    Undertailor.instance.warn(MANAGER_TAG, "ignoring animation file " + file.getName() + " containing invalid animation configuration");
                    continue;
                }
                
                Undertailor.instance.debug(MANAGER_TAG, "loading animation set " + entryName);
                animationMap.put(entryName, new AnimationSetWrapper(entryName, node));
            } catch(Exception e) {
                Undertailor.instance.error(MANAGER_TAG, "could not load animationset " + name + ": " + LuaUtil.formatJavaException(e), e);
            }
        }
    }
    
    public AnimationSetWrapper getStyle(String name) {
        if(animationMap.containsKey(name)) {
            return animationMap.get(name);
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing animation set (" + name + ")");
        return null;
    }
}
