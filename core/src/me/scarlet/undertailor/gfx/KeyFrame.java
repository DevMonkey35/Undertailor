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

import java.util.Map;

public class KeyFrame {
    
    public static class FrameObjectMeta {
        
        public static FrameObjectMeta fromMapping(Map<String, Object> map) {
            FrameObjectMeta meta = new FrameObjectMeta();
            
            if(map.containsKey("offX")) { meta.offX = (float) map.get("offX"); }
            if(map.containsKey("offY")) { meta.offY = (float) map.get("offY"); }
            if(map.containsKey("scaleX")) { meta.scaleX = (float) map.get("scaleX"); }
            if(map.containsKey("scaleY")) { meta.scaleY = (float) map.get("scaleY"); }
            if(map.containsKey("scale")) { meta.scaleX = (float) map.get("scale"); meta.scaleY = (float) map.get("scale"); }
            if(map.containsKey("rotation")) { meta.rotation = (float) map.get("rotation"); }
            if(map.containsKey("flipX")) { meta.flipX = (boolean) map.get("flipX"); }
            if(map.containsKey("flipY")) { meta.flipY = (boolean) map.get("flipY"); }
            
            return meta; // TODO maybe clean this up a bit
        }
        
        public float offX, offY, scaleX, scaleY, rotation;
        public boolean smooth, flipX, flipY;
        public int smoothingType;
        
        public FrameObjectMeta() {
            this.offX = 0F;
            this.offY = 0F;
            this.scaleX = 1.0F;
            this.scaleY = 1.0F;
            this.rotation = 0F;
            this.flipX = false;
            this.flipY = false;
            this.smooth = false;
            // smooth tells animator to smoothly transition to this meta's values from the last frame
            this.smoothingType = 0; // 0 = linear, -1 = fast->slow, 1 = slow->fast
            // smoothingType tells how smoothing should occur; linearly or exponentially
        }
    }
    
    public static class SimpleKeyFrame extends KeyFrame {
        private int spriteIndex;
        private FrameObjectMeta meta;
        
        public SimpleKeyFrame(int spriteIndex, long frameTime) {
            this(spriteIndex, frameTime, new FrameObjectMeta());
        }
        
        public SimpleKeyFrame(int spriteIndex, long frameTime, FrameObjectMeta meta) {
            super(frameTime);
            this.spriteIndex = spriteIndex;
            this.meta = meta;
        }
        
        public int getSpriteIndex() {
            return spriteIndex;
        }
        
        public FrameObjectMeta getMeta() {
            return meta;
        }
    }
    
    private long frameTime; // how long this frame takes to complete
    protected KeyFrame(long frameTime) {
        this.frameTime = frameTime;
    }
    
    public long getFrameTime() {
        return frameTime;
    }
}
