package me.scarlet.undertailor.util;

import com.badlogic.gdx.math.Vector3;

public interface Displayable {
    
    public Vector3 getPosition();
    public void setPosition(Vector3 position);
    
    public float getAlpha();
    public default void setAlpha(float alpha) {
        this.setAlpha(alpha, false);
    }
    
    public void setAlpha(float alpha, boolean smooth);
}
