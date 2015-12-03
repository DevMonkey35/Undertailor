package me.scarlet.undertailor.manager;

import static com.google.common.base.Preconditions.checkArgument;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.wrappers.DisposableWrapper;
import me.scarlet.undertailor.wrappers.MusicWrapper;
import me.scarlet.undertailor.wrappers.SoundWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    
    public static final String MANAGER_TAG = "audioman";
    
    private float musicVolume, soundVolume;
    private Map<String, SoundWrapper> soundFx;
    private Map<String, MusicWrapper> music;
    
    public AudioManager() {
        soundFx = new HashMap<>();
        music = new HashMap<>();
        soundVolume = 1.0F;
        musicVolume = 1.0F;
    }
    
    public void loadSounds(File soundsDir) {
        load(0, soundsDir, null);
        Undertailor.instance.log(MANAGER_TAG, soundFx.entrySet().size() + " sound(s) currently loaded");
    }
    
    public void loadMusic(File musicDir) {
        load(1, musicDir, null);
        Undertailor.instance.log(MANAGER_TAG, music.entrySet().size() + " music track(s) currently loaded");
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
    
    public SoundWrapper getSound(String name) {
        SoundWrapper wrapper = this.getSoundWrapper(name);
        if(wrapper != null) {
            return wrapper;
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing sound (" + name + ")");
        return null;
    }
    
    public MusicWrapper getMusic(String name) {
        MusicWrapper wrapper = this.getMusicWrapper(name);
        if(wrapper != null) {
            return wrapper;
        }
        
        Undertailor.instance.warn(MANAGER_TAG, "system requested non-existing music (" + name + ")");
        return null;
    }
    
    public SoundWrapper getSoundWrapper(String name) {
        return soundFx.get(name);
    }
    
    public MusicWrapper getMusicWrapper(String name) {
        return music.get(name);
    }
    
    public void keepSoundLoaded(String name, boolean preload) {
        SoundWrapper wrapper = this.getSound(name);
        if(wrapper != null) {
            if(preload) {
                wrapper.getReference(this);
            } else {
                wrapper.removeReference(this);
            }
        }
    }
    
    public void keepMusicLoaded(String name, boolean preload) {
        MusicWrapper wrapper = this.getMusic(name);
        if(wrapper != null) {
            if(preload) {
                wrapper.getReference(this);
            } else {
                wrapper.removeReference(this);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void load(int table, File dir, String heading) {
        if(heading == null) {
            heading = "";
        }
        
        String dirPath = dir.getPath();
        checkArgument(table >= 0 && table <= 1, "Unknown table id");
        
        if(!dir.exists()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load music/sound from directory: " + dirPath + " (doesn't exist)");
            return;
        }
        
        if(!dir.isDirectory()) {
            Undertailor.instance.warn(MANAGER_TAG, "could not load music/sound from directory: " + dirPath + " (not a directory)");
            return;
        }
        
        Map<String, DisposableWrapper<?>> mapping = (Map<String, DisposableWrapper<?>>) (table == 0 ? soundFx : music);
        Undertailor.instance.log(MANAGER_TAG, "scanning directory " + dirPath + " for " + (table == 0 ? "sound" : "music"));
        for(File file : dir.listFiles()) {
            if(file.isDirectory()) {
                load(table, file, heading + (heading.isEmpty() ? "" : ".") + file.getName() + ".");
                continue;
            }
            
            String name = heading + file.getName().split("\\.")[0];
            if(!file.getName().endsWith(".ogg") && !file.getName().endsWith(".mp3") && !file.getName().endsWith(".wav")) {
                Undertailor.instance.warn(MANAGER_TAG, "could not register sound/music file \"" + name + "\"; we can only use .OGG, .MP3 and .WAV files");
                continue;
            }
            
            if(mapping.containsKey(name)) {
                Undertailor.instance.log(MANAGER_TAG, "WARN: name conflict with another sound/music file of another file type detected (" + name + "); old one will be replaced with new one");
            }
            
            DisposableWrapper<?> value = null;
            if(table == 0) { // sound
                value = new SoundWrapper(file);
                Undertailor.instance.debug(MANAGER_TAG, "registered sound " + name);
            } else {
                value = new MusicWrapper(file);
                Undertailor.instance.debug(MANAGER_TAG, "registered music " + name);
            }
            
            mapping.put(name, value);
        }
    }
}
