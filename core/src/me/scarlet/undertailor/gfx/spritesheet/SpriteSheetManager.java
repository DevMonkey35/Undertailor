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
    private Map<String, SpriteSheet> sheets;

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
        return this.sheets.get(name);
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
                    new PackagedSpriteSheet(renderer, key, new ZipFile(targetFile)));
                log.info("Loaded packaged spritesheet at " + targetFile.getAbsolutePath()
                    + " under key " + key);
            } catch (Exception e) {
                String message = "Could not load spritesheet at spritesheet file "
                    + targetFile.getAbsolutePath();
                if (e instanceof ZipException)
                    message += " (bad spritesheet file/format)";
                log.error(message, e);
            }
        }
    }
}
