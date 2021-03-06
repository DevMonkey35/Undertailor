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

/**
 * Skeleton implementation for classes that can set to
 * render on several different layers.
 */
public interface Layerable {

    /** The default layer, <code>0</code>, for {@link Layerable}s. */
    public static final short DEFAULT_LAYER = 0;

    /**
     * Returns the layer this {@link Layerable} is set to
     * render on.
     * 
     * <p>The default layer is the value defined by
     * {@link Layerable#DEFAULT_LAYER}.</p>
     * 
     * @return the layer of this Layerable
     */
    short getLayer();

    /**
     * Set the layer this {@link Layerable} should render
     * on.
     * 
     * @param layer the layer to render this Layerable on
     */
    void setLayer(short layer);
}
