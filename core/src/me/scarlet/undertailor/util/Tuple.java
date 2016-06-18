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
 * Class holding a pair of two objects with potentially
 * differring types.
 *
 * @param <A> the type of object A
 * @param <B> the type of object B
 */
public class Tuple<A, B> {

    private A a;
    private B b;

    public Tuple() {}

    public Tuple(A a, B b) {
        this.setItems(a, b);
    }

    /**
     * Returns object A.
     * 
     * @return A
     */
    public A getA() {
        return this.a;
    }

    /**
     * Sets object A.
     * 
     * @param a A
     */
    public void setA(A a) {
        this.a = a;
    }

    /**
     * Returns object B.
     * 
     * @return B
     */
    public B getB() {
        return this.b;
    }

    /**
     * Sets object B.
     * 
     * @param b B
     */
    public void setB(B b) {
        this.b = b;
    }

    /**
     * Sets object A and B.
     * 
     * @param a A
     * @param b B
     */
    public void setItems(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Clears both objects in this {@link Tuple} and sets
     * them to null.
     */
    public void clearItems() {
        this.a = null;
        this.b = null;
    }

    @Override
    public String toString() {
        return "[" + a + ", " + b + "]";
    }
}
