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

import me.scarlet.undertailor.gfx.Sprite;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;

public class Tile implements Cloneable {
    
    public static class TileState {
        
        public static TileState fromConfig(ConfigurationNode config) {
            return null; // TODO;
        }
        
        private Tile parent;
        private Sprite sprite; // TODO tile animation
        private String stateName;
        
        public TileState(String stateName, Tile parent, Sprite sprite) {
            this.sprite = sprite;
            this.parent = parent;
            this.stateName = stateName;
        }
        
        public Tile getParent() {
            return parent;
        }
        
        public String getStateName() {
            return stateName;
        }
        
        public Sprite getSprite() {
            return sprite;
        }
    }
    
    private String tileName;
    private String currentState;
    private Map<String, TileState> states;
    
    private Tile() {
        this.tileName = null;
        this.currentState = null;
        this.states = new HashMap<String, TileState>();
    }
    
    public Tile(String tileName) {
        this.tileName = tileName;
        this.currentState = null;
        this.states = new HashMap<String, TileState>();
    }
    
    public String getTileName() {
        return tileName;
    }
    
    public void addState(TileState state) {
        this.addStates(state);
    }
    
    public void addStates(TileState... states) {
        for(TileState state : states) {
            this.states.put(state.getStateName(), state);
        }
    }
    
    public TileState getCurrentState() {
        return states.get(currentState);
    }
    
    public void setCurrentState(String stateId) {
        if(states.containsKey(stateId)) {
            this.currentState = stateId;
        }
    }
    
    public Tile clone() {
        Tile returned = new Tile();
        returned.tileName = this.tileName;
        returned.currentState = this.currentState;
        returned.states = new HashMap<>(this.states);
        
        return returned;
    }
}
