package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class WorldObject {
    
    private WorldRoom room;
    private Vector2 position;
    private Animation animation;
    private Rectangle boundingBox;
    private float boxOriginX, boxOriginY;
    
    public WorldObject() {
        this.room = null;
        this.boundingBox = new Rectangle(0, 0, 0, 0);
        this.boxOriginX = 0;
        this.boxOriginY = 0;
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
    
    public Animation getCurrentAnimation() {
        return animation;
    }
    
    public void setCurrentAnimation(Animation animation) {
        this.animation = animation;
    }
    
    public Rectangle getBoundingBox() {
        return boundingBox;
    }
    
    public void setBoundingBoxSize(float width, float height) {
        boundingBox.setSize(width, height);
    }
    
    public void setBoundingBoxOrigin(float x, float y) {
        this.boxOriginX = x;
        this.boxOriginY = y;
    }
    
    public void render() {
        
    }
    
    public void renderBoundingBox() {
        
    }
}
