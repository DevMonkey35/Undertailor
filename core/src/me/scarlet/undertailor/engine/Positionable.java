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

package me.scarlet.undertailor.engine;

import com.badlogic.gdx.math.Vector2;

/**
 * Skeleton implementation for classes that can hold a
 * position in which the former's origin point resides
 * across numerous frames.
 */
public interface Positionable {

    /**
     * Returns the current position of this
     * {@link Positionable}.
     * 
     * @return a {@link Vector2} representing the current
     *         position
     */
    Vector2 getPosition();

    /**
     * Sets the position of this {@link Positionable}.
     * 
     * @param x the x-coordinate of the new position
     * @param y the y-coordinate of the new position
     */
    void setPosition(float x, float y);

    /**
     * Returns the current height of this
     * {@link Positionable}.
     * 
     * <p>"Height," pertaining to the positive Y offset at
     * which this Positionable should be rendered at in
     * order to allow objects that seemingly move along the
     * vertical axis.</p>
     * 
     * @return the height of this Positionable
     */
    float getHeight();

    /**
     * Sets the current height of this {@link Positionable}.
     * 
     * @param height the new height of this Positionable
     */
    void setHeight(float height);

    /**
     * Sets the position of this {@link Positionable}.
     * 
     * <p>This does not set the provided {@link Vector2} as
     * the Positionable's new Vector2 object, instead simply
     * copies the coordinates from the former to the current
     * Vector2 instance held by this Positionable.</p>
     * 
     * @param vec the Vector2 object holding the new
     *        position
     */
    default void setPosition(Vector2 vec) {
        this.setPosition(vec.x, vec.y);
    };
}
