/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.audio;

/**
 * Skeleton for any implementations of things that make
 * sounds.
 * 
 * @param <T> the type of the identifier used with this
 *        Audio implementation
 */
public interface Audio<T> {
    
    /**
     * Returns the name of this audio.
     * 
     * @return a name
     */
    String getAudioName();
    
    /**
     * Returns the final volume of this audio.
     * 
     * <p>The "final" volume is the level at which this audio is actually played
     * at when factoring in other parent values like its group volume and the
     * program's master volume.</p>
     * 
     * @return the final volume of this audio
     */
    float getAffectedVolume();
    
    /**
     * Returns the current position at which this audio is playing at.
     * 
     * @return the current position of this audio, in seconds
     */
    float getPosition();
    
    /**
     * Sets the current position of this audio.
     * 
     * @param position the position of this audio to skip to, in seconds
     */
    void setPosition(float position);
    
    /**
     * Returns the local volume of this audio.
     * 
     * <p>"Local" volume pertains to the value at which this audio would play at
     * should other parenting values, like group volume and master volume, not
     * be considered.</p>
     * 
     * @return the local volume of this audio
     */
    float getVolume();
    
    /**
     * Sets the local volume of this audio.
     * 
     * @param volume the local volume of this audio
     * 
     * @see #getVolume()
     */
    void setVolume(float volume);
    
    /**
     * Returns the panning value of this audio.
     * 
     * <p>The panning value determines how this audio is played in terms of
     * whether its more on the left side or the right side. The value is bounded
     * between <code>-1.0</code> and </code>1.0</code>, negative resolving to
     * the left and positive resolving in the right. <code>0.0</code> is the
     * default, and resolves to centered.</p>
     * 
     * @return the panning value of this audio
     */
    float getPan();
    
    /**
     * Sets the panning value of the audio.
     * 
     * @param pan the panning value of the audio
     */
    void setPan(float pan);
    
    /**
     * Returns the pitch value of this audio.
     * 
     * @return the pitch value of this audio
     */
    float getPitch();
    
    /**
     * Sets the pitch value of this audio.
     * 
     * <p>The value will be bounded between <code>0.5</code> and
     * <code>2.0</code> as per OpenAL limitations.</p>
     * 
     * @param pitch the pitch value of this audio
     */
    void setPitch(float pitch);
    
    /**
     * Returns whether or not this audio is set to be looping.
     * 
     * @return if this audio is looping
     */
    boolean isLooping();
    
    /**
     * Sets the looping point of this audio.
     * 
     * <p>The looping point is the position to return to once the audio has
     * finished playing.</p>
     * 
     * @param loopPoint the loop point
     */
    void setLoopPoint(float loopPoint);
    
    /**
     * Returns whether or not this audio is currently playing.
     * 
     * @param id the id of the audio instance, if required
     * 
     * @return if the audio is playing
     */
    boolean isPlaying(T id);
    
    /**
     * Returns whether or not this audio is currently paused.
     * 
     * @param id the id of the audio instance, if required
     * 
     * @return if the audio is playing
     */
    boolean isPaused(T id);
    
    /**
     * Sets the parameters for this audio and play it.
     * 
     * @param volume the volume to play this audio at
     * @param pan the panning value to play this audio with
     * @param pitch the pitch to play this audio at
     *            
     * @return the id of the newly playing audio instance, if used
     */
    T play(float volume, float pan, float pitch);
    
    /**
     * Plays this audio using its currently set parameters.
     * 
     * @return the id of the newly playing audio instance, if used
     */
    T play();
    
    /**
     * Pauses the given instance of this audio.
     * 
     * <p>Typical implementations will allow pausing a specific instance of the
     * audio through the use of the providable identifier, and will also allow
     * passing null in order to pause all instances of this audio.</p>
     * 
     * @param id the identifier of the specific audio instance to pause, or null
     *            to pause all instances of this audio
     */
    void pause(T id);
    
    /**
     * Stopss the given instance of this audio.
     * 
     * <p>Typical implementations will allow stopping a specific instance of the
     * audio through the use of the providable identifier, and will also allow
     * passing null in order to stop all instances of this audio.</p>
     * 
     * @param id the identifier of the specific audio instance to stop, or null
     *            to stop all instances of this audio
     */
    void stop(T id);
    
}
