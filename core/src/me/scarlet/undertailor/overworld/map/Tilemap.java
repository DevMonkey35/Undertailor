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

package me.scarlet.undertailor.overworld.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.TextureTilingException;
import me.scarlet.undertailor.gfx.SpriteSheet;
import me.scarlet.undertailor.gfx.SpriteSheet.SpriteSheetMeta;
import me.scarlet.undertailor.manager.TilemapManager;
import me.scarlet.undertailor.overworld.map.Tile.TileState;
import me.scarlet.undertailor.util.ConfigurateUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.ConfigurationException;

public class Tilemap implements Disposable {
    
    public static final String TILEMAP_SHEET_PREFIX = "$ttlm-";
    public static final String ILLEGAL_CHARACTERS = ",;:=";
    
    public static float getPropertyValue(String str) {
        try {
            return Float.parseFloat(str);
        } catch(NumberFormatException e) {
            if(str.equalsIgnoreCase("true")) {
                return 1.0F;
            }
            
            return 0.0F;
        }
    }
    
    private String name;
    private SpriteSheet sheet;
    private Map<String, Tile> tiles;
    
    public Tilemap(String name, File texture, File meta) throws TextureTilingException, FileNotFoundException, ConfigurationException, IOException {
        this.name = name;
        this.tiles = new HashMap<>();
        
        if(!texture.exists() || !meta.exists()) {
            throw new FileNotFoundException("texture/meta file not found (" + texture.getName() + "/" + meta.getName() + ")");
        }
        
        Texture tx = new Texture(Gdx.files.absolute(texture.getAbsolutePath()));
        if(tx.getWidth() % 20 != 0 || tx.getHeight() % 20 != 0) {
            throw new TextureTilingException("texture does not contain 20x20 tiled sprites");
        }
        
        SpriteSheetMeta smeta = new SpriteSheetMeta();
        smeta.gridX = tx.getWidth() / 20;
        smeta.gridY = tx.getHeight() / 20;
        this.sheet = new SpriteSheet(TILEMAP_SHEET_PREFIX + name, tx, smeta);
        
        JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setFile(meta).build();
        ConfigurationNode node = loader.load();
        
        Map<Object, ? extends ConfigurationNode> map = node.getNode("tiles").getChildrenMap();
        for(Entry<Object, ? extends ConfigurationNode> entry : map.entrySet()) {
            ConfigurationNode tileConfig = entry.getValue();
            String tileName = tileConfig.getKey().toString();
            String defaultState = null;
            Tile tile = new Tile(tileName);
            
            for(int i = 0; i < ILLEGAL_CHARACTERS.length(); i++) {
                char ch = ILLEGAL_CHARACTERS.charAt(i);
                if(tileName.contains(ch + "")) {
                    throw new ConfigurationException("tile name \"" + tileName + "\" contained illegal character '" + ch + "'");
                }
            }
            
            Map<Object, ? extends ConfigurationNode> states = tileConfig.getNode("states").getChildrenMap();
            for(Entry<Object, ? extends ConfigurationNode> stateEntry : states.entrySet()) {
                TileState state = new TileState(stateEntry.getKey().toString(), tile, this.sheet.getSprite(ConfigurateUtil.processInt(stateEntry.getValue().getNode("sprite"), null)));
                tile.addState(state);
                
                if(defaultState == null) {
                    defaultState = state.getStateName();
                    tile.setCurrentState(defaultState);
                }
                
                Undertailor.instance.debug(TilemapManager.MANAGER_TAG, "loading tilestate " + tileName + ":" + state.getStateName());
            }
            
            Undertailor.instance.debug(TilemapManager.MANAGER_TAG, "loaded tile " + this.name + ":" + tileName);
            this.tiles.put(tileName, tile);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public Tile getTile(String id) {
        return tiles.get(id);
    }
    
    @Override
    public void dispose() {
        this.sheet.dispose();
    }
}
