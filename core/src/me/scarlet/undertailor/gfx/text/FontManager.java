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

package me.scarlet.undertailor.gfx.text;

import com.badlogic.gdx.utils.ObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.exception.BadAssetException;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Manager class for {@link Font} instances.
 */
public class FontManager {

    private static Logger log = LoggerFactory.getLogger(FontManager.class);

    private MultiRenderer renderer;
    private ObjectMap<String, Font> fonts;

    public FontManager(MultiRenderer renderer) {
        this.renderer = renderer;
        this.fonts = new ObjectMap<>();
    }

    /**
     * Returns the {@link Font} stored under the provided
     * key.
     * 
     * @param key the key to search under
     * 
     * @return the associated Font, or null if not found
     */
    public Font getFont(String key) {
        return this.fonts.get(key);
    }

    /**
     * Loads fonts found within the given root directory and
     * its subfolders into this {@link FontManager}.
     * 
     * @param rootDirectory the root directory to search in
     */
    public void loadFonts(File rootDirectory) {
        log.info("Loading fonts from directory " + rootDirectory.getAbsolutePath());
        ObjectMap<String, File> files = FileUtil.loadWithIdentifiers(rootDirectory, file -> {
            return file.getName().endsWith(".font");
        });

        for (String key : files.keys()) {
            File fontFile = files.get(key);
            if (key.startsWith("styles.")) {
                continue;
            }

            try {
                Font font = new Font(key, this.renderer, new ZipFile(fontFile));
                this.fonts.put(key, font);

                log.info("Loaded font " + fontFile.getName() + " under key " + key);
            } catch (Exception e) {
                String message = "Could not load font at font file " + fontFile.getAbsolutePath();
                if (e instanceof IOException)
                    message += " (load error)";
                if (e instanceof BadAssetException)
                    message += " (bad asset: " + e.getMessage() + ")";
                if (e instanceof ZipException)
                    message += " (bad file)";

                log.error(message, e);
            }
        }

        log.info(fonts.size + " font(s) loaded.");
    }
}
