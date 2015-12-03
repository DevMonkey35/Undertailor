package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.gfx.Animation;

public abstract class WorldObject {
    
    private int z;
    private WorldRoom room;
    private Vector2 position;
    private Rectangle boundingBox;
    private Animation<?> animation;
    private float boxOriginX, boxOriginY;
    
    public WorldObject() {
        this.room = null;
        this.boundingBox = new Rectangle(0, 0, 0, 0);
        this.boxOriginX = 0;
        this.boxOriginY = 0;
        this.animation = null;
        this.z = 0;
    }
    
    public int getZ() {
        return z;
    }
    
    public void setZ(int z) {
        this.z = z;
    }
    
    public WorldRoom getRoom() {
        return room;
    }
    
    public void setRoom(WorldRoom room) {
        this.room = room;
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
        boundingBox.setPosition(x - boxOriginX, y - boxOriginY);
    }
    
    public Animation<?> getCurrentAnimation() {
        return animation;
    }
    
    public void setCurrentAnimation(Animation<?> animation) {
        this.animation = animation;
    }
    
    public Rectangle getBoundingBox() {
        return boundingBox;
    }
    
    public void setBoundingBoxSize(float width, float height) {
        boundingBox.setSize(width, height);
        boundingBox.setPosition(position.x - boxOriginX, position.y - boxOriginY);
    }
    
    public void setBoundingBoxOrigin(float x, float y) {
        this.boxOriginX = x;
        this.boxOriginY = y;
    }
    
    public void onRender() {
        
    }
    
    public void render() {
        onRender();
    }
    
    public void renderBoundingBox() {
        
    }
}
