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

package me.scarlet.undertailor.engine.overworld.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for {@link Tilemap} instances.
 */
public class TilemapManager {

    static final Logger log = LoggerFactory.getLogger(TilemapManager.class);

    private TilesetManager tilesets;
    private Map<String, TilemapFactory> tilemaps;

    public TilemapManager(TilesetManager tilesets) {
        this.tilemaps = new HashMap<>();
        this.tilesets = tilesets;
    }

    /**
     * Returns the {@link Tilemap} stored under the given
     * key.
     * 
     * @param name the key to search under
     * 
     * @return the associated Tilemap, or null if not found
     */
    public Tilemap getTilemap(String name) {
        if (this.tilemaps.containsKey(name)) {
            return this.tilemaps.get(name).getResource();
        }

        return null;
    }

    /**
     * Loads tilemaps found within the given root directory
     * and its subfolders into this {@link TilemapManager}.
     * 
     * @param rootDirectory the root directory to search in
     */
    public void load(File rootDirectory) {
        log.info("Loading tilemap from directory " + rootDirectory.getAbsolutePath());

        Map<String, File> files = FileUtil.loadWithIdentifiers(rootDirectory,
            file -> file.getName().endsWith(".tmx"), false);

        files.keySet().forEach(key -> {
            File targetFile = files.get(key);
            try {
                this.tilemaps.put(key, new TilemapFactory(targetFile, tilesets));
                log.info("Loaded tilemap " + targetFile.getName() + " under key " + key);
            } catch (Exception e) {
                String message =
                    "Could not load tilemap at tilemap file " + targetFile.getAbsolutePath();

                if (e instanceof SAXException) {
                    message += " (malformed tmx)";
                }

                log.error(message, e);
            }
        });

        log.info(this.tilemaps.size() + " tilemap(s) loaded.");
    }
}
