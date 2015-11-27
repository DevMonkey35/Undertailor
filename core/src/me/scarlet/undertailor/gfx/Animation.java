package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.HashMap;
import java.util.Map;

public abstract class Animation<T extends KeyFrame> {
    
    public static final String DEFAULT_SPRITESET = "default";
    
    private boolean loop;
    private long startTime;
    private String currentSet;
    private Map<String, Sprite[]> spritesets;
    public Animation(long startTime, boolean loop) {
        this.startTime = startTime;
        this.loop = loop;
        this.spritesets = new HashMap<>();
        this.currentSet = DEFAULT_SPRITESET;
    }
    
    public boolean isLooping() {
        return this.loop;
    }
    
    public Sprite[] getCurrentSpriteSet() {
        return spritesets.get(currentSet);
    }
    
    public void setCurrentSpriteSet(String setName) {
        this.currentSet = setName;
    }
    
    public boolean hasSpriteSet(String setName) {
        return spritesets.containsKey(setName);
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public void drawCurrentFrame(Batch batch, long stateTime, float posX, float posY) {
        this.drawCurrentFrame(batch, stateTime, posX, posY, 1F);
    }
    
    public void drawCurrentFrame(Batch batch, long stateTime, float posX, float posY, float scale) {
        this.drawCurrentFrame(batch, stateTime, posX, posY, scale, 0F);
    }
    
    public abstract Map<Long, T> getFrames();
    public abstract T getFrame(long stateTime);
    public abstract void drawCurrentFrame(Batch batch, long stateTime, float posX, float posY, float scale, float rotation);
}
