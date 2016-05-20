package me.scarlet.undertailor.gfx;

/**
 * Skeleton implementation of something that can be drawn.
 */
public interface Renderable {
    
    public default void draw(float x, float y) {
        this.draw(x, y, 1F, 1F);
    }
    
    public default void draw(float x, float y, float scale) {
        this.draw(x, y, scale, scale);
    }
    
    public default void draw(float x, float y, float scaleX, float scaleY) {
        this.draw(x, y, scaleX, scaleY, false, false);
    }
    
    public default void draw(float x, float y, float scaleX, float scaleY, boolean flipX, boolean flipY) {
        this.draw(x, y, scaleX, scaleY, flipX, flipY, 0F);
    }
    
    public void draw(float x, float y, float scaleX, float scaleY, boolean flipX, boolean flipY, float rotation);
    
}
