package me.scarlet.undertailor.environment.overworld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import me.scarlet.undertailor.collision.bbshapes.BoundingBox;
import me.scarlet.undertailor.gfx.AnimationData;

public class WorldObjectComponent {
    
    private Body body;
    private BodyDef bodyDef;
    
    private int localZ;
    private float rotation;
    private Vector2 localPos;
    private WorldObject parent;
    
    private BoundingBox bounding;
    private AnimationData animation;
    
    public WorldObjectComponent(WorldObject parent) {
        this.localZ = 0;
        this.rotation = 0F;
        this.parent = parent;
        this.localPos = new Vector2(0, 0);
        
        this.body = null;
        this.bodyDef = WorldObject.generateDefaultObjectDef();
        
        this.bounding = null;
        this.animation = null;
    }
    
    public void prepareForRegistry() {
        this.parent.getRoom().getCollisionHandler().getWorld().createBody(bodyDef);
    }
    
    public Vector2 getLocalPosition() {
        return this.localPos;
    }
    
    public void setLocalPosition(float x, float y) {
        this.localPos.set(x, y);
        Vector2 parentPos = this.parent.getPosition();
        if(this.body == null) {
            this.bodyDef.position.set(parentPos.x + x, parentPos.y + y);
        }
    }
    
    public Vector2 getPosition() {
        if(this.body == null) {
            return body.getPosition();
        } else {
            return bodyDef.position;
        }
    }   
    
    public float getRotation() {
        return this.rotation;
    }
    
    public void setRotation() {
    }
    
    public int getLocalZ() {
        return this.localZ;
    }
    
    public void setLocalZ(int z) {
        this.localZ = z;
    }
    
    public BoundingBox getBoundingBox() {
        return this.bounding;
    }
    
    public AnimationData getAnimation() {
        return this.animation;
    }
    
    public void updatePosition(float parentX, float parentY, float rotation, float scale) {
        if(this.body != null) {
            this.bounding.applyFixture(body);
            
            WeldJointDef jointDef = new WeldJointDef();
            jointDef.type = JointType.WeldJoint;
            jointDef.collideConnected = false;
            jointDef.referenceAngle = (float) (Math.toRadians(this.rotation) - parent.getBody().getAngle());
            jointDef.dampingRatio = 0;
            jointDef.frequencyHz = 0;
            jointDef.initialize(parent.getBody(), this.body, parent.getPosition());
        }
    }
}
