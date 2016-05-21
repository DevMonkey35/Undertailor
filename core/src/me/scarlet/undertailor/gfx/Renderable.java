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

/**
 * Skeleton implementation of something that can be drawn.
 */
public interface Renderable {

    /**
     * @see #draw(float, float, float, float, boolean,
     *      boolean, float)
     */
    public default void draw(float x, float y) {
        this.draw(x, y, 1F);
    }

    /**
     * @see #draw(float, float, float, float, boolean,
     *      boolean, float)
     */
    public default void draw(float x, float y, float scale) {
        this.draw(x, y, scale, scale);
    }

    /**
     * @see #draw(float, float, float, float, boolean,
     *      boolean, float)
     */
    public default void draw(float x, float y, float scaleX, float scaleY) {
        this.draw(x, y, scaleX, scaleY, false, false);
    }

    /**
     * @see #draw(float, float, float, float, boolean,
     *      boolean, float)
     */
    public default void draw(float x, float y, float scaleX, float scaleY, boolean flipX,
        boolean flipY) {
        this.draw(x, y, scaleX, scaleY, flipX, flipY, 0F);
    }

    /**
     * Draws this {@link Renderable} with the given
     * parameters.
     * 
     * @param x the x position of the sprite's anchor point
     * @param y the y position of the sprite's anchor point
     * @param scaleX the scaling of the sprite horizontally
     * @param scaleY the scaling of the sprite vertically
     * @param rotation the rotation of the sprite, anchored
     *        at the anchor point defined by the origin
     *        offset values
     */
    public void draw(float x, float y, float scaleX, float scaleY, boolean flipX, boolean flipY,
        float rotation);

}
