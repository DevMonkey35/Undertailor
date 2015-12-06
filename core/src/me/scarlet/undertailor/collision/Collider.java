package me.scarlet.undertailor.collision;

import com.badlogic.gdx.math.Polygon;

public interface Collider {
    
    public Polygon getHitbox();
    public void onCollide(CollisionType type, Collider collider);
    
}
