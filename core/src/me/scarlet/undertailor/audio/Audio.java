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

public interface Audio<T> {
    
    public String getAudioName();
    public float getAffectedVolume();
    public float getPosition();
    public void setPosition(float position);
    public float getVolume();
    public void setVolume(float volume);
    public float getPan();
    public void setPan(float pan);
    public float getPitch();
    public void setPitch(float pitch);
    public boolean isLooping();
    public void setLoopPoint(float loopPoint);
    public boolean isPlaying(T id);
    public boolean isPaused(T id);
    public T play(float volume, float pan, float pitch);
    public T play();
    public void pause(T id);
    public void stop(T id);
    
}
