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
import me.scarlet.undertailor.util.ConfigurateUtil;
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
        
        Map<Object, ? extends ConfigurationNode> layerMapping = node.getNode("map").getChildrenMap();
        for(Entry<Object, ? extends ConfigurationNode> entry : layerMapping.entrySet()) {
            RoomMapLayer loaded = new RoomMapLayer(map, entry.getValue());
            map.layers.put(loaded.getName(), loaded);
        }
        
        return map;
    }
    
    private int sizeX, sizeY;
    private Map<String, RoomMapLayer> layers;
    private TilemapWrapper[] tilemaps;
    
    public RoomMap() {
        this.layers = new HashMap<>(); // don't need to organize; worldroom already tries to organize for rendering
    }
    
    public Collection<RoomMapLayer> getLayers() {
        return layers.values();
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
    
    @Override
    public void dispose() {
        for(TilemapWrapper wrapper : tilemaps) {
            wrapper.removeReference(this);
        }
    }
}
