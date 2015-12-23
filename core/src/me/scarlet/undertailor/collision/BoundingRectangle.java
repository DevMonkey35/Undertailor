package me.scarlet.undertailor.collision;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.Undertailor;

public class BoundingRectangle {
    
    private Vector2 pos;
    private Vector2 origin;
    private Vector2 offset;
    private Vector2 dimensions;
    private float rotation;
    private Polygon poly;
    private float scale;
    
    public BoundingRectangle() {
        this.poly = new Polygon(new float[] {0, 0, 0, 0, 0, 0, 0, 0});
        this.pos = new Vector2(0, 0);
        this.offset = new Vector2(0, 0);
        this.origin = new Vector2(0, 0);
        this.dimensions = new Vector2(0, 0);
        this.rotation = 0F;
        this.scale = 1F;
    }
    
    public void updatePoly() {
        poly.setVertices(new float[] {0, 0, dimensions.x, 0, dimensions.x, dimensions.y, 0, dimensions.y});
        poly.setPosition(pos.x - origin.x + offset.x, pos.y - origin.y + offset.y);
        poly.setOrigin(origin.x, origin.y);
        poly.setScale(scale, scale);
        poly.setRotation(rotation);
    }
    
    public Vector2 getPosition() {
        return pos;
    }
    
    public void setPosition(float x, float y) {
        this.pos.set(x, y);
        updatePoly();
    }
    
    public Vector2 getPositionOffset() {
        return offset;
    }
    
    public void setPositionOffset(float x, float y) {
        this.offset.set(x, y);
        updatePoly();
    }
    
    public Vector2 getOrigin() {
        return origin;
    }
    
    public void setOrigin(float x, float y) {
        this.origin.set(x, y);
        updatePoly();
    }
    
    public Vector2 getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(float width, float height) {
        this.dimensions.set(width, height);
        updatePoly();
    }
    
    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.scale = scale;
        updatePoly();
    }
    
    public float getRotation() {
        return rotation;
    }
    
    public void setRotation(float rotation) {
        this.rotation = rotation;
        updatePoly();
    }
    
    public float[] getVertices() {
        return poly.getTransformedVertices();
    }
    
    public Polygon getPolygon() {
        return poly;
    }
    
    public void renderBox() {
        float[] vertices = this.getVertices();
        Vector2 lastVertex = null;
        Vector2 firstVertex = null;
        for(int i = 0; i < 4; i++) {
            Vector2 vertex = new Vector2(vertices[i * 2], vertices[i * 2 + 1]);
            if(firstVertex == null) {
                firstVertex = vertex;
            }
            
            if(lastVertex != null) {
                Undertailor.getRenderer().drawLine(lastVertex, vertex, 0.5F);
            }
            
            lastVertex = vertex;
        }
        
        Undertailor.getRenderer().drawLine(lastVertex, firstVertex, 0.5F);
    }
}
