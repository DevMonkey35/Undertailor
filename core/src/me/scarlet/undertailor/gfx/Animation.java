package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.utils.Disposable;

import java.util.Map;

public abstract class Animation<T extends KeyFrame> implements Disposable {
    
    public static final String DEFAULT_SPRITESET = "default";
    
    private String name;
    private boolean loop;
    private long startTime;
    protected AnimationSet animSet;
    public Animation(String name, long startTime, boolean loop) {
        this.startTime = startTime;
        this.animSet = null;
        this.loop = loop;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isLooping() {
        return this.loop;
    }
    
    public void stop() {
        this.startTime = -1;
    }
    
    public AnimationSet getParentSet() {
        return animSet;
    }
    
    @Override
    public void dispose() {} // nothing
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void start(long startTime) {
        this.startTime = startTime;
    }
    
    public void drawCurrentFrame(float posX, float posY) {
        this.drawCurrentFrame(posX, posY, 1F);
    }
    
    public void drawCurrentFrame(float posX, float posY, float scale) {
        this.drawCurrentFrame(posX, posY, scale, 0F);
    }
    
    public abstract Map<Long, T> getFrames();
    public abstract T getFrame(long stateTime);
    public abstract void drawCurrentFrame(float posX, float posY, float scale, float rotation);
}
