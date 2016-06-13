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

package me.scarlet.undertailor.audio;

public interface AudioPlayable<T> {

    /**
     * Plays an instance of this {@link AudioPlayable} and
     * loops it.
     * 
     * @param volume the volume to play at
     * @param pitch the pitch to play at
     * @param pan the panning value to play at
     * 
     * @return the return data for this AudioPlayable, if
     *         any
     */
    T loop(float volume, float pitch, float pan);

    /**
     * Plays an instance of this {@link AudioPlayable}.
     * 
     * @param volume the volume to play at
     * @param pitch the pitch to play at
     * @param pan the panning value to play at
     * 
     * @return the return data for this AudioPlayable, if
     *         any
     */
    T play(float volume, float pitch, float pan);

    /**
     * Stops all running instances of this
     * {@link AudioPlayable}.
     */
    void stop();

    /**
     * Plays an instance of this {@link AudioPlayable} and
     * loops it with default parameters (volume = 1, pitch =
     * 1, pan = 0).
     * 
     * @return the return data for this AudioPlayable, if
     *         any
     */
    default T loop() {
        return loop(1.0F, 1.0F, 0.0F);
    }

    /**
     * Plays an instance of this {@link AudioPlayable} with
     * default parameters (volume = 1, pitch = 1, pan = 0).
     * 
     * @return the return data for this AudioPlayable, if
     *         any
     */
    default T play() {
        return loop(1.0F, 1.0F, 0.0F);
    }
}
