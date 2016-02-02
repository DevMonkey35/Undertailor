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

import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.SpriteSheet;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.wrappers.SpriteSheetWrapper;
import me.scarlet.undertailor.wrappers.TilemapWrapper;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RoomMap implements Disposable {
    
    public static class TileData implements Cloneable {
        
        public static final String KEY_VISIBLE = "visible";
        public static final String KEY_TRAVERSABLE = "traversable";
        
        private Map<String, Float> values;
        
        public TileData() {
            this.values = new HashMap<>();
            this.values.put(KEY_VISIBLE, 1.0F);
            this.values.put(KEY_TRAVERSABLE, 1.0F);
        }
        
        public boolean getBoolean(String key) {
            if(values.containsKey(key)) {
                return values.get(key) > 0.0F;
            }
            
            return false;
        }
        
        public void setBoolean(String key, boolean flag) {
            values.put(key, flag ? 1.0F : 0.0F);
        }
        
        public float getNumber(String key) {
            if(values.containsKey(key)) {
                return values.get(key);
            }
            
            return 0.0F;
        }
        
        public void setNumber(String key, float num) {
            values.put(key, num);
        }
        
        public boolean isTraversable() {
            return this.getBoolean(KEY_TRAVERSABLE);
        }
        
        public boolean isVisible() {
            return this.getBoolean(KEY_VISIBLE);
        }
        
        @Override
        public TileData clone() {
            TileData data = new TileData();
            data.values = new HashMap<>(values);
            return data;
        }
    }
    
    public static final int DEFAULT_FLOOR_Z = 0;
    public static final int DEFAULT_OBJECT_Z = 1;
    public static final int DEFAULT_CEILING_Z = 2;
    
    public static RoomMap fromConfig(ConfigurationNode node) {
        RoomMap map = new RoomMap();
        map.sizeX = ConfigurateUtil.processInt(node.getNode("sizeX"), null);
        map.sizeY = ConfigurateUtil.processInt(node.getNode("sizeY"), null);
        String[] tilemapNames = ConfigurateUtil.processStringArray(node.getNode("tilemaps"), null);
        String[] spritesheetNames = ConfigurateUtil.processStringArray(node.getNode("spritesheets"), null);
        
        map.tilemaps = new TilemapWrapper[tilemapNames.length];
        for(int i = 0; i < map.tilemaps.length; i++) {
            TilemapWrapper wrapper = Undertailor.getTilemapManager().getTilemap(tilemapNames[i]);
            if(wrapper == null) {
                Undertailor.instance.error(RoomLoader.MANAGER_TAG, "failed to load room: map data referenced non-existing tilemap (" + tilemapNames[i] + ")");
                return null;
            }
            
            map.tilemaps[i] = wrapper;
            wrapper.getReference(map);
        }
        
        map.spritesheets = new SpriteSheetWrapper[spritesheetNames.length];
        for(int i = 0; i < map.spritesheets.length; i++) {
            SpriteSheetWrapper wrapper = Undertailor.getSheetManager().getSheet(spritesheetNames[i]);
            if(wrapper == null) {
                Undertailor.instance.error(RoomLoader.MANAGER_TAG, "failed to load room: map data referenced non-existing spritesheet (" + spritesheetNames[i] + ")");
                return null;
            }
            
            map.spritesheets[i] = wrapper;
            wrapper.getReference(map);
        }
        
        Map<Object, ? extends ConfigurationNode> layerMapping = node.getNode("map").getChildrenMap();
        for(Entry<Object, ? extends ConfigurationNode> entry : layerMapping.entrySet()) {
            RoomMapLayer loaded = new RoomMapLayer(map, entry.getValue());
            if(map.getLayerAtZ(loaded.getZ()) == null) {
                map.layers.put(loaded.getName(), loaded);
            } else {
                Undertailor.instance.error(RoomLoader.MANAGER_TAG, "failed to load room: map data contained multiple layers at one z point");
            }
        }
        
        return map;
    }
    
    private int sizeX, sizeY;
    private Map<String, RoomMapLayer> layers;
    private SpriteSheetWrapper[] spritesheets;
    private TilemapWrapper[] tilemaps;
    
    public RoomMap() {
        this.layers = new HashMap<>(); // don't need to organize; worldroom already tries to organize for rendering
    }
    
    public Collection<RoomMapLayer> getLayers() {
        return layers.values();
    }
    
    public RoomMapLayer getLayerAtZ(int z) {
        for(RoomMapLayer layer : layers.values()) {
            if(layer.getZ() == z) {
                return layer;
            }
        }
        
        return null;
    }
    
    public int getSizeX() {
        return sizeX;
    }
    
    public int getSizeY() {
        return sizeY;
    }
    
    public Tilemap getTilemap(int index) {
        return tilemaps[index].getReference();
    }
    
    public SpriteSheet getSpriteSheet(int index) {
        return spritesheets[index].getReference();
    }
    
    @Override
    public void dispose() {
        for(TilemapWrapper wrapper : tilemaps) {
            wrapper.removeReference(this);
        }
        
        for(SpriteSheetWrapper wrapper : spritesheets) {
            wrapper.removeReference(this);
        }
    }
}
