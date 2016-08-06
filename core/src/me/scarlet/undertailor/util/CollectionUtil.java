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

package me.scarlet.undertailor.util;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

/**
 * Utilities for the collection classes within
 * {@link com.badlogic.gdx.utils}.
 */
public class CollectionUtil {

    /**
     * Returns the first key that was registered into the
     * provided {@link OrderedMap}.
     * 
     * @param map the OrderedMap to query
     * 
     * @return the first key in the provided OrderedMap, or
     *         null if the map was empty
     */
    public static <T> T firstKey(OrderedMap<T, ?> map) {
        Array<T> array = map.orderedKeys();
        if (array.size > 0) {
            return array.get(0);
        }

        return null;
    }

    /**
     * Returns the last key that was registered into the
     * provided {@link OrderedMap}.
     * 
     * @param map the OrderedMap to query
     * 
     * @return the last key in the provided OrderedMap, or
     *         null if the map was empty
     */
    public static <T> T lastKey(OrderedMap<T, ?> map) {
        Array<T> array = map.orderedKeys();
        if (array.size > 0) {
            return array.get(array.size - 1);
        }

        return null;
    }

}
