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

import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.exception.ConfigurationException;

import java.util.HashMap;
import java.util.Map;

public abstract class Animation<T extends KeyFrame> implements Disposable {
    
    public static Map<String, Object> parseParameters(Map<String, Object> defaults, String str) {
        Map<String, Object> map = new HashMap<>(defaults);
        String[] params = str.split(";");
        for(String param : params) {
            String[] keyValuePair = param.split("=");
            if(keyValuePair.length != 2) {
                throw new ConfigurationException("bad frame object data: parameter not structured for key/value pair");
            }
            
            String key = keyValuePair[0];
            String value = keyValuePair[1];
            
            try {
                switch(key) {
                    case "offX":
                    case "offY":
                    case "scale":
                    case "scaleX":
                    case "scaleY":
                    case "rotation":
                    case "frameTime":
                        map.put(key, Float.parseFloat(value));
                        break;
                    case "flipX":
                    case "flipY":
                        map.put(key, Boolean.parseBoolean(value));
                        break;
                    case "smoothingType":
                        map.put(key, Integer.parseInt(value));
                        break;
                    default:
                        break;
                }
            } catch(NumberFormatException e) {
                throw new ConfigurationException("bad frame object data: invalid parameter value \"" + value + "\" for parameter \"" + key + "\"");
            }
        }
        
        return map;
    }
    
    public static final String DEFAULT_SPRITESET = "default";
    
    private String name;
    private boolean loop;
    protected AnimationSet animSet;
    public Animation(String name, boolean loop) {
        this.animSet = null;
        this.loop = loop;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isLooping() {
        return this.loop;
    }
    
    public AnimationSet getParentSet() {
        return animSet;
    }
    
    @Override
    public void dispose() {} // nothing
    
    public abstract Map<Long, T> getFrames();
    public abstract T getFrame(long stateTime, boolean looping);
    
    public void drawFrame(long stateTime, boolean looping, String spriteset, float posX, float posY, float offX, float offY) {
        this.drawFrame(stateTime, looping, spriteset, posX, posY, offX, offY, 1F);
    }
    
    public void drawFrame(long stateTime, boolean looping, String spriteset, float posX, float posY, float offX, float offY, float scale) {
        this.drawFrame(stateTime, looping, spriteset, posX, posY, offX, offY, scale, 0F);
    }
    
    public abstract void drawFrame(long stateTime, boolean looping, String spriteset, float posX, float posY, float offX, float offY, float scale, float rotation);
    
    public void finalChecks() {}
    
    /*public void drawCurrentFrame(float posX, float posY) {
        this.drawCurrentFrame(posX, posY, 1F);
    }
    
    public void drawCurrentFrame(float posX, float posY, float scale) {
        this.drawCurrentFrame(posX, posY, scale, 0F);
    }
    
    public abstract void drawCurrentFrame(float posX, float posY, float scale, float rotation);*/
}
