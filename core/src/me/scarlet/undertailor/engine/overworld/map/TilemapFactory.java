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

import com.badlogic.gdx.utils.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.engine.overworld.map.TilesetFactory.Tileset;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.resource.Resource;
import me.scarlet.undertailor.resource.ResourceFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * {@link ResourceFactory} implementation for generating
 * {@link Tilemap} instances.
 */
public class TilemapFactory extends ResourceFactory<Disposable, Tilemap> {

    static TilemapReader READER;
    static final Logger log = LoggerFactory.getLogger(TilemapFactory.class);

    /**
     * A grid-based map.
     */
    public static class Tilemap extends Resource<Disposable> {

        int width;
        int height;
        float spaceX;
        float spaceY;
        List<TileLayer> layers;
        Map<String, ObjectLayer> objects;
        TreeMap<Integer, Tileset> tilesets;

        Tilemap() {
            this.objects = new HashMap<>();
            this.layers = new ArrayList<>();
            this.tilesets = new TreeMap<>(Integer::compare);
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
        public List<TileLayer> getTileLayers() {
            return this.layers;
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
            for (TileLayer tileLayer : this.layers) {
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
        public Collection<ObjectLayer> getObjectLayers() {
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
            return this.objects.get(name);
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
            return this.layers.get(this.layers.size() - 1).getLayer();
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
        public Renderable getTile(int gid) {
            Entry<Integer, Tileset> entry = tilesets.lowerEntry(gid + 1);
            return entry.getValue().getTile(gid - entry.getKey());
        }
    }

    private File tmxFile;

    public TilemapFactory(File tmxFile, TilesetManager tilesets) {
        if (TilemapFactory.READER == null) {
            TilemapFactory.READER = new TilemapReader(tilesets);
        }

        this.tmxFile = tmxFile;
    }

    @Override
    protected Disposable newDisposable() {
        return null;
    }

    @Override
    protected Tilemap newResource() {
        try {
            return TilemapFactory.READER.read(tmxFile);
        } catch (Exception e) {
            String message = "Failed to load tilemap";
            if (e instanceof SAXException)
                message += " (malformed XML)";

            log.error(message, e);
        }

        return null;
    }

    @Override
    public boolean isDisposable(Disposable disposable) {
        return true;
    }
}
