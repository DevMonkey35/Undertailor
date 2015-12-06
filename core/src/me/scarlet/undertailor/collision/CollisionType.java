package me.scarlet.undertailor.collision;

public enum CollisionType {
    
    INTERSECT(0),
    ENCOMPASS(1);
    
    private int id;
    CollisionType(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public static CollisionType fromId(int id) {
        for(CollisionType type : CollisionType.values()) {
            if(type.id == id) {
                return type;
            }
        }
        
        return null;
    }
}
