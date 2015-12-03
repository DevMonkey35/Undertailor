package me.scarlet.undertailor.overworld;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class WorldRoom {
    
    private static SortedMap<Integer, Set<WorldObject>> orderStore;
    
    static {
        orderStore = new TreeMap<>((Integer i1, Integer i2) -> {
            return i1.compareTo(i2);
        });
    }
    
    private Map<Integer, WorldObject> objects;
    
    public WorldRoom() {
        this.objects = new HashMap<>();
    }
    
    public SortedMap<Integer, Set<WorldObject>> getObjectsInRenderOrder() {
        orderStore.clear();
        for(WorldObject object : objects.values()) {
            if(!orderStore.containsKey(object.getZ())) {
                orderStore.put(object.getZ(), new TreeSet<WorldObject>((WorldObject obj1, WorldObject obj2) -> {
                    return new Float(obj1.getPosition().y).compareTo(obj2.getPosition().y);
                }));
            }
            
            orderStore.get(object.getZ()).add(object);
        }
        
        return orderStore;
    }
}
