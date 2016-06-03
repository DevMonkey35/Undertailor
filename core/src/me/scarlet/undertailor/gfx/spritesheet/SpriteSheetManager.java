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

package me.scarlet.undertailor.gfx.spritesheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Manager class for {@link SpriteSheet} instances.
 */
public class SpriteSheetManager {

    private static Logger log = LoggerFactory.getLogger(SpriteSheetManager.class);

    private MultiRenderer renderer;
    private Map<String, SpriteSheetFactory> sheets;

    public SpriteSheetManager(MultiRenderer renderer) {
        this.renderer = renderer;
        this.sheets = new HashMap<>();
    }

    /**
     * Returns the {@link SpriteSheet} stored under the
     * given key.
     * 
     * @param name the key to search under
     * 
     * @return the associated SpriteSheet, or null if not
     *         found
     */
    public SpriteSheet getSheet(String name) {
        if (this.sheets.containsKey(name)) {
            return this.sheets.get(name).getResource();
        }

        return null;
    }

    /**
     * Loads spritesheets found within the given root
     * directory and its subfolders into this
     * {@link SpriteSheetManager}.
     * 
     * @param rootDirectory the root directory to search in
     */
    public void loadSpriteSheets(File rootDirectory) {
        log.info("Loading spritesheet assets from directory " + rootDirectory.getAbsolutePath());
        Map<String, File> files = FileUtil.loadWithIdentifiers(rootDirectory, file -> {
            return file.getName().endsWith(".spritesheet");
        });

        for (String key : files.keySet()) {
            File targetFile = files.get(key);
            try {
                this.sheets.put(key,
                    new PackagedSpriteSheetFactory(key, renderer, new ZipFile(targetFile)));
                log.info(
                    "Loaded packaged spritesheet " + targetFile.getName() + " under key " + key);
            } catch (Exception e) {
                String message = "Could not load spritesheet at spritesheet file "
                    + targetFile.getAbsolutePath();
                if (e instanceof ZipException)
                    message += " (bad spritesheet file/format)";
                log.error(message, e);
            }
        }

        log.info(this.sheets.size() + " spritesheet(s) loaded.");
    }
}
