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

/**
 * Class holding a pair of two objects of the same type.
 *
 * @param <T> the type of object to hold two of
 */
public class Pair<T> {

    private T item1;
    private T item2;

    public Pair() {
        this(null, null);
    }

    public Pair(T item1, T item2) {
        this.item1 = item1;
        this.item2 = item2;
    }
    
    /**
     * Returns the first item stored by this {@link Pair}.
     * 
     * @return this Pair's first item
     */
    public T getFirst() {
        return this.item1;
    }
    
    public void setFirst(T first) {
        this.item1 = first;
    }

    /**
     * Returns the second item stored by this {@link Pair}.
     * 
     * @return this Pair's second item
     */
    public T getSecond() {
        return this.item2;
    }
    
    public void setSecond(T second) {
        this.item2 = second;
    }
    
    public void setItems(T item1, T item2) {
        this.item1 = item1;
        this.item2 = item2;
    }
    
    public void clearItems() {
        this.item1 = null;
        this.item2 = null;
    }
}
