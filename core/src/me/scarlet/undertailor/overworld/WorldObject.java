package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Animation;
import me.scarlet.undertailor.util.InputRetriever.InputData;

public abstract class WorldObject {
    
    public static boolean renderBoxes = true;
    
    private int z;
    private float scale;
    private WorldRoom room;
    private Vector2 position;
    private Rectangle boundingBox;
    private Animation<?> animation;
    private float boxSizeX, boxSizeY;
    private float boxOriginX, boxOriginY;
    
    public WorldObject() {
        this.boundingBox = new Rectangle(0, 0, 0, 0);
        this.position = new Vector2(0, 0);
        this.animation = null;
        this.boxOriginX = 0F;
        this.boxOriginY = 0F;
        this.boxSizeX = 0F;
        this.boxSizeY = 0F;
        this.room = null;
        this.scale = 1F;
        this.z = 0;
    }
    
    public int getZ() {
        return z;
    }
    
    public void setZ(int z) {
        this.z = z;
    }
    
    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.scale = scale < 0F ? 0F : scale;
        this.updateBox();
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
        this.updateBox();
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
        this.boxSizeX = width;
        this.boxSizeY = height;
        this.updateBox();
    }
    
    public void setBoundingBoxOrigin(float x, float y) {
        this.boxOriginX = x;
        this.boxOriginY = y;
        this.updateBox();
    }
    
    private void updateBox() {
        boundingBox.setSize(boxSizeX * scale, boxSizeY * scale);
        boundingBox.setPosition(position.x - boxOriginX, position.y - boxOriginY);
    }
    
    public void render() {
        onRender();
        if(animation != null) {
            animation.drawCurrentFrame(TimeUtils.timeSinceMillis(animation.getStartTime()), position.x, position.y, scale);
        }
    }
    
    public void renderBox() {
        Undertailor.getRenderer().setShapeColor(Color.WHITE);
        Undertailor.getRenderer().drawRectangle(boundingBox.getPosition(new Vector2()), boundingBox.width, boundingBox.height, 0.5F);
    }
    
    public void process(float delta, InputData input) {}
    public void onRender() {}
    public void onDestroy() {}
}
