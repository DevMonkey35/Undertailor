package me.scarlet.undertailor.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.wrappers.TilemapWrapper;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TilemapManager {
    
    public static final String MANAGER_TAG = "tileman";
    
    private Map<String, TilemapWrapper> tilemaps;
    public TilemapManager() {
        this.tilemaps = new HashMap<>();
    }
    
    public void loadTilemaps(File directory) {
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
            }
            
            String name = file.getName().substring(0, file.getName().length() - 8);
            Undertailor.instance.debug(MANAGER_TAG, "found tilemap data for tilemap " + name);
            File textureFile = new File(dir, name + ".png");
            
            if(!textureFile.exists()) {
                Undertailor.instance.warn(MANAGER_TAG, "ignoring tilemap " + name + " (no paired texture)");
                continue;
            }
            
            if(!textureFile.isFile()) {
                Undertailor.instance.warn(MANAGER_TAG, "ignoring tilemap " + name + " (bad texture file)");
                continue;
            }
            
            Texture texture = new Texture(Gdx.files.absolute(textureFile.getAbsolutePath()));
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setFile(file).build();
            try {
                ConfigurationNode config = loader.load();
                tilemaps.put(name, new TilemapWrapper(name, texture, config));
            } catch(IOException e) {
                Undertailor.instance.error(MANAGER_TAG, "failed to load tilemap: " + e.getMessage(), e.getStackTrace());
                continue;
            }
        }
    }
    
    public TilemapWrapper getTilemap(String name) {
        if(tilemaps.containsKey(name)) {
            return tilemaps.get(name);
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing tilemap (" + name + ")");
        return null;
    }
}
