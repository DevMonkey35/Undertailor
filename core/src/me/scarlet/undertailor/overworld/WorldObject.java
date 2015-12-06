package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.collision.Collider;
import me.scarlet.undertailor.gfx.Animation;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.wrappers.AnimationSetWrapper;

public abstract class WorldObject implements Collider {

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
    private Vector2 position;
    private boolean isVisible;
    private boolean canCollide;
    private Rectangle boundingBox;
    // private Polygon boundingBox;
    private AnimationSetWrapper animSet;
    private Animation<?> animation;
    private Vector2 boxOrigin;
    private Vector2 boxSize;
    
    protected int id;
    protected WorldRoom room;
    
    public WorldObject() {
        this.boundingBox = new Rectangle(0, 0, 0, 0);
        this.position = new Vector2(0, 0);
        this.boxOrigin = new Vector2(0, 0);
        this.boxSize = new Vector2(0, 0);
        this.canCollide = true;
        this.isVisible = true;
        this.animation = null;
        this.animSet = null;
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
    
    public void setCurrentAnimation(AnimationSetWrapper set, Animation<?> animation) {
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
    }
    
    public Rectangle getBoundingBox() {
        return boundingBox;
    }
    
    public Vector2 getBoundingBoxSize() {
        return boxSize;
    }
    
    public void setBoundingBoxSize(float width, float height) {
        this.boxSize.x = width;
        this.boxSize.y = height;
        this.updateBox();
    }
    
    public Vector2 getBoundingBoxOrigin() {
        return boxOrigin;
    }
    
    public void setBoundingBoxOrigin(float x, float y) {
        this.boxOrigin.x = x;
        this.boxOrigin.y = y;
        this.updateBox();
    }
    
    private void updateBox() {
        boundingBox.setSize(boxSize.x * scale, boxSize.y * scale);
        boundingBox.setPosition(position.x - (boxOrigin.x * scale), position.y - (boxOrigin.y * scale));
    }
    
    public void render() {
        onRender();
        if(animation != null && isVisible) {
            long stateTime = TimeUtils.timeSinceMillis(animation.getStartTime());
            animation.drawCurrentFrame(stateTime, position.x, position.y, scale);
        }
    }
    
    public void renderBox() {
        Undertailor.getRenderer().setShapeColor(BOX_COLOR);
        Undertailor.getRenderer().drawRectangle(boundingBox.getPosition(new Vector2()), boundingBox.width, boundingBox.height, 0.5F);
    }
    
    public void onRender() {}
    public void onDestroy() {}
    public void process(float delta, InputData input) {}
    public abstract String getObjectName();
}
