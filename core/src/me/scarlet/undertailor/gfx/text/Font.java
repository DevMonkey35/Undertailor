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

import static me.scarlet.undertailor.util.ConfigUtil.checkExists;
import static me.scarlet.undertailor.util.ConfigUtil.checkValue;

import com.badlogic.gdx.utils.ObjectMap;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.exception.BadAssetException;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.spritesheet.PackagedSpriteSheetFactory;
import me.scarlet.undertailor.gfx.spritesheet.PackagedSpriteSheetFactory.PackagedSpriteSheet;
import me.scarlet.undertailor.gfx.spritesheet.Sprite;
import me.scarlet.undertailor.gfx.spritesheet.SpriteSheet;
import me.scarlet.undertailor.util.Pair;
import me.scarlet.undertailor.util.StreamUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A set of premade textures typically used to draw language
 * within the game.
 */
public class Font {

    static final Logger log = LoggerFactory.getLogger(Font.class);
    public static final String ENTRY_FONT_CONFIG = "font.json";

    private MultiRenderer renderer;
    private PackagedSpriteSheet sheet;

    private float lineSize;
    private float spaceLength;
    private String characterList;
    private Pair<Float> defaultLetterSpacing;
    private ObjectMap<Character, Pair<Float>> letterSpacing;

    public Font(String fontName, MultiRenderer renderer, ZipFile sourceFile)
        throws BadAssetException {
        PackagedSpriteSheetFactory sheetFactory =
            new PackagedSpriteSheetFactory("#fnt-" + fontName, renderer, sourceFile);
        this.sheet = sheetFactory.getResource();
        this.letterSpacing = new ObjectMap<>();
        this.renderer = renderer;

        InputStream configStream = null; // load the configuration json inside the archive for later reading
        try {
            ZipEntry configEntry = sourceFile.getEntry(ENTRY_FONT_CONFIG);
            if (configEntry == null)
                throw new BadAssetException("Font file does not contain font configuration");

            configStream = sourceFile.getInputStream(configEntry);
            BufferedReader reader = new BufferedReader(new InputStreamReader(configStream));
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setSource(() -> {
                return reader;
            }).build();

            this.loadConfig(loader.load());
        } catch (IOException e) {
            BadAssetException thrown = new BadAssetException();
            thrown.initCause(e);
            throw thrown;
        } finally {
            StreamUtil.closeQuietly(configStream);
        }
    }

    // ---------------- g/s core variables ----------------

    /**
     * Returns the {@link MultiRenderer} assigned to this
     * {@link Font}.
     */
    public MultiRenderer getRenderer() {
        return renderer;
    }

    // ---------------- g/s font variables ----------------

    /**
     * Returns the count of vertical units to preserve for a
     * single line.
     * 
     * @return the size of a line written in this font
     */
    public float getLineSize() {
        return this.lineSize;
    }

    /**
     * Returns the count of units to skip to represent a
     * space.
     */
    public float getSpaceLength() {
        return this.spaceLength;
    }

    /**
     * Returns a {@link Pair} object wrapping the letter
     * spacing for the provided character, with the first
     * integer providing the spacing from the left, and the
     * second from the right.
     * 
     * @param character the character to check
     * 
     * @return a Pair holding letter spacing for the
     *         character
     */
    public Pair<Float> getLetterSpacing(char character) {
        if (this.letterSpacing.containsKey(character)) {
            return this.letterSpacing.get(character);
        }

        return this.defaultLetterSpacing;
    }

    /**
     * Returns the list of characters supported by this
     * {@link Font}, in the order of appearance on its
     * {@link SpriteSheet} starting from the top-left,
     * left-to-right.
     * 
     * @return a String holding the supported characters for
     *         this Font
     */
    public String getCharacterList() {
        return this.characterList;
    }

    /**
     * Returns the {@link Sprite} assigned to the provided
     * character as denoted by this {@link Font}, or null if
     * the character is unsupported.
     * 
     * @param character the target character
     */
    public Sprite getCharacterSprite(char character) {
        return this.sheet.getSprite("" + character);
    }

    // ---------------- configuration loader ----------------

    private static Object[] KEY_VERSION = {"version"};

    private static Object[] KEY_SPACE_LENGTH = {"font", "spaceLength"};
    private static Object[] KEY_LINE_SIZE = {"font", "lineSize"};
    private static Object[] KEY_LETTER_SPACING_LEFT = {"font", "spaceL"};
    private static Object[] KEY_LETTER_SPACING_RIGHT = {"font", "spaceR"};

    private static Object[] KEY_META_LIST = {"sprites", "meta", null};

    // inside a meta block

    private static Object[] KEY_META_LETTER_SPACING_LEFT = {"spaceL"};
    private static Object[] KEY_META_LETTER_SPACING_RIGHT = {"spaceR"};


    /**
     * Internal method.
     * 
     * <p>Responsible for loading the font data.</p>
     * 
     * <p>Implementing configuration version 1.</p>
     */
    private void loadConfig(ConfigurationNode rootNode) throws BadAssetException {
        int version = rootNode.getNode(KEY_VERSION).getInt(-1);
        if (version != 1) {
            String message = version == -1
                ? "Cannot continue with an unknown Font configuration version"
                : "This Undertailor version does not support Font configuration version " + version;
            throw new UnsupportedOperationException(message);
        }

        // font.letterSpacingLeft
        // font.letterSpacingRight
        checkExists(rootNode.getNode(KEY_LETTER_SPACING_LEFT));
        checkExists(rootNode.getNode(KEY_LETTER_SPACING_RIGHT));
        this.defaultLetterSpacing =
            new Pair<>(checkValue(rootNode.getNode(KEY_LETTER_SPACING_LEFT).getFloat(0), value -> {
                if (value < 0) {
                    return "Cannot use negative letter spacing";
                }

                return null;
            }), checkValue(rootNode.getNode(KEY_LETTER_SPACING_RIGHT).getFloat(0), value -> {
                if (value < 0) {
                    return "Cannot use negative letter spacing";
                }

                return null;
            }));

        // font.spaceLength
        checkExists(rootNode.getNode(KEY_SPACE_LENGTH));
        this.spaceLength = checkValue(rootNode.getNode(KEY_SPACE_LENGTH).getFloat(-1), value -> {
            if (value < 0) {
                return "Unsupported space length value (cannot be < 0)";
            }

            return null;
        });

        // font.lineSize
        checkExists(rootNode.getNode(KEY_LINE_SIZE));
        this.lineSize = checkValue(rootNode.getNode(KEY_LINE_SIZE).getFloat(-1), value -> {
            if (value <= 0) {
                return "Unsupported line size value (cannot be < 0)";
            }

            return null;
        });

        // font.meta.?
        for (ConfigurationNode node : rootNode.getNode(KEY_META_LIST).getChildrenMap().values()) {
            char character = node.getKey().toString().charAt(0);

            Pair<Float> spacingPair = new Pair<>();
            spacingPair
                .setA(checkValue(node.getNode(KEY_META_LETTER_SPACING_LEFT).getFloat(0), value -> {
                    if (value < 0) {
                        return "Cannot use negative letter spacing";
                    }

                    return null;
                }));

            spacingPair
                .setB(checkValue(node.getNode(KEY_META_LETTER_SPACING_RIGHT).getFloat(0), value -> {
                    if (value < 0) {
                        return "Cannot use negative letter spacing";
                    }

                    return null;
                }));

            this.letterSpacing.put(character, spacingPair);
        }
    }
}
