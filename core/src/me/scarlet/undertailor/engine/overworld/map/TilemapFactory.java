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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.engine.overworld.map.ObjectLayer.ShapeData;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.engine.overworld.map.TilesetFactory.Tileset;
import me.scarlet.undertailor.exception.BadAssetException;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.resource.Resource;
import me.scarlet.undertailor.resource.ResourceFactory;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * {@link ResourceFactory} implementation for generating
 * {@link Tilemap} instances.
 */
public class TilemapFactory extends ResourceFactory<Disposable, Tilemap> {

    /**
     * The name of the layer used to store definition
     * objects. Ignored during collision generation.
     */
    public static final char OBJ_DEF_LAYER_PREFIX = '#';

    static final Logger log = LoggerFactory.getLogger(TilemapFactory.class);

    /**
     * A grid-based map.
     */
    public static class Tilemap extends Resource<Disposable> {

        int width;
        int height;
        float spaceX;
        float spaceY;
        Array<TileLayer> tileLayers;
        Array<ImageLayer> imageLayers;
        ObjectMap<String, String> entrypoints;
        ObjectMap<String, ObjectLayer> objects;
        OrderedMap<Integer, Tileset> tilesets;

        Tilemap() {
            this.objects = new ObjectMap<>();
            this.entrypoints = new ObjectMap<>();
            this.tileLayers = new Array<>(true, 16);
            this.imageLayers = new Array<>(true, 16);
            this.tilesets = new OrderedMap<>();
        }

