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

import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.ScriptManager;
import me.scarlet.undertailor.lua.impl.LuaTextStyle;
import me.scarlet.undertailor.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Manager class for {@link TextStyle} instances.
 */
public class TextStyleManager {

    static final Logger log = LoggerFactory.getLogger(TextStyleManager.class);
    public static final char INTERNAL_STYLE_PREFIX = '#';

    private ScriptManager scripts;
    private ObjectMap<String, TextStyle> styles;

    public TextStyleManager(ScriptManager scripts) {
        this.scripts = scripts;
        this.styles = new ObjectMap<>();
    }

    /**
     * Returns the {@link TextStyle} stored under the given
     * key.
     * 
     * <p>For styles registered through
     * {@link #registerStyle(String, TextStyle)}, the key is
     * prefixed with a hash symbol ('#') to prevent
     * conflicts with styles loaded from Lua files in the
     * loading directory.</p>
     * 
     * @param key the key to search under
     * 
     * @return the associated TextStyle, or null if not
     *         found
     */
    public TextStyle getStyle(String key) {
        return styles.get(key);
    }

    /**
     * Registers an internal {@link TextStyle} to be
     * retrievable through this {@link TextStyleManager}.
     * 
     * <p>TextStyles registered through this method will
     * have their keys prefixed with a hash symbol
     * ('#').</p>
     * 
     * @param key the key to register the style under
     * @param style the style to register
     */
    public void registerStyle(String key, TextStyle style) {
        this.styles.put(INTERNAL_STYLE_PREFIX + key, style);
    }

    /**
     * Loads {@link TextStyle} Lua implementations found
     * within the given root directory and its subfolders
     * into this {@link TextStyleManager}.
     * 
     * @param rootDirectory the root directory to search in
     */
    public void loadStyles(File rootDirectory) {
        ObjectMap<String, File> files = FileUtil.loadWithIdentifiers(rootDirectory, file -> {
            return file.getName().endsWith(".lua");
        });

        files.keys().forEach(key -> {
            File scriptFile = files.get(key);
            try {
                this.styles.put(key, new LuaTextStyle(scripts, scriptFile, key));
            } catch (Exception e) {
                String message = "Could not load style at script file " + scriptFile.getAbsolutePath();
                if(e instanceof FileNotFoundException) message += " (file not found)";
                if(e instanceof LuaScriptException) message += " (implementation error)";
                log.error(message, e);
            }
        });

        log.info(this.styles.size + " style(s) loaded.");
    }
}
