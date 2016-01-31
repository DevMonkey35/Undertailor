package me.scarlet.undertailor.collision.bbshapes;

import com.badlogic.gdx.physics.box2d.Body;

public interface BoundingBox {
    
    public boolean canCollide();
    public void setCanCollide(boolean flag);
    public boolean isSensor();
    public void setSensor(boolean flag);
    public void applyFixture(Body body);
    public void renderBox(Body body);
    
}
