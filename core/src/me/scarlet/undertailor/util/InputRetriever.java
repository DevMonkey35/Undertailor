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
        
        /**
         * Returns whether or not the given key was pressed in the last frame,
         * but is released in the current frame.
         */
        public boolean justReleased(int keycode) {
            if(lastData == null) {
                return false;
            }
            
            PressData oldData = lastData.getPressData(keycode);
            PressData newData = this.getPressData(keycode);
            boolean oldState = oldData == null ? false : oldData.isPressed();
            boolean newState = newData == null ? false : newData.isPressed();
            
            return oldState != newState && newState == false;
        }
        
        /**
         * Returns whether or not the given key was released in the last frame,
         * but is pressed in the current frame.
         */
        public boolean justPressed(int keycode) {
            if(lastData == null) {
                return false;
            }
            
            PressData oldData = lastData.getPressData(keycode);
            PressData newData = this.getPressData(keycode);
            boolean oldState = oldData == null ? false : oldData.isPressed();
            boolean newState = newData == null ? false : newData.isPressed();
            
            return oldState != newState && newState == true;
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
            
            return TimeUtils.timeSinceMillis(holdTime);
        }
        
        public boolean isPressed() {
            return isPressed;
        }
        
        public void up() {
            this.isPressed = false;
            this.holdTime = -1;
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
    }
    
    public InputData getCurrentData() {
        lastData = currentData;
        currentData = new InputData(new HashMap<>(pressData));
        return currentData;
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
