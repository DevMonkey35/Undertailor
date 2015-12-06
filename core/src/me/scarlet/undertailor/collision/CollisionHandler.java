package me.scarlet.undertailor.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CollisionHandler {
    
    public static final Map<Collider, Set<Collider>> RETURN_MAP;
    
    static {
        RETURN_MAP = new HashMap<>();
    }
    
    public CollisionHandler() {}
    
    public Map<Collider, Set<Collider>> process(Set<Collider> targets, Set<Collider> colliders) {
        RETURN_MAP.clear();
        for(Collider target : targets) {
            RETURN_MAP.put(target, new HashSet<>());
            for(Collider collider : colliders) {
                if(!collider.canCollide()) {
                    continue;
                }
                
                Polygon targetBox = target.getBoundingBox().getPolygon();
                Polygon colliderBox = collider.getBoundingBox().getPolygon();
                if(Intersector.overlapConvexPolygons(targetBox, colliderBox)) {
                    RETURN_MAP.get(target).add(collider);
                }
            }
        }
        
        return RETURN_MAP;
    }
    
    public void postProcess(Map<Collider, Set<Collider>> collisionMap) {
        Iterator<Entry<Collider, Set<Collider>>> iterator = collisionMap.entrySet().iterator();
        iterator.forEachRemaining(entry -> {
            for(Collider collider : entry.getValue()) {
                collider.onCollide(entry.getKey());
                entry.getKey().onCollide(collider);
            }
        });
    }
}
