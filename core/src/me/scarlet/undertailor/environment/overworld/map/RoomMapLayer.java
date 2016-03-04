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
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.BadConfigurationException;
import me.scarlet.undertailor.gfx.Animation;
import me.scarlet.undertailor.gfx.AnimationData;
import me.scarlet.undertailor.gfx.Sprite;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.util.Layerable;
import me.scarlet.undertailor.util.NumberUtil;
import me.scarlet.undertailor.util.Positionable;
import me.scarlet.undertailor.util.Renderable;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashSet;
import java.util.Set;

public class RoomMapLayer implements Layerable, Cloneable, Renderable {
    
    public static abstract class ObjectData implements Layerable, Positionable, Renderable {
        
        private int z;
        private Vector2 position;
        
        protected ObjectData(float x, float y, int z) {
            this.position = new Vector2(x, y);
            this.z = z;
        }
        
        @Override
        public Vector2 getPosition() {
            return this.position;
        }

        @Override
        public int getZ() { return z; }

        @Override
        public void setZ(int z) {}

        @Override
        public int getPriority() { return 0; }
        
    }
    
    public static class SpriteObjectData extends ObjectData {
        
        public static SpriteObjectData fromString(RoomMap parent, String str, int z) {
            // format: sheetname:spriteindex/posx,posy,scale,rotation
            // scale/rotation is optional
            String[] split, sheetData, renderData;
            String sheetName;
            int spriteIndex;
            float x, y, scale = 1F, rotation = 0F;
            
            try {
                split = str.split("[/]");
                sheetData = split[0].split(":");
                renderData = split[1].split(",");
                
                sheetName = sheetData[0];
                spriteIndex = Integer.parseInt(sheetData[1]);
                x = Float.parseFloat(renderData[0]);
                y = Float.parseFloat(renderData[1]);
                
                if(renderData.length >= 3) {
                    scale = Float.parseFloat(renderData[2]);
                }
                
                if(renderData.length >= 4) {
                    rotation = Float.parseFloat(renderData[3]);
                }
            } catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
                BadConfigurationException thrown = new BadConfigurationException("bad map data: bad sprite map object data");
                thrown.initCause(e);
                throw thrown;
            }
            
            try {
                return new SpriteObjectData(
                        x,
                        y,
                        z,
                        scale,
                        rotation,
                        parent.getSpriteSheet(sheetName).getSprite(spriteIndex));
            } catch(NullPointerException | ArrayIndexOutOfBoundsException e) {
                BadConfigurationException thrown = new BadConfigurationException("bad map data: data requested non-existing spritesheet index (" + sheetName + ":" + spriteIndex + ")");
                thrown.initCause(e);
                throw thrown;
            }
        }
        
        private Sprite sprite;
        private float scale, rotation;
        public SpriteObjectData(float x, float y, int z, float scale, float rotation, Sprite sprite) {
            super(x, y, z);
            this.sprite = sprite;
            this.scale = scale;
            this.rotation = rotation;
        }
        
