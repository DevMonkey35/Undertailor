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

import me.scarlet.undertailor.exception.BadConfigurationException;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.util.Layerable;
import ninja.leaping.configurate.ConfigurationNode;

public class RoomMapLayer implements Layerable, Cloneable {
    
    private int z;
    private String name;
    private int priority;
    private RoomMap parent;
    private Tile[][] mapping;
    private boolean visible;
    
    private RoomMapLayer() {} // for clones;
    
    // tilemapid:tileid
    public RoomMapLayer(RoomMap parent, ConfigurationNode layerData) {
        this.parent = parent;
        this.priority = ConfigurateUtil.processBoolean(layerData.getNode("wallLayer"), false) ? 1 : 0;
        this.name = layerData.getKey().toString();
        this.mapping = new Tile[parent.getSizeY()][parent.getSizeX()];
        this.visible = true;
        
        this.z = ConfigurateUtil.processInt(layerData.getNode("z"), 0);
        
        String[] mapping = ConfigurateUtil.processStringArray(layerData.getNode("mapping"), null);
        for(int y = 0; y < parent.getSizeY(); y++) {
            String[] tiles = mapping[y].split(",");
            for(int x = 0; x < parent.getSizeX(); x++) {
                this.mapping[y][x] = parseTileMapping(tiles[x]);
            }
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
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean flag) {
        this.visible = flag;
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
        clone.visible = true;
        clone.mapping = new Tile[parent.getSizeY()][parent.getSizeX()];
        
        for(int y = 0; y < parent.getSizeY(); y++) {
            for(int x = 0; x < parent.getSizeX(); x++) {
                clone.mapping[y][x] = this.mapping[y][x].clone();
            }
        }
        
        return clone;
    }
    
    public void render() {
        if(visible) {
            for(int x = 0; x < parent.getSizeX(); x++) {
                for(int y = 0; y < parent.getSizeY(); y++) {
                    Tile tile = mapping[y][x];
                    if(tile != null) {
                        float xPos = x * 20F;
                        float yPos = y * 20F;
                        tile.getCurrentState().getSprite().draw(xPos, yPos, 1F, 1F, 0F, false, false, 20, 20, true);
                    }
                }
            }
        }
    }
    
    Tile parseTileMapping(String mapping) {
        try {
            String[] mappingSplit = mapping.split(":");
            Tilemap map = parent.getTilemap(Integer.parseInt(mappingSplit[0]));
            System.out.println(mappingSplit[1]);
            Tile returned = map.getTile(mappingSplit[1]).clone();
            
            return returned;
        } catch(NullPointerException e) {
            BadConfigurationException thrown = new BadConfigurationException("bad map data: data requested non-existing tilemap or tile");
            thrown.initCause(e);
            throw thrown;
        }
    }
}
