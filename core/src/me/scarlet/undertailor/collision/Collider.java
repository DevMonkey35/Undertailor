package me.scarlet.undertailor.collision;

public interface Collider {
    
    public BoundingRectangle getBoundingBox();
    public void onCollide(Collider collider);
    public boolean focusCollide();
    public boolean canCollide();
    public boolean isSolid();
    
}
