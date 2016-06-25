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

import me.scarlet.undertailor.engine.Identifiable;
import me.scarlet.undertailor.engine.Layerable;
import me.scarlet.undertailor.engine.overworld.OverworldController;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.engine.overworld.map.TilesetFactory.Tileset;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

/**
 * A layer of a set of images defined by a {@link Tileset}
 * placed in a grid.
 */
public class TileLayer implements Layerable, Renderable, Identifiable {

    static final Transform OVW_SIZE_TRANSFORM;

    static {
        OVW_SIZE_TRANSFORM = new Transform();
        OVW_SIZE_TRANSFORM.setScale(OverworldController.PIXELS_TO_METERS);
    }

    short id;
    short layer;
    int[] tiles;
    String name;
    Tilemap parent;
    boolean layerSet;

    TileLayer() {
        this.layerSet = false;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public short getLayer() {
        return this.layer;
    }

    @Override
    public void setLayer(short layer) {
        this.layer = layer;
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}

    // Intended to draw at overworld scale.
    // Ignores transform.
    // Origin bottomleft.
    @Override
    public void draw(float x, float y, Transform transform) {
        int tileY = parent.getHeight() - 1;
        int tileX = 0;

        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != 0) {
                Renderable tile = parent.getTile(tiles[i]);
                float drawX = (x + 10 + (20 * tileX)) * OverworldController.PIXELS_TO_METERS;
                float drawY = (y + 10 + (20 * tileY)) * OverworldController.PIXELS_TO_METERS;
                tile.draw(drawX, drawY, OVW_SIZE_TRANSFORM);
            }

            tileX++;
            if (tileX >= this.parent.width) {
                tileX = 0;
                tileY--;
            }
        }
    }
}
