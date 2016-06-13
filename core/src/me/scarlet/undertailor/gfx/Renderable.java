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

package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.math.Vector2;

/**
 * Skeleton implementation of something that can be drawn.
 */
public interface Renderable {

    /**
     * Returns the {@link Transform} object used to render
     * this {@link Renderable} with.
     * 
     * @return this Renderable's transform
     */
    public Transform getTransform();

    /**
     * Sets a new {@link Transform} object used to render
     * this {@link Renderable} with.
     * 
     * <p>If null is provided, the {@link Renderable} will
     * assume a Transform object with default values.</p>
     * 
     * @param transform the new Transform object
     */
    public void setTransform(Transform transform);

    /**
     * Draws this {@link Renderable} object.
     * 
     * <p>Convenience method for drawing objects who
     * implement the Renderable interface but do not factor
     * a given position or {@link Transform} in its
     * rendering.</p>
     */
    public default void draw() {
        this.draw(0, 0);
    }

    /**
     * Draws this {@link Renderable} object at the given
     * position.
     * 
     * @param pos the position to draw at
     */
    public default void draw(Vector2 pos) {
        this.draw(pos.x, pos.y);
    }

    /**
     * Draws this {@link Renderable} object at the given
     * position.
     * 
     * @param x the x coordinate of the position to draw at
     * @param y the y coordinate of the position to draw at
     */
    public default void draw(float x, float y) {
        this.draw(x, y, this.getTransform());
    }

    /**
     * Draws this {@link Renderable} object at the given
     * position and the provided {@link Transform}.
     * 
     * @param pos the position to draw at
     * @param transform the transformations to apply to the
     *        drawn object
     */
    public default void draw(Vector2 pos, Transform transform) {
        this.draw(pos.x, pos.y, transform);
    }

    /**
     * Draws this {@link Renderable} object at the given
     * position and the provided {@link Transform}.
     * 
     * @param x the x coordinate of the position to draw at
     * @param y the y coordinate of the position to draw at
     * @param transform the transformations to apply to the
     *        drawn object
     */
    public void draw(float x, float y, Transform transform);
}
