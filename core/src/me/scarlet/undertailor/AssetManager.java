package me.scarlet.undertailor;

import me.scarlet.undertailor.audio.AudioManager;
import me.scarlet.undertailor.gfx.spritesheet.SpriteSheetManager;

import java.io.File;

/**
 * Manager class for all the managers.
 */
public class AssetManager {
    
    public static final String DIR_AUDIO_SOUND = "sounds";
    public static final String DIR_AUDIO_MUSIC = "music";
    public static final String DIR_SPRITES = "sprites";
    
    private AudioManager audio;
    private SpriteSheetManager sprites;
    
    public AssetManager(Undertailor undertailor) {
        this.audio = new AudioManager(undertailor);
        this.sprites = new SpriteSheetManager(undertailor.getRenderer());
    }
    
    // ---------------- functional methods ----------------
    
    /**
     * Directs underlying managers to load all their assets
     * from their specified folders within the given root
     * directory.
     * 
     * @param rootDirectory the root directory to load from
     */
    public void loadAll(File rootDirectory) {
        this.audio.loadSounds(new File(rootDirectory, DIR_AUDIO_MUSIC));
        this.audio.loadMusic(new File(rootDirectory, DIR_AUDIO_MUSIC));
        this.sprites.loadSpriteSheets(new File(rootDirectory, DIR_SPRITES));
    }
    
    // ---------------- g/s managers ----------------
    
    /**
     * Returns the underlying {@link AudioManager}.
     * 
     * @return the AudioManager
     */
    public AudioManager getAudioManager() {
        return this.audio;
    }
    
    /**
     * Returns the underlying {@link SpriteSheetManager}.
     * 
     * @return the SpriteSheetManager
     */
    public SpriteSheetManager getSpriteSheetManager() {
        return this.sprites;
    }
}
