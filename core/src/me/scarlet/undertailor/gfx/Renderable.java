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
     * Draws this {@link Renderable} object.
     * 
     * <p>Convenience method for drawing objects who
     * implement the Renderable interface but do not factor
     * a given position or {@link Transform} in its
     * rendering.</p>
     */
    public default void render() {
        this.render(0, 0);
    }

    /**
     * Draws this {@link Renderable} object at the given
     * position.
     * 
     * @param pos the position to draw at
     */
    public default void render(Vector2 pos) {
        this.render(pos.x, pos.y);
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
    public void render(float x, float y);

}
