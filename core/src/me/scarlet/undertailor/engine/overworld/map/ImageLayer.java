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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import me.scarlet.undertailor.engine.Identifiable;
import me.scarlet.undertailor.engine.Layerable;
import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

public class ImageLayer implements Layerable, Renderable, Positionable, Identifiable {

    short id;
    short layer;
    Texture image;
    float threshold;
    Vector2 position;
    Vector2 proxyPosition;
    boolean layerSet;
    MultiRenderer renderer;

    ImageLayer() {
        this.threshold = 0F;
        this.layerSet = false;
        this.position = new Vector2(0, 0);
        this.proxyPosition = new Vector2(0, 0);
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
    public void setLayer(short layer) {}

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public void setHeight(float height) {}

    @Override
    public Vector2 getPosition() {
        this.proxyPosition.set(this.position.x, this.position.y);
        this.proxyPosition.y +=
            (this.image.getHeight() * threshold);
        return this.proxyPosition;
    }

    @Override
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}

    // Ignores position.
    // Ignores transform.
    // Intended to only render on the overworld.
    @Override
    public void render(float x, float y, Transform transform) {
        renderer.draw(image, this.position.x, this.position.y);
    }
}