        @Override
        public void render() {
            Vector2 position = this.getPosition();
            sprite.draw(position.x, position.y, scale, scale, rotation);
        }
    }
    
    public static class AnimationObjectData extends ObjectData {
        
        public static AnimationObjectData fromString(RoomMap parent, String str, int z) {
            // format: animsetid:animationid/posx,posy,scale,rotation
            // scale/rotation is optional
            String[] split, sheetData, renderData;
            String setName, animName;
            float x, y, scale = 1F, rotation = 0F;
            
            try {
                split = str.split("[/]");
                sheetData = split[0].split(":");
                renderData = split[1].split(",");
                
                setName = sheetData[0];
                animName = sheetData[1];
                x = Float.parseFloat(renderData[0]);
                y = Float.parseFloat(renderData[1]);
                
                if(renderData.length >= 3) {
                    scale = Float.parseFloat(renderData[2]);
                }
                
                if(renderData.length >= 4) {
                    rotation = Float.parseFloat(renderData[3]);
                }
            } catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
                BadConfigurationException thrown = new BadConfigurationException("bad map data: bad animation map object data");
                thrown.initCause(e);
                throw thrown;
            }
            
            try {
                return new AnimationObjectData(
                        x,
                        y,
                        z,
                        scale,
                        rotation,
                        parent.getAnimationSetWrapper(setName),
                        parent.getAnimationSet(setName).getAnimation(animName));
            } catch(NullPointerException e) {
                BadConfigurationException thrown = new BadConfigurationException("bad map data: data requested non-existing animation");
                thrown.initCause(e);
                throw thrown;
            }
        }
        
        private float scale, rotation;
        private AnimationData animData;
        public AnimationObjectData(float x, float y, int z, float scale, float rotation, AnimationSetWrapper wrapper, Animation<?> animation) {
            super(x, y, z);
            this.animData = new AnimationData(wrapper, animation);
            this.animData.play();
            
            this.scale = scale;
            this.rotation = rotation;
        }
        
        @Override
        public void render() {
            Vector2 position = this.getPosition();
            animData.drawCurrentFrame(position.x, position.y, scale, rotation);
        }
    }
    
    private int z;
    private String name;
    private int priority;
    private RoomMap parent;
    private Tile[][] mapping;
    private Set<ObjectData> objects;
    private float opacity;
    
    private RoomMapLayer() {} // for clones;
    
    // tilemapid:tileid
    public RoomMapLayer(RoomMap parent, ConfigurationNode layerData) {
        this.parent = parent;
        this.priority = ConfigurateUtil.processBoolean(layerData.getNode("wallLayer"), false) ? 1 : 0;
        this.name = layerData.getKey().toString();
        this.mapping = new Tile[parent.getSizeY()][parent.getSizeX()];
        this.objects = new HashSet<>();
        this.opacity = 1.0F;
        
        this.z = ConfigurateUtil.processInt(layerData.getNode("z"), 0);
        
        String[] mapping = ConfigurateUtil.processStringArray(layerData.getNode("mapping"), null);
        for(int y = 0; y < mapping.length && y < parent.getSizeY(); y++) {
            String[] tiles = mapping[y].split(",");
            for(int x = 0; x < tiles.length && x < parent.getSizeX(); x++) {
                this.mapping[y][x] = parseTileMapping(tiles[x]);
            }
        }
        
        String[] sprites = ConfigurateUtil.processStringArray(layerData.getNode("sprites"), new String[0]);
        for (String sprite : sprites) {
            this.objects.add(SpriteObjectData.fromString(parent, sprite, this.z));
        }
        
        String[] animations = ConfigurateUtil.processStringArray(layerData.getNode("animations"), new String[0]);
        for(String animation : animations) {
            this.objects.add(AnimationObjectData.fromString(parent, animation, this.z));
        }
    }
    
    @Override
    public int getZ() {
        return z;
    }
    
    @Override
    public void setZ(int z) {
        this.z = z;
    }
    
    @Override
    public int getPriority() {
        return priority;
    }
    
    public float getOpacity() {
        return opacity;
    }
    
    public void setOpacity(float opacity) {
        this.opacity = NumberUtil.boundFloat(opacity, 0.0F, 1.0F);
    }
    
    public String getName() {
        return name;
    }
    
    public RoomMap getParent() {
        return parent;
    }
    
    public Tile getTileAt(int x, int y) {
        return mapping[y][x];
    }
    
    public RoomMapLayer clone() {
        RoomMapLayer clone = new RoomMapLayer();
        clone.z = this.z;
        clone.name = this.name;
        clone.parent = this.parent;
        clone.priority = this.priority;
        clone.opacity = 1.0F;
        clone.mapping = new Tile[parent.getSizeY()][parent.getSizeX()];
        
        for(int y = 0; y < parent.getSizeY(); y++) {
            for(int x = 0; x < parent.getSizeX(); x++) {
                clone.mapping[y][x] = this.mapping[y][x].clone();
            }
        }
        
        return clone;
    }
    
    public void render() {
        if(opacity > 0.0F) { // not invisible
            Color oldColor = Undertailor.getRenderer().getBatchColor();
            Undertailor.getRenderer().setBatchColor(oldColor, opacity);
            for(int x = 0; x < parent.getSizeX(); x++) {
                for(int y = 0; y < parent.getSizeY(); y++) {
                    Tile tile = mapping[y][x];
                    if(tile != null) {
                        float xPos = x * 20F;
                        float yPos = y * 20F;
                        tile.draw(xPos, yPos);
                    }
                }
            }
            
            Undertailor.getRenderer().setBatchColor(oldColor, oldColor.a);
        }
    }
    
    public Set<ObjectData> getSpriteObjects() {
        return this.objects;
    }
    
    private Tile parseTileMapping(String mapping) {
        String tilemapName, tileName;
        String[] mappingSplit = mapping.split(":");
        
        if(mappingSplit.length < 2) {
            throw new BadConfigurationException("bad map data: bad tile mapping data");
        }
        
        tilemapName = mappingSplit[0];
        tileName = mappingSplit[1];
        
        try {
            Tilemap map = parent.getTilemap(tilemapName);
            return map.getTile(tileName).clone();
        } catch(NullPointerException e) {
            BadConfigurationException thrown = new BadConfigurationException("bad map data: data requested non-existing tile");
            thrown.initCause(e);
            throw thrown;
        }
    }
}
