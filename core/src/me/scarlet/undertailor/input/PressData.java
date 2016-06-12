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

import com.badlogic.gdx.utils.TimeUtils;

/**
 * Withholds input relevant to a single hardware key,
 * caught by the {@link InputRetriever} owning its
 * parent {@link InputData}.
 */
public class PressData {

    private long holdTime;
    private InputData parent;
    private boolean isPressed;
    private long lastPressTick;
    private long lastPressTime;
    private long lastReleaseTick;
    private long lastReleaseTime;

    public PressData(InputData parent) {
        this.isPressed = false;
        this.parent = parent;
        this.holdTime = -1;
        this.lastPressTime = -1;
        this.lastReleaseTime = -1;
        this.lastReleaseTick = -1;
        this.lastPressTick = -1;
    }

    /**
     * Returns how long its been since the key
     * represented by this {@link PressData} instance
     * was released, in milliseconds.
     * 
     * @return how long it been since this key was
     *         released in ms
     */
    public long getLastReleaseTime() {
        if (lastReleaseTime <= -1) {
            return 0;
        }

        return TimeUtils.timeSinceMillis(lastReleaseTime);
    }

    /**
     * Returns whether or not the key represented by
     * this {@link PressData} was released within the
     * timeframe of <code>time</code> milliseconds.
     * 
     * @param time the timeframe to check within
     * 
     * @return whether or not the key has been released
     *         within the given timeframe
     */
    public boolean justReleased(long time) {
        if (time <= 0) {
            return this.lastReleaseTick == parent.currentTick;
        }

        return this.lastReleaseTime >= 0 && this.getLastReleaseTime() < time;
    }

    /**
     * Returns how long its been since the key
     * represented by this {@link PressData} instance
     * was pressed, in milliseconds.
     * 
     * @return how long it been since this key was
     *         pressed in ms
     */
    public long getLastPressTime() {
        if (lastPressTime <= -1) {
            return 0;
        }

        return TimeUtils.timeSinceMillis(lastPressTime);
    }

    /**
     * Returns whether or not the key represented by
     * this {@link PressData} was pressed within the
     * timeframe of <code>time</code> milliseconds.
     * 
     * @param time the timeframe to check within
     * 
     * @return whether or not the key has been pressed
     *         within the given timeframe
     */
    public boolean justPressed(long time) {
        if (time <= 0) {
            return this.lastPressTick == parent.currentTick;
        }

        return this.lastPressTime >= 0 && this.getLastPressTime() < time;
    }

    /**
     * Returns how long the key represented by this
     * {@link PressData} has been held down for if it is
     * currently pressed. If not pressed, the length of
     * time it was previously held for is returned
     * instead, or 0 if the latter had not been set
     * either.
     * 
     * @return how long this key has been pressed for,
     *         or how long it <strong>had</strong> been
     *         pressed for, or 0 if it was never pressed
     *         before
     */
    public long getHoldTime() {
        if (holdTime <= -1) {
            return 0;
        }

        if (!isPressed) {
            return holdTime;
        }

        return TimeUtils.timeSinceMillis(holdTime);
    }

    /**
     * Returns whether or not the key represented by
     * this {@link PressData} is currently pressed.
     * 
     * @return if this key is currently pressed
     */
    public boolean isPressed() {
        return isPressed;
    }

    /**
     * Internal method.
     * 
     * <p>Updates the data of this {@link PressData} in
     * response to its assigned key being released.</p>
     */
    void up() {
        this.isPressed = false;
        this.holdTime = TimeUtils.timeSinceMillis(this.holdTime);
        this.lastReleaseTick = parent.currentTick;
        this.lastReleaseTime = TimeUtils.millis();
    }

    /**
     * Internal method.
     * 
     * <p>Updates the data of this {@link PressData} in
     * response to its assigned key being pressed.</p>
     */
    void down() {
        this.isPressed = true;
        this.holdTime = TimeUtils.millis();
        this.lastPressTick = parent.currentTick;
        this.lastPressTime = TimeUtils.millis();
    }
}
