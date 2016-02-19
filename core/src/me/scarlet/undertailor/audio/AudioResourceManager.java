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

package me.scarlet.undertailor.audio;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.manager.AudioManager;
import me.scarlet.undertailor.util.NumberUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AudioResourceManager<T extends Audio<?>> {
    
    private AudioManager audioMan;
    private float volume;
    private String rescName;
    private String mTag;
    private Map<String, T> resources;
    
    public AudioResourceManager(AudioManager audioMan, String mTag, String rescName) {
        this.audioMan = audioMan;
        this.volume = 1.0F;
        this.mTag = mTag;
        this.rescName = rescName;
        this.resources = new HashMap<>();
    }
    
    public int getTotalLoaded() {
        return resources.size();
    }
    
    public float getAffectedVolume() {
        return volume * audioMan.getVolume();
    }
    
    public float getVolume() {
        return volume;
    }
    
    public void setVolume(float volume) {
        this.volume = NumberUtil.boundFloat(volume, 0.0F, 1.0F);
    }
    
    public Map<String, T> getResourceMapping() {
        return resources;
    }
    
    public Set<T> getAllPlaying() {
        return resources.values().stream().filter(resc -> resc.isPlaying(null)).collect(Collectors.toSet());
    }
    
    public T getResource(String name) {
        T resc = resources.get(name);
        if(resc != null) {
            return resc;
        }
        
        Undertailor.instance.warn(mTag, "system requested non-existing " + rescName + " (" + name + ")");
        return null;
    }
    
    public void loadResource(String rescName, T resource) {
        this.resources.put(rescName, resource);
        
        if(resource instanceof MusicWrapper) {
            ((MusicWrapper) resource).rescName = rescName;
        } else if(resource instanceof SoundWrapper) {
            ((SoundWrapper) resource).rescName = rescName;
        }
    }
}
