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
 * A wrapped float value bounded between an inclusive higher
 * and lower boundary.
 */
public class BoundedFloat {

    private float lowBound, highBound;
    private float value;

    public BoundedFloat(float lowBound, float highBound, float initValue) {
        this.lowBound = lowBound;
        this.highBound = highBound;
        this.set(initValue);
    }

    /**
     * Returns the current value of the underlying float
     * value.
     * 
     * @return the value of this float
     */
    public float get() {
        return this.value;
    }

    /**
     * Sets the current value of the underlying float value,
     * bounded inclusively by the higher and lower bounds.
     * 
     * @param value the new value
     */
    public void set(float value) {
        if (value > highBound) {
            this.value = highBound;
        } else if (value < lowBound) {
            this.value = lowBound;
        } else {
            this.value = value;
        }
    }
}
