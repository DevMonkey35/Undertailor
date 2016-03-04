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

package me.scarlet.undertailor.environment.overworld.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.collision.CollisionHandler;
import me.scarlet.undertailor.collision.bbshapes.BoundingRectangle;
import me.scarlet.undertailor.gfx.AnimationSet;
import me.scarlet.undertailor.gfx.SpriteSheet;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;
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
            this.values.put(KEY_TRAVERSABLE, 1.0F);
        }
        
        public boolean getBoolean(String key) {
            return values.containsKey(key) && values.get(key) > 0.0F;
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
    
    public static class TraversableData {
        
        private float[][] data;
        private Map<Vector2, BoundingRectangle> boundingShapes;
        
        public TraversableData(float[][] data) {
            this.data = data;
            this.boundingShapes = new HashMap<>();
            this.generateCollision();
        }
        
        public float[][] getData() {
            return data;
        }
        
        public Map<Vector2, BoundingRectangle> getBoundingShapes() {
            return this.boundingShapes;
        }
        
        private void generateCollision() {
            
        }
    }
    
    public static final int DEFAULT_FLOOR_Z = 0;
    public static final int DEFAULT_OBJECT_Z = 1;
    public static final int DEFAULT_CEILING_Z = 2;
    public static final BodyDef TILE_BODY_DEF;
    public static final Color WALL_BOUNDING_COLOR;
    
    static {
        TILE_BODY_DEF = new BodyDef();
        TILE_BODY_DEF.active = true;
        TILE_BODY_DEF.allowSleep = true;
        TILE_BODY_DEF.awake = false;
        TILE_BODY_DEF.fixedRotation = true;
        TILE_BODY_DEF.type = BodyType.StaticBody;
        WALL_BOUNDING_COLOR = Color.PURPLE;
    }
    
    public static RoomMap fromConfig(ConfigurationNode node) {
        RoomMap map = new RoomMap();
        map.sizeX = ConfigurateUtil.processInt(node.getNode("sizeX"), null);
        map.sizeY = ConfigurateUtil.processInt(node.getNode("sizeY"), null);
        map.data = new TileData[map.sizeY][map.sizeX];
        
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
    
    public static TraversableData parseDataPreset(RoomMap parent, ConfigurationNode node) {
        float[][] data = new float[parent.getSizeY()][parent.getSizeX()];
        String[] mapping = ConfigurateUtil.processStringArray(node, null);
        for(int y = 0; y < parent.getSizeY(); y++) {
            String[] current = mapping[y].split(",");
            for(int x = 0; x < parent.getSizeX(); x++) {
                data[y][x] = Float.parseFloat(current[x]);
            }
        }
        
        return new TraversableData(data);
    }
    
    private int sizeX, sizeY;
    private Map<String, RoomMapLayer> layers;
    private Map<String, AnimationSetWrapper> animations;
    private Map<String, SpriteSheetWrapper> spritesheets;
    private Map<String, TilemapWrapper> tilemaps;
    private TileData[][] data;

    private Map<String, TraversableData> travPresets;
    private String currentPreset;
    private Body collision;
    
    public RoomMap() {
        this.layers = new HashMap<>(); // don't need to organize; worldroom already tries to organize for rendering
        this.tilemaps = new HashMap<>();
        this.animations = new HashMap<>();
        this.spritesheets = new HashMap<>();
        this.collision = null;
    }
    
    public void generateCollision(CollisionHandler handler) {
        this.destroyCollision(handler);
        TraversableData preset = this.travPresets.get(currentPreset);
        
        for(int y = 0; y < data.length; y++) {
            TileData[] row = data[y];
            for(int x = 0; x < row.length; x++) {
                row[x].setNumber(TileData.KEY_TRAVERSABLE, preset.getData()[y][x]);
            }
        }

        Body body = handler.getWorld().createBody(TILE_BODY_DEF);
        for(Entry<Vector2, BoundingRectangle> box : preset.getBoundingShapes().entrySet()) {
            box.getValue().applyFixture(body);
        }
    }
    
    public void destroyCollision(CollisionHandler handler) {
        handler.getWorld().destroyBody(collision);
    }
    
    public TileData getDataForTile(int x, int y) {
        if(data[y][x] == null) {
            data[y][x] = new TileData();
        }
        
        return data[y][x];
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
    
    public AnimationSet getAnimationSet(String name) {
        return this.getAnimationSetWrapper(name).getReference();
    }
    
    public AnimationSetWrapper getAnimationSetWrapper(String name) {
        if(!this.animations.containsKey(name)) {
            AnimationSetWrapper wrapper = Undertailor.getAnimationManager().getAnimation(name);
            if(wrapper == null) {
                Undertailor.instance.error(RoomLoader.MANAGER_TAG, "failed to load room: map data referenced non-existing animation (\"" + name + "\")");
                return null;
            }
            
            this.animations.put(name, wrapper);
            wrapper.getReference(this);
            return wrapper;
        }
        
        return this.animations.get(name);
    }
    
    public Tilemap getTilemap(String name) {
        if(!this.tilemaps.containsKey(name)) {
            TilemapWrapper wrapper = Undertailor.getTilemapManager().getTilemap(name);
            if(wrapper == null) {
                Undertailor.instance.error(RoomLoader.MANAGER_TAG, "failed to load room: map data referenced non-existing tilemap (\"" + name + "\")");
                return null;
            }
            
            this.tilemaps.put(name, wrapper);
            return wrapper.getReference(this);
        }
        
        return this.tilemaps.get(name).getReference();
    }
    
    public SpriteSheet getSpriteSheet(String name) {
        if(!this.spritesheets.containsKey(name)) {
            SpriteSheetWrapper wrapper = Undertailor.getSheetManager().getSheet(name);
            if(wrapper == null) {
                Undertailor.instance.error(RoomLoader.MANAGER_TAG, "failed to load room: map data referenced non-existing spritesheet (\"" + name + "\")");
                return null;
            }
            
            this.spritesheets.put(name, wrapper);
            return wrapper.getReference(this);
        }
        
        return this.spritesheets.get(name).getReference();
    }
    
    @Override
    public void dispose() {
        for(TilemapWrapper wrapper : tilemaps.values()) {
            wrapper.removeReference(this);
        }
        
        for(SpriteSheetWrapper wrapper : spritesheets.values()) {
            wrapper.removeReference(this);
        }
    }
}
