package me.scarlet.undertailor.audio;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.manager.AudioManager;
import me.scarlet.undertailor.util.NumberUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        this.resources = new HashMap<String, T>();
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
        Set<T> returned = new HashSet<T>(); // TODO change this so we're not iterating through the entire list later
        
        for(T resc : resources.values()) {
            if(resc.isPlaying()) {
                returned.add(resc);
            }
        }
        
        return returned;
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
    }
}
