package me.scarlet.undertailor.collision;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CollisionHandler {
    
    public CollisionHandler() {}
    
    public <T extends Collider> Object process(Set<T> targets, Set<T> colliders) {
        Map<T, Map<T, CollisionType>> returned = new HashMap<>();
        
        for(T target : targets) {
            returned.put(target, new HashMap<>());
            for(T collider : colliders) {
            }
        }
        
        return returned;
    }
}
