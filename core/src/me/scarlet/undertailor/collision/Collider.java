package me.scarlet.undertailor.collision;

import com.badlogic.gdx.math.Vector2;

public interface Collider {
    
    public BoundingRectangle getBoundingBox();
    public void onCollide(Collider collider);
    public Vector2 getVelocity();
    public boolean focusCollide();
    public boolean canCollide();
    public boolean isSolid();
    
}
