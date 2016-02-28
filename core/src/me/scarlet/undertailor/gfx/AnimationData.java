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

package me.scarlet.undertailor.gfx;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.lua.lib.TimeLib;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;

public class AnimationData {
    
    private double pauseTime;
    private double startTime;
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
        return this.pauseTime < 0 && this.startTime > 0;
    }
    
    public void play() {
        this.startTime = TimeLib.getCurrentRuntime();
    }
    
    public void stop() {
        this.startTime = -1;
        this.pauseTime = -1;
    }
    
    public void pause() {
        if(this.startTime > 0 && pauseTime <= -1) {
            this.pauseTime = TimeLib.getCurrentRuntime();
        }
    }
    
    public void resume() {
        if (this.startTime <= -1) {
            this.play();
        } else if(this.pauseTime > 0) {
            this.startTime = startTime + (TimeLib.getCurrentRuntime() - this.pauseTime);
            this.pauseTime = -1;
        }
    }
    
    public double getRuntime() {
        if(startTime <= -1) {
            return 0;
        } else {
            double time = TimeLib.getCurrentRuntime();
            
            if(pauseTime > 0) {
                return (time - startTime) - (time - pauseTime);
            }
            
            return (time - startTime);
        }
    }
    
    public void setRuntime(double runtime) {
        double time = TimeLib.getCurrentRuntime();
        
        if(this.pauseTime > 0) {
            this.startTime = this.pauseTime - runtime;
        } else {
            if(this.startTime <= -1) {
                this.pauseTime = time;
                this.startTime = pauseTime - runtime;
            } else {
                this.startTime = time - runtime;
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
        /*if(rotation == 0) {
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
        }*/
        
        this.anim.drawFrame(this.getRuntime(), this.isLooping(), spriteset, posX, posY, offset.x, offset.y, scale, rotation);
    }
}
