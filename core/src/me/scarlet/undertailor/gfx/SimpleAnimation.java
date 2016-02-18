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

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.exception.AnimationLoadException;
import me.scarlet.undertailor.exception.ConfigurationException;
import me.scarlet.undertailor.gfx.KeyFrame.FrameObjectMeta;
import me.scarlet.undertailor.gfx.KeyFrame.SimpleKeyFrame;
import me.scarlet.undertailor.manager.AnimationManager;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.util.MapUtil;
import me.scarlet.undertailor.util.NumberUtil;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SimpleAnimation extends Animation<SimpleKeyFrame>{
    
    public static final int TYPE_ID = 0;
    
    public static SimpleAnimation fromConfig(ConfigurationNode node) {
        String name = node.getKey().toString();
        Undertailor.instance.debug(AnimationManager.MANAGER_TAG, "loading simpleanimation " + name);
        
        String[] frames = ConfigurateUtil.processStringArray(node.getNode("frames"), null);
        boolean looping = ConfigurateUtil.processBoolean(node.getNode("looping"), false);
        
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("offX", ConfigurateUtil.processFloat(node.getNode("offX"), 0F));
        defaultData.put("offY", ConfigurateUtil.processFloat(node.getNode("offY"), 0F));
        defaultData.put("flipX", ConfigurateUtil.processBoolean(node.getNode("flipX"), false));
        defaultData.put("flipY", ConfigurateUtil.processBoolean(node.getNode("flipY"), false));
        defaultData.put("frameTime", ConfigurateUtil.processFloat(node.getNode("frameTime"), 0.5F));
        defaultData.put("smoothingType", ConfigurateUtil.processInt(node.getNode("smoothingType"), 0));
        SimpleKeyFrame[] keyFrames = new SimpleKeyFrame[frames.length];
        
        try {
            for(int i = 0; i < frames.length; i++) {
                String frame = frames[i];
                String frameIndexStr = frame.split(";")[0];
                Map<String, Object> data;
                if(frame.length() > frameIndexStr.length()) {
                    data = Animation.parseParameters(defaultData, frame.substring(frameIndexStr.length() + 1)); // number and ;
                } else {
                    data = defaultData;
                }
                
                int frameIndex = Integer.parseInt(frameIndexStr);
                FrameObjectMeta meta = FrameObjectMeta.fromMapping(data);
                float frameTime = (float) data.get("frameTime");
                keyFrames[i] = new SimpleKeyFrame(frameIndex, (long) (1000.0 * frameTime), meta);
            }
        } catch(NumberFormatException e) {
            throw new ConfigurationException("bad frame object data: invalid frame index");
        }
        
        return new SimpleAnimation(name, looping, keyFrames);
    }
    
    private TreeMap<Long, SimpleKeyFrame> frames;
    public SimpleAnimation(String name, boolean loop, SimpleKeyFrame... frames) {
        super(name, loop);
        this.frames = new TreeMap<>();
        long lastTime = 0;
        for(SimpleKeyFrame frame : frames) {
            if(lastTime <= 0) {
                lastTime = frame.getFrameTime();
            } else {
                lastTime += frame.getFrameTime();
            }
            
            this.frames.put(lastTime, frame);
        }
    }

    @Override
    public Map<Long, SimpleKeyFrame> getFrames() {
        return new TreeMap<>(frames);
    }
    
    @Override
    public void finalChecks() {
        for(SimpleKeyFrame frame : frames.values()) {
            for(Sprite[] set : this.getParentSet().getSpritesets()) {
                if(set.length < frame.getSpriteIndex() + 1) {
                    throw new AnimationLoadException("animation " + this.getName() + " in animation set " + this.getParentSet().getName() + " referenced a non-existing sprite index");
                }
            }
        }
    }

    @Override // actual frame first, previous frame second
    public SimpleKeyFrame getFrame(long stateTime, boolean looping) {
        return getFrameEntry(stateTime, looping).getValue();
    }
    
    Entry<Long, SimpleKeyFrame> getFrameEntry(long stateTime, boolean looping) {
        long time = stateTime;
        Entry<Long, SimpleKeyFrame> last = MapUtil.getLastEntry(frames);
        if(this.frames.size() == 1) {
            return last;
        }
        
        if(time > last.getKey()) {
            if(looping) {
                time = (long) (time - ((last.getKey()) * Math.floor(time / (last.getKey()))));
            } else {
                return last;
            }
        }
        
        Entry<Long, SimpleKeyFrame> entry = frames.ceilingEntry(time);
        return entry == null ? last : entry;
    }
    
    @Override
    public void drawFrame(long stateTime, boolean looping, String spriteset, float posX, float posY, float scale, float rotation) {
        Entry<Long, SimpleKeyFrame> currentFrameEntry = getFrameEntry(stateTime, looping);
        if(currentFrameEntry.getValue().getSpriteIndex() <= -1) {
            return;
        }
        
        Entry<Long, SimpleKeyFrame> nextFrameEntry = null;
        if(frames.size() > 1) {
            nextFrameEntry = frames.higherEntry(currentFrameEntry.getKey());
        }
        
        if(nextFrameEntry == null) {
            nextFrameEntry = frames.firstEntry();
        }
        
        float smoothingValue = 0.0F;
        
        if(nextFrameEntry != currentFrameEntry) {
            int smoothingType = nextFrameEntry.getValue().getMeta().smoothingType;
            if(smoothingType > 0) {
                Entry<Long, SimpleKeyFrame> last = MapUtil.getLastEntry(frames);
                float time = (float) stateTime;
                if(stateTime > last.getKey()) {
                    time = (float) (time - ((last.getKey()) * Math.floor(time / (last.getKey()))));
                }
                
                float frameTime = currentFrameEntry.getValue().getFrameTime();
                time = Math.abs(time - currentFrameEntry.getKey()) * -1 + frameTime;
                smoothingValue = NumberUtil.boundFloat(time / frameTime, 0.0F, 1.0F);
                //System.out.println(time + " / " + frameTime);
            }
        }
        
        //System.out.println("smoothing value is " + smoothingValue);
        Sprite sprite = this.getParentSet().getSpriteset(spriteset)[currentFrameEntry.getValue().getSpriteIndex()];
        FrameObjectMeta meta = currentFrameEntry.getValue().getMeta() == null ? new FrameObjectMeta() : currentFrameEntry.getValue().getMeta();
        FrameObjectMeta nextFrame = nextFrameEntry.getValue().getMeta();
        float scaleX, scaleY, offX, offY;
        
        if(smoothingValue > 0) {
            scaleX = (meta.scaleX + ((nextFrame.scaleX - meta.scaleX) * smoothingValue)) * scale;
            scaleY = (meta.scaleY + ((nextFrame.scaleY - meta.scaleY) * smoothingValue)) * scale;
            offX = (meta.offX + ((nextFrame.offX - meta.offX) * smoothingValue)) * scaleX;
            offY = (meta.offY + ((nextFrame.offY - meta.offY) * smoothingValue)) * scaleX;
        } else {
            scaleX = meta.scaleX * scale;
            scaleY = meta.scaleY * scale;
            offX = meta.offX * scaleX;
            offY = meta.offY * scaleY;
        }
        
        sprite.draw(posX + (offX * scaleX), posY + (offY * scaleY), scaleX, scaleY, meta.rotation + rotation, meta.flipX, meta.flipY, sprite.getTextureRegion().getRegionWidth(), sprite.getTextureRegion().getRegionHeight(), false);
    }
}
