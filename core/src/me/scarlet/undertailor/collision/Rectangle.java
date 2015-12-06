package me.scarlet.undertailor.collision;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Rectangle {
    
    private Vector2 pos;
    private float rotation;
    private Vector2 origin;
    private Vector2 dimensions;
    
    public Rectangle() {
        this.rotation = 0;
        this.pos = new Vector2(0, 0);
        this.origin = new Vector2(0, 0);
        this.dimensions = new Vector2(0, 0);
    }
}
