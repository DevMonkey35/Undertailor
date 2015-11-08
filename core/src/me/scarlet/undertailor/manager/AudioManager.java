package me.scarlet.undertailor.manager;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AudioManager {
    
    private List<Map<String, ? extends Object>> soundTables;
    private float musicVolume, soundFxVolume;
    private Map<String, Sound> soundFx;
    private Map<String, Music> music;
    
    public AudioManager() {
        soundTables = new ArrayList<Map<String, ?>>();
        soundFx = new HashMap<>();
        music = new HashMap<>();
        soundFxVolume = 1.0F;
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
    }
    
    public float getSoundEffectsVolume() {
        return soundFxVolume;
    }
    
    public void setSoundEffectsVolume(float volume) {
        this.soundFxVolume = volume;
    }
    
    public Optional<Sound> getSound(String name) {
        return Optional.ofNullable(soundFx.get(name));
    }
    
    public Optional<Music> getMusic(String name) {
        return Optional.ofNullable(music.get(name));
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
        Gdx.app.log("audioman", "scanning directory " + dir.getAbsolutePath() + " for " + (table == 0 ? "sound" : "music"));
        for(File file : dir.listFiles()) {
            if(file.isDirectory() && recursive) {
                if(whitelist == null || whitelist.contains(file.getName())) {
                    load(table, file, whitelist, recursive, heading + (heading.isEmpty() ? "" : ".") + file.getName() + ".");
                }
                
                continue;
            }
            
            String name = heading + file.getName().split("\\.")[0];
            if(whitelist == null || whitelist.contains(name)) {
                if(mapping.containsKey(name)) {
                    Gdx.app.log("audioman", "WARN: name conflict with another sound file of another type detected (" + name + "); old one will be replaced with new one");
                }
                
                try {
                    Object value = null;
                    if(table == 0) { // sound
                        value = Gdx.audio.newSound(Gdx.files.absolute(file.getAbsolutePath()));
                        Gdx.app.log("audioman", "registered sound " + name);
                    } else {
                        value = Gdx.audio.newMusic(Gdx.files.absolute(file.getAbsolutePath()));
                        Gdx.app.log("audioman", "registered music " + name);
                    }
                    
                    mapping.put(name, value);
                } catch(GdxRuntimeException e) {
                    Gdx.app.error("audioman", "could not register sound/music file \"" + name + "\"; we can only use .OGG, .MP3 and .WAV files");
                    exit = notAllLoaded;
                }
            }
        }
        
        return exit;
    }
}
