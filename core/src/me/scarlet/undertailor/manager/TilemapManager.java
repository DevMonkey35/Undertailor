package me.scarlet.undertailor.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.TilemapWrapper;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TilemapManager extends Manager<TilemapWrapper> {
    
    public static final String MANAGER_TAG = "tileman";
    
    private Map<String, TilemapWrapper> tilemaps;
    public TilemapManager() {
        this.tilemaps = new HashMap<>();
    }
    
    public void loadObjects(File directory) {
        loadTilemaps(directory, null);
        Undertailor.instance.log(MANAGER_TAG, tilemaps.keySet().size() + " tilemap(s) currently loaded");
    }
    
    private void loadTilemaps(File dir, String heading) {
        String dirPath = dir.getAbsolutePath();
        if(!dir.exists()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load font directory " + dirPath + " (did not exist)");
            return;
        }
        
        if(!dir.isDirectory()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load font directory " + dirPath + " (not a directory)");
            return;
        }
        
        Undertailor.instance.log(MANAGER_TAG, "searching for tilemaps in " + dirPath);
        if(heading == null) {
            heading = "";
        }
        
        for(File file : dir.listFiles(file -> {
            return file.getName().endsWith(".tilemap") || file.isDirectory();
        })) {
            if(file.isDirectory()) {
                loadTilemaps(file, heading + (heading.isEmpty() ? "" : ".") + file.getName());
                continue;
            }
            
            String name = file.getName().substring(0, file.getName().length() - 8);
            String entryName = heading + (heading.isEmpty() ? "" : ".") + name;
            Undertailor.instance.debug(MANAGER_TAG, "found tilemap data for tilemap " + entryName);
            File textureFile = new File(dir, name + ".png");
            
            if(!textureFile.exists()) {
                Undertailor.instance.warn(MANAGER_TAG, "ignoring tilemap " + entryName + " (no paired texture)");
                continue;
            }
            
            if(!textureFile.isFile()) {
                Undertailor.instance.warn(MANAGER_TAG, "ignoring tilemap " + entryName + " (bad texture file)");
                continue;
            }
            
            Texture texture = new Texture(Gdx.files.absolute(textureFile.getAbsolutePath()));
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setFile(file).build();
            try {
                ConfigurationNode config = loader.load();
                Undertailor.instance.debug(MANAGER_TAG, "loading tilemap " + entryName);
                tilemaps.put(entryName, new TilemapWrapper(name, texture, config));
            } catch(IOException e) {
                Undertailor.instance.error(MANAGER_TAG, "failed to load tilemap: " + LuaUtil.formatJavaException(e), e);
                continue;
            }
        }
    }
    
    public TilemapWrapper getStyle(String name) {
        if(tilemaps.containsKey(name)) {
            return tilemaps.get(name);
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing tilemap (" + name + ")");
        return null;
    }
}
