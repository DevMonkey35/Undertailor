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
            dir.mkdirs();
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
            
            Audio<?> value;
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