        @Override
        public boolean isLoaded() {
            if (super.isLoaded()) {
                for (Tileset tileset : tilesets.values()) {
                    if (!tileset.isLoaded()) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        /**
         * Returns the total horizontal space this
         * {@link Tilemap} occupies rendering all layers at
         * normal scale.
         * 
         * @return this Tilemap's occupying width
         */
        public float getOccupiedWidth() {
            return width * 20F;
        }

        /**
         * Returns the total vertical space this
         * {@link Tilemap} occupies rendering all layers at
         * normal scale.
         * 
         * @return this Tilemap's occupying height
         */
        public float getOccupiedHeight() {
            return height * 20F;
        }

        /**
         * Returns the width of this {@link Tilemap}, in
         * tiles.
         * 
         * @return the width of this Tilemap
         */
        public int getWidth() {
            return this.width;
        }

        /**
         * Returns the height of this {@link Tilemap}, in
         * tiles.
         * 
         * @return the width of this Tilemap
         */
        public int getHeight() {
            return this.height;
        }

        /**
         * Returns the {@link TileLayer}s held by this
         * {@link Tilemap}.
         * 
         * <p>The returned list orders the layers by their
         * layer index. This does not resolve to
         * <code>list.get(i)</code> returning the TileLayer
         * at layer i, rather it resolves to the entires
         * being in render order. That is, should we have
         * TileLayers whose layer indices are 8, 9, 12, 4
         * respectively, the returned list will have them
         * ordered as 4, 8, 9, 12.<p>
         * 
         * @return the TileLayers held by this Tilemap
         */
        public Array<TileLayer> getTileLayers() {
            if (!this.isLoaded()) {
                return null;
            }

            return this.tileLayers;
        }

        /**
         * Returns the {@link ImageLayer}s held by this
         * {@link Tilemap}. Sorting behavior is similar to
         * that of {@link #getTileLayers()}.
         * 
         * @return the ImageLayers held by this TileMap
         */
        public Array<ImageLayer> getImageLayers() {
            if (!this.isLoaded()) {
                return null;
            }

            return this.imageLayers;
        }

        /**
         * Returns the {@link TileLayer} at the specified
         * layer index.
         * 
         * @param layer the target layer
         * 
         * @return the TileLayer at the specified layer
         *         index, or null if no layer is found.
         */
        public TileLayer getLayerAt(short layer) {
            if (!this.isLoaded()) {
                return null;
            }

            for (TileLayer tileLayer : this.tileLayers) {
                if (tileLayer.getLayer() == layer) {
                    return tileLayer;
                }
            }

            return null;
        }

        /**
         * Returns a {@link Collection} of all
         * {@link ObjectLayer}s held by this {@link Tilemap}
         * .
         * 
         * @return a Collection of this Tilemap's
         *         ObjectLayers
         */
        public ObjectMap.Values<ObjectLayer> getObjectLayers() {
            if (!this.isLoaded()) {
                return null;
            }

            return this.objects.values();
        }

        /**
         * Returns the {@link ObjectLayer} of the provided
         * name, held by this {@link Tilemap}.
         * 
         * @param name the name of the ObjectLayer
         * 
         * @return the ObjectLayer of the given name
         */
        public ObjectLayer getObjectLayer(String name) {
            if (!this.isLoaded()) {
                return null;
            }

            return this.objects.get(name);
        }

        /**
         * Returns the {@link ShapeData} definition
         * associated with the provided name.
         * 
         * @param shapeName the name of the defined
         *        ShapeData
         * 
         * @return the defined ShapeData, or null if not
         *         found
         */
        public ShapeData getDefinedShape(String defLayerName, String shapeName) {
            if (!this.isLoaded()) {
                return null;
            }

            String layerName = defLayerName.charAt(0) == OBJ_DEF_LAYER_PREFIX ? defLayerName
                : OBJ_DEF_LAYER_PREFIX + defLayerName;

            if (this.getObjectLayer(layerName) != null) {
                return this.getObjectLayer(layerName).getShape(shapeName);
            }

            return null;
        }


        /**
         * Returns the point definition associated with the
         * provided name as a {@link Vector2}.
         * 
         * @param pointName the name of the defined
         *        ShapeData
         * 
         * @return the defined point as a Vector2, or null
         *         if not found
         */
        public Vector2 getDefinedPoint(String defLayerName, String pointName) {
            if (!this.isLoaded()) {
                return null;
            }

            String layerName = defLayerName.charAt(0) == OBJ_DEF_LAYER_PREFIX ? defLayerName
                : OBJ_DEF_LAYER_PREFIX + defLayerName;
            if (this.getObjectLayer(layerName) != null) {
                return this.getObjectLayer(layerName).getPoint(pointName);
            }

            return null;
        }

        /**
         * Returns the name of the spawnpoint of the
         * provided entrypoint, in <code>layer:point</code>
         * format. The point name is to be in the form of
         * <code>layer:point</code>.
         * 
         * @param entrypointName the name of the entrypoint,
         *        in <code>layer:point</code> format
         * 
         * @return the entrypoint's target spawnpoint, in
         *         <code>layer:point</code> format, or null
         *         if the entrypoint didn't exist
         */
        public String getEntrypointSpawn(String entrypointName) {
            return this.entrypoints.get(entrypointName);
        }

        /**
         * Returns the highest layer index achieved by any
         * of this {@link Tilemap}'s {@link TileLayer}s.
         * 
         * <p>Provided this tilemap has layers with the
         * layer indices 0, 3, 19, 4, in no particular
         * order, this method would return 19.</p>
         * 
         * @return the highest layer index in this Tilemap
         */
        public short getHighestLayerIndex() {
            if (!this.isLoaded()) {
                return -1;
            }

            return this.tileLayers.get(this.tileLayers.size - 1).getLayer();
        }

        /**
         * Returns the {@link Renderable} tile at the
         * specified global index.
         * 
         * @param gid the GID of the tile
         * 
         * @return the Renderable tile, or null if none was
         *         found
         */
        public Tile getTile(int gid) {
            if (!this.isLoaded()) {
                return null;
            }

            for(int i = gid; i >= 0; i--) {
                if(tilesets.containsKey(i)) {
                    return tilesets.get(i).getTile(gid - i);
                }
            }

            return null;
        }
    }

    private File tmxFile;
    private TilemapReader reader;

    public TilemapFactory(File tmxFile, TilesetManager tilesets, MultiRenderer renderer) {
        this.reader = new TilemapReader(tilesets, renderer);
        this.tmxFile = tmxFile;
    }

    @Override
    protected CompletableFuture<Disposable> loadDisposable() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                reader.read(tmxFile, this.getResourceReference());
            } catch (Exception e) {
                String message = "Failed to load tilemap " + this.tmxFile.getAbsolutePath() + " ("
                    + e.getMessage() + ")";
                BadAssetException thrown = new BadAssetException(message);
                thrown.initCause(e);

                Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), thrown);
            }

            return null;
        });
    }

    @Override
    protected Tilemap newResource() {
        return new Tilemap();
    }

    @Override
    public boolean isDisposable(Disposable disposable) {
        return true;
    }
}
