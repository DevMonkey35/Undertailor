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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
        private ObjectMap<String, Sprite> sprites;
        private Array<Sprite> spriteColl;

        public PackagedSpriteSheet(PackagedSpriteSheetFactory factory) {
            this.factory = factory;
            this.sprites = new ObjectMap<>();
            this.spriteColl = null;
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
        public Sprite getSprite(String index) {
            if (!sprites.containsKey(index)) {
                Sprite newSprite = factory.sprites.get(index).clone();
                newSprite.sourceObject = this;
                sprites.put(index, newSprite);
            }

            return sprites.get(index);
        }

        @Override
        public Array<Sprite> getSprites() {
            if (this.spriteColl == null) {
                this.spriteColl = new Array<>(true, 16);
            }

            this.spriteColl.clear();
            factory.sprites.keys().forEach(key -> {
                this.spriteColl.add(this.getSprite(key));
            });

            return this.spriteColl;
        }

        @Override
        public int getSpriteCount() {
            return factory.sprites.size;
        }
    }

    static Logger log = LoggerFactory.getLogger(PackagedSpriteSheetFactory.class);

    public static final String ENTRY_SPRITESHEET = "spritesheet.png";
    public static final String ENTRY_SHEETCONFIG = "spritesheet.json";

    private String name;
    private ZipFile sourceFile;
    private MultiRenderer renderer;
    private ObjectMap<String, Sprite> sprites;
    private ConfigurationNode sheetConfig;

    public PackagedSpriteSheetFactory(String name, MultiRenderer renderer, ZipFile sourceFile) {
        this.name = name;
        this.renderer = renderer;
        this.sourceFile = sourceFile;

        this.sprites = new ObjectMap<>();

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
    protected CompletableFuture<Texture> loadDisposable() {
        InputStream textureStream = null;
        try { // load the spritesheet here
            textureStream = sourceFile.getInputStream(sourceFile.getEntry(ENTRY_SPRITESHEET));
            Gdx2DPixmap readMap = new Gdx2DPixmap(textureStream, Gdx2DPixmap.GDX2D_FORMAT_RGBA8888);
            Texture texture = new Texture(new Pixmap(readMap));
            readMap.dispose();

            this.loadSheet(texture);

            return CompletableFuture.completedFuture(texture);
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

    private static Object[] KEY_META_LIST = {"sprites", "meta"};

    // inside a meta block

    private static Object[] KEY_META_POSX = {"posX"};
    private static Object[] KEY_META_POSY = {"posY"};
    private static Object[] KEY_META_SIZEX = {"sizeX"};
    private static Object[] KEY_META_SIZEY = {"sizeY"};
    private static Object[] KEY_META_OFFX = {"offX"};
    private static Object[] KEY_META_OFFY = {"offY"};
    private static Object[] KEY_META_ORIGINX = {"originX"};
    private static Object[] KEY_META_ORIGINY = {"originY"};

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
        if (version != 1) {
            String message = version == -1
                ? "Cannot continue with an unknown PackagedSpriteSheet configuration version"
                : "This Undertailor version does not support PackagedSpriteSheet configuration version "
                    + version;
            throw new UnsupportedOperationException(message);
        }

        // Read default values for the two things that can be defaulted.
        int defOffX = 0;
        int defOffY = 0;
        float defOriginX = 0F;
        float defOriginY = 0F;

        defOffX = this.sheetConfig.getNode("sprites", "offX").getInt(0);
        defOffY = this.sheetConfig.getNode("sprites", "offY").getInt(0);
        defOriginX = this.sheetConfig.getNode("sprites", "originX").getFloat(0F);
        defOriginY = this.sheetConfig.getNode("sprites", "originY").getFloat(0F);

        // Read each individual sprite.
        Map<Object, ? extends ConfigurationNode> metaNodes =
            this.sheetConfig.getNode(KEY_META_LIST).getChildrenMap();
        for (ConfigurationNode meta : metaNodes.values()) {
            String spriteName = meta.getKey().toString();

            SpriteMeta metadata = new SpriteMeta();

            int posX = meta.getNode(KEY_META_POSX).getInt(0);
            int posY = meta.getNode(KEY_META_POSY).getInt(0);
            int sizeX = meta.getNode(KEY_META_SIZEX).getInt(0);
            int sizeY = meta.getNode(KEY_META_SIZEY).getInt(0);
            metadata.offX = meta.getNode(KEY_META_OFFX).getInt(defOffX);
            metadata.offY = meta.getNode(KEY_META_OFFY).getInt(defOffY);
            metadata.originX = meta.getNode(KEY_META_ORIGINX).getFloat(defOriginX);
            metadata.originY = meta.getNode(KEY_META_ORIGINY).getFloat(defOriginY);

            Sprite added = new Sprite(this.renderer,
                new TextureRegion(texture, posX, posY, sizeX, sizeY), metadata);
            this.sprites.put(spriteName, added);
        }
    }
}
