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

package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.scarlet.undertailor.Undertailor;

public class Sprite {
    
    public static class SpriteMeta {
        
        public static final String[] META_VALUES = {"originX", "originY", "wrapX", "wrapY", "offX", "offY"};
        
        public float originX, originY;
        public int wrapX, wrapY, offX, offY;
        
        public SpriteMeta() {
            this(0.0F, 0.0F, 0, 0, 0, 0);
        }
        
        public SpriteMeta(float originX, float originY, int offX, int offY, int wrapX, int wrapY) {
            this.originX = originX;
            this.originY = originY;
            this.wrapX = wrapX;
            this.wrapY = wrapY;
            this.offX = offX;
            this.offY = offY;
        }
        
        @Override
        public String toString() {
            return "[" + originX + ", " + originY + ", " + wrapX + ", " + wrapY + ", " + offX + ", " + offY + "]";
        }
    }
    
    private SpriteMeta meta;
    private TextureRegion region;
    public Sprite(TextureRegion sprite, SpriteMeta meta) {
        this.region = sprite;
        this.meta = meta;
    }
    
    public TextureRegion getTextureRegion() {
        return region;
    }
    
    public SpriteMeta getMeta() {
        return meta;
    }
    
    public void draw(float posX, float posY) {
        this.draw(posX, posY, 1.0F);
    }
    
    public void draw(float posX, float posY, float scale) {
        this.draw(posX, posY, scale, scale);
    }
    
    public void draw( float posX, float posY, float scaleX, float scaleY) {
        this.draw(posX, posY, scaleX, scaleY, 0F);
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation) {
        this.draw(posX, posY, scaleX, scaleY, rotation, false);
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation, boolean flipX) {
        this.draw(posX, posY, scaleX, scaleY, rotation, flipX, false);
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation, boolean flipX, boolean flipY) {
        this.draw(posX, posY, scaleX, scaleY, rotation, flipX, flipY, region.getRegionWidth(), region.getRegionHeight());
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation, boolean flipX, boolean flipY, int sizeX, int sizeY) {
        this.draw(posX, posY, scaleX, scaleY, rotation, flipX, flipY, sizeX, sizeY, false);
    }
    
    public void draw(float posX, float posY, float scaleX, float scaleY, float rotation, boolean flipX, boolean flipY, int sizeX, int sizeY, boolean ensureBottomLeft) { // for texts
        float originX = 0, originY = 0;
        int offX = 0, offY = 0;
        if(meta != null) {
            originX = meta.originX;
            originY = meta.originY;
            offX = meta.offX;
            offY = meta.offY;
        }
        
        float x = posX + (offX * scaleX);
        float y = posY + (offY * scaleY);
        if(!ensureBottomLeft) {
            x -= originX;
            y -= originY;
        } else {
            x += originX;
            y += originY;
        }
        
        region.flip(flipX, flipY);
        Undertailor.getRenderer().draw(region, x, y, originX, originY, sizeX, sizeY, scaleX, scaleY, rotation);
        region.flip(flipX, flipY);
    }
}
