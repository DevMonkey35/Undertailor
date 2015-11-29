package me.scarlet.undertailor.manager;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static me.scarlet.undertailor.Undertailor.log;
import static me.scarlet.undertailor.Undertailor.warn;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import me.scarlet.undertailor.wrappers.MusicWrapper;
import me.scarlet.undertailor.wrappers.SoundWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioManager {
    
    public static final String MANAGER_TAG = "audioman";
    
    private List<Map<String, ? extends Object>> soundTables;
    private float musicVolume, soundVolume;
    private Map<String, SoundWrapper> soundFx;
    private Map<String, MusicWrapper> music;
    
    public AudioManager() {
        soundTables = new ArrayList<Map<String, ?>>();
        soundFx = new HashMap<>();
        music = new HashMap<>();
        soundVolume = 1.0F;
        musicVolume = 1.0F;
        
        soundTables.add(soundFx);
        soundTables.add(music);
    }
    
    public byte loadSounds(File soundsDir, List<String> whitelist, boolean recursive) {
        return load(0, soundsDir, whitelist, recursive, null);
    }
    
    public byte loadMusic(File musicDir, List<String> whitelist, boolean recursive) {
        return load(1, musicDir, whitelist, recursive, null);
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if(this.musicVolume > 1.0F) {
            this.musicVolume = 1.0F;
        }
        
        if(this.musicVolume < 0.0F) {
            this.musicVolume = 0F;
        }
    }
    
    public float getSoundVolume() {
        return soundVolume;
    }
    
    public void setSoundVolume(float volume) {
        this.soundVolume = volume;
        if(this.soundVolume > 1.0F) {
            this.soundVolume = 1.0F;
        }
        
        if(this.soundVolume < 0.0F) {
            this.soundVolume = 0F;
        }
    }
    
    public Sound getSound(String name) {
        SoundWrapper wrapper = this.getSoundWrapper(name);
        if(wrapper != null) {
            return wrapper.getReference();
        }
        
        return null;
    }
    
    public Music getMusic(String name) {
        MusicWrapper wrapper = this.getMusicWrapper(name);
        if(wrapper != null) {
            return wrapper.getReference();
        }
        
        return null;
    }
    
    public SoundWrapper getSoundWrapper(String name) {
        return soundFx.get(name);
    }
    
    public MusicWrapper getMusicWrapper(String name) {
        return music.get(name);
    }
    
    @SuppressWarnings("unchecked")
    private byte load(int table, File dir, List<String> whitelist, boolean recursive, String heading) {
        if(heading == null) {
            heading = "";
        }
        
        byte clear = 0x00;
        byte notAllLoaded = 0x01;
        byte errorFileNotFound = 0x02;
        
        byte exit = clear;
        
        checkNotNull(dir);
        checkArgument(table < soundTables.size() && table >= 0, "Unknown table id");
        
        if(!dir.exists()) {
            return errorFileNotFound;
        }
        
        checkArgument(dir.isDirectory(), "Not a directory");
        
        Map<String, Object> mapping = (Map<String, Object>) soundTables.get(table);
        log(MANAGER_TAG, "scanning directory " + dir.getAbsolutePath() + " for " + (table == 0 ? "sound" : "music"));
        for(File file : dir.listFiles()) {
            if(file.isDirectory() && recursive) {
                if(whitelist == null || whitelist.contains(file.getName())) {
                    load(table, file, whitelist, recursive, heading + (heading.isEmpty() ? "" : ".") + file.getName() + ".");
                }
                
                continue;
            }
            
            String name = heading + file.getName().split("\\.")[0];
            if(!file.getName().endsWith(".ogg") && !file.getName().endsWith(".mp3") && !file.getName().endsWith(".wav")) {
                warn(MANAGER_TAG, "could not register sound/music file \"" + name + "\"; we can only use .OGG, .MP3 and .WAV files");
                exit = notAllLoaded;
                continue;
            }
            
            if(whitelist == null || whitelist.contains(name)) {
                if(mapping.containsKey(name)) {
                    log(MANAGER_TAG, "WARN: name conflict with another sound file of another file type detected (" + name + "); old one will be replaced with new one");
                }
                
                Object value = null;
                if(table == 0) { // sound
                    value = new SoundWrapper(file);
                    log(MANAGER_TAG, "registered sound " + name);
                } else {
                    value = new MusicWrapper(file);
                    log(MANAGER_TAG, "registered music " + name);
                }
                
                mapping.put(name, value);
            }
        }
        
        return exit;
    }
}
