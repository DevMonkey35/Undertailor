/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package me.scarlet.undertailor.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import mod.com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.util.BoundedFloat;
import me.scarlet.undertailor.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Manager class for instances of audio.
 * 
 * <p>For added flexibility to instances of audio, an
 * AudioManager will attempt to replace the system audio
 * manager ({@link Gdx#audio}) upon instantiation should it
 * find it that the former has not been replaced yet.</p>
 */
public class AudioManager {

    private static boolean audioReplaced = false;
    private static Logger log = LoggerFactory.getLogger(AudioManager.class);

    private BoundedFloat masterVolume;
    private BoundedFloat soundVolume;
    private BoundedFloat musicVolume;

    private Map<String, Audio> sounds;
    private Map<String, Audio> music;

    public AudioManager(Undertailor undertailor) {
        if (!AudioManager.audioReplaced) {
            log.info("Manager will now try to replace the audio system.");
            if (!LwjglApplicationConfiguration.disableAudio) {
                try {
                    ((com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio) Gdx.audio).dispose();

                    LwjglApplicationConfiguration config =
                        undertailor.getApplicationConfiguration();
                    Gdx.audio = new OpenALAudio(config.audioDeviceSimultaneousSources,
                        config.audioDeviceBufferCount, config.audioDeviceBufferSize);

                    AudioManager.audioReplaced = true;
                    log.info("Success.");
                } catch (Throwable t) {
                    log.error("Failed.", t);
                }
            }
        }

        this.masterVolume = new BoundedFloat(0.0F, 1.0F, 1.0F);
        this.musicVolume = new BoundedFloat(0.0F, 1.0F, 1.0F);
        this.soundVolume = new BoundedFloat(0.0F, 1.0F, 1.0F);
        this.sounds = new HashMap<>();
        this.music = new HashMap<>();
    }

    // ---------------- g/s volumes ----------------

    /**
     * Returns the master volume of the game.
     * 
     * <p>The master volume controls both the sound and
     * music volume, with the final volume of any audio
     * resulting in <code>masterVolume * groupVolume *
     * localVolume</code>, with <code>groupVolume</code>
     * being either the music or sound volume depending on
     * the group of the audio in question, and
     * <code>localVolume</code> being the intended volume
     * set for the audio to be played at.</p>
     * 
     * @return the master volume
     */
    public float getMasterVolume() {
        return this.masterVolume.get();
    }

    /**
     * Sets the master volume of the game.
     * 
     * @param volume the new master volume
     */
    public void setMasterVolume(float volume) {
        this.masterVolume.set(volume);
    }

    /**
     * Returns the music volume of the game.
     * 
     * @return the music volume
     */
    public float getMusicVolume() {
        return this.musicVolume.get();
    }

    /**
     * Sets the music volume of the game.
     * 
     * @param volume the new music volume
     */
    public void setMusicVolume(float volume) {
        this.musicVolume.set(volume);
    }

    /**
     * Returns the sound volume of the game.
     * 
     * @return the sound volume
     */
    public float getSoundVolume() {
        return this.soundVolume.get();
    }

    /**
     * Sets the sound volume of the game.
     * 
     * @param volume the new sound volume
     */
    public void setSoundVolume(float volume) {
        this.soundVolume.set(volume);
    }

    // ---------------- loading methods ----------------

    /**
     * Returns the sound under the given key.
     * 
     * <p>Keys are the names of the directories traversed to
     * get to the target source file starting from the root
     * directory, and the name of the target source file
     * itself with its file extension omitted. The names are
     * separated by <code>.</code> characters.</p>
     * 
     * <p><code>manager.getSound(
     * "voices.mettaton.mettatonSpeak" )</code></p>
     * 
     * @param key the key of the target sound
     * 
     * @return the associated Sound instance, or null if not
     *         found
     */
    public Sound getSound(String key) {
        return (Sound) this.sounds.get(key);
    }

    /**
     * Returns the music under the given key.
     * 
     * <p>Keys are the names of the directories traversed to
     * get to the target source file starting from the root
     * directory, and the name of the target source file
     * itself with its file extension omitted. The names are
     * separated by <code>.</code> characters.</p>
     * 
     * <p><code>manager.getMusic( "combat.bosses.asgore"
     * )</code></p>
     * 
     * @param key the key of the target music
     * 
     * @return the associated Music instance, or null if not
     *         found
     */
    public Music getMusic(String key) {
        return (Music) this.music.get(key);
    }

    /**
     * Loads sounds from the provided root directory into
     * the audio manager.
     * 
     * @param rootDirectory the directory to load from
     */
    public void loadSounds(File rootDirectory) {
        this.load(rootDirectory, Sound.class);
    }

    /**
     * Loads music from the provided root directory into the
     * audio manager.
     * 
     * @param rootDirectory the directory to load from
     */
    public void loadMusic(File rootDirectory) {
        this.load(rootDirectory, Music.class);
    }

    // ---------------- functional methods ----------------

    /**
     * Due to the replacement of the audio system, this
     * method needs to be called at the beginning of every
     * frame to keep audio running.
     */
    public void update() {
        ((OpenALAudio) Gdx.audio).update();
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Handles loading both music and sounds from
     * directories.</p>
     */
    private void load(File rootDirectory, Class<? extends Audio> audioClass) {
        String resourceName = audioClass == Music.class ? "Music" : "Sound";
        String resourceNamePlural = audioClass == Music.class ? "Music" : "Sound(s)";
        log.info("Loading " + resourceNamePlural.toLowerCase() + " from directory "
            + rootDirectory.getAbsolutePath());

        Map<String, File> files = FileUtil.loadWithIdentifiers(rootDirectory, file -> {
            String fileName = file.getName();
            return fileName.endsWith(".ogg") || fileName.endsWith(".wav")
                || fileName.endsWith(".mp3");
        });

        Map<String, Audio> targetMap;
        if (audioClass == Music.class) {
            targetMap = this.music;
        } else {
            targetMap = this.sounds;
        }

        for (String key : files.keySet()) {
            File audioFile = files.get(key);
            try {
                if (targetMap.containsKey(key)) {
                    log.warn(resourceName + " file " + audioFile.getAbsolutePath()
                        + " is replacing a previous entry under the key " + key);
                }

                targetMap.put(key, audioClass == Music.class ? new Music(this, key, audioFile)
                    : new Sound(this, key, audioFile));
                log.debug("Loaded " + resourceName.toLowerCase() + " " + audioFile.getName()
                    + " under key " + key);
            } catch (UnsupportedAudioFileException e) {
                log.error("Failed to load " + resourceName.toLowerCase() + " file "
                    + audioFile.getAbsolutePath() + " (unsupported filetype, .ogg/.wav/.mp3 only)");
            }
        }

        log.info(targetMap.size() + " " + resourceNamePlural.toLowerCase() + " loaded.");
    }
}
