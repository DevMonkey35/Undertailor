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

package me.scarlet.undertailor;

import me.scarlet.undertailor.audio.AudioManager;
import me.scarlet.undertailor.gfx.spritesheet.SpriteSheetManager;
import me.scarlet.undertailor.gfx.text.FontManager;
import me.scarlet.undertailor.gfx.text.TextStyleManager;

import java.io.File;

/**
 * Manager class for all the managers.
 */
public class AssetManager {

    public static final String DIR_AUDIO_SOUND = "sounds";
    public static final String DIR_AUDIO_MUSIC = "music";
    public static final String DIR_SPRITES = "sprites";
    public static final String DIR_STYLES = "fonts/styles";
    public static final String DIR_FONTS = "fonts";

    private FontManager font;
    private AudioManager audio;
    private TextStyleManager styles;
    private SpriteSheetManager sprites;

    public AssetManager(Undertailor undertailor) {
        this.font = new FontManager(undertailor.getRenderer());
        this.audio = new AudioManager(undertailor);
        this.styles = new TextStyleManager();
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
        this.styles.loadStyles(new File(rootDirectory, DIR_STYLES));
        this.font.loadFonts(new File(rootDirectory, DIR_FONTS));
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

    /**
     * Returns the underlying {@link FontManager}.
     * 
     * @return the FontManager
     */
    public FontManager getFontManager() {
        return this.font;
    }

    /**
     * Returns the underlying {@link TextStyleManager}.
     * 
     * @return the TextStyleManager
     */
    public TextStyleManager getStyleManager() {
        return this.styles;
    }
}
