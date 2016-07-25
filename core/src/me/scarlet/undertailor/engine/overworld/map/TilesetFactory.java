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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.AssetManager;
import me.scarlet.undertailor.engine.overworld.map.TilesetFactory.Tileset;
import me.scarlet.undertailor.engine.overworld.map.TilesetReader.TilesetMeta;
import me.scarlet.undertailor.exception.BadAssetException;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.animation.Animation;
import me.scarlet.undertailor.gfx.animation.FrameAnimation;
import me.scarlet.undertailor.gfx.animation.FrameAnimation.KeyFrame;
import me.scarlet.undertailor.gfx.spritesheet.Sprite;
import me.scarlet.undertailor.resource.Resource;
import me.scarlet.undertailor.resource.ResourceFactory;
import me.scarlet.undertailor.util.Tuple;
import me.scarlet.undertailor.util.Wrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * {@link ResourceFactory} implementation for generating
 * {@link TilesetFactory.Tileset} instances.
 */
public class TilesetFactory extends ResourceFactory<Texture, Tileset> {

    static final Logger log = LoggerFactory.getLogger(TilesetFactory.class);

    /**
     * {@link Resource} implementation for interfacing with
     * Tiled-generated tilesets (.tsx files).
     */
    public static class Tileset extends Resource<Texture> {

        private TilesetFactory factory;
        private Map<Integer, Tile> tiles;

        public Tileset(TilesetFactory factory) {
            this.factory = factory;
            this.tiles = new HashMap<>();
        }

        /**
         * Returns the tile at the provided local index.
         * 
         * @param index the local index of the tile
         * 
         * @return the {@link Renderable} for the tile
         */
        public Tile getTile(int index) {
            if (!this.isLoaded()) {
                return null;
            }

            if (!this.tiles.containsKey(index)) {
                Renderable renderable = factory.animatedTiles.get(index);
                if (renderable == null) {
                    renderable = factory.tiles.get(index);
                }

                if (renderable instanceof FrameAnimation) {
                    renderable = ((FrameAnimation) renderable).clone();
                    ((FrameAnimation) renderable).getFrames().forEach(frame -> {
                        if (frame.getFrame() instanceof Sprite) {
                            ((Sprite) frame.getFrame()).sourceObject = this;
                        }
                    });
                }

                if (renderable instanceof Sprite) {
                    renderable = ((Sprite) renderable).clone();
                    ((Sprite) renderable).sourceObject = this;
                }

                Tile tile = new Tile();
                tile.renderable = renderable;
                tile.width = factory.meta.getTileWidth();
                tile.height = factory.meta.getTileHeight();
                this.tiles.put(index, tile);
            }

            return this.tiles.get(index);
        }
    }

    // ---------------- object ----------------

    private File tsxFile;
    private TilesetMeta meta;
    private File textureFile;
    private TilesetReader reader;
    private MultiRenderer renderer;
    private boolean reading;

    private List<Sprite> tiles;
    private Map<Integer, Animation> animatedTiles;

    public TilesetFactory(MultiRenderer renderer, File textureFile) {
        this.tiles = new ArrayList<>();
        this.reader = new TilesetReader();
        this.animatedTiles = new HashMap<>();

        this.textureFile = textureFile;
        this.renderer = renderer;
        this.reading = false;

        this.tsxFile = new File(textureFile.getParentFile(),
            textureFile.getName().substring(0, textureFile.getName().length() - 4) + ".tsx");
    }

    // ---------------- abstract method implementation ----------------

    @Override
    protected CompletableFuture<Texture> loadDisposable() {
        Wrapper<Texture> texture = new Wrapper<>();
        AssetManager.addTask(() -> {
            synchronized (texture) {
                texture.set(new Texture(Gdx.files.absolute(this.textureFile.getAbsolutePath())));
                texture.notifyAll();
            }
        });

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (tsxFile.exists()) {
                    if(!reading) {
                        this.reading = true;
                        this.meta = reader.read(tsxFile);
                        this.reading = false;
                    }
                }

                if (texture.get() == null) {
                    synchronized (texture) {
                        while (texture.get() == null) {
                            try {
                                texture.wait();
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                }

                this.loadTexture(texture.get());

                return texture.get();
            } catch (Exception e) {
                RuntimeException thrown = new RuntimeException();
                thrown.initCause(e);
                throw thrown;
            }
        });
    }

    @Override
    protected void handleLoadError(Throwable err) {
        log.error("Failed to load tileset at tileset file " + this.textureFile.getAbsolutePath(),
            err);
    }

    @Override
    protected Tileset newResource() {
        return new Tileset(this);
    }

    @Override
    protected void onDispose() {
        this.tiles.clear();
        this.animatedTiles.clear();
    }

    @Override
    public boolean isDisposable(Texture disposable) {
        return true;
    }

    @Override
    protected boolean disposeOnGameThread() {
        return true;
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Responsible for loading the {@link Texture},
     * turning them into usable {@link Renderable} objects
     * for generated {@link Tileset}s to reference.</p>
     */
    private void loadTexture(Texture texture) throws BadAssetException {
        if (texture.getWidth() % meta.getTileWidth() != 0 || texture.getHeight() % meta.getTileHeight() != 0) {
            throw new BadAssetException(
                "invalid texture size, sprites must all be " + meta.getTileWidth() + "x" + meta.getTileHeight() + "px in size");
        }

        int width = texture.getWidth() / meta.getTileWidth();
        int height = texture.getHeight() / meta.getTileHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.tiles
                    .add(
                        new Sprite(
                            this.renderer, new TextureRegion(texture, x * meta.getTileWidth(),
                                y * meta.getTileHeight(), meta.getTileWidth(), meta.getTileHeight()), null));
            }
        }

        if (this.meta != null) {
            this.meta.getAnimations().forEach((id, animations) -> {
                KeyFrame[] frames = new KeyFrame[animations.size() + 1];

                long currentTime = 0;
                for (int i = 0; i < animations.size(); i++) {
                    Tuple<Integer, Long> tupleFrame = animations.get(i);
                    if (i != 0) {
                        currentTime += animations.get(i - 1).getB();
                    }

                    frames[i] = new KeyFrame(currentTime, this.tiles.get(tupleFrame.getA()));
                }

                Tuple<Integer, Long> last = animations.get(animations.size() - 1);
                frames[frames.length - 1] = new KeyFrame(currentTime + last.getB(),
                    this.tiles.get(animations.get(0).getA()));

                FrameAnimation animation = new FrameAnimation(frames);
                animation.setLooping(true);
                animation.play();

                this.animatedTiles.put(id, animation);
            });
        }
    }
}
