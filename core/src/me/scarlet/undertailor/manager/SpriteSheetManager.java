package me.scarlet.undertailor.manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.SpriteSheet;
import me.scarlet.undertailor.wrappers.SpriteSheetWrapper;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SpriteSheetManager {
    
    public static final String MANAGER_TAG = "sheetman";
    
    private Map<String, SpriteSheetWrapper> sheets;
    
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
        
        Undertailor.instance.log(MANAGER_TAG, "loading sprites from directory " + directory.getAbsolutePath());
        File spriteDef = new File(directory, "sprites.json");
        if(!spriteDef.exists()) {
            Undertailor.instance.error(MANAGER_TAG, "could not load sprites from directory: sprites.json not present");
            return;
        }
        
        if(!spriteDef.isFile()) {
            return;
        }
        
        Undertailor.instance.log(MANAGER_TAG, "sprites.json found");
        try {
            ConfigurationLoader<ConfigurationNode> loader = JSONConfigurationLoader.builder().setFile(spriteDef).build();
            ConfigurationNode root = loader.load();
            root.getNode("sheets").getChildrenMap().values().forEach(node -> {
                /*try {
                    Undertailor.instance.log(MANAGER_TAG, "loading spritesheet \"" + node.getKey().toString() + "\"");
                    SpriteSheet sheet = SpriteSheet.fromConfig(directory, node);
                    sheets.put(sheet.getSheetName(), sheet);
                } catch(FileNotFoundException e) {
                    Undertailor.instance.error(MANAGER_TAG, "failed to load spritesheet: " + e.getMessage());
                } catch(TextureTilingException e) {
                    Undertailor.instance.error(MANAGER_TAG, "failed to load spritesheet: texture check failed (" + e.getMessage() + ")");
                } catch(ConfigurationException e) {
                    Undertailor.instance.error(MANAGER_TAG, "failed to load spritesheet: " + e.getMessage());
                }*/
                
                Undertailor.instance.log(MANAGER_TAG, "loading spritesheet \"" + node.getKey().toString() + "\"");
                SpriteSheetWrapper sheet = new SpriteSheetWrapper(directory, node);
                sheets.put(node.getKey().toString(), sheet);
            });
        } catch(Exception e) {
            Undertailor.instance.error(MANAGER_TAG, "failed to load spritesheet: vm exception (" + e.getMessage() + ")", e.getStackTrace());
        }
    }
    
    public SpriteSheetWrapper getSpriteSheet(String sheetName) {
        if(this.sheets.containsKey(sheetName)) {
            return this.sheets.get(sheetName);
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing spritesheet (" + sheetName + ")");
        return null;
    }
    
    public void keepSheetLoaded(String sheetName, boolean preload) {
        SpriteSheetWrapper wrapper = this.getSpriteSheet(sheetName);
        if(wrapper != null) {
            if(preload) {
                wrapper.getReference(this);
            } else {
                wrapper.removeReference(this);
            }
        }
    }
    
    public void testSheet(String sheetName) {
        SpriteSheetWrapper sheetRef = this.sheets.get(sheetName);;
        SpriteSheet sheet = sheetRef.getReference(this);
        SpriteBatch batch = new SpriteBatch();
        batch.begin();
        int lastSize = 0;
        for(int i = 0; i < sheet.getSprites().length; i++) {
            if(i != 0) {
                lastSize += sheet.getSprite(i).getTextureRegion().getRegionWidth() * 2;
            }
            
            sheet.getSprite(i).draw(40 + lastSize, 35, 2.0F);
        }
        
        batch.end();
        batch.dispose();
        sheetRef.removeReference(this);
    }
}
