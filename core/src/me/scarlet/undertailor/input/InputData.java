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

package me.scarlet.undertailor.input;

import com.badlogic.gdx.Input.Keys;

import java.util.Map;

/**
 * Withholds all data relevant to input caught by its
 * owning {@link InputRetriever}.
 * 
 * @see PressData
 * @see Keys
 */
public class InputData {

    long currentTick;
    boolean isConsumed;
    private Map<Integer, PressData> pressData;

    InputData(Map<Integer, PressData> pressData) {
        this.isConsumed = false;
        this.pressData = pressData;
    }

    /**
     * Returns the {@link PressData} stored under the
     * provided keycode (see {@link Keys}).
     * 
     * <p>If the key was never touched, a new
     * {@link PressData} instance is generated and
     * stored with default values, all of which denote a
     * state where the key has never been interacted
     * with.</p>
     * 
     * @param keycode the keycode to search under
     * 
     * @return a PressData instance
     */
    public PressData getPressData(int keycode) {
        if (!pressData.containsKey(keycode)) {
            pressData.put(keycode, new PressData(this));
        }

        return pressData.get(keycode);
    }

    /**
     * Returns whether or not this {@link InputData} has
     * been "consumed" for the current frame; that is,
     * has been flagged as used by a processing object
     * and therefore should not be used by anything else
     * that follows processing priority and order.</p>
     * 
     * @return whether the InputData has been flagged
     *         for the current frame
     */
    public boolean isConsumed() {
        return isConsumed;
    }

    /**
     * Marks this {@link InputData} as consumed for the
     * current frame.
     * 
     * <p>If already consumed, this method is
     * essentially a no-op.</p>
     */
    public void consume() {
        this.isConsumed = true; // not a no-op cuz it still tries to change something
    }
}
