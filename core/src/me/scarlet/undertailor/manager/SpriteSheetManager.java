/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Sprite;
import me.scarlet.undertailor.gfx.SpriteSheet;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.BatchSpriteSheetWrapper;
import me.scarlet.undertailor.wrappers.SpriteSheetWrapper;
import me.scarlet.undertailor.wrappers.TextureSpriteSheetWrapper;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SpriteSheetManager extends Manager<SpriteSheetWrapper> {
    
    public static final String MANAGER_TAG = "sheetman";
    public static final String BATCH_SHEET_CONFIG = "_batchdir.spritemeta";
    
    private Map<String, SpriteSheetWrapper> sheets;
    
    public SpriteSheetManager() {
        this.sheets = new HashMap<>();
    }
    
    public void loadObjects(File dir) {
        loadObjects(dir, null);
        Undertailor.instance.log(MANAGER_TAG, sheets.keySet().size() + " spritesheet(s) currently loaded");
    }
    
    // directory needs to include sprites.json
    public void loadObjects(File dir, String heading) {
        if(!dir.exists()) {
            dir.mkdirs();
        }
        
        if(!dir.isDirectory()) {
            return;
        }
        
        if(heading == null) {
            heading = "";
        }
        
        // load subdirectories before continuing
        for(File file : dir.listFiles(file1 -> file1.isDirectory())) {
            loadObjects(file, heading + (heading.isEmpty() ? "" : ".") + file.getName());
        }
        
        File batchFile;
        try { // check if the batch definition file is there
            batchFile = dir.listFiles(file1 -> file1.getName().equals(BATCH_SHEET_CONFIG))[0];
        } catch(Exception e) {
            batchFile = null;
        }
        
        File[] files = dir.listFiles(file1 -> file1.getName().endsWith(".png"));
        if(batchFile != null) { // load it as a batch of sprites
            Undertailor.instance.log(MANAGER_TAG, "loading batch spritesheet from directory " + dir.getAbsolutePath());
            if(heading.isEmpty()) {
                Undertailor.instance.error(MANAGER_TAG, "failed to load spritesheets: the root sprites/ folder cannot be used as a batch sprite directory");
            }
            
            String sheetName = heading.isEmpty() ? "sprites" : batchFile.getAbsoluteFile().getParentFile().getName();
            String entryName = heading.isEmpty() ? "sprites" : heading;
            
            try {
                Undertailor.instance.debug(MANAGER_TAG, "batch file is " + batchFile.getAbsolutePath() + ": " + batchFile.exists());
                ConfigurationLoader<ConfigurationNode> loader = JSONConfigurationLoader.builder().setFile(batchFile).build();
                ConfigurationNode root = loader.load();
                SpriteSheetWrapper sheet = new BatchSpriteSheetWrapper(sheetName, files, root);
                
                sheets.put(entryName, sheet);
            } catch(Exception e) {
                Undertailor.instance.error(MANAGER_TAG, "failed to load spritesheet \"" + entryName + "\": vm exception (" + LuaUtil.formatJavaException(e) + ")", e);
            }
        } else { // load it as a normal spritesheet
            Undertailor.instance.log(MANAGER_TAG, "loading spritesheets from directory " + dir.getAbsolutePath());
            for(File file : files) {
                String sheetName = file.getName().substring(0, file.getName().length() - 4);
                String entryName = heading + (heading.isEmpty() ? "" : ".") + sheetName;
                
                // start loading the sheet's metadata
                File spriteDef = new File(dir, sheetName + ".spritemeta");
                if(!spriteDef.exists()) {
                    Undertailor.instance.warn(MANAGER_TAG, "ignoring sheet " + entryName + " (no sheet definition file)");
                    continue;
                }
                
                if(!spriteDef.isFile()) {
                    Undertailor.instance.warn(MANAGER_TAG, "ignoring sheet " + entryName + " (bad sheet definition file)");
                    continue;
                }
                
                try {
                    ConfigurationLoader<ConfigurationNode> loader = JSONConfigurationLoader.builder().setFile(spriteDef).build();
                    ConfigurationNode root = loader.load();
                    Texture texture = new Texture(Gdx.files.absolute(file.getAbsolutePath()));
                    SpriteSheetWrapper sheet = new TextureSpriteSheetWrapper(entryName, texture, root);
                    sheets.put(entryName, sheet);
                    
                    Undertailor.instance.log(MANAGER_TAG, "loading spritesheet " + entryName);
                } catch(Exception e) {
                    Undertailor.instance.error(MANAGER_TAG, "failed to load spritesheet \"" + entryName + "\": vm exception (" + LuaUtil.formatJavaException(e) + ")", e);
                }
            }
        }
    }
    
    public SpriteSheetWrapper getSheet(String sheetName) {
        if(this.sheets.containsKey(sheetName)) {
            return this.sheets.get(sheetName);
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing spritesheet (" + sheetName + ")");
        return null;
    }
    
    public void keepSheetLoaded(String sheetName, boolean preload) {
        SpriteSheetWrapper wrapper = this.getSheet(sheetName);
        if(wrapper != null) {
            if(preload) {
                wrapper.getReference(this);
            } else {
                wrapper.removeReference(this);
            }
        }
    }
    
    public void testSheet(String sheetName) {
        SpriteSheetWrapper sheetRef = this.sheets.get(sheetName);
        SpriteSheet sheet = sheetRef.getReference(this);
        SpriteBatch batch = new SpriteBatch();
        batch.begin();
        int lastSize = -1;
        for(Sprite sprite : sheet.getSprites()) {
            if(lastSize == -1) {
                lastSize = 0;
            } else {
                lastSize += sprite.getTextureRegion().getRegionWidth() * 2;
            }
            
            sprite.draw(40 + lastSize, 35, 2.0F);
        }
        
        batch.end();
        batch.dispose();
        sheetRef.removeReference(this);
    }
}
