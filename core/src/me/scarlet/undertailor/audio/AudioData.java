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

public interface AudioData {

    /**
     * Returns the volume of the owning {@link Audio}.
     * 
     * @return the volume of the Audio
     */
    float getVolume();

    /**
     * Sets the volume of the owning {@link Audio}.
     * 
     * @param volume the new volume of the Audio
     */
    void setVolume(float volume);

    /**
     * Returns the pitch of the owning {@link Audio}.
     * 
     * @return the pitch of the Audio
     */
    float getPitch();

    /**
     * Sets the pitch of the owning {@link Audio}.
     * 
     * @param pitch the new pitch of the Audio
     */
    void setPitch(float pitch);

    /**
     * Returns the pan of the owning {@link Audio}.
     * 
     * @return the pan of the Audio
     */
    float getPan();

    /**
     * Sets the pan of the owning {@link Audio}.
     * 
     * @param pan the new pan of the Audio
     */
    void setPan(float pan);

    /**
     * Returns whether or not the owning {@link Audio} is
     * set to loop playback.
     * 
     * @return if the Audio is looping
     */
    boolean isLooping();

    /**
     * Sets whether or not the owning {@link Audio} should
     * loop playback.
     * 
     * @param flag if the Audio should loop
     */
    void setLooping(boolean flag);

    /**
     * Returns whether or not the owning {@link Audio} is
     * playing.
     * 
     * @return if the Audio is playing
     */
    boolean isPlaying();

    /**
     * Stops the output of the owning {@link Audio}.
     */
    void stop();

}
