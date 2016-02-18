package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;

public class AnimationData {
    
    private long pauseTime;
    private long startTime;
    private Vector2 offset;
    private boolean looping;
    private String spriteset;
    private Animation<KeyFrame> anim;
    
    @SuppressWarnings("unchecked")
    public AnimationData(AnimationSetWrapper wrapper, Animation<? extends KeyFrame> animation) {
        wrapper.getReference(this);
        this.anim = (Animation<KeyFrame>) animation;
        this.looping = animation.isLooping();
        this.startTime = -1;
        this.pauseTime = -1;
        this.offset = new Vector2(0, 0);
        this.spriteset = AnimationSet.DEFAULT_SPRITESET;
    }
    
    public Animation<KeyFrame> getReferenceAnimation() {
        return this.anim;
    }
    
    public Vector2 getOffset() {
        return this.offset;
    }
    
    public void setOffset(float x, float y) {
        this.offset.set(x, y);
    }
    
    public String getSpriteSetName() {
        return this.spriteset;
    }
    
    public void setSpriteSetName(String spriteset) {
        this.spriteset = spriteset;
    }
    
    public boolean isPlaying() {
        return this.pauseTime > 0 && this.startTime > 0;
    }
    
    public void play() {
        this.startTime = TimeUtils.millis();
    }
    
    public void stop() {
        this.startTime = -1;
        this.pauseTime = -1;
    }
    
    public void pause() {
        if(this.startTime > 0 && pauseTime <= -1) {
            this.pauseTime = TimeUtils.millis();
        }
    }
    
    public void resume() {
        if (this.startTime <= -1) {
            this.play();
        } else if(this.pauseTime > 0) {
            this.startTime = startTime + TimeUtils.timeSinceMillis(this.pauseTime);
            this.pauseTime = -1;
        }
    }
    
    public long getRuntime() {
        if(startTime <= -1) {
            return 0;
        } else {
            if(pauseTime > 0) {
                return TimeUtils.timeSinceMillis(startTime) - TimeUtils.timeSinceMillis(pauseTime);
            }
            
            return TimeUtils.timeSinceMillis(startTime);
        }
    }
    
    public void setRuntime(long runtime) {
        if(this.pauseTime > 0) {
            this.startTime = this.pauseTime - runtime;
        } else {
            if(this.startTime <= -1) {
                this.pauseTime = TimeUtils.millis();
                this.startTime = pauseTime - runtime;
            } else {
                this.startTime = TimeUtils.millis() - runtime;
            }
        }
    }
    
    public boolean isLooping() {
        return this.looping;
    }
    
    public void setLooping(boolean flag) {
        this.looping = flag;
    }
    
    public void drawCurrentFrame(float posX, float posY) {
        this.drawCurrentFrame(posX, posY, 1F);
    }
    
    public void drawCurrentFrame(float posX, float posY, float scale) {
        this.drawCurrentFrame(posX, posY, scale, 0F);
    }
    
    public void drawCurrentFrame(float posX, float posY, float scale, float rotation) {
        // KeyFrame frame = this.anim.getFrame(this.getRuntime(), this.looping);
        if(rotation == 0) {
            posX += offset.x;
            posY += offset.y;
        } else if(rotation == 180) {
            posX -= offset.x;
            posY -= offset.y;
        } else if(offset.x != 0 || offset.y != 0) {
            double offPosX = posX + offset.x;
            double offPosY = posY + offset.y;
            double a = offPosX - posX;
            double b = offPosY - posY;
            
            double distance = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
            double currentAngle = Math.atan((b) / (a));
            float radRotation = (float) Math.toRadians(rotation);
            posX += new Float(distance * Math.cos(radRotation + currentAngle));
            posY += new Float(distance * Math.sin(radRotation + currentAngle));
        }
        
        this.anim.drawFrame(this.getRuntime(), this.isLooping(), spriteset, posX, posY, scale, rotation);
    }
}
