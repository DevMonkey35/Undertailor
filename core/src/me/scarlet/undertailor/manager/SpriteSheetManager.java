package me.scarlet.undertailor.manager;

import static me.scarlet.undertailor.Undertailor.log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.scarlet.undertailor.gfx.SpriteSheet;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class SpriteSheetManager {
    
    private Map<String, SpriteSheet> sheets;
    
    public SpriteSheetManager() {
        this.sheets = new HashMap<>();
    }
    
    // directory needs to include sprites.json
    public void loadSprites(File directory) {
        if(!directory.exists()) {
            return;
        }
        
        if(!directory.isDirectory()) {
            return;
        }
        
        log("sheetman", "loading sprites from directory " + directory.getAbsolutePath());
        File spriteDef = new File(directory, "sprites.json");
        if(!spriteDef.exists()) {
            return;
        }
        
        if(!spriteDef.isFile()) {
            return;
        }
        
        log("sheetman", "sprites.json found");
        try {
            ConfigurationLoader<ConfigurationNode> loader = JSONConfigurationLoader.builder().setFile(spriteDef).build();
            ConfigurationNode root = loader.load();
            root.getNode("sheets").getChildrenMap().values().forEach(node -> {
                try {
                    log("sheetman", "loading spritesheet \"" + node.getKey().toString() + "\"");
                    SpriteSheet sheet = SpriteSheet.fromConfig(directory, node);
                    sheets.put(sheet.getSheetName(), sheet);
                } catch(FileNotFoundException e) {
                    Gdx.app.error("sheetman", "failed to load spritesheet: defined texture file was not found");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public SpriteSheet getSpriteSheet(String sheetName) {
        return this.sheets.get(sheetName);
    }
    
    public void testSheet(String sheetName) {
        SpriteSheet sheet = this.sheets.get(sheetName);
        SpriteBatch batch = new SpriteBatch();
        batch.begin();
        int lastSize = 0;
        for(int i = 0; i < sheet.getSprites().length; i++) {
            if(i != 0) {
                lastSize += sheet.getSprite(i).getTextureRegion().getRegionWidth() * 2;
            }
            
            sheet.getSprite(i).draw(batch, 40 + lastSize, 35, 2.0F);
        }
        
        batch.end();
        batch.dispose();
    }
}
