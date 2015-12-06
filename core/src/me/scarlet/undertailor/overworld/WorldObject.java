package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.collision.BoundingRectangle;
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
    private boolean isSolid;
    private boolean isVisible;
    private boolean canCollide;
    private boolean focusCollide;
    private Animation<?> animation;
    private AnimationSetWrapper animSet;
    private Vector2 position;
    private float rotation;
    
    private BoundingRectangle boundingBox;
    
    protected int id;
    protected WorldRoom room;
    
    public WorldObject() {
        this.boundingBox = new BoundingRectangle();
        this.position = new Vector2();
        this.focusCollide = false;
        this.canCollide = true;
        this.isVisible = true;
        this.animation = null;
        this.isSolid = true;
        this.animSet = null;
        this.rotation = 0F;
        this.room = null;
        this.scale = 1F;
        this.z = 0;
    }
    
    public int getId() {
        return id;
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
        this.boundingBox.setScale(scale);
    }
    
    public boolean isSolid() {
        return isSolid;
    }
    
    public void setSolid(boolean flag) {
        this.isSolid = flag;
    }
    
    public boolean focusCollide() {
        return focusCollide;
    }
    
    public void setFocusCollide(boolean flag) {
        this.focusCollide = flag;
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
        boundingBox.setPosition(x, y);
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
            long stateTime = TimeUtils.timeSinceMillis(animation.getStartTime());
            animation.drawCurrentFrame(stateTime, position.x, position.y, scale, rotation);
        }
    }
    
    public void renderBox() {
        if(canCollide) {
            Undertailor.getRenderer().setShapeColor(BOX_COLOR);
        } else {
            Undertailor.getRenderer().setShapeColor(BOX_COLOR_INACTIVE);
        }
        
        float[] vertices = boundingBox.getVertices();
        Vector2 lastVertex = null;
        Vector2 firstVertex = null;
        for(int i = 0; i < 4; i++) {
            Vector2 vertex = new Vector2(vertices[i * 2], vertices[i * 2 + 1]);
            if(firstVertex == null) {
                firstVertex = vertex;
            }
            
            if(lastVertex != null) {
                Undertailor.getRenderer().drawLine(lastVertex, vertex, 0.5F);
            }
            
            lastVertex = vertex;
        }
        
        Undertailor.getRenderer().drawLine(lastVertex, firstVertex, 0.5F);
    }
    
    public void onRender() {}
    public void onDestroy() {}
    public void process(float delta, InputData input) {}
    public void onCollide(Collider collider) {}
    public abstract String getObjectName();
}
