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

/**
 * Manages a specific type of {@link Audio} instances.
 * 
 * @param <T> the type of Audio managed by an instance of this class
 */
public class AudioResourceManager<T extends Audio<?>> {
    
    private AudioManager audioMan;
    private float volume;
    private String rescName;
    private String mTag;
    private Map<String, T> resources;
    
    /**
     * Instantiates a new {@link AudioResourceManager}.
     * 
     * @param audioMan the primary audio manager reference
     * @param mTag the identifer for this resource manager
     * @param rescName the human-readable identifier for the type of audio this
     *            instance manages
     */
    public AudioResourceManager(AudioManager audioMan, String mTag, String rescName) {
        this.audioMan = audioMan;
        this.volume = 1.0F;
        this.mTag = mTag;
        this.rescName = rescName;
        this.resources = new HashMap<>();
    }
    
    /**
     * Returns the total count of currently loaded resources.
     * 
     * @return how many resources are currently loaded
     */
    public int getTotalLoaded() {
        return resources.size();
    }
    
    /**
     * Returns the final volume modifier for {@link Audio} instances managed by
     * this {@link AudioResourceManager}.
     * 
     * @return the volume modifier for underlying Audios' local volumes
     */
    public float getAffectedVolume() {
        return volume * audioMan.getVolume();
    }
    
    /**
     * Returns the local modifier for {@link Audio} instances managed by this
     * {@link AudioResourceManager}.
     * 
     * <p>The local volume of this manager modifies the final volume at which
     * managed Audio instances will be played at. This typically resolves to the
     * final output of the primary audio manager's master volume, this manager's
     * local volume, and the Audio instance's local volume.</p>
     * 
     * @return this manager's local volume
     */
    public float getVolume() {
        return volume;
    }
    
    /**
     * Sets the local modifier for {@link Audio} instances managed by this
     * {@link AudioResourceManager}.
     * 
     * @param volume this manager's local volume
     */
    public void setVolume(float volume) {
        this.volume = NumberUtil.boundFloat(volume, 0.0F, 1.0F);
    }
    
    /**
     * Returns the mapping of loaded resources.
     * 
     * @return a Map containing loaded resources
     */
    public Map<String, T> getResourceMapping() {
        return resources;
    }
    
    /**
     * Returns a Set of {@link Audio} instance currently playing, resolved
     * through the instances' {@link Audio#isPlaying(Object)} method.
     * 
     * @return a Set containing all playing Audio instances from the resources
     *         managed by this manager
     */
    public Set<T> getAllPlaying() {
        return resources.values().stream().filter(resc -> resc.isPlaying(null)).collect(Collectors.toSet());
    }
    
    /**
     * Returns a resource stored under the provided name.
     * 
     * @param name the name of the resource to retrieve
     *            
     * @return the resource under the given name, or null if not found
     */
    public T getResource(String name) {
        T resc = resources.get(name);
        if(resc != null) {
            return resc;
        }
        
        Undertailor.instance.warn(mTag, "system requested non-existing " + rescName + " (" + name + ")");
        return null;
    }
    
    /**
     * Loads a given resource into this manager's mapping under the provided
     * name.
     * 
     * @param rescName the reference name for the provided resource
     * @param resource the resource to store
     */
    public void loadResource(String rescName, T resource) {
        this.resources.put(rescName, resource);
        
        if(resource instanceof MusicWrapper) {
            ((MusicWrapper) resource).rescName = rescName;
        } else if(resource instanceof SoundWrapper) {
            ((SoundWrapper) resource).rescName = rescName;
        }
    }
}
