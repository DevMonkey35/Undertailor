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

package me.scarlet.undertailor.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles input tracking operations. Intended to be set as
 * the input processor of the system, done by feeding an
 * instance to the method
 * <code>Gdx.input.setInputProcessor(InputProcessor)</code>
 * ({@link Input#setInputProcessor(InputProcessor)}.
 * 
 * <p>Input tracking is a frame-by-frame operation,
 * therefore for currently pressed keys to update for a
 * given instance the {@link InputRetriever#update()} method
 * should be called.</p>
 * 
 * <p>Setup for usage of the class is simply by feeding the
 * instance to libGDX as the new input processor, then
 * updating it every frame.</p>
 * 
 * <pre>
 * public void create() {
 *     input = new InputRetriever();
 *     Gdx.input.setInputProcessor(input);
 * }
 * 
 * // ...
 * 
 * public void render() {
 *     input.update();
 *     // ...
 * }
 * </pre>
 * 
 * <p>Usage of the class involves querying the current
 * instance of {@link InputData} provided by the
 * {@link InputRetriever#getCurrentData()} method about
 * which keys are currently pressed, providing key codes
 * denoted by the {@link Keys} class.</p>
 * 
 * <pre>
 * InputRetriever input;
 * // ...
 * 
 * input.getPressData(Keys.A).isPressed();
 * </pre>
 * 
 * @see InputData
 * @see PressData
 * @see Keys
 */
public class InputRetriever implements InputProcessor {

    /**
     * Withholds all data relevant to input caught by its
     * owning {@link InputRetriever}.
     * 
     * @see PressData
     * @see Keys
     */
    public static class InputData {
        
        private long currentTick;
        private boolean isConsumed;
        private Map<Integer, PressData> pressData;
        
        private InputData(Map<Integer, PressData> pressData) {
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
    
    /**
     * Withholds input relevant to a single hardware key,
     * caught by the {@link InputRetriever} owning its
     * parent {@link InputData}.
     */
    public static class PressData {
        
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
            if(lastReleaseTime <= -1) {
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
            if(time <= 0)  {
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
            if(lastPressTime <= -1) {
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
            if(time <= 0)  {
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
            if(holdTime <= -1) {
                return 0;
            }
            
            if(!isPressed) {
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
    
    private long tick;
    private InputData currentData;
    private Map<Integer, PressData> pressData;
    
    public InputRetriever() {
        this.tick = 0;
        this.pressData = new HashMap<>();
        this.currentData = new InputData(pressData);
    }
    
    /**
     * Returns the current {@link InputData} tracked by this
     * {@link InputRetriever}.
     * 
     * @return some InputData
     */
    public InputData getCurrentData() {
        return currentData;
    }
    
    /**
     * Updates the state of this {@link InputRetriever} and
     * renews the underlying {@link InputData}.
     */
    public void update() {
        tick++;
        currentData.isConsumed = false;
        currentData.currentTick = tick;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>Internal method, called by the base libGDX
     * framework. Should not be called by the game
     * engine.</p>
     */
    @Override
    public boolean keyDown(int keycode) {
        if(!pressData.containsKey(keycode)) {
            pressData.put(keycode, new PressData(currentData));
        }
        
        pressData.get(keycode).down();
        return true;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>Internal method, called by the base libGDX
     * framework. Should not be called by the game
     * engine.</p>
     */
    @Override
    public boolean keyUp(int keycode) {
        if(!pressData.containsKey(keycode)) {
            pressData.put(keycode, new PressData(currentData));
        }
        
        pressData.get(keycode).up();
        return true;
    }
    
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean scrolled(int amount) { return false; }
}
