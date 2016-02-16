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

package me.scarlet.undertailor.util;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class InputRetriever implements InputProcessor {
    
    public static InputData currentData;
    
    public static class InputData {

        private long currentTick;
        private boolean isConsumed;
        private Map<Integer, PressData> pressData;
        
        public InputData(Map<Integer, PressData> pressData) {
            this.isConsumed = false;
            this.pressData = pressData;
        }
        
        public PressData getPressData(int keycode) {
            if(!pressData.containsKey(keycode)) {
                pressData.put(keycode, new PressData(this));
            }
            
            return pressData.get(keycode);
        }
        
        public boolean isConsumed() {
            return isConsumed;
        }
        
        public void consume() {
            this.isConsumed = true;
        }
    }
    
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
        
        public long getLastReleaseTime() {
            if(lastReleaseTime <= -1) {
                return 0;
            }
            
            return TimeUtils.timeSinceMillis(lastReleaseTime);
        }
        
        public boolean justReleased(long time) {
            if(time <= 0)  {
                return this.lastReleaseTick == parent.currentTick;
            }
            
            return this.lastReleaseTime >= 0 && this.getLastReleaseTime() < time;
        }
        
        public boolean justPressed(long time) {
            if(time <= 0)  {
                return this.lastPressTick == parent.currentTick;
            }
            
            return this.lastPressTime >= 0 && this.getLastPressTime() < time;
        }
        
        public long getLastPressTime() {
            if(lastPressTime <= -1) {
                return 0;
            }
            
            return TimeUtils.timeSinceMillis(lastPressTime);
        }
        
        public long getHoldTime() {
            if(holdTime <= -1) {
                return 0;
            }
            
            if(!isPressed) {
                return holdTime;
            }
            
            return TimeUtils.timeSinceMillis(holdTime);
        }
        
        public boolean isPressed() {
            return isPressed;
        }
        
        public void up() {
            this.isPressed = false;
            this.holdTime = TimeUtils.timeSinceMillis(this.holdTime);
            this.lastReleaseTick = parent.currentTick;
            this.lastReleaseTime = TimeUtils.millis();
        }
        
        public void down() {
            this.isPressed = true;
            this.holdTime = TimeUtils.millis();
            this.lastPressTick = parent.currentTick;
            this.lastPressTime = TimeUtils.millis();
        }
    }
    
    private long tick;
    private Map<Integer, PressData> pressData;
    
    public InputRetriever() {
        this.tick = 0;
        this.pressData = new HashMap<>();
        currentData = new InputData(pressData);
    }
    
    public InputData getCurrentData() {
        return currentData;
    }
    
    public void update() {
        tick++;
        currentData.isConsumed = false;
        currentData.currentTick = tick;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if(!pressData.containsKey(keycode)) {
            pressData.put(keycode, new PressData(currentData));
        }
        
        pressData.get(keycode).down();
        return true;
    }
    
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
