package me.scarlet.undertailor.engine.overworld.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import me.scarlet.undertailor.engine.overworld.map.TilesetFactory.Tileset;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for {@link Tileset} instances.
 */
public class TilesetManager {

    static final Logger log = LoggerFactory.getLogger(TilesetManager.class);

    private MultiRenderer renderer;
    private Map<String, TilesetFactory> tilesets;

    public TilesetManager(MultiRenderer renderer) {
        this.tilesets = new HashMap<>();
        this.renderer = renderer;
    }

    /**
     * Returns the {@link Tileset} stored under the given
     * key.
     * 
     * @param name the key to search under
     * 
     * @return the associated Tileset, or null if not found
     */
    public Tileset getTileset(String name) {
        if (this.tilesets.containsKey(name)) {
            return this.tilesets.get(name).getResource();
        }

        return null;
    }

    /**
     * Loads tilesets found within the given root directory
     * and its subfolders into this {@link TilesetManager}.
     * 
     * @param rootDirectory the root directory to search in
     */
    public void load(File rootDirectory) {
        log.info("Loading tilesets from directory " + rootDirectory.getAbsolutePath());

        Map<String, File> files =
            FileUtil.loadWithIdentifiers(rootDirectory, file -> file.getName().endsWith(".png"));

        files.keySet().forEach(key -> {
            File targetFile = files.get(key);
            try {
                this.tilesets.put(key, new TilesetFactory(renderer, targetFile));
                log.info("Loaded tileset " + targetFile.getName() + " under key " + key);
            } catch (Exception e) {
                String message =
                    "Could not load tileset at tileset file " + targetFile.getAbsolutePath();

                if (e instanceof SAXException) {
                    message += " (malformed tsx)";
                }

                log.error(message, e);
            }
        });

        log.info(this.tilesets.size() + " tileset(s) loaded.");
    }
}
