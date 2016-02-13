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

import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.gfx.Sprite;

public class Tile implements Cloneable {
    
    public static final long GLOBAL_TILE_START_TIME;
    
    private String tileName;
    private long frameTime;
    private Sprite[] sprites;
    
    static {
        GLOBAL_TILE_START_TIME = TimeUtils.millis();
    }
    
    private Tile() {
        this.tileName = null;
        this.frameTime = 150; 
    }
    
    public Tile(String tileName, long frameTime, Sprite... sprites) {
        this.tileName = tileName;
        this.frameTime = frameTime;
        this.sprites = sprites;
    }
    
    public String getTileName() {
        return tileName;
    }
    
    public void draw(float xPos, float yPos) {
        sprites[getCurrentSprite()].draw(xPos, yPos, 1F, 1F, 0F, false, false, 20, 20, true);
    }
    
    public float getFrameTime() {
        return this.frameTime;
    }
    
    public Tile clone() {
        Tile returned = new Tile();
        returned.tileName = this.tileName;
        returned.frameTime = this.frameTime;
        returned.sprites = this.sprites;
        
        return returned;
    }
    
    private int getCurrentSprite() {
        if(this.frameTime <= 0) {
            return 0;
        }
        
        double runtime = TimeUtils.timeSinceMillis(GLOBAL_TILE_START_TIME);
        
        // shave runtime
        double animationTotalTime = frameTime * sprites.length;
        
        if(runtime >= animationTotalTime) {
            runtime -= Math.floor(runtime / animationTotalTime) * animationTotalTime;
        }
        
        return (int) Math.floor(runtime / frameTime);
    }
}
