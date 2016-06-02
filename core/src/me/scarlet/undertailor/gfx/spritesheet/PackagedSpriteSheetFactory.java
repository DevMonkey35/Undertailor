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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.json.JSONConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.exception.BadAssetException;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.spritesheet.PackagedSpriteSheetFactory.PackagedSpriteSheet;
import me.scarlet.undertailor.gfx.spritesheet.Sprite.SpriteMeta;
import me.scarlet.undertailor.resource.Resource;
import me.scarlet.undertailor.resource.ResourceFactory;
import me.scarlet.undertailor.util.StreamUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.zip.ZipFile;

/**
 * {@link ResourceFactory} implementation for generating
 * {@link PackagedSpriteSheetFactory.PackagedSpriteSheet}
 * instances.
 */
public class PackagedSpriteSheetFactory extends ResourceFactory<Texture, PackagedSpriteSheet>
    implements SpriteSheetFactory {

    /**
     * {@link Resource} implementation for interfacing with
     * {@link Texture} images split into separate regions to
     * form {@link Sprite}s.
     */
    public static class PackagedSpriteSheet extends Resource<Texture> implements SpriteSheet {

        private PackagedSpriteSheetFactory factory;
        private Map<Integer, WeakReference<Sprite>> sprites;

        public PackagedSpriteSheet(PackagedSpriteSheetFactory factory) {
            this.factory = factory;
            this.sprites = new WeakHashMap<>();
        }

        // ---------------- abstract method implementation ----------------

        @Override
        public String getSheetName() {
            return factory.name;
        }

        /*
         * Why?
         * 
         * You could just return factory.sprites.get(index)!
         * 
         * ----
         * 
         * The issue there is that the sprites stored by the
         * factory are not owned by any SpriteSheet.
         * Ownership is determined by that single
         * sourceSheet variable. The reason that variable
         * exists is to keep the SpriteSheet from becoming
         * weakly reachable while the Sprite object is in
         * use, as if it becomes weakly reachable then the
         * ResourceHandler determines it as garbage,
         * disposing the Texture too early.
         */
        @Override
        public Sprite getSprite(int index) {
            if (sprites.get(index) == null || sprites.get(index).get() == null) {
                Sprite original = factory.sprites.get(index);
                Sprite newSprite =
                    new Sprite(factory.renderer, original.getTextureRegion(), original.getMeta());
                newSprite.sourceSheet = this;
                sprites.put(index, new WeakReference<>(newSprite));
            }

            return sprites.get(index).get();
        }

        @Override
        public Collection<Sprite> getSprites() {
            Set<Sprite> sprites = new HashSet<Sprite>();
            for (int i = 0; i < factory.sprites.size(); i++) {
                sprites.add(this.getSprite(i));
            }

            return sprites;
        }

        @Override
        public int getSpriteCount() {
            return 0;
        }
    }

    static Logger log = LoggerFactory.getLogger(PackagedSpriteSheetFactory.class);

    public static final String ENTRY_SPRITESHEET = "spritesheet.png";
    public static final String ENTRY_SHEETCONFIG = "spritesheet.json";

    private String name;
    private ZipFile sourceFile;
    private MultiRenderer renderer;
    private List<Sprite> sprites;
    private ConfigurationNode sheetConfig;

    public PackagedSpriteSheetFactory(String name, MultiRenderer renderer, ZipFile sourceFile) {
        this.name = name;
        this.renderer = renderer;
        this.sourceFile = sourceFile;

        this.sprites = new ArrayList<>();

        InputStream configStream = null; // load the configuration json inside the archive for later reading
        try {
            configStream = sourceFile.getInputStream(sourceFile.getEntry(ENTRY_SHEETCONFIG));
            BufferedReader reader = new BufferedReader(new InputStreamReader(configStream));
            JSONConfigurationLoader loader = JSONConfigurationLoader.builder().setSource(() -> {
                return reader;
            }).build();

            sheetConfig = loader.load();
        } catch (IOException e) {
            log.error("Failed to load packaged spritesheet data", e);
        } finally {
            StreamUtil.closeQuietly(configStream);
        }
    }

    // ---------------- abstract method implementation ----------------

    @Override
    protected Texture newDisposable() {
        InputStream textureStream = null;
        try { // load the spritesheet here
            textureStream = sourceFile.getInputStream(sourceFile.getEntry(ENTRY_SPRITESHEET));
            Gdx2DPixmap readMap = new Gdx2DPixmap(textureStream, Gdx2DPixmap.GDX2D_FORMAT_RGBA8888);
            Texture texture = new Texture(new Pixmap(readMap));
            readMap.dispose();

            this.loadSheet(texture);

            return texture;
        } catch (Exception e) {
            log.error("Failed to load packaged spritesheet", e);
        } finally {
            StreamUtil.closeQuietly(textureStream);
        }

        return null;
    }

    @Override
    protected PackagedSpriteSheet newResource() {
        return new PackagedSpriteSheet(this);
    }

    @Override
    protected void onDispose() {
        this.sprites.clear();
    }

    @Override
    protected boolean disposeOnGameThread() {
        return true;
    }

    @Override
    public boolean isDisposable(Texture disposable) {
        return true;
    }

    // ---------------- configuration loader ----------------

    private static Object[] KEY_VERSION = {"version"};

    private static Object[] KEY_GRID_SIZE_X = {"sprites", "grid", "sizeX"};
    private static Object[] KEY_GRID_SIZE_Y = {"sprites", "grid", "sizeY"};

    private static Object[] KEY_META_LIST = {"sprites", "meta", null};
    // inside a meta block
    private static Object[] KEY_META_ORIGINX = {"originX"};
    private static Object[] KEY_META_ORIGINY = {"originY"};
    private static Object[] KEY_META_WRAPX = {"wrapX"};
    private static Object[] KEY_META_WRAPY = {"wrapY"};
    private static Object[] KEY_META_OFFX = {"offX"};
    private static Object[] KEY_META_OFFY = {"offY"};

    /**
     * Internal method.
     * 
     * <p>Responsible for loading the texture, converting
     * them into usable {@link Sprite}s.</p>
     * 
     * <p>Implementing configuration version 0.</p>
     */
    private void loadSheet(Texture texture) throws BadAssetException { // implementing version 0
        int version = this.sheetConfig.getNode(KEY_VERSION).getInt(-1);
        if (version != 0) {
            String message = version == -1
                ? "Cannot continue with an unknown PackagedSpriteSheet configuration version"
                : "This Undertailor version does not support PackagedSpriteSheet configuration version "
                    + version;
            throw new UnsupportedOperationException(message);
        }

        int gridX = this.sheetConfig.getNode(KEY_GRID_SIZE_X).getInt(0);
        int gridY = this.sheetConfig.getNode(KEY_GRID_SIZE_Y).getInt(0);

        if (texture.getWidth() % gridX != 0 || texture.getHeight() % gridY != 0) {
            throw new BadAssetException("Incorrectly configured spritesheet with bad grid size");
        }

        int spriteWidth = texture.getWidth() / gridX;
        int spriteHeight = texture.getHeight() / gridY;

        for (int y = 0; y < gridY; y++) {
            for (int x = 0; x < gridX; x++) {
                int pos = (y * gridX) + x;
                int wrapX = 0;
                int wrapY = 0;

                SpriteMeta meta = null;
                KEY_META_LIST[2] = pos;
                ConfigurationNode metaNode = this.sheetConfig.getNode(KEY_META_LIST);
                if (!metaNode.isVirtual()) {
                    meta = new SpriteMeta(metaNode.getNode(KEY_META_ORIGINX).getFloat(0),
                        metaNode.getNode(KEY_META_ORIGINY).getFloat(0),
                        metaNode.getNode(KEY_META_WRAPX).getInt(0),
                        metaNode.getNode(KEY_META_WRAPY).getInt(0),
                        metaNode.getNode(KEY_META_OFFX).getInt(0),
                        metaNode.getNode(KEY_META_OFFY).getInt(0));

                    wrapX = meta.wrapX;
                    wrapY = meta.wrapY;
                }

                Sprite added = new Sprite(this.renderer, new TextureRegion(texture, x * spriteWidth,
                    y * spriteHeight, spriteWidth - wrapX, spriteHeight - wrapY), meta);
                this.sprites.add(added);
            }
        }
    }
}
