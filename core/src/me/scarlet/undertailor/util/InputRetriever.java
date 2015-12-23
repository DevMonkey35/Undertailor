package me.scarlet.undertailor.util;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class InputRetriever implements InputProcessor {
    
    public static InputData lastData;
    public static InputData currentData;
    
    public static class InputData {
        
        private boolean isConsumed;
        private Map<Integer, PressData> pressData;
        
        public InputData(Map<Integer, PressData> pressData) {
            this.isConsumed = false;
            this.pressData = pressData;
        }
        
        public PressData getPressData(int keycode) {
            if(pressData.containsKey(keycode)) {
                return pressData.get(keycode);
            }
            
            return PressData.BLANK;
        }
        
        public boolean isConsumed() {
            return isConsumed;
        }
        
        public void consume() {
            this.isConsumed = true;
        }
    }
    
    public static class PressData {
        
        public static final PressData BLANK;
        
        static {
            BLANK = new PressData();
        }
        
        private long holdTime;
        private boolean isPressed;
        private long lastPressTime;
        private long lastReleaseTime;
        
        public PressData() {
            this.isPressed = false;
            this.holdTime = -1;
            this.lastPressTime = -1;
            this.lastReleaseTime = -1;
        }
        
        public long getLastReleaseTime() {
            if(lastReleaseTime <= -1) {
                return 0;
            }
            
            return TimeUtils.timeSinceMillis(lastReleaseTime);
        }
        
        public boolean justReleased(float time) {
            long msTime = (long) (1000.0 * time);
            return this.lastReleaseTime >= 0 && this.getLastReleaseTime() < msTime;
        }
        
        public boolean justPressed(float time) {
            long msTime = (long) (1000.0 * time);
            return this.lastPressTime >= 0 && this.getLastPressTime() < msTime;
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
            this.lastReleaseTime = TimeUtils.millis();
        }
        
        public void down() {
            this.isPressed = true;
            this.holdTime = TimeUtils.millis();
            this.lastPressTime = TimeUtils.millis();
        }
    }
    
    private Map<Integer, PressData> pressData;
    
    public InputRetriever() {
        this.pressData = new HashMap<>();
        lastData = new InputData(pressData);
        currentData = new InputData(pressData);
    }
    
    public InputData getCurrentData() {
        return currentData;
    }
    
    public void update() {
        lastData = currentData;
        currentData = new InputData(pressData);
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if(!pressData.containsKey(keycode)) {
            pressData.put(keycode, new PressData());
        }
        
        pressData.get(keycode).down();
        return true;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        if(!pressData.containsKey(keycode)) {
            pressData.put(keycode, new PressData());
        }
        
        pressData.get(keycode).up();
        return true;
    }
    
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(int amount) { return false; }
}
