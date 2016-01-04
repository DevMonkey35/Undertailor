package me.scarlet.undertailor.manager;

import static com.google.common.base.Preconditions.checkArgument;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.audio.Audio;
import me.scarlet.undertailor.audio.AudioResourceManager;
import me.scarlet.undertailor.audio.MusicWrapper;
import me.scarlet.undertailor.audio.SoundWrapper;
import me.scarlet.undertailor.util.NumberUtil;

import java.io.File;
import java.util.Map;

public class AudioManager {
    
    public static final String MANAGER_TAG = "audioman";
    
    private float masterVolume;
    private AudioResourceManager<MusicWrapper> musicMan;
    private AudioResourceManager<SoundWrapper> soundMan;
    
    public AudioManager() {
        soundMan = new AudioResourceManager<>(this, MANAGER_TAG, "sound");
        musicMan = new AudioResourceManager<>(this, MANAGER_TAG, "music");
        this.masterVolume = 1.0F;
    }
    
    public void loadSounds(File soundsDir) {
        load(0, soundsDir, null);
        Undertailor.instance.log(MANAGER_TAG, soundMan.getTotalLoaded() + " sound(s) currently loaded");
    }
    
    public void loadMusic(File musicDir) {
        load(1, musicDir, null);
        Undertailor.instance.log(MANAGER_TAG, musicMan.getTotalLoaded() + " music track(s) currently loaded");
    }
    
    public float getVolume() {
        return masterVolume;
    }
    
    public void setVolume(float volume) {
        this.masterVolume = NumberUtil.boundFloat(volume, 0.0F, 1.0F);
    }
    
    public AudioResourceManager<MusicWrapper> getMusicManager() {
        return musicMan;
    }
    
    public AudioResourceManager<SoundWrapper> getSoundManager() {
        return soundMan;
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
        
        Map<String, Audio<?>> mapping = (Map<String, Audio<?>>) (table == 0 ? soundMan.getResourceMapping() : musicMan.getResourceMapping());
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
            
            Audio<?> value = null;
            if(table == 0) { // sound
                value = new SoundWrapper(soundMan, file);
                Undertailor.instance.debug(MANAGER_TAG, "registered sound " + name);
            } else {
                value = new MusicWrapper(name, musicMan, file);
                Undertailor.instance.debug(MANAGER_TAG, "registered music " + name);
            }
            
            mapping.put(name, value);
        }
    }
}
