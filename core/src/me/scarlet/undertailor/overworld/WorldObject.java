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

package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.collision.BoundingRectangle;
import me.scarlet.undertailor.collision.Collider;
import me.scarlet.undertailor.gfx.Animation;
import me.scarlet.undertailor.gfx.KeyFrame;
import me.scarlet.undertailor.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.Layerable;
import me.scarlet.undertailor.util.MapUtil;
import me.scarlet.undertailor.util.Renderable;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;

import java.util.Map;
import java.util.Map.Entry;

public abstract class WorldObject implements Collider, Layerable, Renderable {

    public static final Color BOX_COLOR;
    public static final Color BOX_COLOR_INACTIVE;
    public static boolean renderBoxes = true;
    
    static {
        BOX_COLOR = Color.RED;
        BOX_COLOR.a = 1.0F;
        
        BOX_COLOR_INACTIVE = Color.YELLOW;
        BOX_COLOR_INACTIVE.a = 1.0F;
    }
    
    private int z;
    private float scale;
    private boolean isSolid;
    private boolean persists;
    private boolean isVisible;
    private boolean canCollide;
    private boolean focusCollide;
    private Animation<?> animation;
    private AnimationSetWrapper animSet;
    private Vector2 velocity;
    private Vector2 position;
    private float rotation;
    private float height;
    
    private BoundingRectangle boundingBox;
    
    protected long id;
    protected WorldRoom room;
    
    public WorldObject() {
        this.boundingBox = new BoundingRectangle();
        this.velocity = new Vector2();
        this.position = new Vector2();
        this.focusCollide = false;
        this.canCollide = true;
        this.isVisible = true;
        this.animation = null;
        this.isSolid = true;
        this.animSet = null;
        this.rotation = 0F;
        this.room = null;
        this.height = 0F;
        this.scale = 1F;
        this.z = 0;
    }
    
    @Override
    public int getZ() {
        return z;
    }
    
    @Override
    public void setZ(int z) {
        this.z = z;
    }
    
    @Override
    public int getPriority() {
        return 1;
    }
    
    public long getId() {
        return id;
    }
    
    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.scale = scale < 0F ? 0F : scale;
        this.boundingBox.setScale(scale);
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        this.height = height;
    }
    
    public boolean isPersisting() {
        return persists;
    }
    
    public void setPersisting(boolean flag) {
        this.persists = flag;
    }
    
    @Override
    public boolean isSolid() {
        return isSolid;
    }
    
    public void setSolid(boolean flag) {
        this.isSolid = flag;
    }
    
    @Override
    public boolean focusCollide() {
        return focusCollide;
    }
    
    public void setFocusCollide(boolean flag) {
        this.focusCollide = flag;
    }
    
    @Override
    public boolean canCollide() {
        return canCollide;
    }
    
    public void setCanCollide(boolean flag) {
        this.canCollide = flag;
    }
    
    public WorldRoom getRoom() {
        return room;
    }
    
    public void removeFromRoom() {
        room.removeObject(id);
    }
    
    public boolean isVisible() {
        return isVisible();
    }
    
    public void setVisible(boolean flag) {
        this.isVisible = flag;
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
        boundingBox.setPosition(x, y);
    }
    
    public Animation<?> getCurrentAnimation() {
        return animation;
    }
    
    public void setCurrentAnimation(AnimationSetWrapper set, Animation<?> animation, int startFrame) {
        if(!set.equals(animSet)) {
            if(this.animSet != null) {
                this.animSet.removeReference(this);
            }
            
            if(set != null) {
                this.animSet = set;
                set.getReference(this);
            }
        }
        
        this.animation = animation;
        if(animation != null) {
            if(startFrame != 0 && startFrame > 0) {
                Map<Long, ? extends KeyFrame> frames = animation.getFrames();
                Entry<Long, ? extends KeyFrame> entry = MapUtil.getByIndex(frames, startFrame > frames.size() - 1 ? frames.size() - 1 : startFrame);
                animation.start(TimeUtils.millis() - entry.getKey() + entry.getValue().getFrameTime() - 1);
            } else {
                animation.start(TimeUtils.millis());
            }
        }
    }
    
    @Override
    public BoundingRectangle getBoundingBox() {
        return boundingBox;
    }
    
    public float getRotation() {
        return rotation;
    }
    
    public void setRotation(float rotation) {
        this.rotation = rotation;
        this.boundingBox.setRotation(rotation);
    }
    
    public void render() {
        onRender();
        if(animation != null && isVisible) {
            animation.drawCurrentFrame(position.x, position.y + height, scale, rotation);
        }
    }
    
    public void renderBox() {
        if(canCollide) {
            Undertailor.getRenderer().setShapeColor(BOX_COLOR, 1F);
        } else {
            Undertailor.getRenderer().setShapeColor(BOX_COLOR_INACTIVE, 1F);
        }
        
        this.boundingBox.renderBox();
    }
    
    public void onPause() {}
    public void onResume() {}
    public void onRender() {}
    public void onDestroy() {}
    public void onPersist(WorldRoom newRoom, Entrypoint entrypoint) {}
    public void process(float delta, InputData input) {}
    @Override public void onCollide(Collider collider) {}
    public abstract String getObjectName();
}
